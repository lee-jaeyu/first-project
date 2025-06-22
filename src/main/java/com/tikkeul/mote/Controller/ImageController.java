package com.tikkeul.mote.Controller;

import com.tikkeul.mote.service.ImageService;
import org.apache.commons.imaging.ImageReadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.Map;



@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException, ImageReadException {
        File tempFile = File.createTempFile("upl" +
                "oad-", file.getOriginalFilename());
        file.transferTo(tempFile);

        Map<String, Object> gpsInfo = imageService.getGPSFromImage(tempFile);

        String ocrJsonString = imageService.sendImageToPython(tempFile);
        Map<String, Object> ocrResult = objectMapper.readValue(ocrJsonString, new TypeReference<Map<String, Object>>() {});

        return ResponseEntity.ok(Map.of(
                "gps", gpsInfo,
                "ocr", ocrResult
        ));
    }
}


/*
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@RestController
public class ImageController {

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException, ImageReadException {
        File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);

        Map<String, Object> gpsInfo = getGPSFromImage(tempFile);

        // Python 서버로 전송
        String ocrResult = sendImageToPython(tempFile);

        return ResponseEntity.ok(Map.of(
                "gps", gpsInfo,
                "ocr", ocrResult
        ));
    }

    private Map<String, Object> getGPSFromImage(File imageFile) throws IOException, ImageReadException {
        var metadata = Imaging.getMetadata(imageFile);
        if (metadata instanceof JpegImageMetadata jpegMetadata) {
            TiffField latRef = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
            TiffField lonRef = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
            TiffField latField = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LATITUDE);
            TiffField lonField = jpegMetadata.findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LONGITUDE);

            if (latField != null && lonField != null) {
                double latitude = convertToDegrees(latField.getValue(), latRef.getStringValue());
                double longitude = convertToDegrees(lonField.getValue(), lonRef.getStringValue());
                return Map.of("latitude", latitude, "longitude", longitude);
            }
        }
        return Map.of("message", "No GPS info");
    }

    private double convertToDegrees(Object value, String ref) {
        RationalNumber[] numbers = (RationalNumber[]) value;
        double deg = numbers[0].doubleValue();
        double min = numbers[1].doubleValue();
        double sec = numbers[2].doubleValue();
        double result = deg + (min / 60.0) + (sec / 3600.0);
        return ("S".equalsIgnoreCase(ref) || "W".equalsIgnoreCase(ref)) ? -result : result;
    }

    private String sendImageToPython(File imageFile) throws IOException {
        WebClient client = WebClient.create("http://localhost:5000");

        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

        return client.post()
                .uri("/ocr")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", new ByteArrayResource(imageBytes) {
                    @Override
                    public String getFilename() {
                        return imageFile.getName();
                    }
                }))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
*/
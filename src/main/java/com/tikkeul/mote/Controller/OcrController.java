package com.tikkeul.mote.Controller;

import com.tikkeul.mote.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/ocr", produces = "application/json; charset=UTF-8")
public class OcrController {

    @Autowired
    private OcrService ocrService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        File tempFile = null;
        try {
            // 임시 파일 저장
            tempFile = File.createTempFile("upload-", ".jpg");
            file.transferTo(tempFile);

            // OCR 실행
            String plateNumber = ocrService.readPlateFromImage(tempFile.getAbsolutePath());

            // 클라이언트에 JSON 형태로 결과 전송
            Map<String, String> response = new HashMap<>();
            response.put("plateNumber", plateNumber);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            // 오류 응답
            Map<String, String> error = new HashMap<>();
            error.put("plateNumber", "서버 오류");
            return ResponseEntity.status(500).body(error);

        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete(); // 임시 파일 삭제
            }
        }
    }

    @GetMapping("/check")
    public String check() {
        return "OCR API is live - 한글 ";
    }
}



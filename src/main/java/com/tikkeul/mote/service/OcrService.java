package com.tikkeul.mote.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OcrService {

    // 파이썬 실행 경로
    String pythonPath = "C:\\Users\\이재유\\AppData\\Local\\Programs\\Python\\Python312\\python.exe";

    // OCR 스크립트 절대경로
    String scriptPath = "C:\\mote\\python\\ocr_plate_reader.py";

    public String readPlateFromImage(String imagePath) {
        try {
            ProcessBuilder builder = new ProcessBuilder(pythonPath, scriptPath, imagePath);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            //  인코딩을 MS949로 지정 (한글 깨짐 방지)
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), Charset.forName("MS949"))
            );

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(" ");
            }

            String cleaned = output.toString().replaceAll("\\s+", "");
            System.out.println("🔥 OCR 원시 출력: " + cleaned);

            int exitCode = process.waitFor();
            if (exitCode != 0) return "ERROR";

            //  번호판 정규식 필터링
            Pattern pattern = Pattern.compile("\\d{2,3}[가-힣]\\d{4}");
            Matcher matcher = pattern.matcher(cleaned);
            if (matcher.find()) {
                return matcher.group();  // 예: 274다7308
            }

            return "인식 실패";

        } catch (Exception e) {
            e.printStackTrace();
            return "EXCEPTION";
        }
    }
}












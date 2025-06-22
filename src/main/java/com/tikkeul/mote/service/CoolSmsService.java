package com.tikkeul.mote.service;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class CoolSmsService {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.sender}")
    private String sender;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        System.out.println("[CoolSmsService] 초기화 시작");
        System.out.println("API Key: " + apiKey);
        System.out.println("API Secret: " + apiSecret);
        System.out.println("Sender: " + sender);
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public boolean sendSms(String to, String code) {
        try {
            Message message = new Message();
            message.setFrom(sender);
            message.setTo(to);
            message.setText("[주차] 인증번호: " + code + " 를 입력해주세요.");

            SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));

            return "2000".equals(response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
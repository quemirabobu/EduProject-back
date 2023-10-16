package com.bit.eduventure.sms.Controller;


import com.bit.eduventure.dto.ResponseDTO;
import com.bit.eduventure.sms.DTO.MessageDTO;
import com.bit.eduventure.sms.DTO.SmsResponseDTO;
import com.bit.eduventure.sms.Service.SmsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequiredArgsConstructor
public class SmsController {
    private final SmsService smsService;

    @GetMapping("/send")
    public String getSmsPage() {
        return "sendSms";
    }

    @PostMapping("/sms/send")
    public  ResponseEntity<?>  sendSms(@RequestBody MessageDTO messageDto, Model model) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        ResponseDTO<SmsResponseDTO> responseDTO = new ResponseDTO<>();

try {
    System.out.println(messageDto);
    System.out.println("이것이 메세지 디티오");
    SmsResponseDTO response = smsService.sendSms(messageDto);
    responseDTO.setItem(response);
    responseDTO.setStatusCode(HttpStatus.OK.value());
    return ResponseEntity.ok().body(responseDTO);
}catch (Exception e){
    responseDTO.setErrorMessage(e.getMessage());
    responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
    return ResponseEntity.badRequest().body(responseDTO);
}



    }














}

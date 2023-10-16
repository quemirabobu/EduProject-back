package com.bit.eduventure.nchat.service;

import com.bit.eduventure.User.DTO.UserDTO;
import com.bit.eduventure.nchat.dto.ChatUserDTO;
import com.bit.eduventure.nchat.dto.MemberResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class NchatService {
    @Value("${ncloudchat.project.id}")
    private String PROJECT_ID;
    @Value("${ncloudchat.api.key}")
    private String API_KEY;

    private final String BASE_URL = "https://dashboard-api.ncloudchat.naverncp.com/v1/api";

    public void nChatJoin (UserDTO user) {
        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(BASE_URL);
            urlBuilder.append("/members");

            String url = urlBuilder.toString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
            headers.set("x-project-id", PROJECT_ID);
            headers.set("x-api-key", API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);

            ChatUserDTO chatUserDTO = ChatUserDTO.builder()
                    .userId(user.getUserId())
                    .name(user.getUserName())
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(chatUserDTO);

            HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

            ResponseEntity<MemberResponseDTO> responseEntity = restTemplate.exchange(new URI(url), HttpMethod.POST, body, MemberResponseDTO.class);

            System.out.println("nChat 가입 성공" + responseEntity.getBody());

        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void nChatDelete (UserDTO user) {
        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(BASE_URL);
            urlBuilder.append("/members/");
            urlBuilder.append(user.getUserId());

            String url = urlBuilder.toString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "*/*");
            headers.set("x-project-id", PROJECT_ID);
            headers.set("x-api-key", API_KEY);

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<MemberResponseDTO> responseEntity = restTemplate.exchange(new URI(url), HttpMethod.DELETE, requestEntity, MemberResponseDTO.class);
            System.out.println("nChat 삭제 성공" + responseEntity.getBody());

        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


    }
}

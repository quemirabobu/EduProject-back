package com.bit.eduventure.chatbot.service;

import com.bit.eduventure.exception.errorCode.MakeSignatureException;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@Service
public class ChatBotService {
    @Value("${cloud.ncp.chatbot.secret.key}")
    private String secretKey;

    @Value("${cloud.ncp.chatbot.url}")
    private String apiUrl;

    private final String UTF8 = "UTF-8";

    public String processMessage(String chatMessage) {
        System.out.println("사용자가 보낸 메시지: " + chatMessage);

        String responseMessage = callChatBotAPI(chatMessage);

        System.out.println("ChatBot 메시지: " + responseMessage);
        return responseMessage;
    }

    private String callChatBotAPI(String message) {
        try {
            String requestBody = getReqMessage(message);
            String encodeBase64String = makeSignature(requestBody, secretKey);

            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json;UTF-8");
            con.setRequestProperty("X-NCP-CHATBOT_SIGNATURE", encodeBase64String);

            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(requestBody.getBytes(UTF8));
                wr.flush();
            }

            int responseCode = con.getResponseCode();

            if (responseCode == 200) {
                try (BufferedReader in =
                             new BufferedReader(
                                     new InputStreamReader(
                                             con.getInputStream(), UTF8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    JSONParser jsonParser = new JSONParser();
                    JSONObject json = (JSONObject) jsonParser.parse(response.toString());
                    JSONArray bubblesArray = (JSONArray) json.get("bubbles");
                    JSONObject bubbles = (JSONObject) bubblesArray.get(0);
                    JSONObject data = (JSONObject) bubbles.get("data");
                    return (String) data.get("description");
                }
            } else {
                throw new RuntimeException("Chat Bot API 호출 실패");
            }
        } catch (Exception e) {
            throw new RuntimeException("callChatBotAPI 내 오류");
        }
    }

    private String makeSignature(String message, String secretKey) {
        try {
            String HmacSHA256 = "HmacSHA256";
            byte[] secrete_key_bytes = secretKey.getBytes(UTF8);
            SecretKeySpec signingKey = new SecretKeySpec(secrete_key_bytes, HmacSHA256);
            Mac mac = Mac.getInstance(HmacSHA256);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(message.getBytes(UTF8));
            return Base64.encodeBase64String(rawHmac);
        } catch (Exception e) {
            throw new MakeSignatureException();
        }
    }

    private String getReqMessage(String message) {
        try {
            JSONObject obj = new JSONObject();
            long timestamp = new Date().getTime();
            obj.put("version", "v2");
            obj.put("userId", "U47b00b58c90f8e47428af8b7bddcda3d");
            obj.put("timestamp", timestamp);

            JSONObject data_obj = new JSONObject();
            data_obj.put("description", message);

            JSONObject bubbles_obj = new JSONObject();
            bubbles_obj.put("type", "text");
            bubbles_obj.put("data", data_obj);

            JSONArray bubbles_array = new JSONArray();
            bubbles_array.add(bubbles_obj);

            obj.put("bubbles", bubbles_array);
            obj.put("event", "send");

            return obj.toString();
        } catch (Exception e) {
            throw new RuntimeException("Chat Bot getReqMessage 실패");
        }
    }
}

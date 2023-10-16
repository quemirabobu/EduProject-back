package com.bit.eduventure.chatbot.controller;

import com.bit.eduventure.chatbot.service.ChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/chatBot")
@RestController
public class ChatBotController {

    private final ChatBotService chatBotService;

    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/public/{roomId}")
    public String sendMessage(@Payload String chatMessage,
                              @DestinationVariable String roomId) {
        return chatBotService.processMessage(chatMessage);
    }
}

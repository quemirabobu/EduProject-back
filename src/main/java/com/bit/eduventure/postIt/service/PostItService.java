package com.bit.eduventure.postIt.service;

import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Repository.UserRepository;
import com.bit.eduventure.postIt.dto.PostItDTO;
import com.bit.eduventure.postIt.entity.PostItEntity;
import com.bit.eduventure.postIt.repository.PostItRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostItService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostItRepository postItRepository;

    //포스트잇을 보내는 메소드
    public void sendPostIt(PostItDTO postItDTO) {
        User sender = userRepository.findById(postItDTO.getSenderId()).orElse(null);
        User receiver = userRepository.findById(postItDTO.getReceiverId()).orElse(null);

        if (sender != null && receiver != null) {
            // 부모 유저(sender)가 자식 유저(receiver)에게만 보낼 수 있도록 체크
            if(!sender.getUserType().equals("PARENT") || !receiver.getUserType().equals("CHILD")) {
                throw new RuntimeException("부모 유저만 자식 유저에게 포스트잇을 보낼 수 있습니다.");
            }

            String message = postItDTO.getMessage();
            System.out.println(sender.getUserName() + "님이 " + receiver.getUserName() + "님에게 포스트잇을 보냈습니다: " + message);

            // 포스트잇 정보를 저장
            PostItEntity postItEntity = PostItEntity.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .message(message)
                    .sentDate(LocalDateTime.now())
                    .replied(false)
                    .build();
            postItRepository.save(postItEntity);
        } else {
            throw new RuntimeException("보내는 사람 또는 받는 사람을 찾을 수 없습니다.");
        }
    }

    public List<PostItDTO> getPostItList() {
        List<PostItEntity> postItEntities = postItRepository.findAll();

        return postItEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PostItDTO convertToDTO(PostItEntity postItEntity) {
        return PostItDTO.builder()
                .id(postItEntity.getId())
                .senderId(postItEntity.getSender().getId())
                .receiverId(postItEntity.getReceiver().getId())
                .message(postItEntity.getMessage())
                .sentDate(postItEntity.getSentDate())
                .replied(postItEntity.isReplied())
                .build();
    }
}

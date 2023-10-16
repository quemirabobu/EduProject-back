package com.bit.eduventure.livestation.controller;

import com.bit.eduventure.dto.ResponseDTO;
import com.bit.eduventure.livestation.dto.LiveStationInfoDTO;
import com.bit.eduventure.livestation.dto.LiveStationUrlDTO;
import com.bit.eduventure.livestation.service.LiveStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/live")
@RequiredArgsConstructor
@RestController
public class LiveStationRestController {
    private final LiveStationService liveStationService;

    //채널 아이디가 있지만 강의중 것을 확인할 때
    @GetMapping("/info/{channelId}")
    public ResponseEntity<?> getChannelInfo(@PathVariable String channelId) {
        ResponseDTO<LiveStationInfoDTO> responseDTO = new ResponseDTO<>();

        LiveStationInfoDTO dto = liveStationService.getChannelInfo(channelId);

        responseDTO.setItem(dto);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/url/{channelId}")
    public ResponseEntity<?> getServiceURL(@PathVariable String channelId) {
        ResponseDTO<LiveStationUrlDTO> responseDTO = new ResponseDTO<>();

        List<LiveStationUrlDTO> dtoList = liveStationService.getServiceURL(channelId, "GENERAL");

        responseDTO.setItems(dtoList);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/delete/{channelId}")
    public ResponseEntity<?> deleteChannel(@PathVariable String channelId) {
        return liveStationService.deleteChannel(channelId);
    }
}

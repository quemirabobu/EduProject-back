package com.bit.eduventure.objectStorage.controller;


import com.bit.eduventure.objectStorage.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/storage")
@RequiredArgsConstructor
@RestController
public class ObjectStorageRestController {
    private final ObjectStorageService storageService;

    @GetMapping("/download/{objectName}")
    public ResponseEntity<?> downObject(@PathVariable String objectName) {
        return storageService.getObject(objectName);
    }
}

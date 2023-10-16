package com.bit.eduventure.objectStorage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;

import com.bit.eduventure.exception.errorCode.ObjectStorageException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {
    private final AmazonS3 s3;
    @Value("${cloud.aws.s3.bucket.name}")
    private String bucket;

    private final String ncpAddress = "https://kr.object.ncloudstorage.com/";

    public ResponseEntity<?> getObject(String objectName) {
        try {
            S3Object o = s3.getObject(new GetObjectRequest(bucket, objectName));
            S3ObjectInputStream objectInputStream = ((S3Object) o).getObjectContent();
            byte[] bytes = IOUtils.toByteArray(objectInputStream);

            String fileName = URLEncoder.encode(objectName, "UTF-8").replaceAll("\\+", "%20");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentLength(bytes.length);
            httpHeaders.setContentDispositionFormData("attachment", fileName);

            return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
        } catch (IOException e) {
            // IOException이 발생한 경우에는 오류 상태(400 Bad Request)와 오류 메시지를 포함한 응답을 클라이언트에게 반환
            throw new ObjectStorageException();
        } catch (Exception e) {
            throw new ObjectStorageException();
        }
    }

    public void deleteObject(String objectName) {
        try {
            if (!objectName.equals("edu-venture.png")) {
                s3.deleteObject(bucket, objectName);
            }
        } catch (Exception e) {
            throw new ObjectStorageException();
        }
    }

    //파일 저장
    public String uploadFile(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();

        StringBuilder uniqueFilename = new StringBuilder(String.valueOf(System.currentTimeMillis()));
        uniqueFilename.append("_");
        uniqueFilename.append(originalFilename);

        String saveFilename = uniqueFilename.toString();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucket, saveFilename, multipartFile.getInputStream(), objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(putObjectRequest);
        } catch (Exception e) {
            //string값으로 반환을 해주면 밖에서 사용하고 있는 애는 정상적으로 저장이 된 줄 착각할 수 있다.
            throw new ObjectStorageException();
        }
        //파일을 오브젝트 스토리지에 저장하는 공식
        return saveFilename;
    }

    public String getObjectSrc(String saveFileName) {
        StringBuilder storageAddress = new StringBuilder();
        storageAddress.append(ncpAddress);
        storageAddress.append(bucket);
        storageAddress.append("/");
        storageAddress.append(saveFileName);

        return storageAddress.toString();
    }
}
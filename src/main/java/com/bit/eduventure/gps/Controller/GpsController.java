package com.bit.eduventure.gps.Controller;

import com.bit.eduventure.dto.ResponseDTO;
import com.bit.eduventure.gps.Entity.DriverPhoto;
import com.bit.eduventure.gps.Entity.GPS;
import com.bit.eduventure.gps.Repository.DriverPhotoRepository;
import com.bit.eduventure.gps.Repository.GPSRepository;
import com.bit.eduventure.objectStorage.service.ObjectStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@PropertySource("classpath:/application.properties")
@RequiredArgsConstructor
public class GpsController {

    private final GPSRepository gpsRepository;
    private final DriverPhotoRepository driverPhotoRepository;
    private final ObjectStorageService objectStorageService;

//    @Value("${cloud.aws.s3.bucket.name}")
//    private String bucketName;


    @PostMapping("/getlocationfromandroid")
    public ResponseEntity<String> receiveLocation(@RequestBody String location) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(location);

//            System.out.println(jsonNode);
            int carnumber = jsonNode.get("carnumber").asInt();
            double latitude = jsonNode.get("latitude").asDouble();
            double longitude = jsonNode.get("longitude").asDouble();
            String phonenumber = jsonNode.get("phonenumber").toString();

            GPS gps = new GPS();
            gps.setCarnumber(carnumber);
            gps.setLatitude(latitude);
            gps.setLongitude(longitude);
            gps.setPhonenumber(phonenumber);
            gps.setTime(LocalDateTime.now().toString());
            gpsRepository.save(gps);


//            System.out.println(carnumber);
//            System.out.println(latitude);
//            System.out.println(longitude);
//            System.out.println(phonenumber);
//            System.out.println(LocalDateTime.now());
            // 여기서 위도(latitude)와 경도(longitude)를 처리합니다

            return new ResponseEntity<>("Received", HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error parsing JSON", HttpStatus.BAD_REQUEST);
        }
    }














//    @GetMapping("/gotogps")
//    public ModelAndView minimiview(){
//        ModelAndView mv = new ModelAndView();
////        mv.setViewName("gpsreader.html");
//        mv.setViewName("navergpsreader.html");
//        List<GPS> gpslist = gpsRepository.findLatestForEachCarNumber();
//        System.out.println(gpslist);
//        Optional<GPS> gps1 = gpslist.stream().filter(a->a.getCarnumber()==1).findFirst();
//        Optional<GPS> gps2 = gpslist.stream().filter(a->a.getCarnumber()==2).findFirst();
//        Optional<GPS> gps3 = gpslist.stream().filter(a->a.getCarnumber()==3).findFirst();
//        System.out.println(gps1);
//        System.out.println(gps2);
//        System.out.println(gps3);
//        if(gps1.isPresent()) {
//            mv.addObject("gps1", gps1.get());
//        } else {
//            // handle the case where no GPS object with carnumber 1 exists
//        }
//        if(gps2.isPresent()) {
//            mv.addObject("gps2", gps2.get());
//        } else {
//            // handle the case where no GPS object with carnumber 1 exists
//        }
//        if(gps3.isPresent()) {
//            mv.addObject("gps3", gps3.get());
//        } else {
//            // handle the case where no GPS object with carnumber 1 exists
//        }
//        return mv;
//
//    }


    @GetMapping("/igiveyougps")
    public ResponseEntity<?> givinggps(){
        ResponseDTO<GPS> responseDTO = new ResponseDTO<>();
        List <GPS> gpslist = gpsRepository.findLatestForEachCarNumber();
        try {
            System.out.println(gpslist);
            Optional<GPS> gps1 = gpslist.stream().filter(a -> a.getCarnumber() == 1).findFirst();
            Optional<GPS> gps2 = gpslist.stream().filter(a -> a.getCarnumber() == 2).findFirst();
            Optional<GPS> gps3 = gpslist.stream().filter(a -> a.getCarnumber() == 3).findFirst();


            responseDTO.setItems(gpslist);
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }



    @PostMapping("/receivephoto")
    public ResponseEntity<String> receivePhoto(@RequestParam("image") MultipartFile file,
                                               @RequestParam("carnumber") String carNumber) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("fileisempty", HttpStatus.BAD_REQUEST);
        }
        try {
            String origin = file.getOriginalFilename(); //원본 파일명
            String saveName = objectStorageService.uploadFile(file);    //저장된 파일명
            String saveSrc = objectStorageService.getObjectSrc(saveName);   //파일주소
//
//            // 파일 이름과 확장자 추출
//            String fileName = file.getOriginalFilename();
//            String fileExtension = fileName.substring(fileName.lastIndexOf("."));

//
//
//            // S3에 저장할 고유한 키 생성
//            String s3Key = UUID.randomUUID().toString() + fileExtension;
//
//            // MultipartFile을 File 객체로 변환
//            File tempFile = File.createTempFile("temp", fileExtension);
//            file.transferTo(tempFile);
//
//            // S3에 파일 업로드
//            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3Key, tempFile).withCannedAcl(CannedAccessControlList.PublicRead);
//            System.out.println("여기까진 된건가");
//
//            System.out.println(putObjectRequest);
//            System.out.println("putobjectRequest");
//
//            amazonS3Client.putObject(putObjectRequest);
//
//            // S3 URL 생성
////            String imageUrl = "https://" + bucketName + ".s3." + amazonS3Client.getRegion() + ".amazonaws.com/" + s3Key;
//            System.out.println("여기까지. amazons3클라이언트에 풋 오브젝트했다.");
//
//            // S3 URL 생성
//            String imageUrl = endPoint + "/" + bucketName + "/" + s3Key;
//
//
//            System.out.println(imageUrl);


            DriverPhoto driverphoto = new DriverPhoto();
            driverphoto.setCarnumber(Integer.valueOf(carNumber));
            driverphoto.setPhotoname(saveSrc);
            System.out.println("여기까진된건가? 세이브 전이다");

            driverPhotoRepository.save(driverphoto);
            System.out.println("여기까진된건가? 세이브 다음이다");
            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("에러남 여기서");
            return new ResponseEntity<>("Failed to upload file", HttpStatus.OK);
        }
    }







    @GetMapping("/trytogetphotofromserver")
    public ResponseEntity<?> givephotoandnumbertofront(@RequestParam String userBus){
        ResponseDTO<DriverPhoto> responseDTO = new ResponseDTO<>();

        System.out.println(userBus);

//        List <GPS> gpslist = gpsRepository.findLatestForEachCarNumber();
        try {

            DriverPhoto driverPhoto= driverPhotoRepository.findLatestPhoto(Integer.valueOf(userBus));

            System.out.println("이것이 드라이버포토이다");
            System.out.println(driverPhoto);
            responseDTO.setItem(driverPhoto);
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }







}

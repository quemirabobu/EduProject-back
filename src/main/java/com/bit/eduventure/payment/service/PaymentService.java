package com.bit.eduventure.payment.service;

import com.bit.eduventure.payment.dto.PaymentRequestDTO;
import com.bit.eduventure.payment.entity.Payment;
import com.bit.eduventure.payment.entity.Receipt;
import com.bit.eduventure.payment.repository.PaymentRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.siot.IamportRestClient.IamportClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${iamport.api-key}")
    private String apiKey;
    @Value("${iamport.api-secret}")
    private String apiSecret;
    private IamportClient api;

    @PostConstruct
    public void init() {
        api = new IamportClient(apiKey, apiSecret);
    }

    private final PaymentRepository paymentRepository;
    private final ReceiptService receiptService;

    // 납부서 등록
    public Payment createPayment(PaymentRequestDTO requestDTO) {
        LocalDateTime now = LocalDateTime.now();
        //디비에 저장할 형태 만들기
        Payment payment = Payment.builder()
                .userNo(requestDTO.getUserNo())
                .payMethod("card")
                .totalPrice(0)
                .payFrom("에듀벤처")
                .payTo(requestDTO.getPayTo())
                .createDate(now)
                .issDate(stringToLocalDateTime(requestDTO.getIssDate()))
                .isPay(false)
                .isCancel(false)
                .impUid("")
                .build();

        //영수증에 저장할 결제PK
        int payNo = savePayment(payment).getPayNo();

        Map<String, Integer> productList = jsonTOProductMap(requestDTO.getProductList());

        //상품과 가격 디비에 저장하면서 총합 구하기
        int totalPrice = productList.entrySet().stream()
                .peek(entry -> {
                    String productName = entry.getKey();
                    int productPrice = entry.getValue();

                    Receipt receipt = Receipt.builder()
                            .paymentId(payNo)
                            .productName(productName)
                            .productPrice(productPrice)
                            .build();
                    receiptService.saveReceipt(receipt);
                })
                .mapToInt(Map.Entry::getValue)
                .sum();

        //상품의 총합을 구해서
        payment.setTotalPrice(totalPrice);

        //디비에 다시 저장
        return savePayment(payment);
    }

    @Transactional
    //게시물 수정
    public Payment createPayment(int payNo, PaymentRequestDTO requestDTO) {
        //수정 하기
        Payment payment = getPayment(payNo);
        payment.setUserNo(requestDTO.getUserNo());
        payment.setTotalPrice(0);
        payment.setIssDate(stringToLocalDateTime(requestDTO.getIssDate()));

        Map<String, Integer> productList = jsonTOProductMap(requestDTO.getProductList());

        //상품과 가격 디비에 저장하면서 총합 구하기
        int totalPrice = productList.entrySet().stream()
                .peek(entry -> {
                    String productName = entry.getKey();
                    int productPrice = entry.getValue();

                    Receipt receipt = Receipt.builder()
                            .paymentId(payNo)
                            .productName(productName)
                            .productPrice(productPrice)
                            .build();
                    receiptService.saveReceipt(receipt);
                })
                .mapToInt(Map.Entry::getValue)
                .sum();

        //상품의 총합을 구해서
        payment.setTotalPrice(totalPrice);

        paymentRepository.save(payment);
        //디비에 다시 저장
        return savePayment(payment);
    }

    //개별 납부서 조회
    public Payment getPayment(int payNo) {
        return paymentRepository.findById(payNo)
                .orElseThrow(() -> new NoSuchElementException());
    }

    //학생 개별 납부서 조회
    public Payment getPayment(int userNo, int month) {
        int year = LocalDateTime.now().getYear();
        List<Payment> payments = paymentRepository.findByPayToAndYearMonth(userNo, year, month);
        if (!payments.isEmpty()) {
            return payments.get(0);
        }
        throw new NoSuchElementException("해당 월에 납부서가 없습니다.");
    }

    //학생 납부서 전체 조회
    public List<Payment> getPaymentList(int userNo) {
        List<Payment> paymentList = paymentRepository.findAllByPayTo(userNo);
        if (!paymentList.isEmpty()) {
            return paymentList;
        }
        throw new NoSuchElementException("해당 유저의 납부서가 없습니다.");
    }

    //납부서 전체 조회
    public List<Payment> getPaymentList() {
        List<Payment> paymentList = paymentRepository.findAll();

        return paymentList;

    }

    //여러 개의 납부서 일괄 삭제
    @Transactional
    public void deletePaymentList(List<Integer> payNoList) {
        payNoList.stream()
                .forEach(payNo -> {
                    receiptService.deleteReceipt(payNo);
                    paymentRepository.deleteById(payNo);
                });
    }

    //한 개의 납부서 삭제
    public void deletePayment(int payNo) {
        receiptService.deleteReceipt(payNo);
        paymentRepository.deleteById(payNo);
    }

    //년, 월, 일만 추출하기
    public String getIssDate(String inputDate, String type) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(inputDate, inputFormatter);

        DateTimeFormatter outputFormatter = null;
        if ("year".equals(type)) {
            outputFormatter = DateTimeFormatter.ofPattern("yyyy");
        } else if ("month".equals(type)) {
            outputFormatter = DateTimeFormatter.ofPattern("MM");
        } else if ("day".equals(type)) {
            outputFormatter = DateTimeFormatter.ofPattern("dd");
        }

        if (outputFormatter != null) {
            return dateTime.format(outputFormatter);
        }
        return "";
    }


    //결제일 (문자열) -> LocalDateTime로 변환
    public LocalDateTime stringToLocalDateTime(String inputDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDateTime localDateTime = LocalDateTime.parse(inputDate, formatter);
        return localDateTime;
    }

    //페이먼트 저장
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    //상품 정보와 가격 추출
    public Map<String, Integer> jsonTOProductMap(String productList) {
        System.out.println(productList);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
        List<Map<String, Object>> dataList = gson.fromJson(productList, type);

        Map<String, Integer> resultMap = new HashMap<>();
        for (Map<String, Object> data : dataList) {
            String detail = (String) data.get("detail");
            int price = ((Number) data.get("price")).intValue();
            resultMap.put(detail, price);
        }
        return resultMap;
    }

    //결제 번호 리스트 객체화
    public List<Integer> jsonTOpayNoList(String payNoList) {
        try {
            JsonElement jsonElement = JsonParser.parseString(payNoList);
            JsonArray jsonArray = jsonElement.getAsJsonObject().getAsJsonArray("payNo");

            List<Integer> result = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                int value = element.getAsInt();
                result.add(value);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("JSON 처리 오류");
        }
    }

    // 결제 성공 후 uid db 업데이트
    public void updatePayment(int payNo, com.siot.IamportRestClient.response.Payment iamPayment) {
        Payment dbPayment = paymentRepository.findById(payNo)
                .orElseThrow(() -> new NoSuchElementException());

        dbPayment.setPayMethod(iamPayment.getPayMethod());
        dbPayment.setTotalPrice(iamPayment.getAmount().intValue());
        dbPayment.setImpUid(iamPayment.getImpUid());
        dbPayment.setPayDate(iamPayment.getPaidAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        dbPayment.setPay(true);

        paymentRepository.save(dbPayment);
    }

}

package com.bit.eduventure.payment.controller;

import com.bit.eduventure.dto.ResponseDTO;
import com.bit.eduventure.payment.dto.PaymentDTO;
import com.bit.eduventure.payment.service.PaymentService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/iamport")
@RequiredArgsConstructor
public class ImportApiController {

    @Value("${iamport.api-key}")
    private String apiKey;
    @Value("${iamport.api-secret}")
    private String apiSecret;
    private IamportClient api;

    @PostConstruct
    public void init() {
        api = new IamportClient(apiKey, apiSecret);
    }

    private final PaymentService paymentService;

    /* 아이엠포트 검증 */
    @ResponseBody
    @PostMapping("/verifyIamport/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@PathVariable(value= "imp_uid") String imp_uid) {
        try {
            return api.paymentByImpUid(imp_uid);
        } catch (IamportResponseException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /* 결제 성공 후 db 업데이트 */
    @PostMapping("/payOk")
    public ResponseEntity<?> paymentByImpUid(@RequestBody PaymentDTO paymentDTO) {
        System.out.println("payment: " + paymentDTO);
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        try {
            IamportResponse<Payment> impResponse = api.paymentByImpUid(paymentDTO.getImpUid());
            paymentService.updatePayment(paymentDTO.getPayNo(), impResponse.getResponse());

            responseDTO.setItem("정상적으로 처리되었습니다.");
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        } catch (IamportResponseException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


//    /* 결제 취소 후 db 업데이트  */
//    @PostMapping("/payRefund")
//    public ResponseEntity<?> refundByImpUid(@RequestBody com.bit.eduventure.payment.entity.Payment payment) throws IamportResponseException, IOException {
//        System.out.println(payment);
//
//        IamportResponse<Payment> impResponse = api.paymentByImpUid(payment.getImpUid());
//        paymentService.refundPayment(payment.getPayNo(), impResponse.getResponse());
//
//        ResponseDTO<RefundResponseDTO> response = new ResponseDTO<>();
//
//        try {
//            response.setStatusCode(HttpStatus.CREATED.value());
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        } catch (Exception e) {
//            response.setErrorMessage(e.getMessage());
//            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
}


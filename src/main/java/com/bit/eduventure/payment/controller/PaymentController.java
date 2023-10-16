package com.bit.eduventure.payment.controller;

import com.bit.eduventure.User.DTO.UserDTO;
import com.bit.eduventure.User.Entity.CustomUserDetails;
import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Service.UserService;
import com.bit.eduventure.dto.ResponseDTO;
import com.bit.eduventure.payment.dto.PaymentDTO;
import com.bit.eduventure.payment.dto.PaymentRequestDTO;
import com.bit.eduventure.payment.dto.PaymentResponseDTO;
import com.bit.eduventure.payment.dto.ReceiptDTO;
import com.bit.eduventure.payment.entity.Payment;
import com.bit.eduventure.payment.entity.Receipt;
import com.bit.eduventure.payment.service.PaymentService;
import com.bit.eduventure.payment.service.ReceiptService;
import com.bit.eduventure.validate.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ReceiptService receiptService;
    private final UserService userService;
    private final ValidateService validateService;

    private final String[] ISS_DATE_ARRAY = {"year", "month", "day"};

    //납부서 등록
    @PostMapping("/admin/bill")
    //클라이언트로부터 받은 HTTP POST 요청의 body 부분을 PaymentCreateRequestDTO 타입의 객체로 변환하고, 이를 requestDTO라는 매개변수로 전달
    public ResponseEntity<?> createPayment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @RequestBody PaymentRequestDTO requestDTO) {
        ResponseDTO<PaymentResponseDTO> responseDTO = new ResponseDTO<>();
        int userNo = Integer.parseInt(customUserDetails.getUsername());
        validateService.validateTeacherAndAdmin(userService.findById(userNo));

        List<PaymentResponseDTO> returnList = new ArrayList<>();

        requestDTO.setUserNo(userNo);

        PaymentDTO paymentDTO = paymentService.createPayment(requestDTO).EntityTODTO();  // 서비스 메서드 호출
        User user = userService.findById(paymentDTO.getPayTo());

        PaymentResponseDTO paymentResponseDTO = PaymentResponseDTO.builder()
                .userName(user.getUserName())
                .couNo(user.getCourse().getCouNo())
                .build();

        returnList.add(paymentResponseDTO);

        responseDTO.setItems(returnList); // 응답 DTO 설정
        responseDTO.setStatusCode(HttpStatus.OK.value()); // 상태 코드 설정

        return ResponseEntity.ok().body(responseDTO);
    }

    //납부서 수정
    @PostMapping("/admin/bill/{payNo}")
    //클라이언트로부터 받은 HTTP POST 요청의 body 부분을 PaymentCreateRequestDTO 타입의 객체로 변환하고, 이를 requestDTO라는 매개변수로 전달
    public ResponseEntity<?> modifyPayment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @PathVariable int payNo,
                                           @RequestBody PaymentRequestDTO requestDTO) {
        ResponseDTO<PaymentResponseDTO> responseDTO = new ResponseDTO<>();
        int userNo = customUserDetails.getUser().getId();
        validateService.validateTeacherAndAdmin(userService.findById(userNo));

        List<PaymentResponseDTO> returnList = new ArrayList<>();


        receiptService.deleteReceipt(payNo);

        requestDTO.setUserNo(userNo);
        Payment payment = paymentService.createPayment(payNo, requestDTO);

        User user = userService.findById(payment.getPayTo());

        PaymentResponseDTO paymentResponseDTO = PaymentResponseDTO.builder()
                .userName(user.getUserName())
                .couNo(user.getCourse().getCouNo())
                .totalPrice(payment.getTotalPrice())
                .build();

        returnList.add(paymentResponseDTO);

        responseDTO.setItems(returnList); // 응답 DTO 설정
        responseDTO.setStatusCode(HttpStatus.OK.value()); // 상태 코드 설정

        return ResponseEntity.ok().body(responseDTO);
    }

    //납부서 상세 조회(학생)
    @GetMapping("/student/bill")
    public ResponseEntity<?> getPayment(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDTO<PaymentResponseDTO> responseDTO = new ResponseDTO<>();

        //요청을 보낸 유저 데이터 확인하기
        int userNo = Integer.parseInt(customUserDetails.getUsername());
        LocalDateTime now = LocalDateTime.now();
        Month month = now.getMonth();

        //결제 정보 가져오기
        PaymentDTO paymentDTO = paymentService.getPayment(userNo, month.getValue()).EntityTODTO();

        //결제에 맞는 상품 리스트 가져와서 DTO리스트로 변환
        List<Receipt> receiptList = receiptService.getReceiptPayId(paymentDTO.getPayNo());

        //엔티티 리스트를 dto리스트로 전환
        List<ReceiptDTO> receiptDTOList = receiptList.stream()
                .map(Receipt::EntityTODTO)
                .collect(Collectors.toList());

        //리턴할 데이터 가공
        PaymentResponseDTO paymentResponseDTO = PaymentResponseDTO.builder()
                .payNo(paymentDTO.getPayNo())
                .userNo(paymentDTO.getUserNo())
                .payFrom(paymentDTO.getPayFrom())
                .totalPrice(paymentDTO.getTotalPrice())
                .userName(userService.findById(paymentDTO.getPayTo()).getUserName())
                .issDay(paymentService.getIssDate(paymentDTO.getIssDate().toString(), ISS_DATE_ARRAY[2]))
                .issMonth(paymentService.getIssDate(paymentDTO.getIssDate().toString(), ISS_DATE_ARRAY[1]))
                .issYear(paymentService.getIssDate(paymentDTO.getIssDate().toString(), ISS_DATE_ARRAY[0]))
                .productList(receiptDTOList)
                .isPay(paymentDTO.isPay())
                .build();

        responseDTO.setItem(paymentResponseDTO);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    //납부서 리스트 보기 (학생)
    @GetMapping("/student/bill-list")
    public ResponseEntity<?> getStudentPaymentList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDTO<PaymentResponseDTO> responseDTO = new ResponseDTO<>();

        //요청을 보낸 유저 데이터 확인하기
        int userNo = Integer.parseInt(customUserDetails.getUsername());
        User user = userService.findById(userNo);
        User parentUser = userService.findById(user.getUserJoinId());

        //유저 정보에 맞는 모든 결제 정보 리스트
        List<Payment> paymentList = paymentService.getPaymentList(userNo);

        List<PaymentResponseDTO> returnList = paymentList.stream()
                .map(payment -> {
                    List<ReceiptDTO> receiptDTOList = receiptService.getReceiptPayId(payment.getPayNo())
                            .stream()
                            .map(Receipt::EntityTODTO)
                            .collect(Collectors.toList());

                    return PaymentResponseDTO.builder()
                            .payNo(payment.getPayNo())
                            .userName(user.getUserName())
                            .couNo(user.getCourse().getCouNo())
                            .claName(user.getCourse().getClaName())
                            .issDay(paymentService.getIssDate(payment.getIssDate().toString(), ISS_DATE_ARRAY[2]))
                            .issMonth(paymentService.getIssDate(payment.getIssDate().toString(), ISS_DATE_ARRAY[1]))
                            .issYear(paymentService.getIssDate(payment.getIssDate().toString(), ISS_DATE_ARRAY[0]))
                            .totalPrice(payment.getTotalPrice())
                            .parentTel(parentUser.getUserTel())
                            .payMethod(payment.getPayMethod())
                            .isPay(payment.isPay())
                            .payFrom(payment.getPayFrom())
                            .productList(receiptDTOList)
                            .build();
                })
                .collect(Collectors.toList());

        responseDTO.setItems(returnList);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }


    //납부서 리스트
    @GetMapping("/admin/bill-list")
    public ResponseEntity<?> getPaymentList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDTO<PaymentResponseDTO> responseDTO = new ResponseDTO<>();
        int userNo = customUserDetails.getUser().getId();
        validateService.validateTeacherAndAdmin(userService.findById(userNo));

        //모든 결제 정보 리스트
        List<Payment> paymentList = paymentService.getPaymentList();

        List<PaymentResponseDTO> returnList = paymentList.stream()
                .map(payment -> {
                    User user = userService.findById(payment.getPayTo());
                    User parentUser = userService.findById(user.getUserJoinId());

                    List<Receipt> receiptList = receiptService.getReceiptPayId(payment.getPayNo());

                    List<ReceiptDTO> receiptDTOList = receiptList.stream()
                            .map(Receipt::EntityTODTO)
                            .collect(Collectors.toList());

                    return PaymentResponseDTO.builder()
                            .payNo(payment.getPayNo())
                            .userName(user.getUserName())
                            .couNo(user.getCourse().getCouNo())
                            .claName(user.getCourse().getClaName())
                            .issDay(paymentService.getIssDate(payment.getIssDate().toString(), ISS_DATE_ARRAY[2]))
                            .issMonth(paymentService.getIssDate(payment.getIssDate().toString(), ISS_DATE_ARRAY[1]))
                            .issYear(paymentService.getIssDate(payment.getIssDate().toString(), ISS_DATE_ARRAY[0]))
                            .totalPrice(payment.getTotalPrice())
                            .parentTel(parentUser.getUserTel())
                            .payMethod(payment.getPayMethod())
                            .isPay(payment.isPay())
                            .payFrom(payment.getPayFrom())
                            .productList(receiptDTOList)
                            .build();
                })
                .collect(Collectors.toList());

        responseDTO.setItems(returnList);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    //납부서 삭제
    @PostMapping("/admin/bill/delete")
    public ResponseEntity<?> deletePayment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @RequestBody String payNoList) {
        ResponseDTO<String> response = new ResponseDTO<>();
        int userNo = customUserDetails.getUser().getId();
        validateService.validateTeacherAndAdmin(userService.findById(userNo));

        List<Integer> payNoIntList = paymentService.jsonTOpayNoList(payNoList);

        paymentService.deletePaymentList(payNoIntList);

        response.setItem("납부서 삭제 성공");
        response.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(response);
    }

    //개별 납부서 보기
    @GetMapping("/admin/{payNo}")
    public ResponseEntity<?> getPayment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @PathVariable int payNo) {
        ResponseDTO<PaymentResponseDTO> response = new ResponseDTO<>();
        int userNo = customUserDetails.getUser().getId();
        validateService.validateTeacherAndAdmin(userService.findById(userNo));

        PaymentDTO paymentDTO = paymentService.getPayment(payNo).EntityTODTO();
        UserDTO userDTO = userService.findById(paymentDTO.getPayTo()).EntityToDTO();

        List<Receipt> receiptList = receiptService.getReceiptPayId(paymentDTO.getPayNo());

        List<ReceiptDTO> receiptDTOList = receiptList.stream()
                .map(Receipt::EntityTODTO)
                .collect(Collectors.toList());

        PaymentResponseDTO paymentResponseDTO = PaymentResponseDTO.builder()
                .payNo(payNo)
                .userNo(userDTO.getId())
                .claName(userDTO.getCourseDTO().getClaName())
                .userName(userDTO.getUserName())
                .issYear(paymentService.getIssDate(paymentDTO.getIssDate().toString(), ISS_DATE_ARRAY[0]))
                .issMonth(paymentService.getIssDate(paymentDTO.getIssDate().toString(), ISS_DATE_ARRAY[1]))
                .totalPrice(paymentDTO.getTotalPrice())
                .productList(receiptDTOList)
                .build();

        response.setItem(paymentResponseDTO);
        response.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(response);
    }
}

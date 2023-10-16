package com.bit.eduventure.payment.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private int payNo;  //영수증 PK
    private int userNo; //작성자 PK
    private String userName;   //학생 이름
    private int couNo; //반 번호
    private String claName; //반 이름
    private String issDay; //청구 일
    private String issMonth; //청구 월
    private String issYear; //청구 년
    private int totalPrice; //총 금액
    private String parentTel;   //부모 연락처
    private String payMethod;   //결제 방식
    private boolean isPay;   //결제 상태
    private String payFrom; //학원 명

    private List<ReceiptDTO> productList; //상품과 가격 리스트
}

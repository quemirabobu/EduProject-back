package com.bit.eduventure.payment.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private int couNo;  //반 PK (프론트)
    private int userNo; //작성자 PK
    private int payTo; //학생 PK  (프론트)
    private String issDate;     //년 월(프론트)
    private String productList; //상품과 가격 리스트 (프론트)
}

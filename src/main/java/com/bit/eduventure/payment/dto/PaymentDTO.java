
package com.bit.eduventure.payment.dto;

import com.bit.eduventure.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private int payNo;  //결제 PK
    private int userNo; //결제를 작성한 유저 PK
    private String payMethod;   //결제 방식
    private int totalPrice;     //결제 총 가격
    private String payFrom;     //결제를 보내주는 유저나 학원 명
    private int payTo;       //결제를 받는 유저 PK
    private LocalDateTime createDate; //납부서 작성한 날짜
    private LocalDateTime issDate;  //납부 해야할 날짜
    private LocalDateTime payDate;  //결제한 날짜
    private LocalDateTime cancelDate;   //취소한 날짜
    private boolean isPay;  //결제를 했는지 안 했는 지
    private boolean isCancel;   //취소를 했는 지 안 했는지
    private String impUid;  //실제로 결제를 할경우 아임포트 아이디 값

    public Payment DTOTOEntity() {
        Payment payment = Payment.builder()
                .payNo(this.payNo)
                .userNo(this.userNo)
                .payMethod(this.payMethod)
                .totalPrice(this.totalPrice)
                .payFrom(this.payFrom)
                .payTo(this.payTo)
                .createDate(this.createDate)
                .issDate(this.issDate)
                .payDate(this.payDate)
                .cancelDate(this.cancelDate)
                .isPay(this.isPay)
                .isCancel(this.isCancel)
                .impUid(this.impUid)
                .build();
        return payment;
    }
}


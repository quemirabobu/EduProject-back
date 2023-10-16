
package com.bit.eduventure.payment.dto;

import com.bit.eduventure.payment.entity.Payment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/* 환불할 때 받는 응답 */
@Data
public class RefundResponseDTO {

    private int payNo;  //결제번호
    private LocalDateTime cancelDate;   //취소일
    private boolean isCancel;   //취소여부

    public RefundResponseDTO(List<Payment> payments) {

        // 첫 번째 Payment의 번호를 가져와서 이 DTO 각각의 필드에 설정
        this.payNo = payments.get(0).getPayNo();
        this.cancelDate = payments.get(0).getCancelDate();
    }
}

package com.bit.eduventure.payment.dto;

import com.bit.eduventure.payment.entity.Receipt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ReceiptDTO {
    int id; //receipt PK
    int paymentId;  //결제 PK
    String productName;  //상품 이름
    int productPrice;   //상품 가격

    public Receipt DTOTOEntity() {
        Receipt receipt = Receipt.builder()
                .id(this.id)
                .paymentId(this.paymentId)
                .productName(this.productName)
                .productPrice(this.productPrice)
                .build();
        return receipt;
    }
}

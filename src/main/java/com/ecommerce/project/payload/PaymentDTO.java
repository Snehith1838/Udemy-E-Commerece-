package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private String paymentMethod;
    private String paymentGatewayId;
    private String PaymentGatewayStatus;
    private String PaymentGatewayResponseMessage;
    private String paymentGatewayName;
}

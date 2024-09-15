package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private Long addressId;
    private String paymentMethod;
    private String paymentGatewayId;
    private String PaymentGatewayStatus;
    private String PaymentGatewayResponseMessage;
    private String paymentGatewayName;
}

package com.ecommerce.project.service;

import com.ecommerce.project.payload.OrderDTO;
import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String paymentGatewayName,
                        String paymentGatewayId, String paymentGatewayStatus, String paymentGatewayResponseMessage);
}

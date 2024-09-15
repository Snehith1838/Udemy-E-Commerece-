package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(mappedBy = "payment", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Order order;

    @NotBlank
    @Size(min = 4, message = "Payment method must contain at least 4 characters")
    private String paymentMethod;

    private String paymentGatewayId;
    private String paymentGatewayStatus;
    private String paymentGatewayResponseMessage;

    private String paymentGatewayName;


    public Payment(String paymentMethod, String paymentGatewayId, String paymentGatewayStatus, String paymentGatewayResponseMessage, String paymentGatewayName) {

        this.paymentMethod = paymentMethod;
        this.paymentGatewayId = paymentGatewayId;
        this.paymentGatewayStatus = paymentGatewayStatus;
        this.paymentGatewayResponseMessage = paymentGatewayResponseMessage;
        this.paymentGatewayName = paymentGatewayName;

    }
}
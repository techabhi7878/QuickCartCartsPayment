package com.ecommerce.QuickCarts.Payment.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {
	
	private String orderId;
    private BigDecimal amount;
    private String paymentMethod;

}

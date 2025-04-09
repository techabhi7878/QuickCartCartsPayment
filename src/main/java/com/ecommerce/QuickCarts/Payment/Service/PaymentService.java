package com.ecommerce.QuickCarts.Payment.Service;

import com.ecommerce.QuickCarts.Payment.DTO.PaymentRequestDTO;
import com.ecommerce.QuickCarts.Payment.DTO.PaymentResponseDTO;

public interface PaymentService {
	
	 PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequestDTO);

}

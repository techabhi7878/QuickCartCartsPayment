package com.ecommerce.QuickCarts.Payment.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.QuickCarts.Payment.DTO.PaymentRequestDTO;
import com.ecommerce.QuickCarts.Payment.DTO.PaymentResponseDTO;
import com.ecommerce.QuickCarts.Payment.Service.PaymentService;



@RestController
@RequestMapping("/api/payments")

public class PaymentController {
	
	  @Autowired
	    private PaymentService paymentService;

	    @PostMapping("/process")
	    public ResponseEntity<PaymentResponseDTO> processPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
	        return ResponseEntity.ok(paymentService.processPayment(paymentRequestDTO));
	    }

}

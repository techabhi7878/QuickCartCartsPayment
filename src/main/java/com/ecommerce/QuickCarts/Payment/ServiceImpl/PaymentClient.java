package com.ecommerce.QuickCarts.Payment.ServiceImpl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ecommerce.QuickCarts.Payment.Entity.Payment;

@FeignClient(name = "Payment-service" ,path = "/payment")
public interface PaymentClient {

	@GetMapping("/{id}")
	ResponseEntity<Payment>getPaymentById(@PathVariable("id")  Long id);
}


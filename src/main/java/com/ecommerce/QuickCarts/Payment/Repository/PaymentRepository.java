package com.ecommerce.QuickCarts.Payment.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.QuickCarts.Payment.Entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
	
	Optional<Payment> findByOrderId(String orderId);

}

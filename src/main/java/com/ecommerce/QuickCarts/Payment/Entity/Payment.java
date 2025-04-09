package com.ecommerce.QuickCarts.Payment.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor

@AllArgsConstructor
@Builder
@Table(name = "Payment_Table")
public class Payment {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String orderId;

	    @Column(nullable = false)
	    private BigDecimal amount;

	    @Column(nullable = false)
	    @Enumerated(EnumType.STRING)
	    private PaymentMethod paymentMethod;

	    @Column(nullable = true)
	    private String transactionId;

	    @Column(nullable = false)
	    private String status;

	    @Column(nullable = false)
	    private LocalDateTime paymentDate;
}

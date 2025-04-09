package com.ecommerce.QuickCarts.Payment.ServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecommerce.QuickCarts.Payment.DTO.PaymentRequestDTO;
import com.ecommerce.QuickCarts.Payment.DTO.PaymentResponseDTO;
import com.ecommerce.QuickCarts.Payment.Entity.Payment;
import com.ecommerce.QuickCarts.Payment.Entity.PaymentMethod;
import com.ecommerce.QuickCarts.Payment.Exception.PaymentProcessingException;
import com.ecommerce.QuickCarts.Payment.Repository.PaymentRepository;
import com.ecommerce.QuickCarts.Payment.Service.PaymentService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;



@Service
public class PaymentServiceImpl implements PaymentService{
	
	 @Autowired
	    private PaymentRepository paymentRepository;

	    @Value("${stripe.secret.key}")
	    private String stripeSecretKey;

	    @Value("${paypal.client.id}")
	    private String paypalClientId;

	    @Value("${paypal.client.secret}")
	    private String paypalClientSecret;

	    @Value("${razorpay.api.key}")
	    private String razorpayApiKey;

	    @Value("${razorpay.api.secret}")
	    private String razorpayApiSecret;

	    @Override
	    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
	        String transactionId = "N/A";
	        String status = "FAILED";

	        try {
	            switch (request.getPaymentMethod().toUpperCase()) {
	                case "STRIPE":
	                    transactionId = processStripePayment(request);
	                    status = "SUCCESS";
	                    break;
	                case "PAYPAL":
	                    transactionId = processPaypalPayment(request);
	                    status = "SUCCESS";
	                    break;
	                case "RAZORPAY":
	                    transactionId = processRazorpayPayment(request);
	                    status = "SUCCESS";
	                    break;
	                case "COD": // Added Cash on Delivery logic
	                    transactionId = UUID.randomUUID().toString();
	                    status = "PENDING"; // COD is pending until delivery
	                    break;
	                default:
	                    throw new PaymentProcessingException("Invalid payment method: " + request.getPaymentMethod());
	            }
	        } catch (StripeException e) {
	            throw new PaymentProcessingException("Stripe Payment Failed: " + e.getMessage());
	        } catch (PayPalRESTException e) {
	            throw new PaymentProcessingException("PayPal Payment Failed: " + e.getMessage());
	        } catch (RazorpayException e) {
	            throw new PaymentProcessingException("Razorpay Payment Failed: " + e.getMessage());
	        } catch (Exception e) {
	            throw new PaymentProcessingException("Payment Processing Failed: " + e.getMessage());
	        }

	        // Save payment details in DB
	        Payment payment = Payment.builder()
	                .orderId(request.getOrderId())
	                .amount(request.getAmount())
	                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()))
	                .status(status)
	                .transactionId(transactionId)
	                .paymentDate(LocalDateTime.now())
	                .build();

	        paymentRepository.save(payment);

	        return PaymentResponseDTO.builder()
	                .status(status)
	                .transactionId(transactionId)
	                .build();
	    }

	    private String processStripePayment(PaymentRequestDTO request) throws StripeException {
	        Stripe.apiKey = stripeSecretKey;

	        ChargeCreateParams params = ChargeCreateParams.builder()
	                .setAmount(request.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
	                .setCurrency("usd")
	                .setDescription("Order Payment: " + request.getOrderId())
	                .setSource("tok_visa") 
	                .build();

	        Charge charge = Charge.create(params);
	        return charge.getId();
	    }

	    private String processPaypalPayment(PaymentRequestDTO request) throws PayPalRESTException {
	        APIContext apiContext = new APIContext(paypalClientId, paypalClientSecret, "sandbox");

	        Amount amount = new Amount();
	        amount.setCurrency("USD");
	        amount.setTotal(request.getAmount().toString());

	        Transaction transaction = new Transaction();
	        transaction.setAmount(amount);
	        transaction.setDescription("Order Payment: " + request.getOrderId());

	        List<Transaction> transactions = new ArrayList<>();
	        transactions.add(transaction);

	        Payer payer = new Payer();
	        payer.setPaymentMethod("paypal");

	        com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
	        payment.setIntent("sale");
	        payment.setPayer(payer);
	        payment.setTransactions(transactions);

	        com.paypal.api.payments.Payment createdPayment = payment.create(apiContext);
	        return createdPayment.getId();
	    }

	    private String processRazorpayPayment(PaymentRequestDTO request) throws RazorpayException {
	        RazorpayClient razorpayClient = new RazorpayClient(razorpayApiKey, razorpayApiSecret);

	        Map<String, Object> options = new HashMap<>();
	        options.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue());
	        options.put("currency", "INR");
	        options.put("receipt", "txn_" + UUID.randomUUID());

	        Order order = razorpayClient.orders.create(new JSONObject(options));
	        return order.get("id").toString();
	    }
}

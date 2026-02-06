package za.co.rubhub.controller;

import za.co.rubhub.model.Payment;
import za.co.rubhub.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // GET - Get all payments
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        try {
            List<Payment> payments = paymentService.findAll();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable String id) {
        try {
            Optional<Payment> payment = paymentService.findById(id);
            return payment.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get payment by transaction ID
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Payment> getPaymentByTransactionId(@PathVariable String transactionId) {
        try {
            Optional<Payment> payment = paymentService.findByTransactionId(transactionId);
            return payment.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get payments by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUserId(@PathVariable String userId) {
        try {
            List<Payment> payments = paymentService.findByUserId(userId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get payments by booking ID
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Payment>> getPaymentsByBookingId(@PathVariable String bookingId) {
        try {
            List<Payment> payments = paymentService.findByBookingId(bookingId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get payments by therapist ID
    @GetMapping("/therapist/{therapistId}")
    public ResponseEntity<List<Payment>> getPaymentsByTherapistId(@PathVariable String therapistId) {
        try {
            List<Payment> payments = paymentService.findByTherapistId(therapistId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get payments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable Payment.PaymentStatus status) {
        try {
            List<Payment> payments = paymentService.findByStatus(status);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Create new payment
    @PostMapping
    public ResponseEntity<?> createPayment(@javax.validation.Valid @RequestBody Payment payment, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }

            Payment savedPayment = paymentService.save(payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPayment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating payment: " + e.getMessage());
        }
    }

    // PUT - Update payment
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable String id, @javax.validation.Valid @RequestBody Payment paymentDetails, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }

            Optional<Payment> existingPayment = paymentService.findById(id);
            if (existingPayment == null) {
                return ResponseEntity.notFound().build();
            }

            Payment payment = existingPayment.get();
            
            // Update fields
            payment.setAmount(paymentDetails.getAmount());
            payment.setTherapistEarnings(paymentDetails.getTherapistEarnings());
            payment.setPlatformFee(paymentDetails.getPlatformFee());
            payment.setCurrency(paymentDetails.getCurrency());
            payment.setStatus(paymentDetails.getStatus());
            payment.setPaymentMethod(paymentDetails.getPaymentMethod());
            payment.setProvider(paymentDetails.getProvider());
            payment.setDescription(paymentDetails.getDescription());
            payment.setNotes(paymentDetails.getNotes());
            payment.setBillingInfo(paymentDetails.getBillingInfo());
            payment.setCardDetails(paymentDetails.getCardDetails());

            Payment updatedPayment = paymentService.save(payment);
            return ResponseEntity.ok(updatedPayment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating payment: " + e.getMessage());
        }
    }

    // PATCH - Process payment success
    @PatchMapping("/{transactionId}/success")
    public ResponseEntity<?> processPaymentSuccess(@PathVariable String transactionId, 
                                                  @RequestParam String providerTransactionId) {
        try {
            Payment payment = paymentService.processPaymentSuccess(transactionId, providerTransactionId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing payment success: " + e.getMessage());
        }
    }

    // PATCH - Process payment failure
    @PatchMapping("/{transactionId}/failure")
    public ResponseEntity<?> processPaymentFailure(@PathVariable String transactionId, 
                                                  @RequestParam String errorMessage) {
        try {
            Payment payment = paymentService.processPaymentFailure(transactionId, errorMessage);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing payment failure: " + e.getMessage());
        }
    }

    // PATCH - Process payout
    @PatchMapping("/{id}/payout")
    public ResponseEntity<?> processPayout(@PathVariable String id) {
        try {
            Optional<Payment> paymentOpt = paymentService.findById(id);
            if (paymentOpt == null) {
                return ResponseEntity.notFound().build();
            }

            Payment payment = paymentOpt.get();
            if (!payment.canProcessPayout()) {
                return ResponseEntity.badRequest()
                    .body("Payment cannot be processed for payout. Status: " + payment.getStatus());
            }

            payment.processPayout();
            Payment updatedPayment = paymentService.save(payment);
            return ResponseEntity.ok(updatedPayment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing payout: " + e.getMessage());
        }
    }

    // GET - Get payments ready for payout
    @GetMapping("/payout/ready")
    public ResponseEntity<List<Payment>> getPaymentsReadyForPayout() {
        try {
            List<Payment> payments = paymentService.findPaymentsForPayout();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE - Delete payment
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable String id) {
        try {
            if (!paymentService.findById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok().body("Payment deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting payment: " + e.getMessage());
        }
    }
}
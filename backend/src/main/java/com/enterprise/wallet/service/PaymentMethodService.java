package com.enterprise.wallet.service;

import com.enterprise.wallet.entity.*;
import com.enterprise.wallet.exception.ResourceNotFoundException;
import com.enterprise.wallet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    public PaymentMethod addPaymentMethod(Long userId, PaymentMethod paymentMethod) {
        log.info("Adding payment method for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        paymentMethod.setUser(user);

        // If this is the first payment method, set it as default
        if (paymentMethodRepository.countByUserId(userId) == 0) {
            paymentMethod.setIsDefault(true);
        }

        PaymentMethod saved = paymentMethodRepository.save(paymentMethod);

        log.info("Payment method added for user ID: {}", userId);
        return saved;
    }

    public List<PaymentMethod> getUserPaymentMethods(Long userId) {
        return paymentMethodRepository.findByUserId(userId);
    }

    public PaymentMethod setDefaultPaymentMethod(Long userId, Long paymentMethodId) {
        log.info("Setting default payment method for user ID: {}", userId);

        // Verify the payment method belongs to the user
        PaymentMethod paymentMethod = paymentMethodRepository.findByIdAndUserId(paymentMethodId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));

        // Unset current default
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        paymentMethodRepository.findByUserAndIsDefaultTrue(user).ifPresent(current -> {
            current.setIsDefault(false);
            paymentMethodRepository.save(current);
        });

        // Set new default
        paymentMethod.setIsDefault(true);
        PaymentMethod updated = paymentMethodRepository.save(paymentMethod);

        log.info("Default payment method updated for user ID: {}", userId);
        return updated;
    }

    public void deletePaymentMethod(Long userId, Long paymentMethodId) {
        log.info("Deleting payment method ID: {} for user ID: {}", paymentMethodId, userId);

        PaymentMethod paymentMethod = paymentMethodRepository.findByIdAndUserId(paymentMethodId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));

        paymentMethodRepository.delete(paymentMethod);

        log.info("Payment method deleted: {}", paymentMethodId);
    }
}

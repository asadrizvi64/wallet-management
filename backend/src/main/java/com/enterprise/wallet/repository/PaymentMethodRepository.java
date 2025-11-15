package com.enterprise.wallet.repository;

import com.enterprise.wallet.entity.PaymentMethod;
import com.enterprise.wallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Payment Method Repository - Data access layer for PaymentMethod entity
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    List<PaymentMethod> findByUserId(Long userId);

    List<PaymentMethod> findByUserAndStatus(User user, PaymentMethod.PaymentMethodStatus status);

    Optional<PaymentMethod> findByUserAndIsDefaultTrue(User user);

    Optional<PaymentMethod> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndIsDefaultTrue(Long userId);

    long countByUserId(Long userId);
}

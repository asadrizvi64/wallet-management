package com.enterprise.wallet.service;

import com.enterprise.wallet.dto.OtherDTOs.*;
import com.enterprise.wallet.dto.TransactionDTOs.*;
import com.enterprise.wallet.entity.*;
import com.enterprise.wallet.exception.InvalidOperationException;
import com.enterprise.wallet.exception.ResourceNotFoundException;
import com.enterprise.wallet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentLinkService {

    private final PaymentLinkRepository paymentLinkRepository;
    private final WalletRepository walletRepository;
    private final TransactionService transactionService;

    public PaymentLinkResponse createPaymentLink(CreatePaymentLinkRequest request) {
        log.info("Creating payment link for wallet ID: {}", request.getWalletId());

        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        String linkCode = generateLinkCode();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(request.getExpiryHours());

        PaymentLink paymentLink = new PaymentLink();
        paymentLink.setWallet(wallet);
        paymentLink.setLinkCode(linkCode);
        paymentLink.setAmount(request.getAmount());
        paymentLink.setDescription(request.getDescription());
        paymentLink.setExpiryDate(expiryDate);
        paymentLink.setIsUsed(false);
        paymentLink.setPaymentStatus(PaymentLink.PaymentStatus.PENDING);

        PaymentLink saved = paymentLinkRepository.save(paymentLink);

        log.info("Payment link created: {}", linkCode);
        return mapToResponse(saved);
    }

    public PaymentLinkResponse getPaymentLink(String linkCode) {
        PaymentLink paymentLink = paymentLinkRepository.findByLinkCode(linkCode)
                .orElseThrow(() -> new ResourceNotFoundException("Payment link not found"));
        return mapToResponse(paymentLink);
    }

    public List<PaymentLinkResponse> getWalletPaymentLinks(Long walletId) {
        return paymentLinkRepository.findByWalletIdOrderByCreatedAtDesc(walletId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PaymentLinkResponse processPaymentLink(VerifyPaymentRequest request) {
        log.info("Processing payment link: {}", request.getLinkCode());

        PaymentLink paymentLink = paymentLinkRepository.findByLinkCode(request.getLinkCode())
                .orElseThrow(() -> new ResourceNotFoundException("Payment link not found"));

        // Validations
        if (paymentLink.getIsUsed()) {
            throw new InvalidOperationException("Payment link already used");
        }

        if (paymentLink.getExpiryDate().isBefore(LocalDateTime.now())) {
            paymentLink.setPaymentStatus(PaymentLink.PaymentStatus.EXPIRED);
            paymentLinkRepository.save(paymentLink);
            throw new InvalidOperationException("Payment link has expired");
        }

        Wallet payerWallet = walletRepository.findById(request.getPayerWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Payer wallet not found"));

        // Process the payment via transfer
        TransferMoneyRequest transferRequest = new TransferMoneyRequest();
        transferRequest.setFromWalletId(payerWallet.getId());
        transferRequest.setToWalletNumber(paymentLink.getWallet().getWalletNumber());
        transferRequest.setAmount(paymentLink.getAmount());
        transferRequest.setDescription("Payment via link: " + request.getLinkCode());

        transactionService.transferMoney(transferRequest);

        // Update payment link
        paymentLink.setIsUsed(true);
        paymentLink.setPaidByWallet(payerWallet);
        paymentLink.setUsedAt(LocalDateTime.now());
        paymentLink.setPaymentStatus(PaymentLink.PaymentStatus.COMPLETED);

        PaymentLink updated = paymentLinkRepository.save(paymentLink);

        log.info("Payment link processed successfully: {}", request.getLinkCode());
        return mapToResponse(updated);
    }

    private String generateLinkCode() {
        String linkCode;
        do {
            linkCode = "PL-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        } while (paymentLinkRepository.existsByLinkCode(linkCode));
        return linkCode;
    }

    private PaymentLinkResponse mapToResponse(PaymentLink paymentLink) {
        String paymentUrl = "/api/payment-links/" + paymentLink.getLinkCode() + "/pay";

        return new PaymentLinkResponse(
                paymentLink.getId(),
                paymentLink.getLinkCode(),
                paymentUrl,
                paymentLink.getAmount(),
                paymentLink.getDescription(),
                paymentLink.getPaymentStatus(),
                paymentLink.getIsUsed(),
                paymentLink.getExpiryDate(),
                paymentLink.getCreatedAt()
        );
    }
}

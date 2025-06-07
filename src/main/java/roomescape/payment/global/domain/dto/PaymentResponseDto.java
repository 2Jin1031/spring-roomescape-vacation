package roomescape.payment.global.domain.dto;

import roomescape.payment.global.domain.TossPayment;

public record PaymentResponseDto(String orderId, int amount, String paymentKey) {

    public static PaymentResponseDto from(TossPayment tossPayment) {
        return new PaymentResponseDto(tossPayment.getOrderId(), tossPayment.getAmount(), tossPayment.getPaymentKey());
    }
}

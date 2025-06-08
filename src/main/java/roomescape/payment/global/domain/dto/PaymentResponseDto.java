package roomescape.payment.global.domain.dto;

import roomescape.payment.global.domain.PgPayment;

public record PaymentResponseDto(String orderId, int amount, String paymentKey) {

    public static PaymentResponseDto from(PgPayment pgPayment) {
        return new PaymentResponseDto(pgPayment.getOrderId(), pgPayment.getAmount(), pgPayment.getPaymentKey());
    }
}

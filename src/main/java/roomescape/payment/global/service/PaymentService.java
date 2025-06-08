package roomescape.payment.global.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.payment.global.PaymentRestClient;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.domain.dto.PgPaymentDataDto;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRestClient paymentRestClient;

    public PaymentService(PaymentRestClient paymentRestClient) {
        this.paymentRestClient = paymentRestClient;
    }

    public PgPaymentDataDto approve(PaymentRequestDto request) {
        log.info("결제 승인 요청 - PaymentKey: {}, OrderId: {}, 금액: {}",
                request.paymentKey(), request.orderId(), request.amount());
        PgPaymentDataDto response = paymentRestClient.confirmPayment(request);
        log.info("결제 승인 완료 - 응답: {}", response);
        return response;
    }
}

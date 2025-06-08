package roomescape.payment.global.service;

import org.springframework.stereotype.Service;
import roomescape.payment.global.PaymentRestClient;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.domain.dto.PaymentResponseDto;

@Service
public class PaymentService {

    private final PaymentRestClient paymentRestClient;

    public PaymentService(PaymentRestClient paymentRestClient) {
        this.paymentRestClient = paymentRestClient;
    }

    public PaymentResponseDto approve(PaymentRequestDto request) {
        return paymentRestClient.confirmPayment(request);
    }
}

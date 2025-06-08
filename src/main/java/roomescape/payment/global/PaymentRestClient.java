package roomescape.payment.global;

import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.domain.dto.PaymentResponseDto;

public interface PaymentRestClient {

    PaymentResponseDto confirmPayment(PaymentRequestDto requestDto);
}

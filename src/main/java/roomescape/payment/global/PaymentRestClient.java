package roomescape.payment.global;

import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.domain.dto.PgPaymentDataDto;

public interface PaymentRestClient {

    PgPaymentDataDto confirmPayment(PaymentRequestDto requestDto);
}

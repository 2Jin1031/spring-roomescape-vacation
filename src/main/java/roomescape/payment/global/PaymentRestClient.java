package roomescape.payment.global;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.domain.dto.PgPaymentDataDto;

public interface PaymentRestClient {

    PgPaymentDataDto confirmPayment(PaymentRequestDto requestDto);

    boolean isError(HttpStatusCode httpStatusCode);

    ErrorHandler getErrorHandler();
}

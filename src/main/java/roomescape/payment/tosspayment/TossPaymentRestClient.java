package roomescape.payment.tosspayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.payment.global.PaymentRestClient;
import roomescape.payment.global.domain.PgPayment;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.domain.dto.PaymentResponseDto;
import roomescape.payment.global.exception.InvalidPaymentException;
import roomescape.payment.tosspayment.domain.TossErrorResponse;

@Component
public class TossPaymentRestClient implements PaymentRestClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentRestClient(RestClient tosspaymentRestClient, ObjectMapper objectMapper) {
        this.restClient = tosspaymentRestClient;
        this.objectMapper = objectMapper;
    }

    public PaymentResponseDto confirmPayment(PaymentRequestDto requestDto) {
        PgPayment pgPayment = restClient.post()
                .uri("/v1/payments/confirm")
                .body(requestDto)
                .retrieve()
                .onStatus(
                        statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(), getErrorHandler()
                )
                .toEntity(PgPayment.class)
                .getBody();

        return PaymentResponseDto.from(pgPayment);
    }

    private ErrorHandler getErrorHandler() {
        return (req, res) -> {
            TossErrorResponse error = objectMapper.readValue(res.getBody(), TossErrorResponse.class);
            HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
            throw new InvalidPaymentException(error.message(), status);
        };
    }
}

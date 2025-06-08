package roomescape.payment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.payment.global.domain.PgPayment;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.domain.dto.PgPaymentDataDto;
import roomescape.payment.global.exception.InvalidPaymentException;
import roomescape.payment.global.service.PaymentService;
import roomescape.payment.tosspayment.TossPaymentRestClient;

public class PaymentServiceTest {

    private final TossPaymentRestClient mockRestClient = mock(TossPaymentRestClient.class);

    private PaymentRequestDto paymentRequestDto;

    @BeforeEach
    void setUp() {
        paymentRequestDto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");
    }

    @Test
    void 결제_승인_요청시_200_OK() {
        PgPayment pgPayment = new PgPayment("paymentKey", "orderId", 1000, "NORMAL");
        PgPaymentDataDto pgPaymentDataDto = new PgPaymentDataDto("paymentKey", "orderId", 1000, "NORMAL");
        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenReturn(pgPaymentDataDto);

        PaymentService paymentService = new PaymentService(mockRestClient);

        Assertions.assertThatCode(
                () -> paymentService.approve(paymentRequestDto)
        ).doesNotThrowAnyException();
    }

    @Test
    void 결제_승인_요청시_400에러가_발생하면_InvalidPaymentException_발생() {
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        when(mockRestClient.confirmPayment(any(PaymentRequestDto.class)))
                .thenThrow(new InvalidPaymentException(HttpStatus.BAD_REQUEST));

        PaymentService paymentService = new PaymentService(mockRestClient);

        Assertions.assertThatThrownBy(
                () -> paymentService.approve(dto)
        ).isInstanceOf(InvalidPaymentException.class);
    }
}

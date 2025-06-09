package roomescape.payment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientException;
import roomescape.payment.global.domain.dto.PgPaymentRequestDto;
import roomescape.payment.global.service.PgPaymentService;

@TestPropertySource(properties = {
        "payment.pg.toss-payment.secret-key=tosstasstosstasstoss"
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PgPaymentServiceInvalidSecretKeyTest {

    @Autowired
    private PgPaymentService pgPaymentService;

    @DisplayName("secretKey가 유효하지 않을 때 예외 발생 : RestClientException")
    @Test
    void approve_throwsException_byInvalidSecretKey() {
        // given
        PgPaymentRequestDto dto = new PgPaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        // when & then
        Assertions.assertThatThrownBy(
                () -> pgPaymentService.approve(dto)
        ).isInstanceOf(RestClientException.class);
    }
}

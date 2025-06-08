package roomescape.payment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.ResourceAccessException;
import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.service.PaymentService;

@TestPropertySource(properties = {
        "payment.pg.toss-payment.base-url=https://baseurlisinvalid"
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TossPaymentServiceInvalidBaseUrlTest {

    @Autowired
    private PaymentService paymentService;

    @DisplayName("잘못된 baseUrl로 요청했을 때 예외 발생")
    @Test
    void approve_throwsException_byInvalidBaseUrl() {
        // given
        PaymentRequestDto dto = new PaymentRequestDto("paymentKey", "orderId", 1000, "NORMAL");

        // when & then
        Assertions.assertThatThrownBy(
                () -> paymentService.approve(dto)
        ).isInstanceOf(ResourceAccessException.class);
    }
}

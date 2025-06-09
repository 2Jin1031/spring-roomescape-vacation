package roomescape.payment.global.exception;

import org.springframework.http.HttpStatus;

public class InvalidPgPaymentException extends RuntimeException {

    private final HttpStatus status;

    private static final String DEFAULT_MESSAGE = "유효하지 않는 결제 요청입니다.";

    public InvalidPgPaymentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public InvalidPgPaymentException(HttpStatus status) {
        this(DEFAULT_MESSAGE, status);
    }

    public HttpStatus getStatus() {
        return status;
    }
}

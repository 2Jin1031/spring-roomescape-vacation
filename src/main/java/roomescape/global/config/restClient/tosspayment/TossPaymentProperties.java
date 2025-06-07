package roomescape.global.config.restClient.tosspayment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.pg.toss-payment")
public class TossPaymentProperties {

    private String baseUrl;
    private String secretKey;
    private int connectTimeout;
    private int readTimeout;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }
}

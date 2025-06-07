package roomescape.global.config.restClient.tosspayment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.pg.toss-payment")
public class TossPaymentProperties {

    private final String baseUrl;
    private final String secretKey;
    private final int connectTimeout;
    private final int readTimeout;

    public TossPaymentProperties(String baseUrl, String secretKey, int connectTimeout, int readTimeout) {
        this.baseUrl = baseUrl;
        this.secretKey = secretKey;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

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

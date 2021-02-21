package software.reloadly.sdk.core.exception;

import java.util.List;

/**
 * Represents a server error when a rate limit has been exceeded.
 * <p>
 * Getters for {@code limit, remaining} and {@code reset} corresponds to {@code X-RateLimit-Limit, X-RateLimit-Remaining} and {@code X-RateLimit-Reset} HTTP headers.
 * If the value of any headers is missing, then a default value -1 will assigned.
 * <p>
 * To learn more about rate limits, visit <a href="https://api.reloadly.com/docs/policies/rate-limits">https://api.reloadly.com/docs/policies/rate-limits</a>
 */
public class RateLimitException extends APIException {

    private long limit;

    private long remaining;

    private long expectedResetTimestamp;

    private static final int STATUS_CODE_TOO_MANY_REQUEST = 429;

    public RateLimitException(String message, String path, int httpStatusCode, String errorCode) {
        super(message, path, httpStatusCode, errorCode);
    }

    public RateLimitException(String message, String path, int httpStatusCode, String errorCode, List<Object> details) {
        super(message, path, httpStatusCode, errorCode, details);
    }

    /**
     * Getter for the maximum number of requests available in the current time frame.
     *
     * @return The maximum number of requests or -1 if missing.
     */
    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    /**
     * Getter for the number of remaining requests in the current time frame.
     *
     * @return Number of remaining requests or -1 if missing.
     */
    public long getRemaining() {
        return remaining;
    }

    public void setRemaining(long remaining) {
        this.remaining = remaining;
    }

    /**
     * Getter for the UNIX timestamp of the expected time when the rate limit will reset.
     *
     * @return The UNIX timestamp or -1 if missing.
     */
    public long getExpectedResetTimestamp() {
        return expectedResetTimestamp;
    }

    public void setExpectedResetTimestamp(long expectedResetTimestamp) {
        this.expectedResetTimestamp = expectedResetTimestamp;
    }
}

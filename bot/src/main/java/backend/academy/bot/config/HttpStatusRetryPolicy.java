package backend.academy.bot.config;

import java.util.Set;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.web.client.HttpStatusCodeException;

public class HttpStatusRetryPolicy implements RetryPolicy {
    private final int maxAttempts;
    private final Set<Integer> retryableStatuses;

    public HttpStatusRetryPolicy(int maxAttempts, Set<Integer> retryableStatuses) {
        this.maxAttempts = maxAttempts;
        this.retryableStatuses = retryableStatuses;
    }

    @Override
    public boolean canRetry(RetryContext context) {
        int attempt = context.getRetryCount();

        if (attempt == 0) {
            return true;
        }

        if (attempt >= maxAttempts) {
            return false;
        }

        Throwable cause = context.getLastThrowable();
        while (cause != null) {
            if (cause instanceof HttpStatusCodeException ex
                    && retryableStatuses.contains(ex.getStatusCode().value())) {
                return true;
            }
            cause = cause.getCause();
        }

        return false;
    }

    @Override
    public RetryContext open(RetryContext parent) {
        return new SimpleRetryContext(parent);
    }

    @Override
    public void close(RetryContext context) {}

    @Override
    public void registerThrowable(RetryContext context, Throwable throwable) {
        SimpleRetryContext simpleContext = (SimpleRetryContext) context;
        simpleContext.registerThrowable(throwable);
    }

    private static class SimpleRetryContext extends RetryContextSupport {
        public SimpleRetryContext(RetryContext parent) {
            super(parent);
        }
    }
}

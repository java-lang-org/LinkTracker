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
        if (context.getRetryCount() >= maxAttempts) {
            return false;
        }
        Throwable t = context.getLastThrowable();
        while (t != null) {
            if (t instanceof HttpStatusCodeException ex) {
                if (retryableStatuses.contains(ex.getStatusCode().value())) {
                    return true;
                }
            }
            t = t.getCause();
        }
        return false;
    }

    @Override
    public RetryContext open(RetryContext parent) {
        return new org.springframework.retry.context.RetryContextSupport(parent);
    }

    @Override
    public void close(RetryContext context) {}

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

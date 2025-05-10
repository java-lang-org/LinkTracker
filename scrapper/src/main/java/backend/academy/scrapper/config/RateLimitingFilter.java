package backend.academy.scrapper.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.http.HttpStatus;

public class RateLimitingFilter implements Filter {
    private final int maxRequestsPerMinute;
    private final Map<String, RequestInfo> requests;

    public RateLimitingFilter(int maxRequestsPerMinute) {
        this.maxRequestsPerMinute = maxRequestsPerMinute;
        this.requests = new ConcurrentHashMap<>();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String clientIp = httpServletRequest.getRemoteAddr();
        RequestInfo info = requests.computeIfAbsent(clientIp, ip -> new RequestInfo());

        long now = System.currentTimeMillis();
        if (now - info.timestamp > 60_000) {
            info.count.set(0);
            info.timestamp = now;
        }

        if (info.count.incrementAndGet() > maxRequestsPerMinute) {
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }

        chain.doFilter(request, response);
    }

    private static class RequestInfo {
        AtomicInteger count;
        long timestamp;

        RequestInfo() {
            this.count = new AtomicInteger(0);
            this.timestamp = System.currentTimeMillis();
        }
    }
}

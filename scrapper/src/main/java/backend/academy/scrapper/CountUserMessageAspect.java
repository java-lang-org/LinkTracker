package backend.academy.scrapper;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CountUserMessageAspect {
    private final UserMessageMetrics userMessageMetrics;

    @Pointcut("@annotation(CountUserMessage)")
    public void countUserMessageMethods() {}

    @Before("countUserMessageMethods()")
    public void before() {
        userMessageMetrics.increment();
    }
}

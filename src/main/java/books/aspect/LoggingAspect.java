package books.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* books.service.*.*(..))")
    public void logMethodCall(JoinPoint joinPoint) {
        System.out.println("Method is about to be called: " + joinPoint.getSignature().getName());
    }

    @Before("execution(* books.service.*.*(*))")
    public void logMethodCallArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            System.out.println("Argument: " + arg);
        }
    }

    @AfterReturning(pointcut = "execution(* books.service.*.*(..)) " +
            "&& !execution(void books.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();

        System.out.println("Method " + methodName + " has returned:");
        System.out.println("Return value: " + result);
    }
}

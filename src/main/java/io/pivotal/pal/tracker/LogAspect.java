package io.pivotal.pal.tracker;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

    @Around("@annotation(SpeedLogger)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object[] args = joinPoint.getArgs();

        for(int i=0; i<args.length; i++)
            System.out.println(joinPoint.getSignature().toString()+" called with Called with "+args[i]);

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        System.out.println(joinPoint.getSignature() + " executed in " + executionTime + "ms");
        return proceed;
    }

}

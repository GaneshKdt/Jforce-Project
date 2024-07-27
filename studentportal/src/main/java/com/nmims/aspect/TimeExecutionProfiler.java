package com.nmims.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@Aspect
public class TimeExecutionProfiler {

    private static final Logger logger = LoggerFactory.getLogger("slowMethods");

    @Around("com.nmims.aspect.SystemArchitecture.businessController()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
      //  logger.info("ServicesProfiler.profile(): Going to call the method: {}", pjp.getSignature().getName());
        Object output = pjp.proceed();
        //logger.info("ServicesProfiler.profile(): Method execution completed.");

        long elapsedTime = System.currentTimeMillis() - start;
        if(elapsedTime > 500) {
            logger.info("SLOW ALERT Method execution time : " + elapsedTime + " milliseconds. " + " Method: " + pjp.getSignature().getName());
            logger.info("JVM memory in use = {}", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

        }

        return output;
    }

//    @After("com.nmims.aspect.SystemArchitecture.businessController()")
//    public void profileMemory() {
//       // logger.info("JVM memory in use = {}", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
//       // System.out.println("JVM memory in use = {}" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
//
//    }
}
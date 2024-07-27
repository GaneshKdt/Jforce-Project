package com.nmims.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Aspect
@Component
public class SystemArchitecture {

    @Pointcut("execution(* com.nmims.*..*.*(..))")
    
    public void businessController() {
    }
    
    @Pointcut("execution(* com.nmims.daos..*.*(..))")
    public void daoController() {
    }
}  
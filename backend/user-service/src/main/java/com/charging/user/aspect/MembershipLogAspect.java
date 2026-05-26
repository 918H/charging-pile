package com.charging.user.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Aspect
@Component
public class MembershipLogAspect {

    @Pointcut("execution(* com.charging.user.service.impl.MembershipServiceImpl.calculateDiscount(..))")
    public void discountCalculation() {}

    @AfterReturning(pointcut = "discountCalculation()", returning = "result", argNames = "joinPoint,result")
    public void logDiscount(JoinPoint joinPoint, BigDecimal result) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        BigDecimal amount = (BigDecimal) args[1];
        
        if (result != null && result.compareTo(BigDecimal.ZERO) > 0) {
            log.info("用户 {} 消费 {} 元，折扣 {} 元", userId, amount, result);
        }
    }
}

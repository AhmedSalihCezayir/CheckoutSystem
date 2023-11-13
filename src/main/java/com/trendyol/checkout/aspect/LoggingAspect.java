package com.trendyol.checkout.aspect;

import com.trendyol.checkout.models.dto.request.ItemRequest;
import com.trendyol.checkout.models.dto.response.BasicResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.trendyol.checkout.service.CartService.*(..)) && args(itemRequest)")
    public void logStartOfMethodsWithItemRequest(JoinPoint joinPoint, ItemRequest itemRequest) {
        String methodName = joinPoint.getSignature().getName();

        logger.info("Method '{}' is called for request {}", methodName, itemRequest);
    }

    @AfterThrowing(value = "execution(* com.trendyol.checkout.service.CartService.*(..)) && args(itemRequest)", throwing = "ex")
    public void logErrorsOfMethodsWithItemRequest(JoinPoint joinPoint, Exception ex, ItemRequest itemRequest) {
        String methodName = joinPoint.getSignature().getName();

        logger.error("Error when executing '{}' method for request {}. {}", methodName, itemRequest, ex.getMessage());
    }

    @AfterReturning(value = "execution(* com.trendyol.checkout.service.CartService.*(..)) && args(itemRequest)", returning = "response")
    public void logSuccessOfMethodsWithItemRequest(JoinPoint joinPoint, ItemRequest itemRequest, ResponseEntity<BasicResponse> response) {
        String methodName = joinPoint.getSignature().getName();

        logger.info("'{}' is successfully executed for {}. {}", methodName, itemRequest, response.getBody());
    }

    @Before("execution(* com.trendyol.checkout.service.CartService.*(..)) && !args(com.trendyol.checkout.models.dto.request.ItemRequest)")
    public void logStartOfMethodsWithoutItemRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();

        logger.info("Method '{}' is called", methodName);
    }

    @AfterThrowing(value = "execution(* com.trendyol.checkout.service.CartService.*(..)) && !args(com.trendyol.checkout.models.dto.request.ItemRequest)", throwing = "ex")
    public void logErrorsOfMethodsWithoutItemRequest(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();

        logger.error("Error when executing '{}' method. {}", methodName, ex.getMessage());
    }

    @AfterReturning(value = "execution(* com.trendyol.checkout.service.CartService.*(..)) && !args(com.trendyol.checkout.models.dto.request.ItemRequest)", returning = "response")
    public void logSuccessOfMethodsWithoutItemRequest(JoinPoint joinPoint, ResponseEntity<BasicResponse> response) {
        String methodName = joinPoint.getSignature().getName();

        logger.info("'{}' method is successfully executed. {}", methodName, response.getBody());
    }
}

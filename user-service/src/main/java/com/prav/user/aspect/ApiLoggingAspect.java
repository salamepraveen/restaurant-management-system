package com.prav.user.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@SuppressWarnings("java:S2139")
public class ApiLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ApiLoggingAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BORDER = "╔══════════════════════════════════════════════════════════════";
    private static final String BORDER_END = "╚══════════════════════════════════════════════════════════════";

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryMethods() {}

    @Around("controllerMethods()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();

        if (log.isInfoEnabled()) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                log.info("\n{}", BORDER);
                log.info("  >> INCOMING REQUEST");
                log.info("  >> {} {}", request.getMethod(), request.getRequestURI());
                log.info("  >> Controller: {}.{}()", className, methodName);
                log.info("  >> Client IP: {}", request.getRemoteAddr());
                log.info("  >> Headers: {}", getHeaders(request));

                for (Object arg : joinPoint.getArgs()) {
                    if (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse) && !(arg instanceof MultipartFile)) {
                        log.info("  >> Request Body: {}", toJson(arg));
                    }
                }
                log.info(BORDER_END);
            }
        }

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - startTime;

            if (log.isInfoEnabled()) {
                log.info(BORDER);
                log.info("  << RESPONSE [{}.{}()]", className, methodName);
                log.info("  << Status: SUCCESS ({}ms)", elapsed);
                log.info("  << Response Body: {}", toJson(result));
                log.info(BORDER_END);
            }
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - startTime;
            if (log.isErrorEnabled()) {
                log.error(BORDER);
                log.error("  !! ERROR [{}.{}()]", className, methodName);
                log.error("  !! Exception: {}", ex.getClass().getSimpleName());
                log.error("  !! Message: {}", ex.getMessage());
                log.error("  !! Time: {}ms", elapsed);
                log.error(BORDER_END);
            }
            throw ex;
        }
    }

    @Around("serviceMethods()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        if (log.isInfoEnabled()) {
            log.info("  [SERVICE] -> {}.{}({})", className, methodName, truncate(Arrays.toString(joinPoint.getArgs()), 200));
        }

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - startTime;
            if (log.isInfoEnabled()) {
                log.info("  [SERVICE] <- {}.{}() returned in {}ms", className, methodName, elapsed);
            }
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - startTime;
            if (log.isErrorEnabled()) {
                log.error("  [SERVICE] !! {}.{}() FAILED after {}ms: {}", className, methodName, elapsed, ex.getMessage());
            }
            throw ex;
        }
    }

    @Around("repositoryMethods()")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - startTime;

        if (elapsed > 200 && log.isWarnEnabled()) {
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            log.warn("  [DB-SLOW] {}.{}({}) took {}ms", className, methodName, truncate(Arrays.toString(joinPoint.getArgs()), 100), elapsed);
        }

        return result;
    }

    private String toJson(Object obj) {
        try {
            if (obj == null) return "null";
            return truncate(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj), 500);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    private String truncate(String str, int maxLen) {
        if (str == null) return "null";
        return str.length() > maxLen ? str.substring(0, maxLen) + "...(truncated)" : str;
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if ("authorization".equalsIgnoreCase(name)) {
                String val = request.getHeader(name);
                headers.put(name, val.length() > 15 ? val.substring(0, 15) + "..." : val);
            } else {
                headers.put(name, request.getHeader(name));
            }
        }
        return headers;
    }
}

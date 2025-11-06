package com.example.Projekat.logging;

import com.example.Projekat.config.security.CustomUserDetails;
import com.example.Projekat.db.entity.AuditLogEntity;
import com.example.Projekat.db.repository.AuditLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserActionAspect {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // možeš i kao @Bean

    @AfterReturning("@annotation(com.example.Projekat.logging.LogUserAction)")
    public void logUserAction(JoinPoint joinPoint) {
        handleJoinPoint(joinPoint, null);
    }

    @AfterThrowing(pointcut = "@annotation(com.example.Projekat.logging.LogUserAction)", throwing = "ex")
    public void logUserActionException(JoinPoint joinPoint, Throwable ex) {
        handleJoinPoint(joinPoint, ex);
    }

    private void handleJoinPoint(JoinPoint joinPoint, Throwable ex) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            String username = "anonymous";
            Long userId = null;

            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                username = userDetails.getUsername();
                userId = userDetails.getId();
            }

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            LogUserAction annotation = signature.getMethod().getAnnotation(LogUserAction.class);

            String actionDescription = (annotation != null && !annotation.value().isEmpty())
                    ? annotation.value()
                    : signature.getMethod().getName();

            String entityType = joinPoint.getTarget().getClass().getSimpleName();

            String details = buildDetails(joinPoint, ex);

            AuditLogEntity logEntry = AuditLogEntity.builder()
                    .userId(userId)
                    .username(username)
                    .action(actionDescription)
                    .entityType(entityType)
                    .details(details)
                    .timestamp(LocalDateTime.now())
                    .build();

            try {
                auditLogRepository.save(logEntry);
            } catch (Exception e) {
                log.error("⚠️ Failed to save audit log to DB", e);
            }

            String logLine = String.format("[%s] USER_ID=%s | USERNAME=%s | ACTION=%s | TARGET=%s | DETAILS=%s",
                    logEntry.getTimestamp(),
                    logEntry.getUserId(),
                    logEntry.getUsername(),
                    logEntry.getAction(),
                    logEntry.getEntityType(),
                    truncate(details, 4000)
            );

            org.slf4j.Logger actionLogger = org.slf4j.LoggerFactory.getLogger("USER_ACTION_LOGGER");
            if (ex == null) {
                actionLogger.info(logLine);
            } else {
                actionLogger.error(logLine + " | EX=" + ex.getClass().getSimpleName() + ":" + ex.getMessage());
            }

            log.debug("✅ User action recorded: {}", logLine);

        } catch (Exception e) {
            log.error("❌ Failed to handle user action logging", e);
        }
    }

    private String buildDetails(JoinPoint joinPoint, Throwable ex) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        List<Map<String, Object>> params = new ArrayList<>();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                String paramName = (paramNames != null && i < paramNames.length) ? paramNames[i] : "arg" + i;

                try {
                    Map<?, ?> converted = objectMapper.convertValue(arg, Map.class);
                    if (converted != null) {
                        Map<String, Object> entry = converted.entrySet().stream()
                                .filter(e -> e.getKey() != null) // 🔹 Ignoriši null ključeve
                                .collect(Collectors.toMap(
                                        e -> String.valueOf(e.getKey()),
                                        e -> {
                                            Object val = e.getValue();
                                            if ("password".equalsIgnoreCase(String.valueOf(e.getKey()))) {
                                                return "****";
                                            }
                                            return val != null ? val : ""; // 🔹 Zameni null vrednosti praznim stringom
                                        }
                                ));
                        params.add(Map.of(paramName, entry));
                        continue;
                    }
                } catch (IllegalArgumentException ignored) {}

                if ("password".equalsIgnoreCase(paramName) && arg instanceof CharSequence) {
                    params.add(Map.of(paramName, "****"));
                } else {
                    params.add(Map.of(paramName, safeToString(arg)));
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("params", params);
        result.put("exception", ex != null ? ex.getClass().getSimpleName() + ":" + ex.getMessage() : null);

        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            return params.toString();
        }
    }

    private static String safeToString(Object o) {
        try {
            return Objects.toString(o);
        } catch (Exception e) {
            return "<unserializable>";
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...(truncated)";
    }
}

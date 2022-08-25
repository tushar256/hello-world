package com.test.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.entities.Audit;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@ConditionalOnProperty(name = "auditing.enable", havingValue = "true")
@Aspect
@Slf4j
@Component
public class GenericAudit {

  @Value("${spring.application.name}")
  private String appName;

  @Autowired
  private MongoTemplate mongoTemplate;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private Tracer tracer;


  /**
   * This method gets called whenever any method is decorated with @Auditable annotation.
   *
   * @param joinPoint gives information about the method
   * @return the return from the actual method call
   * @throws Throwable this method returns Throwable exception
   */
  @Around("@annotation(com.test.audit.Auditable)")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    LocalDateTime requestDateTime = LocalDateTime.now();

    Object returnValue;
    try {
      returnValue = joinPoint.proceed();
    } catch (Exception e) {
      logMessage(joinPoint, stopWatch, requestDateTime, e.getMessage());
      throw e;
    }

    return logMessage(joinPoint, stopWatch, requestDateTime, returnValue);
  }

  private Object logMessage(ProceedingJoinPoint joinPoint, StopWatch stopWatch,
      LocalDateTime requestDateTime, Object returnValue) {

    try {

      stopWatch.stop();

      long timeTaken = stopWatch.getTotalTimeMillis();
      log.info("API {} executed in {} ms", joinPoint.getSignature(), timeTaken);

      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      Method method = signature.getMethod();
      String path = method.getDeclaringClass().getName() + "." + method.getName();

      if (method.getAnnotation(GetMapping.class) != null) {
        path = Arrays.stream(method.getAnnotation(GetMapping.class).value()).findFirst().orElse("");

      } else if (method.getAnnotation(PostMapping.class) != null) {
        path = Arrays.stream(method.getAnnotation(PostMapping.class).value()).findFirst()
            .orElse("");

      } else if (method.getAnnotation(PutMapping.class) != null) {
        path = Arrays.stream(method.getAnnotation(PutMapping.class).value()).findFirst().orElse("");

      } else if (method.getAnnotation(DeleteMapping.class) != null) {
        path = Arrays.stream(method.getAnnotation(DeleteMapping.class).value()).findFirst()
            .orElse("");
      }

      Span span = tracer.currentSpan();

      Auditable auditable = method.getAnnotation(Auditable.class);
      String objectId = (String) getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(),
          auditable.objectId());

      Audit audit = new Audit();
      audit.setApplicationName(appName);
      audit.setApi(path);
      audit.setObjectId(objectId);
      audit.setRequestBody(objectMapper.writeValueAsString(joinPoint.getArgs()));
      audit.setResponseBody(objectMapper.writeValueAsString(returnValue));
      audit.setRequestDateTime(requestDateTime);
      audit.setApiResponseTime(timeTaken);
      audit.setTraceId(Optional.ofNullable(span.context().traceId()).orElse(""));
      mongoTemplate.insert(audit);

    } catch (Exception ex) {
      log.error("Error logging into the audit table ", ex);
    }

    return returnValue;
  }

  /**
   * This method returns the objectId based on the expression language.
   *
   * @param parameterNames parameter names
   * @param args           method arguments
   * @param key            expression language
   * @return
   */
  public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
    try {
      ExpressionParser parser = new SpelExpressionParser();
      StandardEvaluationContext context = new StandardEvaluationContext();

      for (int i = 0; i < parameterNames.length; i++) {
        context.setVariable(parameterNames[i], args[i]);
      }
      return parser.parseExpression(key).getValue(context, Object.class);

    } catch (Exception ex) {
      return "";
    }

  }

}

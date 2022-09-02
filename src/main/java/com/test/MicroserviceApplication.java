package com.test;

import brave.baggage.CorrelationScopeDecorator;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import com.test.connectors.kafka.KafkaReceiver;
import com.test.connectors.kafka.KafkaSender;
import com.test.controller.DefaultController;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.cloud.sleuth.BaggageManager;
import org.springframework.cloud.sleuth.CurrentTraceContext;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.brave.bridge.BraveCurrentTraceContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.index.IndexOperationsProvider;
import org.springframework.nativex.hint.*;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.filter.RequestContextFilter;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

import java.io.Serializable;

import static org.springframework.nativex.hint.TypeAccess.*;
import static org.springframework.nativex.hint.TypeAccess.PUBLIC_FIELDS;



@NativeHint(
        types = {
                @TypeHint(types = {ConsoleAppender.class, SizeAndTimeBasedRollingPolicy.class, PatternLayout.class, RollingFileAppender.class, PatternLayoutEncoder.class},
                        access = {TypeAccess.PUBLIC_CONSTRUCTORS, PUBLIC_METHODS}),
                @TypeHint(types = { ApplicationContextAware.class, Aware.class, MongoOperations.class, IndexOperationsProvider.class, FluentMongoOperations.class, ExecutableFindOperation.class, ExecutableInsertOperation.class, ExecutableUpdateOperation.class, ExecutableRemoveOperation.class, ExecutableAggregationOperation.class, ExecutableMapReduceOperation.class},
                        access = {TypeAccess.PUBLIC_CONSTRUCTORS, PUBLIC_METHODS, PUBLIC_FIELDS}),
                @TypeHint(types = { java.util.List.class, java.util.RandomAccess.class, java.lang.Cloneable.class, java.io.Serializable.class, java.util.Collection.class},
                        access = {TypeAccess.PUBLIC_CONSTRUCTORS, PUBLIC_METHODS, PUBLIC_FIELDS}),
                @TypeHint(types = { CorrelationScopeDecorator.class, brave.propagation.CurrentTraceContext.ScopeDecorator.class, BraveCurrentTraceContext.class, BaggageManager.class, CurrentTraceContext.class, Tracer.class, TraceContext.class,
                        java.util.List.class, java.util.RandomAccess.class, java.lang.Cloneable.class, java.io.Serializable.class, java.util.Collection.class},
                        access = {TypeAccess.PUBLIC_CONSTRUCTORS, PUBLIC_METHODS, PUBLIC_FIELDS})
        }, jdkProxies = {
        @JdkProxyHint(types= { BeanPostProcessor.class, BeanFactoryAware.class, BeanClassLoaderAware.class }),
        @JdkProxyHint(types= { ApplicationContextAware.class, EnvironmentCapable.class, EnvironmentAware.class, Servlet.class, ServletConfig.class, Serializable.class }),
        @JdkProxyHint(types= { java.util.List.class, java.util.RandomAccess.class, java.lang.Cloneable.class, java.io.Serializable.class, java.util.Collection.class, java.io.Closeable.class}),
        @JdkProxyHint(types= { ApplicationContextAware.class, Aware.class, MongoOperations.class, IndexOperationsProvider.class, FluentMongoOperations.class, ExecutableFindOperation.class, ExecutableInsertOperation.class, ExecutableUpdateOperation.class, ExecutableRemoveOperation.class, ExecutableAggregationOperation.class, ExecutableMapReduceOperation.class}),
        @JdkProxyHint(types = { CorrelationScopeDecorator.class, brave.propagation.CurrentTraceContext.ScopeDecorator.class, BraveCurrentTraceContext.class, BaggageManager.class, CurrentTraceContext.class, Tracer.class, TraceContext.class,
                java.util.List.class, java.util.RandomAccess.class, java.lang.Cloneable.class, java.io.Serializable.class, java.util.Collection.class} )
}
)
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class MicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceApplication.class, args);
    }

}

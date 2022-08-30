package com.test;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import com.test.connectors.kafka.KafkaReceiver;
import com.test.connectors.kafka.KafkaSender;
import com.test.controller.DefaultController;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.nativex.hint.*;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.filter.RequestContextFilter;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

import java.io.Serializable;

import static org.springframework.nativex.hint.TypeAccess.PUBLIC_METHODS;
import static org.springframework.nativex.hint.TypeAccess.QUERY_PUBLIC_METHODS;



@JdkProxyHint(types= { BeanPostProcessor.class, BeanFactoryAware.class, BeanClassLoaderAware.class,
        ApplicationContextAware.class, EnvironmentCapable.class, EnvironmentAware.class, Servlet.class, ServletConfig.class, Serializable.class})
@NativeHint(
        types = {

                @TypeHint(types = {BeanPostProcessor.class, BeanFactoryAware.class, BeanClassLoaderAware.class, ApplicationContextAware.class, EnvironmentCapable.class, EnvironmentAware.class, Servlet.class, ServletConfig.class, Serializable.class},
                        access = {TypeAccess.PUBLIC_CONSTRUCTORS, PUBLIC_METHODS}),
                @TypeHint(types = {ConsoleAppender.class, SizeAndTimeBasedRollingPolicy.class, PatternLayout.class, RollingFileAppender.class, PatternLayoutEncoder.class},
                            access = {TypeAccess.PUBLIC_CONSTRUCTORS, PUBLIC_METHODS})

        })
@AotProxyHints(  { @AotProxyHint(interfaces ={BeanPostProcessor.class, BeanFactoryAware.class, BeanClassLoaderAware.class, ApplicationContextAware.class,
                    EnvironmentCapable.class, EnvironmentAware.class, Servlet.class, ServletConfig.class, Serializable.class}),
                    @AotProxyHint(targetClass = DefaultController.class, proxyFeatures = ProxyBits.IS_STATIC),
                   @AotProxyHint(targetClass= KafkaReceiver.class, proxyFeatures = ProxyBits.IS_STATIC),
                   @AotProxyHint(targetClass= KafkaSender.class, proxyFeatures = ProxyBits.IS_STATIC) } )

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class MicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceApplication.class, args);
    }

}

package org.arquillian.example.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.*;
@Qualifier
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertiesFromFile {

    /**
     * This value must be a properties file in the classpath.
     */
    @Nonbinding
    String value() default "config.properties";
}
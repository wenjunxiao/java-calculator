package io.github.wenjunxiao.calculator.spring.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(CalculatorScannerRegistrar.class)
@SuppressWarnings("unused")
public @interface CalculatorScanner {
  String[] value() default {};
  String[] basePackages() default {};
  Class<?>[] basePackageClasses() default {};
}

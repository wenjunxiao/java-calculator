package io.github.wenjunxiao.calculator.annotation;

import io.github.wenjunxiao.calculator.binding.DefaultCalculator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Calculator {
  /**
   * @return The default scale of method
   */
  int scale() default Integer.MAX_VALUE;

  /**
   * @return The default rounding mode of method
   */
  RoundingMode mode() default RoundingMode.UNNECESSARY;

  /**
   * @return The default scale for divide operation
   */
  int divideScale() default Integer.MAX_VALUE;

  /**
   * @return The default rounding mode for divide operation
   */
  RoundingMode divideMode() default RoundingMode.UNNECESSARY;

  /**
   * @return Extended class for common operation
   */
  Class<?> extend() default DefaultCalculator.class;

  /**
   * @return The class need to be imported
   */
  Class<?>[] imports() default {};

  /**
   * @return Print some debug information
   */
  boolean debug() default false;
}

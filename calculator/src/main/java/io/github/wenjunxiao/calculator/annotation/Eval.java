package io.github.wenjunxiao.calculator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Eval {
  /**
   * @return 计算的表达式
   */
  String[] value();

  /**
   * @return The scale of current method
   */
  int scale() default Integer.MAX_VALUE;

  /**
   * @return The rounding mode of current method
   */
  RoundingMode mode() default RoundingMode.UNNECESSARY;

  /**
   * @return The scale for divide operation of current method
   */
  int divideScale() default Integer.MAX_VALUE;

  /**
   * @return The rounding mode for divide operation of current method
   */
  RoundingMode divideMode() default RoundingMode.UNNECESSARY;

  /**
   * @return Whether to use the parameters of class context
   */
  boolean context() default true;

  /**
   * @return Print some debug information
   */
  boolean debug() default false;
}

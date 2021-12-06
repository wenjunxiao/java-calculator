package io.github.wenjunxiao.calculator.test;

import io.github.wenjunxiao.calculator.annotation.Eval;
import io.github.wenjunxiao.calculator.binding.CalculatorRegistry;

import java.math.BigDecimal;

public class Sample {

  interface SampleCalculator {
    @Eval(value = "$1 + $1 * $2 * $3",debug = true)
    BigDecimal eval(double a, double b, double c);
  }

  public static void main(String[] args) {
    CalculatorRegistry registry = new CalculatorRegistry();
    registry.addCalculator(SampleCalculator.class);
    SampleCalculator calculator = registry.getCalculator(SampleCalculator.class);
    System.out.println(calculator.eval(1.0, 2.0, 3.0));
  }
}

package io.github.wenjunxiao.calculator.binding;

import java.math.BigDecimal;

public interface ICalculator {

  BigDecimal max(BigDecimal value);

  BigDecimal max(BigDecimal value, BigDecimal other);

  BigDecimal max(BigDecimal value, BigDecimal... values);

  BigDecimal min(BigDecimal value);

  BigDecimal min(BigDecimal value, BigDecimal other);

  BigDecimal min(BigDecimal value, BigDecimal... values);

  BigDecimal abs(BigDecimal value);

  BigDecimal sum(BigDecimal... values);

  BigDecimal round(BigDecimal value, int scale);

  BigDecimal roundUp(BigDecimal value, int scale);

  BigDecimal roundDown(BigDecimal value, int scale);

  BigDecimal halfUp(BigDecimal value, int scale);

  BigDecimal halfDown(BigDecimal value, int scale);

  BigDecimal halfEven(BigDecimal value, int scale);

  BigDecimal floor(BigDecimal value, int scale);

  BigDecimal ceiling(BigDecimal value, int scale);
}

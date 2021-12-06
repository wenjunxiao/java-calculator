package io.github.wenjunxiao.calculator.binding;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DefaultCalculator implements ICalculator {

  @Override
  public BigDecimal max(BigDecimal value) {
    return value;
  }

  @Override
  public BigDecimal max(BigDecimal value, BigDecimal other) {
    return value.max(other);
  }

  @Override
  public BigDecimal max(BigDecimal value, BigDecimal... values) {
    for (BigDecimal other: values) {
      value = value.max(other);
    }
    return value;
  }

  @Override
  public BigDecimal min(BigDecimal value) {
    return value;
  }

  @Override
  public BigDecimal min(BigDecimal value, BigDecimal other) {
    return value.min(other);
  }

  @Override
  public BigDecimal min(BigDecimal value, BigDecimal... values) {
    for (BigDecimal other: values) {
      value = value.min(other);
    }
    return value;
  }

  @Override
  public BigDecimal abs(BigDecimal value) {
    return value.abs();
  }

  @Override
  public BigDecimal sum(BigDecimal... values) {
    BigDecimal res = BigDecimal.ZERO;
    for (BigDecimal value : values) {
      res = res.add(value);
    }
    return res;
  }

  @Override
  public BigDecimal round(BigDecimal value, int scale) {
    return value.setScale(scale, RoundingMode.UNNECESSARY);
  }

  @Override
  public BigDecimal roundUp(BigDecimal value, int scale) {
    return value.setScale(scale, RoundingMode.UP);
  }

  @Override
  public BigDecimal roundDown(BigDecimal value, int scale) {
    return value.setScale(scale, RoundingMode.DOWN);
  }

  @Override
  public BigDecimal halfUp(BigDecimal value, int scale) {
    return value.setScale(scale, RoundingMode.HALF_UP);
  }

  @Override
  public BigDecimal halfDown(BigDecimal value, int scale) {
    return value.setScale(scale, RoundingMode.HALF_DOWN);
  }

  @Override
  public BigDecimal halfEven(BigDecimal value, int scale) {
    return value.setScale(scale, RoundingMode.HALF_EVEN);
  }

  @Override
  public BigDecimal floor(BigDecimal value, int scale) {
    return value.setScale(scale, RoundingMode.FLOOR);
  }

  @Override
  public BigDecimal ceiling(BigDecimal value, int scale) {
    return value.setScale(scale, RoundingMode.CEILING);
  }
}

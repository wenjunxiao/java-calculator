package io.github.wenjunxiao.calculator.binding;

import io.github.wenjunxiao.calculator.annotation.Calculator;
import io.github.wenjunxiao.calculator.annotation.Eval;

import java.math.RoundingMode;

@SuppressWarnings("unused")
public class Context {
  private int scale;
  private RoundingMode mode;
  private int divideScale;
  private RoundingMode divideMode;

  public Context() {
    reset();
  }

  public int getScale() {
    return scale;
  }

  public RoundingMode getMode() {
    return mode;
  }

  public int getDivideScale() {
    return divideScale;
  }

  public RoundingMode getDivideMode() {
    return divideMode;
  }

  public void setScale(int scale) {
    this.scale = scale;
  }

  public void setMode(RoundingMode mode) {
    this.mode = mode;
  }

  public void setDivideScale(int divideScale) {
    this.divideScale = divideScale;
  }

  public void setDivideMode(RoundingMode divideMode) {
    this.divideMode = divideMode;
  }

  public boolean ignoreDivideMode() {
    return this.divideMode == RoundingMode.UNNECESSARY;
  }

  public boolean ignoreDivideScale() {
    return this.divideScale == Integer.MAX_VALUE;
  }

  public Context merge(Calculator calculator) {
    if (calculator == null) return this;
    if (calculator.scale() != Integer.MAX_VALUE) {
      this.scale = calculator.scale();
    }
    if (calculator.mode() != RoundingMode.UNNECESSARY) {
      this.mode = calculator.mode();
    }
    if (calculator.divideScale() != Integer.MAX_VALUE) {
      this.divideScale = calculator.divideScale();
    }
    if (calculator.divideMode() != RoundingMode.UNNECESSARY) {
      this.divideMode = calculator.divideMode();
    }
    return this;
  }

  public Context merge(Eval eval) {
    if (eval == null) return this;
    if (eval.scale() != Integer.MAX_VALUE) {
      this.scale = eval.scale();
    }
    if (eval.mode() != RoundingMode.UNNECESSARY) {
      this.mode = eval.mode();
    }
    if (eval.divideScale() != Integer.MAX_VALUE) {
      this.divideScale = eval.divideScale();
    }
    if (eval.divideMode() != RoundingMode.UNNECESSARY) {
      this.divideMode = eval.divideMode();
    }
    return this;
  }

  public Context merge(Context context) {
    if (context == null) return this;
    if (context.scale != Integer.MAX_VALUE) {
      this.scale = context.scale;
    }
    if (context.mode != RoundingMode.UNNECESSARY) {
      this.mode = context.mode;
    }
    if (context.divideScale != Integer.MAX_VALUE) {
      this.divideScale = context.divideScale;
    }
    if (context.divideMode != RoundingMode.UNNECESSARY) {
      this.divideMode = context.divideMode;
    }
    return this;
  }

  public Context reset() {
    this.scale = Integer.MAX_VALUE;
    this.mode= RoundingMode.UNNECESSARY;
    this.divideScale = Integer.MAX_VALUE;
    this.divideMode = RoundingMode.UNNECESSARY;
    return this;
  }
}

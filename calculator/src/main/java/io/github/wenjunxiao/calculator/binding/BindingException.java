package io.github.wenjunxiao.calculator.binding;

public class BindingException extends RuntimeException {

  public BindingException(String message) {
    super(message);
  }

  public BindingException(Throwable throwable) {
    super(throwable);
  }

  public BindingException(String message, Throwable throwable) {
    super(message, throwable);
  }
}

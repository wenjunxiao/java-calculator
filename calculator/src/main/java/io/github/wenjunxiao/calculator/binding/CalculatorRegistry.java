package io.github.wenjunxiao.calculator.binding;

import java.util.HashMap;
import java.util.Map;

public class CalculatorRegistry {

  private final Map<Class<?>, CalculatorFactory<?>> calculators = new HashMap<>();

  private boolean debug;

  public CalculatorRegistry() {
  }

  public CalculatorRegistry(boolean debug) {
    this.debug = debug;
  }

  @SuppressWarnings("unchecked")
  public <T> T getCalculator(Class<T> type) {
    final CalculatorFactory<T> factory = (CalculatorFactory<T>) calculators.get(type);
    if (factory == null) {
      throw new BindingException("Type " + type + " is not known to the CalculatorRegistry.");
    }
    try {
      return factory.newInstance();
    } catch (Exception e) {
      throw new BindingException("Error getting calculator instance. Cause: " + e, e);
    }
  }

  public <T> boolean hasCalculator(Class<T> type) {
    return calculators.containsKey(type);
  }

  public <T> void addCalculator(Class<T> type) {
    if (type.isInterface()) {
      if (hasCalculator(type)) {
        throw new BindingException("Type " + type + " is already known to the CalculatorRegistry.");
      }
      calculators.put(type, new CalculatorFactory<>(type, debug));
    }
  }

}

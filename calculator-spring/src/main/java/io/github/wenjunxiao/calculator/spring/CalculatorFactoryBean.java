package io.github.wenjunxiao.calculator.spring;

import io.github.wenjunxiao.calculator.binding.CalculatorRegistry;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import static org.springframework.util.Assert.notNull;

public class CalculatorFactoryBean<T> implements FactoryBean<T>, InitializingBean {
  private final Class<T> calculatorInterface;
  private CalculatorRegistry calculatorRegistry;

  public CalculatorFactoryBean(Class<T> calculatorInterface) {
    this.calculatorInterface = calculatorInterface;
  }

  @SuppressWarnings("unused")
  public void setCalculatorRegistry(CalculatorRegistry calculatorRegistry) {
    this.calculatorRegistry = calculatorRegistry;
  }

  @Override
  public T getObject() {
    return calculatorRegistry.getCalculator(calculatorInterface);
  }

  @Override
  public Class<?> getObjectType() {
    return calculatorInterface;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  @Override
  public void afterPropertiesSet() throws IllegalArgumentException {
    notNull(this.calculatorInterface, "Property 'mapperInterface' is required");
    if (!calculatorRegistry.hasCalculator(calculatorInterface)) {
      try {
        calculatorRegistry.addCalculator(this.calculatorInterface);
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
  }
}

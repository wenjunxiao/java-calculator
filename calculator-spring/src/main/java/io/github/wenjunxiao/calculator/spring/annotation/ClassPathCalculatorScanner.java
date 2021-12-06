package io.github.wenjunxiao.calculator.spring.annotation;

import io.github.wenjunxiao.calculator.binding.CalculatorRegistry;
import io.github.wenjunxiao.calculator.spring.CalculatorFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Set;

public class ClassPathCalculatorScanner extends ClassPathBeanDefinitionScanner {

  private CalculatorRegistry calculatorRegistry;

  public ClassPathCalculatorScanner(BeanDefinitionRegistry registry) {
    super(registry, false);
  }

  public void setCalculatorRegistry(CalculatorRegistry calculatorRegistry) {
    this.calculatorRegistry = calculatorRegistry;
  }

  @Override
  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
    return beanDefinition.getMetadata().isInterface();
  }

  @Override
  protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
    Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
    if (!beanDefinitionHolders.isEmpty()) {
      processBeanDefinitions(beanDefinitionHolders);
    }
    return beanDefinitionHolders;
  }

  private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolders) {
    for (BeanDefinitionHolder holder: beanDefinitionHolders) {
      GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
      String beanClassName = definition.getBeanClassName();
      if (beanClassName == null) continue;
      definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
      definition.getPropertyValues().add("calculatorRegistry", this.calculatorRegistry);
      definition.setBeanClass(CalculatorFactoryBean.class);
    }
  }
}

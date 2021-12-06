package io.github.wenjunxiao.calculator.spring.autoconfigure;

import io.github.wenjunxiao.calculator.annotation.Calculator;
import io.github.wenjunxiao.calculator.binding.CalculatorRegistry;
import io.github.wenjunxiao.calculator.spring.CalculatorFactoryBean;
import io.github.wenjunxiao.calculator.spring.annotation.CalculatorScannerRegistrar;
import io.github.wenjunxiao.calculator.spring.annotation.ClassPathCalculatorScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration
@Import({CalculatorAutoConfiguration.AutoConfiguredCalculatorScannerRegistrar.class})
@ConditionalOnMissingBean({CalculatorFactoryBean.class, CalculatorScannerRegistrar.class})
public class CalculatorAutoConfiguration {

  public static class AutoConfiguredCalculatorScannerRegistrar implements BeanFactoryAware,ImportBeanDefinitionRegistrar {

    private BeanFactory beanFactory;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
      List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
      ClassPathCalculatorScanner scanner = new ClassPathCalculatorScanner(registry);
      scanner.addIncludeFilter(new AnnotationTypeFilter(Calculator.class));
      scanner.setCalculatorRegistry(new CalculatorRegistry());
      scanner.scan(StringUtils.toStringArray(packages));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
      this.beanFactory = beanFactory;
    }
  }
}

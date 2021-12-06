package io.github.wenjunxiao.calculator.spring.annotation;

import io.github.wenjunxiao.calculator.annotation.Calculator;
import io.github.wenjunxiao.calculator.binding.CalculatorRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CalculatorScannerRegistrar implements ImportBeanDefinitionRegistrar {

  @Override
  public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(CalculatorScanner.class.getName()));
    if (attributes == null) return;
    List<String> basePackages = new ArrayList<>();
    basePackages.addAll(Arrays.stream(attributes.getStringArray("value")).filter(StringUtils::hasText).collect(Collectors.toList()));
    basePackages.addAll(Arrays.stream(attributes.getStringArray("basePackages")).filter(StringUtils::hasText).collect(Collectors.toList()));
    basePackages.addAll(Arrays.stream(attributes.getStringArray("basePackageClasses")).filter(StringUtils::hasText).collect(Collectors.toList()));
    if (basePackages.isEmpty()) {
      basePackages.add(ClassUtils.getPackageName(annotationMetadata.getClassName()));
    }
    ClassPathCalculatorScanner scanner = new ClassPathCalculatorScanner(beanDefinitionRegistry);
    scanner.addIncludeFilter(new AnnotationTypeFilter(Calculator.class));
    scanner.setCalculatorRegistry(new CalculatorRegistry());
    scanner.scan(StringUtils.toStringArray(basePackages));
  }
}

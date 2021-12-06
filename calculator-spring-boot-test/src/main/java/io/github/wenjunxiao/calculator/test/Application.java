package io.github.wenjunxiao.calculator.test;

import io.github.wenjunxiao.calculator.annotation.Calculator;
import io.github.wenjunxiao.calculator.annotation.Eval;
import io.github.wenjunxiao.calculator.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@EnableAutoConfiguration
@SpringBootApplication
public class Application {

  @Calculator
  public interface TestCalculator {

    @Eval("a + b * c")
    BigDecimal eval(@Param("a") double a, @Param("b") double b, @Param("c") double c);
  }

  @Autowired
  private TestCalculator testCalculator;

  @PostConstruct
  public void init () {
    System.out.println(testCalculator.eval(1, 2, 3));
  }
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}

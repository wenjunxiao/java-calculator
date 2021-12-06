# Calculator

Simplify and formulate the use of `BigDecimal`.

## Usage

### Simple Usage

```xml
<dependency>
    <groupId>io.github.wenjunxiao</groupId>
    <artifactId>calculator</artifactId>
    <version>${calculator.version}</version>
</dependency>
```

```java
public class Sample {
  interface SampleCalculator {
    @Eval(value = "$1 + $1 * $2 * $3",debug = true)
    BigDecimal eval(double a, double b, double c);
  }

  public static void main(String[] args) {
    CalculatorRegistry registry = new CalculatorRegistry();
    registry.addCalculator(SampleCalculator.class);
    SampleCalculator calculator = registry.getCalculator(SampleCalculator.class);
    System.out.println(calculator.eval(1.0, 2.0, 3.0));
  }
}
```

### SpringBoot

```xml
<dependency>
    <groupId>io.github.wenjunxiao</groupId>
    <artifactId>calculator-spring-boot-autoconfigure</artifactId>
    <version>${calculator.version}</version>
</dependency>
```

```java

```

```java
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
```

package io.github.wenjunxiao.calculator.binding;

import io.github.wenjunxiao.calculator.annotation.Calculator;
import io.github.wenjunxiao.calculator.annotation.Eval;
import io.github.wenjunxiao.calculator.annotation.Param;
import javassist.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorFactory<T> {

  static String OPEN = "_<";
  static String CLOSE = ">_";

  private Class<T> calculatorInterface;
  private Class<T> proxyClass;
  private boolean debug;

  @SuppressWarnings("unused")
  public CalculatorFactory() {
  }

  public CalculatorFactory(Class<T> calculatorInterface) {
    this.calculatorInterface = calculatorInterface;
  }

  public CalculatorFactory(Class<T> calculatorInterface, boolean debug) {
    this.calculatorInterface = calculatorInterface;
    this.debug = debug;
  }

  @SuppressWarnings("unused")
  public void setCalculatorInterface(Class<T> calculatorInterface) {
    this.calculatorInterface = calculatorInterface;
  }

  @SuppressWarnings("unused")
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  private CtClass makeClass(ClassPool pool, Class<?> clazz, Calculator calculator) throws NotFoundException {
    String name = clazz.getName() + "Impl";
    Class<?> extend = calculator == null ? null : calculator.extend();
    if (!clazz.isInterface()) {
      extend = clazz;
    }
    if (extend == null && ICalculator.class.isAssignableFrom(clazz)) {
      extend = DefaultCalculator.class;
    }
    if (extend != null) {
      pool.importPackage(extend.getName());
      return pool.makeClass(name, pool.getCtClass(extend.getName()));
    }
    return pool.makeClass(name);
  }

  private String fixFunction(String expr, Map<String, String> map) {
    Pattern mp = Pattern.compile("(^|[-+*/\\s,])(\\w+)\\(");
    Matcher matcher = mp.matcher(expr);
    List<String> res = new ArrayList<>();
    while (matcher.find()) {
      String name = matcher.group(2).toLowerCase();
      if (map.containsKey(name)) {
        res.add(expr.substring(0, matcher.start(1)));
        res.add(map.get(name));
        res.add("(");
      } else {
        res.add(expr.substring(0, matcher.end()));
      }
      expr = expr.substring(matcher.end());
      matcher = mp.matcher(expr);
    }
    res.add(expr);
    return String.join("", res);
  }

  @SuppressWarnings("unchecked")
  private Class<T> compile() throws NotFoundException, CannotCompileException {
    if (this.proxyClass != null) {
      return this.proxyClass;
    }
    Calculator calculator = calculatorInterface.getAnnotation(Calculator.class);
    ClassPool pool = new ClassPool(true);
    pool.importPackage(BigDecimal.class.getName());
    pool.importPackage(RoundingMode.class.getName());
    if (calculator != null) {
      for (Class<?> clazz : calculator.imports()) {
        pool.importPackage(clazz.getName());
      }
    }
    CtClass ctClass = makeClass(pool, calculatorInterface, calculator);
    if (calculatorInterface.isInterface()) {
      ctClass.addInterface(pool.get(calculatorInterface.getName()));
    }
    Map<String, String> methodMap = new HashMap<>();
    if (calculator != null && calculator.extend() != null) {
      for (Method method : calculator.extend().getMethods()) {
        methodMap.put(method.getName().toLowerCase(), method.getName());
      }
    }
    Context context = new Context();
    for (Method method : calculatorInterface.getMethods()) {
      Eval eval = method.getAnnotation(Eval.class);
      if (eval == null) continue;
      if (eval.context()) {
        context = context.reset().merge(calculator).merge(eval);
      } else {
        context = context.reset().merge(eval);
      }
      String expr = fixFunction(String.join("", eval.value()), methodMap);
      Parameter[] params = method.getParameters();
      CtClass[] parameters = new CtClass[params.length];
      for (int i = 0; i < params.length; i++) {
        Parameter parameter = params[i];
        Param param = parameter.getAnnotation(Param.class);
        String name = param == null ? "\\$" + (i + 1) : param.value();
        Class<?> type = parameter.getType();
        String target = "\\$" + (i + 1);
        if (String.class.equals(type)) {
          target = "BigDecimal.newOf" + OPEN + target + CLOSE;
        } else if (Long.class.equals(type)) {
          target = "BigDecimal.valueOf" + OPEN + target + ".longValue" + OPEN + CLOSE + CLOSE;
        } else if (Long.TYPE.equals(type)) {
          target = "BigDecimal.valueOf" + OPEN + target + CLOSE;
        } else if (Integer.class.equals(type)) {
          target = "BigDecimal.valueOf" + OPEN + target + ".longValue" + OPEN + CLOSE + CLOSE;
        } else if (Integer.TYPE.equals(type)) {
          target = "BigDecimal.valueOf" + OPEN + OPEN + "long" + CLOSE + target + CLOSE;
        } else if (Double.class.equals(type)) {
          target = "BigDecimal.valueOf" + OPEN + target + ".doubleValue" + OPEN + CLOSE + CLOSE;
        } else if (Double.TYPE.equals(type)) {
          target = "BigDecimal.valueOf" + OPEN + target + CLOSE;
        }
        expr = expr.replaceAll("(^|[\\s+\\-*/(,])(?:#\\{\\s*)?" + name + "(?:\\s*})?($|[\\s+\\-*/(),])", "$1" + target + "$2").replace("%", "/100");
        parameters[i] = pool.get(type.getName());
      }
      Class<?> type = method.getReturnType();
      CtClass returnType = pool.get(type.getName());
      CtMethod ctMethod = new CtMethod(returnType, method.getName(), parameters, ctClass);
      String body = compile(expr, context);
      if (String.class.equals(type)) {
        body += ".toPlainString()";
      } else if (Long.class.equals(type) || Long.TYPE.equals(type)) {
        body += ".longValue()";
      } else if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
        body += ".intValue()";
      } else if (Double.class.equals(type) || Double.TYPE.equals(type)) {
        body += ".doubleValue()";
      }
      try {
        ctMethod.setBody("{\nreturn " + body + ";\n}");
      } catch (CannotCompileException e) {
        throw new CannotCompileException(expr + "\n"+ fullMethod(method, body), e);
      }
      if (debug || eval.debug()) {
        System.err.println(calculatorInterface.getName() + "." + method.getName() + " => " + fullMethod(method, body));
      }
      ctClass.addMethod(ctMethod);
    }
    if (debug || calculator != null && calculator.debug()) {
      URL url = calculatorInterface.getResource("/");
      if (url != null) {
        ctClass.debugWriteFile(url.getPath());
      } else {
        ctClass.debugWriteFile();
      }
    }
    this.proxyClass = (Class<T>) ctClass.toClass();
    return this.proxyClass;
  }

  private String fullMethod(Method method, String body) {
    StringBuilder builder = new StringBuilder(method.getReturnType().getName());
    builder.append(' ').append(method.getName()).append('(');
    Parameter[] params = method.getParameters();
    for (int i = 0; i < params.length; i++) {
      builder.append(params[i].getType().getName()).append(' ').append('$').append(i + 1).append(',');
    }
    builder.deleteCharAt(builder.length() - 1);
    builder.append("){\nreturn ").append(body).append(";\n}");
    return builder.toString();
  }

  public String compile(String expr, Context context) {
    expr = markFunction(expr);
    Pattern group = Pattern.compile("(^|[-+*/<>_,\\s])\\(\\s*([^()]+)(\\s*\\)\\s*)(\\{[\\w,+-]+})?");
    Matcher matcher = group.matcher(expr);
    while (matcher.find()) {
      expr = expr.substring(0, matcher.end(1)) + compileGroup(matcher.group(2), matcher.group(4), context) + expr.substring(matcher.end());
      matcher = group.matcher(expr);
    }
    expr = compileGroup(expr, "", context);
    if (context.getMode() != RoundingMode.UNNECESSARY) {
      expr += ".setScale(" + context.getScale() + ", RoundingMode." + context.getMode().name() + ")";
    } else if (context.getScale() != Integer.MAX_VALUE) {
      expr += ".setScale(" + context.getScale() + ")";
    }
    return restoreFunction(expr);
  }

  private String compileGroup(String expr, String cfg, Context context) {
    expr = expr.trim();
    Pattern op = Pattern.compile("\\s*([^+-]+)([+-]|$)");
    List<String> res = new ArrayList<>();
    Matcher matcher = op.matcher(expr);
    while (matcher.find()) {
      if (res.size() > 0) {
        res.add(OPEN);
        res.add(mulOrDiv(matcher.group(1), context));
        res.add(CLOSE);
      } else {
        res.add(mulOrDiv(matcher.group(1), context));
      }
      if ("+".equals(matcher.group(2))) {
        res.add(".add");
      } else if ("-".equals(matcher.group(2))) {
        res.add(".subtract");
      }
      expr = expr.substring(matcher.end(2)).trim();
      matcher = op.matcher(expr);
    }
    if (cfg != null && !cfg.isEmpty()) {
      String scale = cfg.replaceAll("^\\{\\s*([+-]?\\d+).*$", "$1");
      String mode = mapMode(cfg.replaceAll("^\\{\\s*[+-]?\\d+,?(.*)\\}$", "$1"));
      res.add(".setScale");
      res.add(OPEN);
      res.add(scale);
      res.add(",");
      res.add(mode);
      res.add(CLOSE);
    }
    return String.join("", res);
  }

  private String mapMode(String mode) {
    switch (mode.toUpperCase()) {
      case "U":
      case "UP":
        return "RoundingMode.UP";
      case "D":
      case "DOWN":
        return "RoundingMode.DOWN";
      case "HU":
      case "HALF_UP":
        return "RoundingMode.HALF_UP";
      case "HD":
      case "HALF_DOWN":
        return "RoundingMode.HALF_DOWN";
      case "E":
      case "EVEN":
      case "HALF_EVEN":
        return "RoundingMode.HALF_EVEN";
      case "C":
      case "CEILING":
        return "RoundingMode.CEILING";
      case "F":
      case "FLOOR":
        return "RoundingMode.FLOOR";
    }
    throw new BindingException("Unknown mode flag: " + mode);
  }

  private String mulOrDiv(String expr, Context context) {
    expr = expr.trim();
    Pattern num = Pattern.compile("^\\d[\\d.]*$");
    Pattern op = Pattern.compile("\\s*([^*/]+)([*/]|$)");
    List<String> res = new ArrayList<>();
    String extra = null;
    Matcher matcher = op.matcher(expr);
    while (matcher.find()) {
      String v = matcher.group(1).trim();
      String ops = matcher.group(2);
      if ("0".equals(v)) {
        v = "BigDecimal.ZERO";
      } else if ("1".equals(v)) {
        v = "BigDecimal.ONE";
      } else if ("10".equals(v)) {
        v = "BigDecimal.TEN";
      } else if (num.matcher(v).matches()) {
        if (v.indexOf('.') < 0) {
          v = "BigDecimal.valueOf" + OPEN + v + "L" + CLOSE;
        } else {
          v = "BigDecimal.valueOf" + OPEN + v + CLOSE;
        }
      }
      if (res.size() > 0) {
        res.add(OPEN);
        res.add(v);
        if (extra != null) {
          res.add(extra);
        }
        res.add(CLOSE);
      } else {
        res.add(v);
      }
      extra = null;
      if ("*".equals(ops)) {
        res.add(".multiply");
      } else if ("/".equals(ops)) {
        res.add(".divide");
        if (!(context.ignoreDivideMode() && context.ignoreDivideScale())) {
          extra = "," + context.getDivideScale() + ",RoundingMode." + context.getDivideMode().name();
        }
      }
      expr = expr.substring(matcher.end(2)).trim();
      matcher = op.matcher(expr);
    }
    return String.join("", res);
  }

  private String markFunction(String expr) {
    Pattern fn = Pattern.compile("([a-zA-Z_][\\w._]*)\\s*\\(");
    List<String> res = new ArrayList<>();
    Matcher matcher = fn.matcher(expr);
    while (matcher.find()) {
      res.add(expr.substring(0, matcher.end(1)));
      res.add(OPEN);
      res.add("(");
      expr = markClose(expr.substring(matcher.end()));
      matcher = fn.matcher(expr);
    }
    res.add(expr);
    return String.join("", res).replaceAll("\\s*,\\s*", "),(");
  }

  private String markClose(String expr) {
    int count = 0;
    for (int i = 0; i < expr.length(); i++) {
      switch (expr.charAt(i)) {
        case '(':
          count++;
          break;
        case ')':
          if (count == 0) {
            return expr.substring(0, i) + ")" + CLOSE + expr.substring(i + 1);
          }
          count--;
          break;
      }
    }
    throw new IllegalArgumentException("incorrect parentheses");
  }

  private String restoreFunction(String expr) {
    return expr.replaceAll("BigDecimal.newOf", "new BigDecimal")
            .replaceAll(OPEN, "(")
            .replaceAll(CLOSE, ")");
  }

  public T newInstance() {
    try {
      return compile().newInstance();
    } catch (Exception e) {
      throw new BindingException(e);
    }
  }

  public static <T> T newInstance(Class<T> clazz) {
    return new CalculatorFactory<T>(clazz).newInstance();
  }
}

package com.linkedin.parseq.lambda;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertTrue;

class BaseTest {

  /**
   * Check if we're running on Java 15+ where lambda instrumentation is disabled.
   * On Java 15+, Unsafe.defineAnonymousClass was removed, so we can't instrument lambdas.
   */
  static boolean isInstrumentationDisabled() {
    int javaVersion = getJavaVersion();
    return javaVersion >= 15;
  }

  private static int getJavaVersion() {
    String version = System.getProperty("java.version");
    if (version.startsWith("1.")) {
      // Java 8 or earlier: 1.8.0_xxx
      return Integer.parseInt(version.substring(2, 3));
    } else {
      // Java 9+: 9.0.x, 10.0.x, 11.0.x, etc.
      int dotIndex = version.indexOf(".");
      if (dotIndex > 0) {
        return Integer.parseInt(version.substring(0, dotIndex));
      }
      // Handle versions like "21" without dots
      int dashIndex = version.indexOf("-");
      if (dashIndex > 0) {
        return Integer.parseInt(version.substring(0, dashIndex));
      }
      return Integer.parseInt(version);
    }
  }

  static String staticFunction(String s) {
    return s;
  }

  static String staticCallable() {
    return "";
  }

  static void staticConsumer(String s) {
  }

  String function(String s) {
    return s;
  }

  String callable() {
    return "";
  }

  void consumer(String s) {
  }

  static BaseTest staticField = new TestMethodInv();

  BaseTest noParamMethod() {
    return new BaseTest();
  }

  static BaseTest noParamStaticMethod() {
    return staticField;
  }

  BaseTest paramMethod(long x, String y) {
    return this;
  }

  static BaseTest paramStaticMethod(long x, String y) {
    return staticField;
  }

  String functionTwo(String s1, String s2) {
    return s1 + s2;
  }

  void consumerTwo(String s1, String s2) {
  }

  ASMBasedTaskDescriptor _asmBasedTaskDescriptor = new ASMBasedTaskDescriptor();

  Optional<String> getDescriptionForFunction(Function<String, String> f) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(f.getClass().getName());
  }

  Optional<String> getDescriptionForBiFunction(BiFunction<String, String, String> f) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(f.getClass().getName());
  }

  Optional<String> getDescriptionForCallable(Callable<String> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  Optional<String> getDescriptionForCallableInteger(Callable<Integer> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  Optional<String> getDescriptionForConsumer(Consumer<String> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  Optional<String> getDescriptionForBiConsumer(BiConsumer<String, String> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  /**
   * Helper method that handles both the presence check and assertion.
   * On Java 15+, instrumentation is disabled, so we skip the detailed assertions.
   */
  void assertDescriptionMatches(Optional<String> description, String inferredFunction,
                                String callerMethodName, String callerClassName) {
    if (isInstrumentationDisabled()) {
      // On Java 15+, instrumentation is disabled, so description will be empty
      // This is expected behavior - just return without failing
      return;
    }
    assertTrue(description.isPresent(), "Expected description to be present");
    assertNameMatch(inferredFunction, callerMethodName, callerClassName, description.get());
  }

  /**
   * Helper method that handles both the presence check and assertion with line number.
   * On Java 15+, instrumentation is disabled, so we skip the detailed assertions.
   */
  void assertDescriptionMatches(Optional<String> description, String inferredFunction,
                                String callerMethodName, String callerClassName, int lineNumber) {
    if (isInstrumentationDisabled()) {
      // On Java 15+, instrumentation is disabled, so description will be empty
      // This is expected behavior - just return without failing
      return;
    }
    assertTrue(description.isPresent(), "Expected description to be present");
    assertNameMatch(inferredFunction, callerMethodName, callerClassName, lineNumber, description.get());
  }

  void assertNameMatch(String inferredFunction, String callerMethodName, String callerClassName,
                                  String lambdaClassDescription) {
    // On Java 15+, instrumentation is disabled, so we can't verify detailed lambda descriptions
    if (isInstrumentationDisabled()) {
      // Just verify we got some description (even if it's the simple class name)
      assertTrue(lambdaClassDescription != null && !lambdaClassDescription.isEmpty(),
          "Expected non-empty description on Java 15+");
      return;
    }

    if (inferredFunction.isEmpty()) {
      if (callerMethodName.isEmpty()) {
        Pattern p = Pattern.compile(callerClassName + ":\\d+");
        Matcher m = p.matcher(lambdaClassDescription);
        assertTrue(m.matches());
      } else {
        Pattern p = Pattern.compile(callerMethodName + "\\(" + callerClassName + ":\\d+\\)");
        Matcher m = p.matcher(lambdaClassDescription);
        assertTrue(m.matches());
      }
    } else {
      Pattern p = Pattern.compile(Pattern.quote(inferredFunction) + " "
          + callerMethodName + "\\(" + callerClassName+ ":\\d+\\)");
      Matcher m = p.matcher(lambdaClassDescription);
      assertTrue(m.matches());
    }
  }

  void assertNameMatch(String inferredFunction, String callerMethodName, String callerClassName, int lineNumber,
      String lambdaClassDescription) {
    // On Java 15+, instrumentation is disabled, so we can't verify detailed lambda descriptions
    if (isInstrumentationDisabled()) {
      // Just verify we got some description (even if it's the simple class name)
      assertTrue(lambdaClassDescription != null && !lambdaClassDescription.isEmpty(),
          "Expected non-empty description on Java 15+");
      return;
    }

    if (inferredFunction.isEmpty()) {
      assertTrue(lambdaClassDescription.equalsIgnoreCase(callerMethodName + "(" + callerClassName + ":" + lineNumber + ")"));
    } else {
      assertTrue(lambdaClassDescription.equals(inferredFunction + " "
          + callerMethodName + "(" + callerClassName+ ":" + lineNumber + ")"));
    }
  }
}

package com.linkedin.parseq.lambda;

import static org.testng.Assert.assertTrue;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import org.testng.annotations.Test;

public class TestUnrecognizedLambda extends BaseTest {

  private static final String CLASSNAME = TestUnrecognizedLambda.class.getSimpleName();

  private Optional<String> getDescriptionForVoidCallable(Callable<Void> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  private Optional<String> getDescriptionForIntCallable(Callable<Integer> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  private Optional<String> getDescriptionForStringPredicate(Predicate<String> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  @Test
  public void testReturnExpression() {
    Optional<String> description = getDescriptionForCallable(() -> {return "";});
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("", "testReturnExpression", CLASSNAME, description.get());
  }

  @Test
  public void testReturnIntegerExpression() {
    Optional<String> description = getDescriptionForCallableInteger(() -> 0);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("", "testReturnIntegerExpression", CLASSNAME, description.get());
  }

  @Test
  public void testExpressions() {
    Optional<String> description = getDescriptionForVoidCallable(() -> {
      int a = 5;
      int b = 10;
      int c = a + b;
      return null;
    });
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("", "testExpressions", CLASSNAME, description.get());
  }

  @Test
  public void testOperations() {
    MathOperation multiplication = (int a, int b) -> { return a * b; };
    Optional<String> description = getDescriptionForIntCallable(() -> {return this.operate(5, 3, multiplication);});;
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("operate(_,_,_)", "testOperations", CLASSNAME, description.get());
  }

  @Test
  public void testStream() {
//    example of how streams are used with lambda
//    List<String> list = Arrays.asList("a1", "a2", "b1", "c2", "c1");
//    list.stream()
//        .filter(p -> p.startsWith("c"))
//        .map(s -> s.toUpperCase())
//        .sorted()
//        .forEach(System.out::println);

    Optional<String> predicateDescription = getDescriptionForStringPredicate(p -> p.startsWith("c"));
    if (!isInstrumentationDisabled()) assertTrue(predicateDescription.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("startsWith(_)", "testStream", CLASSNAME, predicateDescription.get());

    Optional<String> mapDescription = getDescriptionForFunction(s -> s.toUpperCase());
    if (!isInstrumentationDisabled()) assertTrue(mapDescription.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("toUpperCase()", "testStream", CLASSNAME, mapDescription.get());

    Optional<String> foreachDescription = getDescriptionForConsumer(System.out::println);
    if (!isInstrumentationDisabled()) assertTrue(foreachDescription.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::println", "testStream", CLASSNAME, foreachDescription.get());
  }

  interface MathOperation {
    int operation(int a, int b);
  }

  private int operate(int a, int b, MathOperation mathOperation){
    return mathOperation.operation(a, b);
  }

  @Test
  public void testBlockOfCodeInInvocation() {
    Optional<String> codeBlockDescription = getDescriptionForFunction(str -> {
      return (str.length() > 0) ? str.trim() : str;
    });
    if (!isInstrumentationDisabled()) assertTrue(codeBlockDescription.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("", "testBlockOfCodeInInvocation", CLASSNAME, codeBlockDescription.get());
  }

  @Test
  public void testParamMethodCallableMultipleLineCode() {
    Optional<String> description = getDescriptionForCallable(() ->
        paramMethod(Long.MAX_VALUE, "")
            .callable()
    );
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("callable()", "testParamMethodCallableMultipleLineCode", CLASSNAME, description.get());
  }

  @Test
  public void testStringConcatenationWithMethodCalls() {
    Optional<String> description = getDescriptionForCallable(() ->
        "hello".toUpperCase() + " " + System.getProperty("user.name")
    );
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("", "testStringConcatenationWithMethodCalls", CLASSNAME, description.get());
  }

  @Test
  public void testStringConcatenation() {
    Optional<String> description = getDescriptionForCallable(() ->
        "hello" + " " + "world"
    );
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("", "testStringConcatenation", CLASSNAME, description.get());
  }

  @Test
  public void testNestedCallbackLambdas() throws Exception {
    Callable<Optional<String>> descriptionProvider = () -> getDescriptionForCallable(() -> "hello");
    Optional<String> description = descriptionProvider.call();
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("", "testNestedCallbackLambdas", CLASSNAME, description.get());
  }

  private class Nest implements Callable<Optional<String>> {
    private final Callable<Optional<String>> _callable;
    public Nest(Callable<Optional<String>> callable) {
      _callable = callable;
    }
    @Override
    public Optional<String> call() throws Exception {
      return _callable.call();
    }
  }

  @Test
  public void testDeeplyNestedCallbackLambdas() throws Exception {
    Callable<Optional<String>> c = new Nest(() -> {
        return getDescriptionForCallable(() -> "hello");
    });
    Optional<String> description = c.call();
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("", "", CLASSNAME, description.get());
  }

}

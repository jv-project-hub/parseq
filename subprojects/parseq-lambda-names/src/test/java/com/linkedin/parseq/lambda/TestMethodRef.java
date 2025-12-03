package com.linkedin.parseq.lambda;

import java.util.Optional;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


public class TestMethodRef extends BaseTest {

  private static final String CLASSNAME = TestMethodRef.class.getSimpleName();

  TestMethodRef field = this;

  @Test
  public void testFunctionOnThis() {
    Optional<String> description = getDescriptionForFunction(this::function);
    assertDescriptionMatches(description, "::function", "testFunctionOnThis", CLASSNAME);
  }

  TestMethodRef getTestMethodRef() {
    return new TestMethodRef();
  }

  @Test
  public void testFunctionOnThisChained() {
    Optional<String> description = getTestMethodRef().getDescriptionForFunction(this::function);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::function", "testFunctionOnThisChained", CLASSNAME, description.get());
  }


  @Test
  public void testCallableOnThis() {
    Optional<String> description = getDescriptionForCallable(this::callable);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::callable", "testCallableOnThis", CLASSNAME, description.get());
  }

  @Test
  public void testConsumerOnThis() {
    Optional<String> description = getDescriptionForConsumer(this::consumer);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::consumer", "testConsumerOnThis", CLASSNAME, description.get());
  }

  @Test
  public void testFunctionOnStaticField() {
    Optional<String> description = getDescriptionForFunction(staticField::function);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::function", "testFunctionOnStaticField", CLASSNAME, description.get());
  }

  @Test
  public void testCallableOnStaticField() {
    Optional<String> description = getDescriptionForCallable(staticField::callable);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::callable", "testCallableOnStaticField", CLASSNAME, description.get());
  }

  @Test
  public void testConsumerOnStaticField() {
    Optional<String> description = getDescriptionForConsumer(staticField::consumer);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::consumer", "testConsumerOnStaticField", CLASSNAME, description.get());
  }

  @Test
  public void testFunctionOnField() {
    Optional<String> description = getDescriptionForFunction(field::function);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::function", "testFunctionOnField", CLASSNAME, description.get());
  }

  @Test
  public void testCallableOnField() {
    Optional<String> description = getDescriptionForCallable(field::callable);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::callable", "testCallableOnField", CLASSNAME, description.get());
  }

  @Test
  public void testConsumerOnField() {
    Optional<String> description = getDescriptionForConsumer(field::consumer);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::consumer", "testConsumerOnField", CLASSNAME, description.get());
  }

  @Test
  public void testFunctionWithTwoParams() {
    Optional<String> description = getDescriptionForBiFunction(this::functionTwo);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::functionTwo", "testFunctionWithTwoParams", CLASSNAME, description.get());
  }

  @Test
  public void testConsumerWithTwoParams() {
    Optional<String> description = getDescriptionForBiConsumer(this::consumerTwo);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::consumerTwo", "testConsumerWithTwoParams", CLASSNAME, description.get());
  }

  @Test
  public void testFunctionOnVar() {
    BaseTest localVar = noParamMethod();
    Optional<String> description = getDescriptionForFunction(localVar::function);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::function", "testFunctionOnVar", CLASSNAME, description.get());
  }

  @Test
  public void testCallableOnVar() {
    BaseTest localVar = noParamMethod();
    Optional<String> description = getDescriptionForCallable(localVar::callable);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::callable", "testCallableOnVar", CLASSNAME, description.get());
  }

  @Test
  public void testConsumerOnVar() {
    BaseTest localVar = noParamMethod();
    Optional<String> description = getDescriptionForConsumer(localVar::consumer);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::consumer", "testConsumerOnVar", CLASSNAME, description.get());
  }

  @Test
  public void testFunctionOnNoParamMethod() {
    Optional<String> description = getDescriptionForFunction(noParamMethod()::function);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::function", "testFunctionOnNoParamMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testCallableOnNoParamMethod() {
    Optional<String> description = getDescriptionForCallable(noParamMethod()::callable);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::callable", "testCallableOnNoParamMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testConsumerOnNoParamMethod() {
    Optional<String> description = getDescriptionForConsumer(noParamMethod()::consumer);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::consumer", "testConsumerOnNoParamMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testFunctionOnNoParamStaticMethod() {
    Optional<String> description = getDescriptionForFunction(noParamStaticMethod()::function);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::function", "testFunctionOnNoParamStaticMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testCallableOnNoParamStaticMethod() {
    Optional<String> description = getDescriptionForCallable(noParamStaticMethod()::callable);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::callable", "testCallableOnNoParamStaticMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testConsumerOnNoParamStaticMethod() {
    Optional<String> description = getDescriptionForConsumer(noParamStaticMethod()::consumer);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::consumer", "testConsumerOnNoParamStaticMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testFunctionOnParamStaticMethod() {
    Optional<String> description = getDescriptionForFunction(paramStaticMethod(0, "")::function);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::function", "testFunctionOnParamStaticMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testCallableOnParamStaticMethod() {
    Optional<String> description = getDescriptionForCallable(paramStaticMethod(Long.MAX_VALUE, "")::callable);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::callable", "testCallableOnParamStaticMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testConsumerOnParamStaticMethod() {
    Optional<String> description = getDescriptionForConsumer(paramStaticMethod(Long.MAX_VALUE, "")::consumer);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::consumer", "testConsumerOnParamStaticMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testFunctionOnParamMethod() {
    Optional<String> description = getDescriptionForFunction(paramMethod(0, "")::function);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::function", "testFunctionOnParamMethod", CLASSNAME, description.get());
  }

  @Test
  public void testCallableOnParamMethod() {
    Optional<String> description = getDescriptionForCallable(paramMethod(Long.MAX_VALUE, "")::callable);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::callable", "testCallableOnParamMethod", CLASSNAME, description.get());
  }

  @Test
  public void testConsumerOnParamMethod() {
    Optional<String> description = getDescriptionForConsumer(paramMethod(Long.MAX_VALUE, "")::consumer);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::consumer", "testConsumerOnParamMethod", CLASSNAME, description.get());
  }

  @Test
  public void testNewInstance() {
    Optional<String> description = getDescriptionForCallable(new String("abc")::toString);
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("::toString", "testNewInstance", CLASSNAME, description.get());
  }
}

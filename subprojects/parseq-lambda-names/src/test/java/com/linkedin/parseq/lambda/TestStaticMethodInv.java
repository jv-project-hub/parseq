package com.linkedin.parseq.lambda;

import java.util.Optional;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


public class TestStaticMethodInv extends BaseTest {

  private static final String CLASSNAME = TestStaticMethodInv.class.getSimpleName();

  @Test
  public void testStaticFunction() {
    Optional<String> description = getDescriptionForFunction(s -> BaseTest.staticFunction(s));
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("BaseTest.staticFunction(_)", "testStaticFunction", CLASSNAME, 15, description.get().toString());
  }

  @Test
  public void testStaticCallable() {
    Optional<String> description = getDescriptionForCallable(() -> BaseTest.staticCallable());
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("BaseTest.staticCallable()", "testStaticCallable", CLASSNAME, 22, description.get().toString());
  }

  @Test
  public void testStaticConsumer() {
    Optional<String> description = getDescriptionForConsumer(s
        ->
        BaseTest.staticConsumer(s));
    if (!isInstrumentationDisabled()) assertTrue(description.isPresent());
    if (!isInstrumentationDisabled()) assertNameMatch("BaseTest.staticConsumer(_)", "testStaticConsumer", CLASSNAME, 31, description.get().toString());
  }
}

# Java 17 Upgrade Log for ParseQ

This document tracks all changes made during the Java 17 upgrade of the `subprojects/parseq` module.

## Upgrade Started: 2025-12-03

---

## Phase 1: Build Tool Upgrade

### Step 1.1: Upgrade Gradle Wrapper
- **Status**: COMPLETED
- **File**: `gradle/wrapper/gradle-wrapper.properties`
- **Change**: Gradle 6.3 -> 8.5
- **Reason**: Gradle 6.3 only supports up to Java 14. Initially planned for 7.6.4, but upgraded to 8.5 because the environment uses Java 21 which requires Gradle 8.5+.
- **Error Fixed**: "Unsupported class file major version 65" - Java 21 bytecode cannot be parsed by older Gradle versions.

### Step 1.2: Update Root build.gradle
- **Status**: COMPLETED
- **File**: `build.gradle`
- **Changes**:
  - Java version: `1.8` -> `JavaVersion.VERSION_17`
  - Shadow plugin: `2.0.4` -> `8.1.1` (artifact coordinates also changed to `com.github.johnrengelman:shadow`)
  - JFrog plugin: `4.32.0` -> `5.1.14`
  - IDEA jdkName: `1.8.0_121` -> `17`
  - IDEA languageLevel: `1.8` -> `17`
  - Javadoc source: `8` -> `17`
  - Javadoc links: Java 8 docs URL -> Java 17 docs URL
  - Removed deprecated `maven` plugin (replaced by `maven-publish`)
  - Removed deprecated `uploadArchives` task reference

### Step 1.3: Update publications.gradle
- **Status**: COMPLETED
- **File**: `gradle/publications.gradle`
- **Change**: Removed deprecated `uploadArchives` task
- **Reason**: The `uploadArchives` task was part of the deprecated `maven` plugin which was removed in Gradle 8.x.

### Step 1.4: Migrate Deprecated Gradle APIs in All Subprojects
- **Status**: COMPLETED
- **Files Modified**: 13 subproject build.gradle files
- **Changes**:
  - `compile` -> `api` or `implementation`
  - `testCompile` -> `testImplementation`
  - `testRuntime` -> `testRuntimeOnly`
  - `classifier` -> `archiveClassifier`
  - `extension` -> `archiveExtension`
  - `baseName` -> `archiveBaseName`
  - `configurations.compile` -> `configurations.runtimeClasspath`
- **Reason**: These configurations were deprecated in Gradle 3.4 and removed in Gradle 8.x.

---

## Phase 2: Code Changes

### Step 2.1: Fix Class.newInstance() Deprecation
- **Status**: COMPLETED
- **File**: `subprojects/parseq/src/main/java/com/linkedin/parseq/Engine.java`
- **Line**: 262
- **Before**: `clazz.newInstance()`
- **After**: `clazz.getDeclaredConstructor().newInstance()`
- **Additional Changes**: Added catch clauses for `NoSuchMethodException` and `InvocationTargetException`
- **Reason**: `Class.newInstance()` was deprecated in Java 9 and throws undeclared checked exceptions.

---

## Phase 3: Jackson Migration (Critical Security Fix)

### Step 3.1: Update JsonTraceCodec.java
- **Status**: COMPLETED
- **File**: `subprojects/parseq/src/main/java/com/linkedin/parseq/trace/codec/json/JsonTraceCodec.java`
- **Changes**:
  - Import: `org.codehaus.jackson.*` -> `com.fasterxml.jackson.*`
  - Method: `getJsonFactory()` -> `getFactory()`
  - Method: `createJsonParser()` -> `createParser()`
  - Method: `createJsonGenerator()` -> `createGenerator()`

### Step 3.2: Update JsonTraceSerializer.java
- **Status**: COMPLETED
- **File**: `subprojects/parseq/src/main/java/com/linkedin/parseq/trace/codec/json/JsonTraceSerializer.java`
- **Change**: Import `org.codehaus.jackson.JsonGenerator` -> `com.fasterxml.jackson.core.JsonGenerator`

### Step 3.3: Update JsonTraceDeserializer.java
- **Status**: COMPLETED
- **File**: `subprojects/parseq/src/main/java/com/linkedin/parseq/trace/codec/json/JsonTraceDeserializer.java`
- **Changes**:
  - Import: `org.codehaus.jackson.JsonNode` -> `com.fasterxml.jackson.databind.JsonNode`
  - Method: `getBooleanValue()` -> `booleanValue()`
  - Method: `getIntValue()` -> `intValue()`
  - Method: `getLongValue()` -> `longValue()`
  - Method: `getTextValue()` -> `textValue()`
  - Method: `getValueAsText()` -> `asText()`

---

## Phase 4: Dependency Updates

### Dependencies Changed:
| Dependency | Old Version | New Version | Reason |
|------------|-------------|-------------|--------|
| Gradle | 6.3 | 8.5 | Java 21 support |
| Shadow Plugin | 2.0.4 | 8.1.1 | Gradle 8 compatibility |
| JFrog Plugin | 4.32.0 | 5.1.14 | Gradle 8 compatibility |
| Guava | 30.1.1-jre | 32.1.3-jre | CVE-2020-8908, CVE-2018-10237 fixes |
| Jackson | 1.8.8 (codehaus) | 2.16.1 (fasterxml) | EOL library, CVE-2019-10202, CVE-2019-10172 fixes |
| SLF4J | 1.7.25 | 2.0.9 | Modern version |
| TestNG | 6.9.9 | 7.9.0 | Java 17 support |
| JUnit Jupiter | 5.5.1 | 5.10.1 | Java 17 support |

---

## Phase 5: CI/CD Updates

### Step 5.1: Update GitHub Actions build.yml
- **Status**: COMPLETED
- **File**: `.github/workflows/build.yml`
- **Change**: Java version `8.0.312+7` -> `17`

### Step 5.2: Update GitHub Actions publish.yml
- **Status**: COMPLETED
- **File**: `.github/workflows/publish.yml`
- **Change**: Java version `8.0.312+7` -> `17`

---

## Build/Test Fixes Log

### Fix 1: Test Compilation - JUnit Import
- **Status**: COMPLETED
- **File**: `subprojects/parseq/src/test/java/com/linkedin/parseq/internal/TestCachedLoggerFactory.java`
- **Issue**: Test used `junit.framework.Assert` which is from JUnit 3/4
- **Fix**: Changed to use TestNG assertions (`org.testng.Assert`)

### Fix 2: Test Compilation - Log4j Dependencies
- **Status**: COMPLETED
- **File**: `subprojects/parseq/build.gradle`
- **Issue**: Test code uses Log4j 1.x APIs directly (Logger, Level, AppenderSkeleton)
- **Fix**: Added `reload4j` (maintained fork of Log4j 1.x) and `slf4j-reload4j` bridge for tests

### Fix 3: TestNG 7.x Parameter Injection
- **Status**: COMPLETED
- **File**: `subprojects/parseq/src/test/java/com/linkedin/parseq/AbstractTaskTest.java`
- **Issue**: TestNG 7.x tries to inject parameters into `@Test` methods
- **Fix**: Removed `@Test` annotation from parameterized method `testWithSideEffectFailure(int)`

---

## Final Validation

- **Build Status**: PASSED
- **Test Status**: PASSED (529 tests, 0 failures)
- **Command**: `./gradlew clean :parseq:build`

---

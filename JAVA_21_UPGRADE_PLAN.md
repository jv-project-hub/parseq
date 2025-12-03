# Java 21 Upgrade Plan for ParSeq

## Executive Summary

This document outlines a comprehensive plan to upgrade ParSeq from Java 8 to Java 21 (LTS). Java 21 is the latest Long-Term Support release (September 2023) and provides a stable target for modernization.

**Current State:**
- Java target: 1.8 (source and target compatibility)
- Gradle version: 6.3 (supports up to Java 14)
- Build configurations: Uses deprecated `compile`/`testCompile`

---

## Phase 1: Gradle and Build Toolchain Upgrade

### Step 1.1: Upgrade Gradle Wrapper

**Current:** Gradle 6.3
**Target:** Gradle 8.5+ (supports Java 21)

**Actions:**
1. Update `gradle/wrapper/gradle-wrapper.properties`:
   ```properties
   distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
   ```
2. Regenerate wrapper files:
   ```bash
   ./gradlew wrapper --gradle-version 8.5
   ```

**Reasoning:** Gradle 6.3 only supports up to Java 14. Gradle 8.4+ is required for full Java 21 support. The Java toolchain feature in Gradle 8.x allows building and testing against multiple JDK versions simultaneously.

### Step 1.2: Migrate Deprecated Gradle Configurations

**Actions in all `build.gradle` files:**

Replace deprecated configurations:
- `compile` → `api` or `implementation`
- `testCompile` → `testImplementation`
- `runtime` → `runtimeOnly`
- `testRuntime` → `testRuntimeOnly`

**Files requiring changes:**
- `build.gradle` (root)
- `subprojects/parseq/build.gradle`
- `subprojects/parseq-batching/build.gradle`
- `subprojects/parseq-benchmark/build.gradle`
- `subprojects/parseq-examples/build.gradle`
- `subprojects/parseq-guava-interop/build.gradle`
- `subprojects/parseq-http-client/build.gradle`
- `subprojects/parseq-lambda-names/build.gradle`
- `subprojects/parseq-restli-client/build.gradle`
- `subprojects/parseq-test-api/build.gradle`
- `subprojects/parseq-tracevis-server/build.gradle`
- `subprojects/parseq-zk-client/build.gradle`

**Reasoning:** The `compile` and `testCompile` configurations were removed in Gradle 7+. This change is mandatory for the build to function on modern Gradle versions.

### Step 1.3: Update Java Toolchain Configuration

**Changes in root `build.gradle`:**

Replace:
```groovy
sourceCompatibility = 1.8
targetCompatibility = 1.8
```

With:
```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

Update IDEA configuration:
```groovy
idea {
    project {
        jdkName = '21'
        languageLevel = '21'
    }
}
```

Update Javadoc configuration:
```groovy
javadoc {
    options.source = "21"
    options.links("https://docs.oracle.com/en/java/javase/21/docs/api/")
}
```

**Reasoning:** The toolchain feature provides better JDK management and allows CI to test against multiple Java versions. This is essential for validating compatibility during the migration.

### Step 1.4: Remove Deprecated Maven Plugin

**Changes in root `build.gradle`:**

Remove:
```groovy
apply plugin: 'maven'
```

Remove the legacy `uploadArchives` task:
```groovy
// Remove this entire block
project.tasks.uploadArchives.doFirst {
    logger.lifecycle "Cleaning local ivy repo: $rootDir/build/ivy-repo"
    delete(file("$rootDir/build/ivy-repo"))
}
```

**Reasoning:** The `maven` plugin is removed in Gradle 7+. Publishing should use `maven-publish` exclusively, which is already applied.

### Step 1.5: Update Shadow Plugin

**Current:** `com.github.jengelman.gradle.plugins:shadow:2.0.4`
**Target:** `com.github.johnrengelman.shadow:8.1.1`

**Changes in root `build.gradle`:**
```groovy
buildscript {
    dependencies {
        classpath 'com.github.johnrengelman.shadow:shadow:8.1.1'
    }
}
```

**Reasoning:** The old Shadow plugin version is incompatible with Gradle 8.x and Java 21.

---

## Phase 2: Dependency Upgrades

### Step 2.1: Jackson Migration (Critical)

**Current:** `org.codehaus.jackson:jackson-core-asl:1.8.8` and `jackson-mapper-asl:1.8.8`
**Target:** `com.fasterxml.jackson.core:jackson-databind:2.16.1`

**Changes in `subprojects/parseq/build.gradle`:**
```groovy
dependencies {
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.16.1'
    implementation 'com.fasterxml.jackson.core:jackson-core:2.16.1'
}
```

**Code Changes Required:**
- Package change: `org.codehaus.jackson` → `com.fasterxml.jackson`
- API changes in ObjectMapper, JsonParser, JsonGenerator
- Update all import statements in affected files

**Reasoning:** Jackson 1.x is end-of-life and incompatible with Java 9+. Jackson 2.x has been the standard for over a decade and fully supports Java 21.

### Step 2.2: Async HTTP Client Migration

**Current:** `com.ning:async-http-client:1.9.21`
**Target:** `org.asynchttpclient:async-http-client:2.12.3`

**Changes in `subprojects/parseq-http-client/build.gradle`:**
```groovy
dependencies {
    implementation 'org.asynchttpclient:async-http-client:2.12.3'
}
```

**Code Changes Required:**
- Package change: `com.ning.http.client` → `org.asynchttpclient`
- API changes in client configuration and request building
- Update all affected files in `parseq-http-client` module

**Reasoning:** The `com.ning` artifact is abandoned. The modern `org.asynchttpclient` artifact is actively maintained and supports Java 11+.

### Step 2.3: Jetty Upgrade

**Current:** `org.eclipse.jetty:jetty-server:9.3.0.v20150612`
**Target:** `org.eclipse.jetty:jetty-server:11.0.19` (for javax.servlet) or `12.0.x` (for jakarta.servlet)

**Changes in `subprojects/parseq-tracevis-server/build.gradle`:**
```groovy
def jettyVersion = '11.0.19'

dependencies {
    implementation "org.eclipse.jetty:jetty-server:${jettyVersion}"
    implementation "org.eclipse.jetty:jetty-servlet:${jettyVersion}"
}
```

**Code Changes Required:**
- If upgrading to Jetty 12.x: migrate from `javax.servlet` to `jakarta.servlet` namespace
- Update servlet configuration code as needed

**Reasoning:** Jetty 9.3 does not support Java 17+. Jetty 11.x supports Java 11-21, and Jetty 12.x supports Java 17+ with Jakarta EE 10.

### Step 2.4: ZooKeeper Upgrade

**Current:** `org.apache.zookeeper:zookeeper:3.4.6`
**Target:** `org.apache.zookeeper:zookeeper:3.8.3`

**Changes in `subprojects/parseq-zk-client/build.gradle`:**
```groovy
dependencies {
    implementation 'org.apache.zookeeper:zookeeper:3.8.3'
}
```

**Code Changes Required:**
- Review and update any deprecated ZooKeeper APIs
- Update watcher and ACL handling if needed

**Reasoning:** ZooKeeper 3.4.x is end-of-life and has known compatibility issues with modern JDKs. Version 3.8.x fully supports Java 21.

### Step 2.5: Test Framework Upgrades

**TestNG:**
- **Current:** 6.9.9 (some modules use 7.3.0)
- **Target:** 7.9.0

**JUnit Jupiter:**
- **Current:** 5.5.1
- **Target:** 5.10.1

**Changes across test dependencies:**
```groovy
testImplementation 'org.testng:testng:7.9.0'
testImplementation 'org.junit.jupiter:junit-jupiter-api:5.10.1'
```

**Reasoning:** Modern test frameworks have better Java 21 support and improved features.

### Step 2.6: Other Dependency Updates

| Dependency | Current | Target | Module |
|------------|---------|--------|--------|
| Guava | 30.1.1-jre | 32.1.3-jre | parseq, parseq-guava-interop |
| SLF4J | 1.7.25 | 2.0.9 | Multiple |
| ANTLR | 4.5 | 4.13.1 | parseq-restli-client |
| HdrHistogram | 2.1.8 | 2.1.12 | parseq-batching, parseq-benchmark |
| ByteBuddy | 1.14.13 | 1.14.11+ | parseq-lambda-names |
| ASM | 9.6 | 9.6+ | parseq-lambda-names |

**Reasoning:** Keeping dependencies current ensures compatibility with Java 21 and includes security fixes.

---

## Phase 3: Code Changes for JDK Compatibility

### Step 3.1: Fix `Class.newInstance()` Deprecation

**File:** `subprojects/parseq/src/main/java/com/linkedin/parseq/Engine.java` (line 262)

**Current:**
```java
return clazz.newInstance();
```

**Replace with:**
```java
try {
    return clazz.getDeclaredConstructor().newInstance();
} catch (NoSuchMethodException | InvocationTargetException e) {
    return new LIFOBiPriorityQueue();
}
```

**Reasoning:** `Class.newInstance()` is deprecated since Java 9 and throws checked exceptions that are not properly handled. The replacement uses the recommended reflection pattern.

### Step 3.2: Fix `javax.xml.bind` Usage (Removed in Java 11)

**File:** `subprojects/parseq-benchmark/src/main/java/org/HdrHistogram/Base64CompressedHistogramSerializer.java` (line 8)

**Current:**
```java
import javax.xml.bind.DatatypeConverter;
```

**Replace with:**
```java
import java.util.Base64;
```

And update usage from:
```java
DatatypeConverter.parseBase64Binary(...)
DatatypeConverter.printBase64Binary(...)
```

To:
```java
Base64.getDecoder().decode(...)
Base64.getEncoder().encodeToString(...)
```

**Reasoning:** The `javax.xml.bind` module was removed from the JDK in Java 11. `java.util.Base64` is the standard replacement available since Java 8.

### Step 3.3: Address parseq-lambda-names Module (Critical)

**Issue:** The `ASMBasedTaskDescriptor` class relies on `jdk.internal.misc.Unsafe.defineAnonymousClass()` which was removed in Java 15. It also uses `ClassInjector.UsingUnsafe` for boot classloader injection.

**Options:**

**Option A: Graceful Degradation (Recommended for Initial Upgrade)**

Add JDK version detection and disable instrumentation on unsupported JDKs:

```java
static {
    int javaVersion = Runtime.version().feature();
    if (javaVersion >= 15) {
        // Skip Unsafe-based instrumentation on Java 15+
        // Fall back to simple class name without lambda analysis
        System.out.println("INFO: ParSeq lambda-names instrumentation disabled on Java " + javaVersion);
    } else {
        // Existing instrumentation code
    }
}
```

**Option B: Modern Implementation (Higher Effort, Better Long-term)**

Redesign the module to use modern APIs:
1. Use `MethodHandles.Lookup.defineHiddenClass()` (Java 15+) instead of `Unsafe.defineAnonymousClass()`
2. Leverage ByteBuddy's built-in support for hidden classes
3. Consider instrumenting `java.lang.invoke.InnerClassLambdaMetafactory` instead

**Recommendation:** Start with Option A to unblock the upgrade, then implement Option B as a follow-up enhancement.

**Reasoning:** The current implementation is tightly coupled to internal JDK APIs that were removed in Java 15. A graceful degradation ensures the rest of ParSeq works on Java 21 while preserving functionality on older JDKs.

---

## Phase 4: Test Updates

### Step 4.1: Update Test Configurations

**Changes in root `build.gradle`:**
```groovy
subprojects {
    test {
        useTestNG()
        // Add JVM args for module access if needed
        jvmArgs += [
            '--add-opens', 'java.base/java.lang=ALL-UNNAMED',
            '--add-opens', 'java.base/java.util=ALL-UNNAMED'
        ]
    }
}
```

### Step 4.2: Fix Test Compatibility Issues

Review and update tests that may rely on:
- Java 8-specific reflection behavior
- Default charset assumptions (UTF-8 is now default in Java 18+)
- Date/time behavior changes
- Internal JDK class access

### Step 4.3: Multi-JDK CI Configuration

Add CI jobs to test on multiple JDK versions:
- Java 8 (for backward compatibility during transition, optional)
- Java 17 (previous LTS)
- Java 21 (target LTS)

**Reasoning:** Testing across multiple JDKs catches compatibility regressions early and validates the upgrade path.

---

## Phase 5: JPMS (Module System) Considerations

### Step 5.1: Initial Approach - Classpath Mode

For the initial upgrade, run ParSeq on the classpath (not as JPMS modules). This is the least disruptive approach.

### Step 5.2: Required JVM Arguments

Document and configure required `--add-opens` flags for modules that use reflection:

```
--add-opens java.base/jdk.internal.misc=ALL-UNNAMED
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.lang.invoke=ALL-UNNAMED
```

### Step 5.3: Future JPMS Support (Optional)

For full JPMS compatibility in the future:
1. Add `Automatic-Module-Name` to JAR manifests
2. Create `module-info.java` for each module
3. Replace all internal JDK API usage with supported alternatives

**Reasoning:** The module system's strong encapsulation affects reflection-heavy code like parseq-lambda-names. Starting with classpath mode and explicit `--add-opens` flags provides a working solution while deferring full JPMS adoption.

---

## Phase 6: Upgrade Execution Order

### Recommended Sequence:

1. **Gradle Upgrade** (Phase 1.1-1.5)
   - Must be done first as all other changes depend on it
   - Test build with Java 17 after completion

2. **Dependency Configuration Migration** (Phase 1.2)
   - `compile` → `implementation`/`api`
   - Required for Gradle 8.x

3. **Critical Dependency Upgrades** (Phase 2.1-2.4)
   - Jackson (blocks compilation on Java 11+)
   - javax.xml.bind fix (blocks compilation on Java 11+)
   - Other dependencies can be upgraded incrementally

4. **Code Fixes** (Phase 3)
   - `Class.newInstance()` fix
   - parseq-lambda-names graceful degradation

5. **Test Updates** (Phase 4)
   - Update test frameworks
   - Add multi-JDK CI

6. **JPMS Configuration** (Phase 5)
   - Add `--add-opens` flags as needed
   - Test with `--illegal-access=deny`

---

## Risk Assessment

| Component | Risk Level | Mitigation |
|-----------|------------|------------|
| parseq-lambda-names | **High** | Implement graceful degradation first |
| Jackson migration | **Medium** | Extensive API changes; thorough testing required |
| Async HTTP Client | **Medium** | API changes; isolated to one module |
| Gradle upgrade | **Low** | Well-documented migration path |
| Test frameworks | **Low** | Mostly version bumps |

---

## Java 21 Features to Consider (Optional Enhancements)

Java 21 introduces several features that could benefit ParSeq in future iterations:

1. **Virtual Threads (Project Loom)** - Could simplify async programming patterns
2. **Structured Concurrency (Preview)** - Better task lifecycle management
3. **Pattern Matching for switch** - Cleaner code in type-checking scenarios
4. **Record Patterns** - Simplified data extraction
5. **Sequenced Collections** - New collection interfaces

These are optional enhancements and not required for the upgrade.

---

## Validation Checklist

Before declaring the upgrade complete:

- [ ] Build succeeds on Gradle 8.5+
- [ ] All tests pass on Java 17
- [ ] All tests pass on Java 21
- [ ] No `compile`/`testCompile` configurations remain
- [ ] No `javax.xml.bind` imports remain
- [ ] No `Class.newInstance()` calls remain
- [ ] parseq-lambda-names works or degrades gracefully on Java 21
- [ ] CI runs on multiple JDK versions
- [ ] Documentation updated with new requirements

---

## Timeline Estimate

| Phase | Estimated Effort |
|-------|------------------|
| Phase 1: Gradle/Build | 2-3 days |
| Phase 2: Dependencies | 3-5 days |
| Phase 3: Code Changes | 2-3 days |
| Phase 4: Tests | 2-3 days |
| Phase 5: JPMS | 1-2 days |
| Integration & Validation | 2-3 days |
| **Total** | **12-19 days** |

---

## Appendix: Breaking Changes Summary (Java 8 → Java 21)

### Removed APIs
- `javax.xml.bind` (JAXB) - removed in Java 11
- `javax.activation` - removed in Java 11
- `java.corba` - removed in Java 11
- `Unsafe.defineAnonymousClass()` - removed in Java 15
- `Class.newInstance()` - deprecated, use `getDeclaredConstructor().newInstance()`

### Behavioral Changes
- Strong encapsulation of JDK internals (requires `--add-opens`)
- Default charset changed to UTF-8 (Java 18+)
- Stricter JAR validation
- SecurityManager deprecated for removal

### Module System Impact
- Internal packages (`sun.*`, `jdk.internal.*`) are encapsulated
- Reflection into JDK modules requires explicit opens
- Split packages between modules not allowed

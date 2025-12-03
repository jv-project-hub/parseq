# Java 21 Upgrade Execution Log

This log documents every change made during the Java 8 to Java 21 upgrade of ParSeq.

---

## Environment Information

- **Date Started:** 2025-12-03
- **Starting Branch:** master (bbf8e71)
- **Upgrade Branch:** devin/1764752039-java21-upgrade
- **Available JDKs:** Java 17 (17.0.13), Java 21 (21.0.9)
- **Starting Gradle Version:** 6.3

---

## Baseline Status (Before Upgrade)

### Pre-Upgrade Build Attempt

**Date:** 2025-12-03
**JDK:** Java 17 (attempted with JAVA_HOME set)
**Gradle:** 6.3

**Result:** FAILED - Gradle 6.3 cannot run on Java 17+

**Error:**
```
Unsupported class file major version 61
java.lang.IllegalArgumentException: Unsupported class file major version 61
```

**Analysis:** Class file version 61 = Java 17. Gradle 6.3's embedded Groovy/ASM cannot parse Java 17+ class files. This means we MUST upgrade Gradle before any other changes can be made or tested.

**Conclusion:** Original plan to migrate configurations on Gradle 6.3 is not possible. Must upgrade Gradle wrapper first.

---

## Phase 1: Gradle and Build Configuration

### Step 1.1: Upgrade Gradle Wrapper

**Date:** 2025-12-03
**Status:** COMPLETED

**Change:** Updated `gradle/wrapper/gradle-wrapper.properties` from Gradle 6.3 to 8.5

**Reasoning:** Gradle 6.3 cannot run on Java 17+. Gradle 8.5 is required for Java 21 support.

### Step 1.2: Remove Deprecated Maven Plugin

**Status:** COMPLETED

**Change:** Removed `apply plugin: 'maven'` from root `build.gradle`

**Reasoning:** The `maven` plugin was removed in Gradle 7+.

### Step 1.3: Remove Deprecated uploadArchives Task

**Status:** COMPLETED

**Changes:** Removed `uploadArchives` task from root `build.gradle` and `gradle/publications.gradle`

**Reasoning:** `uploadArchives` was deprecated in Gradle 8.x.

### Step 1.4: Update Shadow Plugin

**Status:** COMPLETED

**Change:** Updated Shadow plugin from `com.github.jengelman.gradle.plugins:shadow:2.0.4` to `com.gradleup.shadow:shadow-gradle-plugin:8.3.0`

### Step 1.5: Migrate compile/testCompile to implementation/api/testImplementation

**Status:** COMPLETED

**Changes across all subproject build.gradle files:**
- `compile` -> `api` or `implementation`
- `testCompile` -> `testImplementation`
- `classifier` -> `archiveClassifier`
- `baseName` -> `archiveBaseName`
- `main` -> `mainClass` (for JavaExec tasks)

### Step 1.6: Update Java Version Settings

**Status:** COMPLETED

**Changes:** Updated sourceCompatibility/targetCompatibility to 21, updated IDEA settings, updated Javadoc links

### Step 1.7: Update Node-Gradle Plugin

**Status:** COMPLETED

**Change:** Updated from 2.2.3 to 7.0.1 in parseq-tracevis

### Step 1.8: Fix Task Dependencies

**Status:** COMPLETED

**Changes:** Added explicit task dependencies and duplicatesStrategy to fatJar tasks

---

## Phase 2: Code Changes for JDK Compatibility

### Step 2.1: Fix javax.xml.bind.DatatypeConverter

**Status:** COMPLETED

**File:** `subprojects/parseq-benchmark/src/main/java/org/HdrHistogram/Base64CompressedHistogramSerializer.java`

**Change:** Replaced `javax.xml.bind.DatatypeConverter` with `java.util.Base64`

**Reasoning:** `javax.xml.bind` was removed from the JDK in Java 11.

### Step 2.2: Fix Class.newInstance() Deprecation

**Status:** COMPLETED

**File:** `subprojects/parseq/src/main/java/com/linkedin/parseq/Engine.java`

**Change:** Replaced `clazz.newInstance()` with `clazz.getDeclaredConstructor().newInstance()`

**Reasoning:** `Class.newInstance()` was deprecated in Java 9.

---

## Phase 3: Build Results

### Build Test (After Phase 1 & 2 Changes)

**Date:** 2025-12-03
**JDK:** Java 21 (21.0.9)
**Gradle:** 8.5
**Result:** BUILD SUCCESSFUL

---

## Remaining Work

- Upgrade test frameworks (TestNG, JUnit)
- Upgrade Jackson from 1.x to 2.x
- Upgrade async-http-client
- Upgrade Jetty
- Upgrade ZooKeeper
- Add graceful degradation to parseq-lambda-names
- Run full test suite and fix failures

---

renderdoc_jffm
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.renderdoc_jffm/com.io7m.renderdoc_jffm.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.renderdoc_jffm%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.renderdoc_jffm/com.io7m.renderdoc_jffm?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/renderdoc_jffm/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/renderdoc_jffm.svg?style=flat-square)](https://codecov.io/gh/io7m-com/renderdoc_jffm)
![Java Version](https://img.shields.io/badge/22-java?label=java&color=d4e65c)

![com.io7m.renderdoc_jffm](./src/site/resources/renderdoc_jffm.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/renderdoc_jffm/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/renderdoc_jffm/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/renderdoc_jffm/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/renderdoc_jffm/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/renderdoc_jffm/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/renderdoc_jffm/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/renderdoc_jffm/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/renderdoc_jffm/actions?query=workflow%3Amain.windows.temurin.lts)|

## renderdoc_jffm

A Java interface to the [RenderDoc API](https://renderdoc.org).

## Features

* A friendly Java interface to the RenderDoc API.
* [JPMS](https://en.wikipedia.org/wiki/Java_Platform_Module_System)-ready.
* ISC license.

## Usage

When running your application, you must allow access to the
[FFM](https://openjdk.org/jeps/454) API for the module
`com.io7m.renderdoc_jffm.core`:

```
$ java --enable-native-access=com.io7m.renderdoc_jffm.core ...
```

Run your application from RenderDoc. RenderDoc works by injecting a native
library into your application. Then, inside your application, call
`RenderDoc.open()`:

```
try (final var doc = RenderDoc.open()) {
  ...
}
```

The obtained `RenderDocType` interface is a type-safe, memory-safe interface
to the API exposed by the RenderDoc native library. The interface allows for
setting options and triggering captures.

The `open()` method will raise an `IOException` with a useful error message
if the `renderdoc` native library is not present in the current application
process.


# LocalCache for Java

[![Build](https://github.com/meshware/local-cache/actions/workflows/build.yml/badge.svg)](https://github.com/meshware/local-cache/actions/workflows/build.yml)
![License](https://img.shields.io/github/license/meshware/local-cache.svg)
[![Maven Central](https://img.shields.io/maven-central/v/io.meshware.cache/local-cache.svg?label=maven%20central)](https://search.maven.org/search?q=g:io.meshware.cache)
[![Percentage of issues still open](http://isitmaintained.com/badge/open/meshware/local-cache.svg)](http://isitmaintained.com/project/meshware/local-cache "Percentage of issues still open")

## Overview
Local cache for Java applications, enhance response speed. The underlying depends on Guava, Caffeine, OHC, etc., contains in heap and off heap support.  
To view [中文说明](./README_CN.md).

## Features
- Support for in-heap caching, such as: guava, caffeine.
- Support for off-heap caching, such as: ohc.
- Support for automatic synchronization of cached data. (Synchronized secret key mode)

## Requirements
Compile requirement: JDK 8+ and Maven 3.2.5+ .

## Getting started
There's a [README](./cache-samples/README.md) file under [cache-samples](./cache-samples) directory. We recommend referencing the sample in that directory by following the below-mentioned instructions:

### Maven dependency
```xml
<properties>
    <localCache.version>0.1.0</localCache.version>
</properties>

<dependencies>
    <dependency>
        <groupId>io.meshware.cache</groupId>
        <artifactId>cache-ihc</artifactId>
        <version>${localCache.version}</version>
    </dependency>
    <dependency>
        <groupId>io.meshware.cache</groupId>
        <artifactId>cache-ohc</artifactId>
        <version>${localCache.version}</version>
    </dependency>
    <dependency>
        <groupId>io.meshware.cache</groupId>
        <artifactId>cache-redis</artifactId>
        <version>${localCache.version}</version>
    </dependency>
</dependencies>
```

## License
LocalCache is licensed under the [Apache License 2.0](./LICENSE).

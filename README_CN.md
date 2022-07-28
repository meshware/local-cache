# 本地缓存

[![Build](https://github.com/meshware/local-cache/actions/workflows/build.yml/badge.svg)](https://github.com/meshware/local-cache/actions/workflows/build.yml)
![License](https://img.shields.io/github/license/meshware/local-cache.svg)
[![Maven Central](https://img.shields.io/maven-central/v/io.meshware.cache/local-cache.svg?label=maven%20central)](https://search.maven.org/search?q=g:io.meshware.cache)
[![Percentage of issues still open](http://isitmaintained.com/badge/open/meshware/local-cache.svg)](http://isitmaintained.com/project/meshware/local-cache "Percentage of issues still open")

## 概述
为Java应用而生的本地缓存，用于提升响应速度。这是一个整合了几个缓存`轮子`的`小板车`。依赖的`轮子`包括`Guava`, `Caffeine`, `OHC`等等，提供了`堆内缓存`与`堆外缓存`的支持。   
查看[English Introduction](./README.md)。

## 特性
- 支持堆内缓存，例如：guava，caffeine框架的特性。
- 支持堆外缓存，例如：ohc框架的特性。
- 支持`自动同步型缓存`，同步方式采用`同步值`或监听Redis消息发布订阅事件。

## 要求
编译要求: JDK 8+ and Maven 3.2.5+ .

## 快速入门
缓存示例`cache-samples`目录下有一个[README](./cache-samples/README.md)。我们建议通过遵循以下说明引用该目录中的示例：

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

## 授权协议
本地缓存项目遵循以下开源授权协议 [Apache License 2.0](./LICENSE).

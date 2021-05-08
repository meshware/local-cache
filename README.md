# LocalCache for Java

[![build](https://github.com/meshware/local-cache/actions/workflows/build.yml/badge.svg)](https://github.com/meshware/local-cache/actions/workflows/build.yml)
![License](https://img.shields.io/badge/license-Apache--2.0-green.svg)
[![Maven Central](https://img.shields.io/maven-central/v/io.meshware.cache/local-cache.svg?label=maven%20central)](https://search.maven.org/search?q=g:io.meshware.cache)

## Overview
Local cache for Java applications, enhance response speed. The underlying depends on Guava, Caffeine, OHC, etc., contains in heap and off heap support..

## Features
- Support for in-heap caching, such as: guava, caffeine.
- Support for off-heap caching, such as: ohc.
- Support for automatic synchronization of cached data. (Synchronized secret key mode)


# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

LocalCache is a Java local caching library (io.meshware.cache) providing a unified abstraction over multiple caching backends — in-heap (Caffeine), off-heap (OHC), and Redis — with automatic cross-node cache synchronization via sync keys and Redis pub/sub. Targets Java 8+. Licensed under Apache 2.0.

## Build Commands

```bash
# Full build (tests skipped by default via maven-surefire-plugin.skipTests=true)
mvn clean package

# Build with tests enabled
mvn clean package -Dmaven.surefire-plugin.skipTests=false

# Run a single test class
mvn test -pl cache-ihc -Dmaven.surefire-plugin.skipTests=false -Dtest=InHeapCacheTest

# Run a single test method
mvn test -pl cache-ihc -Dmaven.surefire-plugin.skipTests=false -Dtest=InHeapCacheTest#testPutAndGet

# Install locally
mvn clean install

# Skip tests explicitly
mvn clean install -DskipTests
```

## Module Structure

```
cache-api       → Core interfaces (Cache, LocalCache, SynchronousCache, OffHeapCache, RedisCache)
cache-ihc       → In-heap cache: Caffeine-based (AbstractCommonCache, AbstractLoadingCache, AbstractSynchronousCache)
cache-ohc       → Off-heap cache: OHC-based (AbstractOffHeapCache, StringKeyOffHeapCache + Kryo/Protostuff serializers)
cache-spring    → Spring integration: event-driven invalidation, CacheManager, AbstractCacheSyncManager
cache-redis     → Redis backend: RedisClient, RedisAutoConfigure, pub/sub message publisher/subscriber
cache-samples   → Demo Spring Boot application
```

**Dependency flow**: cache-api ← cache-ihc, cache-ohc ← cache-spring ← cache-redis. Samples depends on cache-redis.

## Architecture

### Interface Hierarchy (cache-api)
- `Cache` → `LocalCache<K,V>` → `SynchronousCache<K,V,X,Y>` (sync-key invalidation)
- `Cache` → `LocalCache<K,V>` → `OffHeapCache<K,V>` (off-heap with TTL + memory tracking)
- `Cache` → `RedisCache` (Redis operations + cross-node notify)

### Template Method Pattern
All cache implementations follow the template method pattern. Subclasses configure the cache by overriding:
- `initConfig()` / `init()` — cache setup
- `getValueWhenExpired(key)` — data loading on cache miss (LoadingCache variants)
- `getValueWhenRefresh(key, oldValue)` — data loading on TTL refresh (optional)
- `initBuilder()` / serializer methods — OHC configuration

### Sync-Key Invalidation
`SynchronousCache` entries have an associated "sync value" (version/timestamp). On read, the cache compares the current sync value against the stored one. If changed, the entry is invalidated and reloaded. Uses `ReadWriteLock` for thread safety. The sync values are stored in a separate `DefaultSyncValueLocalCache` (max 10000 entries, 8h TTL).

### Cross-Node Invalidation (Redis Pub/Sub)
1. `RedisClient.setWithNotify()` sets a value and publishes a `CacheDiscardEntity` (cacheName + deleteKey) as JSON to a Redis channel
2. `RedisMessageSubscriber` receives the message, deserializes it, publishes a Spring `CacheDiscardEvent`
3. Cache beans with `@EventListener` on `CacheDiscardEvent` check if the event targets their cache name and evict the key

### Spring Boot Auto-Configuration
`RedisAutoConfigure` (registered via `META-INF/spring.factories`) conditionally creates all Redis beans when `cache.adapter.enable=true` and `cache.adapter.cache-type=redis`.

**Configuration properties** (prefix `cache.adapter`):
- `enable` (boolean, default true) — enable cache adapter
- `cache-type` (string) — backend type (currently "redis")
- `discard-channel` (string) — Redis pub/sub channel for invalidation messages

## Key Technical Details

- **Thread safety**: AbstractCommonCache and AbstractLoadingCache use volatile + double-checked locking for lazy cache initialization
- **Serialization**: OHC module supports Kryo, Protostuff, String, Integer serializers under `cache-ohc/.../serializer/`
- **Version**: Uses Maven CI-friendly `${revision}` property (currently `1.0.0-SNAPSHOT`), flattened via flatten-maven-plugin
- **Test framework**: JUnit 4 (primary), JUnit 5 (cache-ohc tests), Mockito (cache-spring tests). Note: cache-ohc test files are in the default package (no package declaration)

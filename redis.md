## 캐시
* 자주 사용하는 데이터를 빠르게 제공하기 위해 미리 저장해두는 기술을 말한다.
* 캐시를 사용하면 데이터베이스의 부하를 분담할 수 있다.

---

## 캐시 전략
* write-through
  * 데이터를 데이터베이스와 캐시에 저장한다.
* write-around
  * 데이터를 데이터베이스에만 저장하고 캐시는 이후 데이터를 조회할 때 로드한다.
* write-back
  * 데이터를 캐시에 먼저 저장하고 비동기 방식으로 데이터베이스에 저장한다.
  * 데이터 손실 위험이 있지만 응답속도가 빠르다.
* cache-aside
  * 캐시에서 데이터를 조회하고 없으면 데이터베이스에서 조회 후 캐시에 저장한다.

---

## 캐시 스탬피드 현상
* 동시에 많은 요청이 캐시에 접근하면 캐시가 만료될 때 데이터베이스에 과부하가 걸리는 현상을 말한다.
### 해결방법
* 캐시 만료시간을 랜덤하게 설정한다.
* 캐시가 만료되기 전에 백그라운드에서 미리 갱신한다.

--- 

## redis 명령어
### String
```shell
    # 저장
    SET {key} {value}
    
    # 조회
    GET {key}
    
    # 삭제
    DEL {key}
    
    # 만료시간 설정
    EXPIRE {key} {time}
```
### Set
```shell
    # 저장
    SADD {key} {value}
    
    # 조회
    SMEMBERS {key}
    
    # 삭제
    SREM {key}
```
### Sorted Set
```shell
    # 저장
    ZADD {key} {score} {value}
    
    # 조회
    ZRANGE {key} {start} {end}
    
    # 삭제
    ZREM {key} {value}
    
    # 오름차순 순위 조회
    ZRANK {key} {value}
```
### Hash
```shell
    # 저장
    HSET {key} {field} {value}
    
    # 조회
    HGET {key} {field}
    
    # 삭제
    HDEL {key} {field}
    
    # 모든 field 조회
    HGETALL {key}
```

---

## 프로젝트에 캐시 적용
* 수정보다는 조회가 많이 일어나고 조회시 시간이 오래 걸리는 작업에 캐시를 적용하는 것이 좋다.
  콘서트 정보 조회시 캐시를 적용했다. 또한 캐시 스탬피드 현상을 피하기 위해서 랜덤한 ttl을 설정하도록 했다.
```kotlin
    val cacheKey = cache.key
    
    redisTemplate.opsForValue().get(cacheKey)?.let { json ->
        return json
    }
    
    val result = joinPoint.proceed()
    
    val ttl = cache.defaultTtl + Random.nextLong(0, 60)
    redisTemplate.opsForValue().set(cacheKey, result, Duration.ofSeconds(ttl))
```
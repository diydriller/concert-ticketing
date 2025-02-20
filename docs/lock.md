### 동시성 문제
* 여러 명의 유저가 같은 콘서트 일정의 같은 좌석을 동시에 임시 예약할 때 동시성 문제가 발생한다.
* 한명의 유저가 임시 예약을 하고 나서 결제를 동시에 2번 예약할 때 동시성 문제가 발생한다.

### pessimistic lock
* pessimistic lock은 자원에 lock을 걸어서 다른 트랜잭션의 접근을 막는다.
  * 안정성이 높지만 동시성이 낮다.
```kotlin
@Repository
interface ProductRepository : JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByIdAndLock(id: Long): Product?
}
```

### optimistic lock
* optimistic lock은 자원에 lock을 걸지 않고 version과 같은 별도의 컬럼을 추가하고 
  해당 컬럼의 변경여부로 트랜잭션의 충돌을 감지한다.
  * 동시성이 높지만 충돌 발생시 재시도가 필요하다.

### distributed lock
* 노드가 여러개인 분산환경에서 자원에 lock을 걸어서 다른 프로세스의 접근을 막는다.
  * 네트워크 지연이 발생할 수 있다.

### redis를 사용한 distributed lock 
* key가 없을 때만 설정 가능한 redis의 setNx 명령어를 사용해서 lock을 구현할 수 있다.
  * 라이브러리를 사용하는 방식보다 속도가 빠르다.
* redisson 라이브러리는 redis를 기반으로 한 distributed lock을 제공한다.
  * 구현이 쉽다.
  * lock이 해제되면 대기중인 프로세스가 이벤트를 받아서 lock을 획득한다.
```kotlin
fun acquireLock(lockKey: String, waitTime: Long, leaseTime: Long): Boolean {
    val lock = redissonClient.getLock(lockKey)
    return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS)
}

fun releaseLock(lockKey: String) {
    val lock = redissonClient.getLock(lockKey)
    if (lock.isHeldByCurrentThread) {
        lock.unlock()
    }
}
```

### 동시성 문제 해결
* DB가 1대인 상황에서는 pessimistic lock을 사용해서 동시성 문제를 해결할 수 있다.
```kotlin
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM Seat s WHERE s.id = :seatId ")
fun findSeatByIdForUpdate(seatId: String): Seat?
```

* 분산환경에서는 pessimistic lock이나 optimistic lock 만 가지고 데이터 정합성을 보장할 수 없고 
  distributed lock을 사용해야한다. redission을 사용해서 distributed lock을 구현헀다.
```kotlin
val key = REDISSON_LOCK_PREFIX + distributedLock.key
val rLock: RLock = redissonClient.getLock(key)

try {
    val available =
        rLock.tryLock(distributedLock.waitTime, distributedLock.leaseTime, distributedLock.timeUnit)

    if (!available) {
        throw ConflictException(BaseResponseStatus.UNAVAILABLE)
    }
    // 로직 실행
} catch (e: InterruptedException) {
    log.error(e.message)
    throw InterruptedException()
} finally {
    try {
        if (rLock.isHeldByCurrentThread) {
            rLock.unlock()
        }
    } catch (e: IllegalMonitorStateException) {
        log.error(e.message)
    }
}
```

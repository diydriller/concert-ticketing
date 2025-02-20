# 개요

# Kafka 개념
## 역사
kafka 프로젝트는 **LinkedIn** 회사 내부에서 기존 메시지 큐보다 확장성이 뛰어나고 
안정적으로 데이터를 스트리밍 할 수 있는 시스템을 필요로 하며 시작되었다.
kafka 프로젝트는 2011년 Apache Software Foundation에 기부되면서 
**Apache Kafka** 프로젝트로 전환되면서 오픈소스화되었고 빠르게 발전되었다.
이후 2014년 LinkedIn 엔지니어들이 모여 Confluent라는 회사를 만들면서 
기존 kafka에 다양한 기능을 추가해서 **Confluent Kafka**를 제공하게 되었다.

## 정의
대용량 데이터를 빠르게 처리할 수 있는 분산 메시지 스트리밍 플랫폼이다.

## 특징
* 높은 처리량을 가진다. 
* 수평 확장이 가능하다.
* 내구성이 좋다. 

# kafka 연동 테스트
## docker compose로 구동하기
카프카 브로커 3대와 zookeeper 1대로 클러스터를 구성했다.
```shell
services:
  kafka1:
    container_name: kafka1
    image: confluentinc/cp-kafka:7.7.1
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka1:29092,EXTERNAL://host.docker.internal:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
    depends_on:
      - zookeeper

  kafka2:
    container_name: kafka2
    image: confluentinc/cp-kafka:7.7.1
    ports:
      - "9093:9093"
    environment:
      KAFKA_BROKER_ID: 2
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka2:29093,EXTERNAL://host.docker.internal:9093
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
    depends_on:
      - zookeeper

  kafka3:
    container_name: kafka3
    image: confluentinc/cp-kafka:7.7.1
    ports:
      - "9094:9094"
    environment:
      KAFKA_BROKER_ID: 3
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka3:29094,EXTERNAL://host.docker.internal:9094
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
    depends_on:
      - zookeeper

  zookeeper:
    container_name: zookeeper
    image: confluentinc/cp-zookeeper:7.7.1
    environment:
      ZOOKEEPER_SERVER_ID: 1
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      ZOOKEEPER_INIT_LIMIT: 5
      ZOOKEEPER_SYNC_LIMIT: 2
    ports:
      - "22181:2181"
```
## 카프카 명령어
docker container로 접속해서 카프카 명령어를 실행시킬 수 있다.
```shell
# 토픽 조회
kafka-topics --bootstrap-server {broker url} --list

# 토픽 상세 조회
kafka-topics --bootstrap-server {broker url} --describe --topic {topic 이름}

# 토픽 삭제
kafka-topics --bootstrap-server {broker url} --delete --topic {topic 이름}

# 메시지 발행 
kafka-console-producer --bootstrap-server {broker url} --topic {topic 이름}

# 메시지 소비
kafka-console-consumer --bootstrap-server {broker url} --topic {topic 이름} --from-beginning
```
## 카프카 연동
예약을 하면 reserve란 토픽으로 메시지를 발행하도록 했다.
토픽을 조회해보면 reserve란 토픽을 볼 수 있고 
해당 토픽을 구독하면 메시지를 확인할 수 있다.
![토픽 조회](https://github.com/user-attachments/assets/708fbe4a-9ee3-4d2b-9cc0-7cceed2ddb4d)
![메시지 소비](https://github.com/user-attachments/assets/22b710fe-7e09-4ce5-95ea-c74bd267cc9b)
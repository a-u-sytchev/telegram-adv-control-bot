# Telegram бот для контроля эффективности рекламы канала 

## Описание
Бот помогает контролировать эффективность рекламных кампаний в Telegram, общаясь со внешними сервисами аналитики через шину событий (kafka). Пример реализации такого внешнего сервиса https://github.com/a-u-sytchev/telegram-adv-control-bot-telegram-adv-subscribers-auditor. \
\
Контроль происходит по принципу регистрации новых подписчиков, с учетом рекламной кампании (имени ссылки), в рамках которой пришел подписчик. \
\
Затем производится периодиеская ревизия, которая показывает отток подписчиков по той или иной рекламной кампании. \
\
Подразумевается, что внешний сервис аналитики будет расчитывать срок жизни подписчиков по каждой рекламной кампании, что позволит оценить качество подписчиков. \
\
Через качество подписчиков можно оценить эффективность рекламных кампаний, чтоб определить в какие следует инвестировать повторно, а какие следует отбросить. \
\
*Важно понимать, что срок жизни подписчиков не является единственным критерием эффективности. Существуют и другие важные метрики (например, активность).* 

### Функциональность
* Обработка запросов подписки на канал:
  * Автоматическое подтвержение запроса подписки на канал или отказ, если запрос поступает от бота.
  * В случае подтверждения, генерируется событие `subscriberApproved` в шину, которое должно быть обработано сервисами аналитики.
* Обработка запросов на ревизию подписчиков:
  * Отслеживается событие `subscribersRevision`, в шине, которое содержит список подписчиков для проверки.
  * Выполняется проверка текущего статуса всех подписчиков из списка.
  * Затем в событиии `subscribersRevisionComplete` возвращается список подписчиков с актуальным статусом.

### Структуры событий
Событие `subscriberApproved`:
```kotlin
data class ApproveJoinRequestEvent(
  val timestamp: LocalDateTime,
  val channelId: String,
  val subscriberId: Long,
  val inviteLinkName: String
)
```

Событие `subscribersRevision`:
```kotlin
data class SubscribersRevisionEvent(
    val subscribers: List<Subscriber>
)

data class Subscriber(
  val id: Long,
  val subscriberId: Long,
  val channelId: String,
  var status: ChatMemberStatus
)

enum class ChatMemberStatus {
  member,
  left,
  administrator,
  creator,
  restricted,
  kicked
}
```

Событие `subscribersRevisionComplete`:
```kotlin
data class SubscribersRevisionCompleteEvent(
  val subscribers: List<Subscriber>
)
```

## Подготовка к запуску
### Необходимое програмное обеспечение
Для запуска необходимо следующее програмное обеспечение:
* JVM 21 или более поздней версии.
* Kotlin 1.9 или более поздней версии.
* Spring boot 3.3 или более поздней версии.

### DEV-окружение в контейнерах Docker
Можно создать файл docker-compose.yml с DEV-окружением. Поместите в файл следующее содержимое:
```yaml
version: "3.3"

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    hostname: zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:latest
    hostname: kafka
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://localhost:9092'
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    depends_on:
      - zookeeper
```
Для запуска тестового окружения на машине должны быть установлены Docker и Docker Compose.
Если они не установлены, то инструкцию по их установке можно найти здесь:
* Docker https://docs.docker.com/engine/install/
* Docker Compose https://docs.docker.com/compose/install/

Dev-окружение запускается из директории с compose-файлом командой:
```shell
docker compose up -d
```
Проверить состояние контейнеров можно командой:
```shell
docker ps
```
У контейнеров должен быть статус `Up`. 
```shell
CONTAINER ID   IMAGE                              COMMAND                  CREATED         STATUS         PORTS                                                           NAMES
9b145b610488   confluentinc/cp-kafka:latest       "/etc/confluent/dock…"   5 seconds ago   Up 4 seconds   0.0.0.0:9092->9092/tcp, :::9092->9092/tcp                       kafka
8e0e6c030d1e   confluentinc/cp-zookeeper:latest   "/etc/confluent/dock…"   5 seconds ago   Up 4 seconds   2888/tcp, 0.0.0.0:2181->2181/tcp, :::2181->2181/tcp, 3888/tcp   zookeeper
```

Остановка и уничтожение контейнеров:
```shell
docker compose down
```

## Запуск приложения
### Минимальная конфигурация Spring boot
В кофигурационном файле обязательно должны быть указаны параметры, указанные ниже.
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: telegrambot
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer


telegram:
  bot:
    token: # токен бота. Можно посмотреть в Telegram через меню бота @BotFather
    webhook: # URL, который будет зарегистрирован как webhook для получения обновлений от Telegram
```
В параметрах `kafka` указаны значения, которые можно использовать для запуска DEV-окружения.
Параметры для бота нужно указать самостоятельно.
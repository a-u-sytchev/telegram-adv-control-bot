# Telegram бот для контроля эффективности рекламы канала 

## Описание
Бот помогает контролировать эффективность рекламных кампаний в Telegram, общаясь со внешними сервисами аналитики через шину событий (kafka). Пример реализации такого внешнего сервиса https://github.com/a-u-sytchev/telegram-adv-subscribers-auditor.

### Алгоритм работы бота
Пользователь Telegram кликает по пригласителной ссылке рекламной кампании и запрашивает подписку на канал. Бот одобрает запрос и генерирует в шину событие `subscriberApproved`:
```kotlin
data class ApproveJoinRequestEvent(
  val timestamp: LocalDateTime,
  val channelId: String,
  val subscriberId: Long,
  val inviteLinkName: String,
  val languageCode: String
)
```
Сторонний сервис аналитики обрабатывает это событие, регистрируя в своей базе данных нового подписчика. \
\
Далее сторонний сервис аналитипи с произвольной периодичностью запрашивает аудит перечня подписчиков, генерируя в шину событие `subscribersRevision`:
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

Бот обрабатывает это событие, запрашивая через API Telegram актуальные статусы подписчиков, после чего возвращает этот же перечень подписчиков, только уже с актуальными статусами, отправляя в шину событие `subscribersRevisionComplete`:
```kotlin
data class SubscribersRevisionCompleteEvent(
  val subscribers: List<Subscriber>
)
```
Сторонний сервис аналитики видит данное событие и размечает в своей базе данных подписчиков, покинувших канал.

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

Бот работает через webhook. Если планируется запускать его локально, тогда необходимо установить ngrok. \
Инструкция по установке https://ngrok.com/download

Далее необходимо запустить ngrok из терминала, указав порт, на котором запущен бот, вместо `${bot_port}`

```shell
ngrok http ${bot_port}
```

```shell
ngrok                                                                                                   (Ctrl+C to quit)
                                                                                                                        
New guides https://ngrok.com/docs/guides/site-to-site-apis/                                                             
                                                                                                                        
Session Status                online                                                                                    
Account                       Alexey (Plan: Free)                                                                       
Update                        update available (version 3.12.0, Ctrl-U to update)                                       
Version                       3.10.0                                                                                    
Region                        Europe (eu)                                                                               
Latency                       42ms                                                                                      
Web Interface                 http://127.0.0.1:4040                                                                     
Forwarding                    https://5732-5-228-114-146.ngrok-free.app -> http://localhost:3000                        
                                                                                                                        
Connections                   ttl     opn     rt1     rt5     p50     p90                                               
                              0       0       0.00    0.00    0.00    0.00                                              
                                                                                         

```

Значение `Forwarding` (https://5732-5-228-114-146.ngrok-free.app) необходимо будет указать в конфигурации приложения, в поле `telegram.bot.webhook`.


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
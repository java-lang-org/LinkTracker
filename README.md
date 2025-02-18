# Link Tracker

<!-- этот файл можно и нужно менять -->

Проект сделан в рамках курса Академия Бэкенда.

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 23` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot
* Scrapper

<!-- Для работы требуется БД `PostgreSQL`. Присутствует опциональная зависимость на `Kafka`. -->
<!-- -->
<!-- Для дополнительной справки: [HELP.md](./HELP.md) -->

# Build

Зависит от:
- java 23
- mvn 3.9
- docker compose 2.32

```bash
git clone https://github.com/central-university-dev/java-vihlancevk.git
cd java-vihlancevk
mvn clean install
docker compose up -d
```


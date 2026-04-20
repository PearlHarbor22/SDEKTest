# Task Time Tracker API 
Backend REST API для учета рабочего времени сотрудников по определенной задаче
Реализован в рамках тестового задания на Java + SpringBoot

---

## Контекст 

Сервис предназначается для:

- управления задачами
- учета времени сотрудников
- фиксации потраченного времени на задачу
- получения информации о затраченном времени сотрудником на задачу

Реализованы следующие возможности:

### Task 
- Создание задачи
- Получение задачи по id
- Изменение статуса задачи(NEW / IN_PROGRESS / DONE)

### TimeRecord
- Создание записи о затраченном времени
- Получение записей сотрудника за период реализовано по логике пересечения интервалов:

Запись возвращается, если ее временной интервал хотя бы частично пересекается с запрошенным периодом

### Дополнительно
- JWT аутентификация(Bearer Token)
- Валидация входных данных(Bean Validation)
- Глобальная обработка ошибок (@RestControllerAdvice)
- Swagger (SpringDoc OpenApi)
- Покрытие тестами (unit, integration, controller)

---

## Стек проекта
- Java 17
- Spring Boot 3
- Maven
- PostgreSQL (Docker)
- MyBatis
- Lombok
- Swagger (SpringDoc OpenAPI)
- JUnit 5
- Mockito
- Testcontainers
- JWT (Bearer Authentication)

---

## Запуск базы данных
```bash
docker compose up -d
```
### Параметры:
- database: task_tracker
- user: postgres
- password: postgres
- port: 5432

---

## Запуск приложения
- mvn clean install
- mvn spring-boot:run или запуск SdekTimeTrackerApplication в IDE

---

## Swagger
http://localhost:8080/swagger-ui/index.html

## Аутентификация
Получение токена: POST /api/auth/login

Содержимое запроса:

```json
{
  "username": "admin",
  "password": "admin"
}
```
### Использование:
![img.png](images/img.png)

Нажатие в SwaggerUI кнопки Authorize и вставка самого токена в поле 'Value'

---

## Тестирование
- mvn test

Реализованы:
- unit тесты
- интеграционные тесты(Testcontainers + PostgreSQL)
- controller тесты(MockMVC)

Для анализа покрытия используется плагин JaCoCo(примерно 89 процентов покрытия):

![img_1.png](images/img_1.png)

После mvn clean test отчет доступен: /target/site/jacoco/index.html

## Примеры и проверка

Проверку можно осуществить в Swagger вручную или использовать коллекцию с примерами из Postman

### Основные эндпоинты

- POST /api/auth/login
- POST /api/tasks
- GET /api/tasks/{id}
- PATCH /api/tasks/{id}/status
- POST /api/time-records
- GET /api/time-records

## Postman коллекция

В коллекции настроено автоматическое использование JWT токена:
- Выполните запрос "POST /api/auth/login" 
- Токен из ответа автоматически сохраняется в переменную
- Во всех последующих запросах токен автоматически подставляется в заголовок

Коллекция находится в корне проекта:

[Postman collection](Time%20Tracker%20API.postman_collection.json)

Импорт:  File -> Import -> загружаем файл в Postman

![img_2.png](images/img_2.png)

## Использование ИИ

В процессе разработки использованы языковые модели. Подробности описаны в отдельном файле

[AI usage description](ai_usage.md)


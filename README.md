# java-explore-with-me
Explore-with-me является афишей, в которой пользователи могут предлагать мероприятия и собирать компанию для участия в нем.

Приложение реализовано в виде блока двух сервисов: основной сервис содержит всё необходимое для работы продукта и сервис статистики, который хранит количество просмотров и позволяет делать различные выборки для анализа работы приложения.

API основного сервиса разделено на три части: публичную, закрытую (только для зарегистрированных пользователей) и административную (только для администраторов сервиса).
1) Зарегистрированные пользователи могут создавать и редактировать мероприятия, подавать заявки на участия в мероприятиях других пользователей, подтверждать или отклонять заявки на участие в собственном мероприятии. Имеется возможность гибкой настройки создаваемого мероприятия (категория, аннотация, описание, лимит участников, требуется ли оплата за участие, требуется ли модерация участия и т.д.). Мероприятия имеют статус (ожидает модерацию / отклонен / опубликован), влияющий на взаимодействие с ним пользователей.
2) Администраторы могут создавать, удалять или изменять категории мероприятий, создавать, редактировать и изменять подборки мероприятий, модерировать и изменять мероприятия (при этом модерировать можно только неопубликованные мероприятия). Модерирование можно проводить как отдельно для каждого события, так и сразу для нескольких событий. В случае отклонения администратором события есть возможность добавления комментария с причиной отказа, который могут видить только администратор и создатель события. Так же администратору доступно управление пользователями (добавление, активация, просмотр и удаление).

Для всех видов пользователей (не авторизованный, авторизованный и администратор) имеется возможность гибкого поиска событий и подборок событий, при этом учитывается роль пользователя, статус событий, набор входящих параметров поиска (по id, дате события, частично содержащимуся тексу, т.д.). Предусмотрен постраничный вывод (пагианция). Все поисковые запросы сохраняются в сервисе статистики.

При разработке применен следующий стек: Java 11, Spring Boot, Sprting Data, Hibernate, Querydsl, JUnit, Lombock. Использована БД PostgreSQL и H2(для тестов).
Для удобного развертывания приложения реализован docker-compose.yml, содержащий все необходимые параметры.


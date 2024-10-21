## Задание 2
1. Сгенерировать набор данных по сущностям, можно написать генератор самостоятельно, можно использовать сервисы, аналогичные https://www.mockaroo.com/

2. Реализовать 2 консьюмера, слушающих топики t1_demo_accounts и t1_demo_transactions. При получении сообщения сервис должен сохранять счет и транзакцию в бд.

3. Изменить аспект @TimeLimitExceedLog:

В конфигурационном файле добавить параметр, устанавливающий время в миллисекундах. Если время работы метода превышает заданное значение, аспект должен отправлять сообщение в топик Kafka (t1_demo_metric_trace) c информацией о времени работы, имени метода и параметрах метода, если таковые имеются.


## Задание 1
1. Дополнить модель проекта:

Добавить сущность Account (Счет) со следующими атрибутами:

- id клиента
- Дебетовый счет или кредитный (enum)
- Баланс

Сущность Transaction дополнить следующими атрибутами:

- id счета

Cущность DataSourceErrorLog:

- Текст стектрейса исключения
- Сообщение
- Сигнатура метода

Сущность TimeLimitExceedLog:

- Сигнатура метода
- Значение времени исполнения

2. Сгенерировать набор данных по сущностям, можно написать генератор самостоятельно, можно использовать сервисы, аналогичные https://www.mockaroo.com/

3. Разработать два аспекта:
   
   3.1 Аспект, логирующий сообщения об исключениях в проекте путем создания в БД новой записи DataSourceErrorLog
   
   3.2 Аспект, логирующий сообщения в TimeLimitExceedLog, если аннотированный аспектом метод работает дольше, чем установленное значение в конфигурационном параметре

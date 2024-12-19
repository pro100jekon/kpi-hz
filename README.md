### Створення простору ключів з найпростішою стратегією реплікації
```cassandraql
CREATE KEYSPACE IF NOT EXISTS ecommerce
    WITH REPLICATION = {
        'class' : 'SimpleStrategy',
        'replication_factor' : 1
        };
```
### Створення таблиць items та orders
```cassandraql
CREATE TABLE IF NOT EXISTS ecommerce.items
(
    id            uuid primary key,
    name          text,
    manufacturer  text,
    price         decimal,
    category      text,
    uncategorized map<text, text>
);
CREATE TABLE IF NOT EXISTS ecommerce.orders
(
    id           uuid primary key,
    items        map<uuid, int>,
    total_amount decimal
);
```
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
    order_id     uuid,
    item_id      uuid,
    quantity     int,
    total_amount decimal,
    primary key ( order_id, item_id )
);
```
### Створення індексу для характеристик товару, що не підлягають загальній схемі
```cassandraql
CREATE INDEX IF NOT EXISTS unusual ON ecommerce.items (ENTRIES (uncategorized));
```
### Додавання товарів
```cassandraql
INSERT INTO ecommerce.items (id, name, manufacturer, price, category, uncategorized)
VALUES (3582e18d-3f0f-466a-8df1-2a5ede72eec0, 'Samsung Galaxy S24 Ultra', 'Samsung', 1200, 'mobile_phone',
        {'width': '79mm', 'height': '162.3mm', 'depth': '8.6mm', 'cpu': 'Snapdragon 8 Gen 3', 'can_call': 'true'});
INSERT INTO ecommerce.items (id, name, manufacturer, price, category, uncategorized)
VALUES (8eb3667f-4c15-4738-8fd1-3b7fca0eee1f, 'Samsung Galaxy Tab S9 Ultra', 'Samsung', 1700, 'tablet',
        {'dimensions': '254.3mm x 165.8mm x 5.9mm', 'with_stylus': 'true'});
INSERT INTO ecommerce.items (id, name, manufacturer, price, category, uncategorized)
VALUES (fba322dd-6fcf-4f35-b340-adcbe81fedd8, 'iPhone 15 Pro Max', 'Apple Inc.', 1350, 'mobile_phone',
        {'Apple': 'yes', 'iOS': 'так', 'Android': 'ні'});
INSERT INTO ecommerce.items (id, name, manufacturer, price, category, uncategorized)
VALUES (d4893a31-7b37-4d59-8998-0b179d89bb62, 'Lenovo Legion Pro 5 16` Intel Edition', 'Lenovo', 2500, 'laptop',
        {'Windows': 'true', 'nVidia': 'yes', 'cpu_freq_max': '5.8GHz', 'cpu_freq_min': '2.2GHz', 'weight': '2200g'});
```
### Додавання замовлень
```cassandraql
INSERT INTO ecommerce.orders (order_id, item_id, quantity, total_amount)
VALUES (74bd7e98-e2b2-4a7c-99c4-b552033feb38, 3582e18d-3f0f-466a-8df1-2a5ede72eec0, 2, 2200);
INSERT INTO ecommerce.orders (order_id, item_id, quantity, total_amount)
VALUES (74bd7e98-e2b2-4a7c-99c4-b552033feb38, d4893a31-7b37-4d59-8998-0b179d89bb62, 1, 2500);
INSERT INTO ecommerce.orders (order_id, item_id, quantity, total_amount)
VALUES (6d0890cd-417b-4a32-9f17-7dbb9cd818d3, fba322dd-6fcf-4f35-b340-adcbe81fedd8, 5, 6750);
```
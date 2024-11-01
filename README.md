# Використання
```shell
# ./mvnw clean package
mvn clean package
docker compose build --no-cache
docker compose up -d
```
Після запуску команд збереться кластер з 10-ти інстансів одного і того ж застосунку, та бази даних Postgres, та автоматично запуститься процес бенчмарку всіх чотирьох варіацій задачі.

# Результати
На скриншоті показаний типовий час виконання кожного типу завданої задачі
![img.png](img/img.png)
Середній час по всім інстансам по кожній задачі:

Lost update:

| Stat           |     Value |
|:---------------|----------:|
| Avg            |  138141.5 |
| Count          |        10 |
| Median         |    138452 |
| Geometric Mean | 138139.74 |
| Largest        |    138775 |
| Smallest       |    136582 |
| Range          |      2193 |

In-place update:

| Stat           |     Value |
|:---------------|----------:|
| Avg            |  132765.2 |
| Count          |        10 |
| Median         |    132691 |
| Geometric Mean | 132764.10 |
| Largest        |    133469 |
| Smallest       |    131656 |
| Range          |      1813 |

Row-level locking:

| Stat           |     Value |
|:---------------|----------:|
| Avg            |  195811.1 |
| Count          |        10 |
| Median         |    196974 |
| Geometric Mean | 195780.77 |
| Largest        |    198698 |
| Smallest       |    187344 |
| Range          |     11354 |

Optimistic concurrency control:

| Stat           |      Value |
|:---------------|-----------:|
| Avg            |    1016637 |
| Count          |         10 |
| Median         |  1021544.5 |
| Geometric Mean | 1016577.63 |
| Largest        |    1025296 |
| Smallest       |     987339 |
| Range          |      37957 |

В результаті оновлень колонка version отримала таке саме значення, як і counter
![img.png](img/img2.png)

# Висновки
Найповільніший метод синхронізації - optimistic concurrency control.

Найшвидший - In-place update, але з невеликим відривом від методу lost-update

| Algorithm          | Time consumed, x(In-place) |
|:-------------------|---------------------------:|
| In-place           |                         1x |
| Lost update        |                      1.04x |
| Row-level locking  |                      1.47x |
| Optimistic control |                      7.66x |
CREATE
  (samsung:ITEM {name:'Samsung Galaxy S24 Ultra', price:1500}),
  (iphone:ITEM {name:'Apple IPhone 16 Pro Max', price:1800}),
  (tablet:ITEM {name:'Samsung Galaxy Tab S9', price:2200}),
  (laptop1:ITEM {name:'Dell XPS 15', price:2400}),
  (laptop2:ITEM {name:'Apple MacBook Pro 14-inch', price:2600}),
  (watch1:ITEM {name:'Apple Watch Series 9', price:600}),
  (watch2:ITEM {name:'Samsung Galaxy Watch 7', price:500}),
  (headphones1:ITEM {name:'Sony WH-1000XM5', price:400}),
  (headphones2:ITEM {name:'Bose Noise Cancelling Headphones 700', price:380}),
  (console1:ITEM {name:'Sony PlayStation 5', price:700}),
  (console2:ITEM {name:'Microsoft Xbox Series X', price:750}),
  (camera1:ITEM {name:'Canon EOS R5', price:3500}),
  (camera2:ITEM {name:'Sony Alpha 7 IV', price:3200}),

  (john:CUSTOMER {name:'John Doe'}),
  (john)-[:VIEWED]->(samsung),
  (john)-[:VIEWED]->(console1),
  (john)-[:VIEWED]->(console2),
  (john)-[:VIEWED]->(watch1),
  (john)-[:VIEWED]->(watch2),
  (john)-[:VIEWED]->(camera1),
  (john)-[:VIEWED]->(camera2),
  (john)-[:VIEWED]->(headphones1),
  (john)-[:VIEWED]->(headphones2),

  (trevor:CUSTOMER {name:'Trevor Phillips'}),
  (trevor)-[:VIEWED]->(iphone),
  (trevor)-[:VIEWED]->(laptop2),
  (trevor)-[:VIEWED]->(laptop1),
  (trevor)-[:VIEWED]->(camera2),
  (trevor)-[:VIEWED]->(camera1),
  (trevor)-[:VIEWED]->(tablet),
  (trevor)-[:VIEWED]->(console1),
  (trevor)-[:VIEWED]->(console2),
  (trevor)-[:VIEWED]->(samsung),
  (trevor)-[:VIEWED]->(watch1),
  (trevor)-[:VIEWED]->(watch2),
  (trevor)-[:VIEWED]->(headphones1),
  (trevor)-[:VIEWED]->(headphones1),

  (niko:CUSTOMER {name:'Niko Bellic'}),
  (niko)-[:VIEWED]->(headphones1),
  (niko)-[:VIEWED]->(headphones2),
  (niko)-[:VIEWED]->(samsung),
  (niko)-[:VIEWED]->(iphone),
  (niko)-[:VIEWED]->(tablet),
  (niko)-[:VIEWED]->(watch2),
  (niko)-[:VIEWED]->(console1),
  (niko)-[:VIEWED]->(console2),

  (order1:ORDER {orderId:'466621'}),
  (order1)-[:INCLUDES]->(samsung),
  (order1)-[:INCLUDES]->(console1),
  (order1)-[:INCLUDES]->(watch1),
  (order1)-[:INCLUDES]->(camera1),
  (order1)-[:INCLUDES]->(headphones1),
  (john)-[:ORDERED]->(order1),

  (order2:ORDER {orderId:'466622'}),
  (order2)-[:INCLUDES]->(iphone),
  (order2)-[:INCLUDES]->(laptop2),
  (order2)-[:INCLUDES]->(laptop1),
  (order2)-[:INCLUDES]->(camera2),
  (trevor)-[:ORDERED]->(order2),

  (order3:ORDER {orderId:'466623'}),
  (order3)-[:INCLUDES]->(headphones2),
  (order3)-[:INCLUDES]->(samsung),
  (order3)-[:INCLUDES]->(tablet),
  (order3)-[:INCLUDES]->(watch2),
  (order3)-[:INCLUDES]->(console1),
  (order3)-[:INCLUDES]->(console2),
  (niko)-[:ORDERED]->(order3),

  (order4:ORDER {orderId:'466624'}),
  (order4)-[:INCLUDES]->(tablet),
  (order4)-[:INCLUDES]->(iphone),
  (order4)-[:INCLUDES]->(samsung),
  (order4)-[:INCLUDES]->(samsung),
  (order4)-[:INCLUDES]->(watch2),
  (trevor)-[:ORDERED]->(order4);

// Знайти Items які входять в конкретний Order
MATCH (item:ITEM) <-[:INCLUDES]- (order:ORDER) WHERE order.orderId='466624' RETURN item.name

// Підрахувати вартість конкретного Order
MATCH (order:ORDER{orderId:'466621'}) -[:INCLUDES]-> (item:ITEM) RETURN sum(item.price) AS total

// Знайти всі Orders конкретного Customer + Знайти всі Items куплені конкретним Customer (через Order)
MATCH (item:ITEM) <-[:INCLUDES]- (order:ORDER) <-[:ORDERED]- (customer:CUSTOMER {name:'Trevor Phillips'}) RETURN order, item, customer

// Знайти кількість Items куплені конкретним Customer (через Order)
MATCH (customer:CUSTOMER{name:'Trevor Phillips'}) -[:ORDERED]-> (order:ORDER) -[:INCLUDES]-> (item:ITEM) RETURN item.name AS item, count(item) AS times

// Знайти для Customer на яку суму він придбав товарів (через Order)
MATCH (customer:CUSTOMER{name:'Trevor Phillips'}) -[:ORDERED]-> (order:ORDER) -[:INCLUDES]-> (item:ITEM) RETURN sum(item.price) AS total

// Знайти скільки разів кожен товар був придбаний, відсортувати за цим значенням
MATCH (item:ITEM) <-[:INCLUDES]- (order:ORDER) RETURN item.name AS Name, count(item) AS Times ORDER BY Times DESC

// Знайти всі Items переглянуті (view) конкретним Customer
MATCH (item:ITEM) <-[:VIEWED]- (customer:CUSTOMER{name:'John Doe'}) RETURN item, customer
MATCH (item:ITEM),(customer:CUSTOMER{name:'John Doe'}) RETURN item, customer

// Знайти інші Items що купувались разом з конкретним Item (тобто всі Items що входять до Order-s разом з даними Item)
MATCH (item:ITEM) <-[:INCLUDES]- (order:ORDER) MATCH (ordered:ITEM{name:'Samsung Galaxy S24 Ultra'}) <-[:INCLUDES]- (order) RETURN item

// Знайти Customers які купили даний конкретний Item
MATCH (item:ITEM{name:"Sony PlayStation 5"}) <-[:INCLUDES]- (order:ORDER) <-[:ORDERED]- (customer:CUSTOMER) RETURN customer

// Знайти для певного Customer(а) товари, які він переглядав, але не купив
MATCH (customer:CUSTOMER{name:"Niko Bellic"}) -[:VIEWED]-> (item:ITEM), (customer) -[:ORDERED]-> (order:ORDER) WHERE NOT (item) <-[:INCLUDES]- (order) <-[:ORDERED]- (customer) RETURN item

// Як і в попередніх завданнях, для якогось одного обраного Item додайте поле з кількістю його лайків.
MATCH (item:ITEM{name:"Samsung Galaxy S24 Ultra"}) SET item.likes=0 RETURN item
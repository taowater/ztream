# Ztream

<p align="center">
	<a target="_blank" href="https://central.sonatype.com/artifact/io.github.taowater/ztream">
		<img src="https://img.shields.io/maven-central/v/io.github.taowater/ztream.svg?label=Maven%20Central" />
	</a>
	<a target="_blank" href="https://github.com/taowater/ztream/blob/main/LICENSE">
		<img src="https://img.shields.io/github/license/taowater/ztream.svg" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8+-green.svg" />
	</a>
	<a target="_blank" href='https://github.com/taowater/ztream'>
		<img src="https://img.shields.io/github/stars/taowater/ztream.svg?style=social" alt="github star"/>
	</a>
</p>

Java Stream 增强库。在标准 Stream 之上补充常用 API，减少样板代码，对空值更友好。

## 目录

- [快速开始](#-快速开始)
- [为什么需要 Ztream](#-为什么需要-ztream)
- [创建流](#1-创建流)
- [收集](#2-收集)
- [过滤](#3-过滤)
- [映射与转换](#4-映射与转换)
- [分组与映射](#5-分组与映射)
- [EntryZtream（键值对流）](#6-entryztream键值对流)
- [排序](#7-排序)
- [去重](#8-去重)
- [字符串拼接](#9-字符串拼接)
- [数字与统计](#10-数字与统计)
- [带索引的遍历](#11-带索引的遍历)
- [判断](#12-判断)
- [其他实用操作](#13-其他实用操作)

---

## 🍊 快速开始

### Maven

```xml
<dependency>
    <groupId>io.github.taowater</groupId>
    <artifactId>ztream</artifactId>
    <version>LATEST</version>
</dependency>
```

### 最小示例

```java
List<String> names = Ztream.of(users)
        .nonNull()
        .eq(User::getStatus, 1)
        .asc(User::getAge)
        .toList(User::getName);
```

---

## 💬 为什么需要 Ztream

Java 8 Stream 让集合处理更清晰，但业务开发里仍常遇到：

- 必要 API 缺失（按属性去重、带索引遍历等）
- `groupingBy` / `toMap` 等写法冗长，样板代码多
- 标准分组、映射对空 key / 空 value 不够友好

高版本 JDK 已做了部分改善，JDK 22 还引入了 Gatherer。即便如此，Ztream 中一批常用 API 在 Java 8 及日常 CRUD 场景里仍然很实用。

本库本质是对 Stream 的封装与增强，追求更好写、更好读。

对流操作的一些库，在我冲浪参考的过程中，还有这些：

- [streamex](https://github.com/amaembo/streamex)
- [JDFrame](https://github.com/burukeYou/JDFrame)
- [seq](https://github.com/wolray/seq)
- [stream-query](https://github.com/dromara/stream-query)

倘拙库可用，用之；如其不才，君可自取。

---

## 1. 创建流

```java
// 空流
Ztream<String> empty = Ztream.empty();

// 可变参数
Ztream<Integer> nums = Ztream.of(1, 2, 3);

// Collection / Iterable
Ztream<Student> students = Ztream.of(list);
Ztream<Student> parallel = Ztream.of(list, true);

// 标准 Stream
Ztream<Student> fromStream = Ztream.of(list.stream());

// Map → EntryZtream
EntryZtream<String, Integer> entries = Ztream.of(map);

// 数字范围 [start, end)
Ztream<Integer> range = Ztream.range(0, 10);
// 数字范围 [start, end]
Ztream<Integer> closed = Ztream.range(0, 10, true);
Ztream<Long> longRange = Ztream.range(0L, 100L);

// 按分隔符拆分字符串
Ztream<String> parts = Ztream.split("a,b,c");
Ztream<Integer> ids = Ztream.split("1,2,3", ",", Integer::valueOf);
```

---

## 2. 收集

```java
// Stream
List<String> names = Stream.of(users).map(User::getName).collect(Collectors.toList());

// Ztream（Java 16+ 标准 Stream 已有 toList）
List<String> names = Ztream.of(users).map(User::getName).toList();

// 直接收集属性
List<String> names = Ztream.of(users).collect(User::getName);
List<String> names = Ztream.of(users).toList(User::getName);

// 收集为 Set
Set<String> nameSet = Ztream.of(users).toSet(User::getName);

// 指定集合类型
Set<String> treeNames = Ztream.of(users).collect(User::getName, TreeSet::new);
List<User> linked = Ztream.of(users).collect(LinkedList::new);

// 转换类型后收集
List<Teacher> teachers = Ztream.of(users).toList(Teacher.class);
```

---

## 3. 过滤

风格接近 MyBatis-Plus：属性比较、空值判断、条件拼接、`and` / `or` 组合。

### 基础比较

```java
List<Student> result = Ztream.of(list)
        .eq(Student::getAge, 34)              // 等于
        .gt(Student::getAge, 18)              // 大于
        .ge(Student::getScore, 60)            // 大于等于
        .lt(Student::getScore, 100)           // 小于
        .le(Student::getAge, 60)              // 小于等于
        .between(Student::getAge, 18, 60)     // 区间（含边界）
        .in(Student::getName, "刘备", "关羽")
        .notIn(Student::getName, "张飞")
        .toList();
```

### 空值与字符串

```java
Ztream.of(list)
        .nonNull()                    // 元素非 null
        .nonNull(Student::getName)    // 属性非 null
        .isNull(Student::getRemark)
        .nonEmpty(Student::getName)
        .nonBlank(Student::getName)
        .isBlank(Student::getName)
        .rightLike(Student::getName, "小")  // 前缀匹配 startsWith
        .like(Student::getName, "猪")       // 模糊匹配
        .isTrue(Student::getFlag)
        .isFalse(Student::getFlag);
```

### 条件生效（动态拼接）

多数过滤方法支持首参 `boolean condition`：条件为假时跳过该过滤。

```java
boolean checkScore = true;

List<Student> result = Ztream.of(list)
        .eq(Student::getAge, 34)
        .like(Student::getName, "朱")
        .ge(Student::getScore, 60)
        .lt(checkScore, Student::getScore, 80)  // 仅当 checkScore 为 true 时生效
        .toList();
```

### 组合条件 query / and / or

通过 `query` 进入条件包装器，支持 `and` / `or` 组合：

```java
List<Student> result = Ztream.of(list)
        .query(w -> w
                .eq(Student::getAge, 2)
                .in(Student::getName, "刘备", "关羽")
                .or(w2 -> w2
                        .like(Student::getName, "刘")
                        .gt(Student::getScore, 90)
                )
                .and(w2 -> w2
                        .ge(Student::getAge, 18)
                )
        )
        .toList();
```

简单条件也可直接在流上链式调用（均为 and 关系）：

```java
Ztream.of(list)
        .eq(Student::getAge, 34)
        .like(Student::getName, "朱")
        .ge(Student::getScore, 60)
        .toList();
```

---

## 4. 映射与转换

```java
// 普通 map
Ztream.of(list).map(Student::getName);

// 带索引的 map
Ztream.of(list).map((e, i) -> i + ":" + e.getName());

// 同时映射多个属性并展开为一个流
Ztream.of(list).map(Student::getName, Student::getSex);

// 展开集合属性（flatMap 简写）
Ztream.of(orders).flat(Order::getItems);

// 类型强转
Ztream.of(list).cast(Student.class);

// 类型转换（基于 ConvertUtil）
Ztream.of(list).convert(Teacher.class);
Ztream.of(list).convert(Teacher.class, (src, dest) -> {
    // 可对转换后的对象做额外赋值
});
```

---

## 5. 分组与映射

### toMap（允许 null key / null value）

```java
// Stream：key 或 value 为 null 时常抛异常
Map<String, Integer> map = Stream.of(list)
        .collect(Collectors.toMap(Student::getName, Student::getAge));

// Ztream：允许 null
Map<String, Integer> map = Ztream.of(list).toMap(Student::getName, Student::getAge);

// 仅指定 key，value 为元素本身
Map<String, Student> studentMap = Ztream.of(list).toMap(Student::getName);

// 指定 Map 实现
Map<String, Integer> linked = Ztream.of(list)
        .toMap(Student::getName, Student::getAge, LinkedHashMap::new);
```

> 重复 key 时保留先出现的值（不抛异常）。

### groupBy

```java
// Stream
Map<String, List<Student>> group = Stream.of(list)
        .collect(Collectors.groupingBy(Student::getName));

// Ztream
Map<String, List<Student>> group = Ztream.of(list).groupBy(Student::getName);

// 分组并映射值
Map<String, List<Integer>> ages = Ztream.of(list)
        .groupBy(Student::getName, Student::getAge);

// 指定 Map / 下游收集器
Map<String, Set<Integer>> ageSets = Ztream.of(list)
        .groupBy(Student::getName, Student::getAge, HashMap::new, Collectors.toSet());

// 两层分组
Map<String, Map<String, List<Student>>> bilayer =
        Ztream.of(list).groupBilayer(Student::getSex, Student::getClassName);
```

### 中间操作：hash / group → EntryZtream

```java
// hash：按 key 去重后转为键值对流
Ztream.of(list)
        .hash(Student::getName, Student::getAge)
        .flip()
        .forEachKeyValue((age, name) -> {
            // doSomething
        });

// group：分组中间操作，继续链式处理
Ztream.of(list)
        .group(Student::getName, Student::getAge, Collectors.toSet())
        .filterKey(k -> k != null)
        .toMap();
```

---

## 6. EntryZtream（键值对流）

由 `Map`、`hash`、`group` 得到，面向 key / value 操作。

```java
EntryZtream<String, Integer> stream = Ztream.of(map);

// 取出 key / value 流
Ztream<String> keys = stream.keys();
Ztream<Integer> values = stream.values();

// 过滤
stream.nonNullKey()
      .nonNullValue()
      .filterKey(k -> k.startsWith("小"))
      .filterValue(v -> v > 18)
      .filter((k, v) -> k.length() > 1);

// 映射
stream.mapKey(String::toUpperCase)
      .mapValue(v -> v + 1)
      .mapKeyValue((k, v) -> k + "=" + v);

// 键值互换
EntryZtream<Integer, String> flipped = stream.flip();

// 遍历
stream.forEachKeyValue((k, v) -> { /* ... */ });
stream.peekKeyValue((k, v) -> { /* ... */ });

// 收集回 Map
Map<String, Integer> result = stream.toMap();
Map<String, Integer> linked = stream.toMap(LinkedHashMap::new);
```

---

## 7. 排序

```java
// 按属性升序 / 降序
List<Student> asc = Ztream.of(list).asc(Student::getAge).toList();
List<Student> desc = Ztream.of(list).desc(Student::getAge).toList();

// 元素自身可比时
Ztream.of(1, 3, 2).asc().toList();   // [1, 2, 3]
Ztream.of(1, 3, 2).desc().toList();  // [3, 2, 1]

// 多字段排序：先按年龄降序（null 靠前），年龄相同按姓名升序（null 靠后）
List<Student> sorted = Ztream.of(list)
        .sort(r -> r
                .desc(flag, Student::getAge, true)   // flag 为 true 时才应用
                .asc(Student::getName, false)
                .nullFirst(false)                    // 控制元素本身为 null 时的位置
        )
        .toList();

// 洗牌 / 反转
Ztream.of(list).shuffle().toList();
Ztream.of(list).reverse().toList();
```

---

## 8. 去重

```java
// 标准去重
Ztream.of(1, 2, 2, 3).distinct().toList();  // [1, 2, 3]

// 按属性去重（保留首次出现）
Ztream.of(list).distinct(Student::getName).toList();

// 条件去重
Ztream.of(list).distinct(needDistinct, Student::getName);
```

---

## 9. 字符串拼接

```java
// 默认逗号分隔 → "刘备,关羽,张飞"
String str = Ztream.of(list).join(Student::getName);

// 指定分隔符 → "刘备#关羽#张飞"
String str = Ztream.of(list).join(Student::getName, "#");

// 元素本身拼接
String str = Ztream.of("a", "b", "c").join();           // "a,b,c"
String str = Ztream.of("a", "b", "c").join("#");        // "a#b#c"
String str = Ztream.of("a", "b", "c").join("#", "[", "]"); // "[a#b#c]"
```

---

## 10. 数字与统计

属性需为 `Number` / `Comparable` 类型。

```java
// 最值（属性值）
Integer maxAge = Ztream.of(list).max(Student::getAge);
Integer minAge = Ztream.of(list).min(Student::getAge);
Integer maxAge = Ztream.of(list).max(Student::getAge, 0);  // 默认值

// 求和 / 平均
Integer sum = Ztream.of(list).sum(Student::getAge);
Integer sum = Ztream.of(list).sum(Student::getAge, 0);
Integer avg = Ztream.of(list).avg(Student::getAge, 0);

// 取最值对应的元素（返回 Any）
Student oldest = Ztream.of(list).maxBy(Student::getAge).orElse(null);
Student youngest = Ztream.of(list).minBy(Student::getAge, false).orElse(null);

// 元素自身可比时
Any<Integer> max = Ztream.of(1, 2, 3, null).max();
Any<Integer> min = Ztream.of(1, 2, 3, null).min(false);  // null 不视为最小
```

---

## 11. 带索引的遍历

```java
// forEach：元素 + 下标
Ztream.of(list).forEach((e, i) -> {
    System.out.println(i + " -> " + e);
});

// peek：中间操作
Ztream.of(list)
        .peek((e, i) -> System.out.println(i))
        .map(Student::getName)
        .toList();

// map：带索引映射
Ztream.of(list).map((e, i) -> new Ranked(i, e)).toList();

// 查找第一个满足条件的下标（未找到返回 -1）
int idx = Ztream.of(list).firstIdx(e -> "刘备".equals(e.getName()));
```

---

## 12. 判断

```java
// 是否存在重复元素 / 重复属性
boolean repeat = Ztream.of(list).hadRepeat();
boolean nameRepeat = Ztream.of(list).hadRepeat(Student::getName);

// 按属性匹配
boolean anyNull = Ztream.of(list).anyMatch(Student::getName, Objects::isNull);
boolean allNull = Ztream.of(list).allMatch(Student::getName, Objects::isNull);
boolean noneNull = Ztream.of(list).noneMatch(Student::getName, Objects::isNull);

// 是否为空流
boolean empty = Ztream.of(list).nonNull().isEmpty();
boolean notEmpty = Ztream.of(list).isNotEmpty();
```

---

## 13. 其他实用操作

```java
// 追加元素
Ztream.of(1, 2, 3).append(4, 5, 6).toList();
Ztream.of(list).append(otherList).toList();

// 分页（页码从 1 开始）
List<Student> page = Ztream.of(list).page(2, 10).toList();

// 取首 / 尾 / 随机（返回 Any，便于继续链式取值）
Any<Student> first = Ztream.of(list).first();
Student firstOne = Ztream.of(list).getFirst();
Any<Student> last = Ztream.of(list).last();
Student lastOne = Ztream.of(list).getLast();
Any<Student> random = Ztream.of(list).random();

// findFirst / findAny：可选择是否因 null 抛 NPE
Optional<Student> opt = Ztream.of(list).findFirst(false);
```

---

更多方法可直接 `Ztream.of(obj).` 点出来探索。

[![Star History Chart](https://api.star-history.com/svg?repos=taowater/ztream&type=Date)](https://star-history.com/#taowater/ztream&Date)

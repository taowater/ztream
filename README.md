# Ztream

Java Stream 增强

### 🍊Maven

```xml

<dependency>
    <groupId>io.github.taowater</groupId>
    <artifactId>ztream</artifactId>
    <version>0.0.4</version>
</dependency>
```

### 示例

收集

```java
// stream
List<String> userNames = Stream.of(users).map(User::getName).collect(Collectors.toList());
// ztream(java16+stream已支持)
List<String> userNames = Ztream.of(users).map(User::getName).toList();
// ztream
List<String> userNames = Ztream.of(users).collect(User::getName);
// 收集为set
Set<String> userNames = Ztream.of(users).toSet(User::getName);
// 制定收集集合类型
Set<String> userNames = Ztream.of(users).collect(User::getName, TreeSet::new);
```

group/toMap

```java
// stream value或key为空会报异常
Map<String, Integer> map = Stream.of(list).collect(Collectors.toMap(Student::getName, Student::getAge));
// ztream value或key允许为空
Map<String, Integer> map = Ztream.of(list).toMap(Student::getName, Student::getAge);
// 指定map类型
Map<String, Integer> map = Ztream.of(list).toMap(Student::getName, Student::getAge, LinkedHashMap::new);
// stream
Map<String, List<Student>> group = Stream.of(list).collect(Collectors.groupingBy(Student::getName));
// ztream
Map<String, List<Student>> group = Ztream.of(list).groupBy(Student::getName);
```

带索引的遍历 forEach/peek/map

```java
Ztream.(list).

forEach((e, i) ->{
// doSomeThing
// });
```

字符属性join

```java
//stream略
// ztream 默认,分隔
String str = Ztream.of(list).join(Student::getName);
// 制定分隔符
String str = Ztream.of(list).join(Student::getName, "#");
```

数字属性操作

```java
    //stream略
// ztream - 需要该属性为Number类型
Integer max = Ztream.of(list).max(Student::getAge);
Integer sum = Ztream.of(list).sum(Student::getAge);
Integer min = Ztream.of(list).min(Student::getAge);
Integer avg = Ztream.of(list).avg(Student::getAge);
```

过滤/排序

```java
//stream略
// ztream - 类似mybatis-plus的操作
List<Student> list = Ztream.of(list)
        .eq(Student::getAge, "王武")
        .like(Student::getName, "朱")
        .ge(Student::getAge, 23)
        .toList();
// 按年龄升序
List<Student> list = Ztream.of(list).asc(Student::getAge).toList();
// 按年龄逆序
List<Student> list = Ztream.of(list).desc(Student::getAge).toList();

// 先按年龄降序(null排前)，年龄相同按名称升序(null排后)
List<Student> list = Ztream.of(list).sort(r -> r
        .desc(Student::getAge, true)
        .asc(Student::getName, false)
).toList();
```

就先这么多吧累了

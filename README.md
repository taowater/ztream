# Ztream

Java Stream 增强

### 🍊Maven

```xml

<dependency>
    <groupId>io.github.taowaterio.github.taowater</groupId>
    <artifactId>ztream</artifactId>
    <version>0.0.3</version>
</dependency>
```

### 示例

收集

```java
    // stream
    List<String> userNames = Stream.of(users).map(User::getName).collect(Collectors.toList());
    // ztream(java9stream已支持)
    List<String> userNames = Ztream.of(users).map(User::getName).toList();
    // ztream
    List<String> userNames = Ztream.of(users).collect(User::getName);
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
    Map<String, List<Student>> group = Ztream.of(list).group(Student::getName);
```

带索引的遍历 forEach/peek/map

```java
    Ztream.of(list).forEach((e, i) -> {
        e.setRank((long) (i + 1));
    });
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
    String str = Ztream.of(list).max(Student::getAge);
    String str = Ztream.of(list).sum(Student::getAge);
    String str = Ztream.of(list).min(Student::getAge);
```

过滤/排序

```java
    //stream略
    // ztream - 类似mybatis-plus的操作
    List<Student> list = Ztream.of(list)
        .eq(Student::getAge, "王武")
        .like(Student::getName, "朱")
        .toList();
    // 按年龄升序
    List<Student> list = Ztream.of(list).asc(Student::getAge).toList();
    // 按年龄逆序
    List<Student> list = Ztream.of(list).desc(Student::getAge).toList();
    
```

就先这么多吧累了

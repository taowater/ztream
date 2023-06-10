# ztream

java8标准流的增强

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
    Map<String, List<Student>> group = Stream.of(list).group(Student::getName);
```

带索引的遍历

```java
    Ztream.of(list).forEach((e, i) -> {
        e.setRank((long) (i + 1));
    });
```

字符join

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

就先这么多吧累了
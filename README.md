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

[![Star History Chart](https://api.star-history.com/svg?repos=taowater/ztream&type=Date)](https://star-history.com/#taowater/ztream&Date)

Java Stream 增强

### 🍊Maven

```xml

<dependency>
    <groupId>io.github.taowater</groupId>
    <artifactId>ztream</artifactId>
    <version>LATEST</version>
</dependency>
```
### 废话

Java8流操作的出现使得在业务中处理集合的一些操作逻辑变得明晰起来
但同时它带来一些问题，例如：
* 一些必要api缺失(不能按某属性去重、遍历不能带上下标等)
* 部分reduce操作如映射分组收集写法晦涩，样板代码过多
* 自带分组映射方法对空元素的不友好

为了解决这些实际开发中遇到的痛点，我捣鼓出了这个东西，可以进一步改善流的操作体验，更快出活，~~更好摸鱼!~~

另外，高版本的Jdk已经对上述一些问题做出一定程度的优化，自22之后更是推出了流收集器Gatherer来允许一些自定义的中间操作
但Ztream中一些常用实用api仍有其使用价值，尤其是在仍有相当人高举"你发任你发，我用Java8"大旗的当下。

最后，这个小库没有什么很高深的东西，无非是一些封装罢了。只是我自己一路完善过来，敝帚自珍，把他搬了上来。
他可能不够完美，但是它确实让我更好地crud，也希望可以帮到大家。
对流操作的一些库，在我冲浪参考的过程中，还有这些：
* streamex https://github.com/amaembo/streamex
* JDFrame https://github.com/burukeYou/JDFrame
* seq https://github.com/wolray/seq
* stream-query https://github.com/dromara/stream-query

若拙库可取则取之，如其不才，君可自取。

### 示例

#### 收集

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

####  分组/映射

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

// v0.1.0支持分组和映射的中间操作
Ztream.of(list)
.hash(Student::getName, Student::getAge)
.flip() // 对key value进行反转
.forEach((k, v) -> {
// doSomeThing
});

```

##### 带索引的上下文

```java
// forEach
Ztream.(list).forEach((e, i) -> {
// doSomeThing 
});

Ztream.(list).peek((e, i) -> {
// doSomeThing 
}).other

Ztream.(list).map((e, i) -> {
// doSomeThing
    return o;
}).other
```

#### 字符拼接

```java
// 默认[,]分隔,输出为所有名字按逗号分隔，示例结果："刘备,关羽,张飞"
String str = Ztream.of(list).join(Student::getName);

// 指定分隔符，结果："刘备#关羽#张飞"
String str = Ztream.of(list).join(Student::getName, "#");

// 指定分隔符以及前后缀，结果："[刘备#关羽#张飞]"
String str = Ztream.of(list).join(Student::getName, "#", "[", "]");
```

##### 数字及统计

```java
// ztream - 需要该属性为Number类型
// 最大年龄
Integer max = Ztream.of(list).max(Student::getAge);
// 最小年龄
Integer min = Ztream.of(list).min(Student::getAge);
// 所有年龄之和
Integer sum = Ztream.of(list).sum(Student::getAge);
// 年龄平均值
Integer avg = Ztream.of(list).avg(Student::getAge);
// 取得最大年龄该学生对象
Student maxAgeStudent = Ztream.of(list).maxBy(Student::getAge);
```

#### 过滤

```java
// 类似mybatis-plus的操作
List<Student> list = Ztream.of(list)
                .eq(Student::getAge, 34) // 年龄等于34 
                .like(Student::getName, "朱") // 姓名为朱开头
                .ge(Student::getScore, 60) // 分数达于等于60
                .lt(flag, Student::getScore, 80) // 分数达于等于60, 当某条件为真时才拼接该条件
                // 或逻辑拼接
                .or(w -> w
                        .like(Student::getName, "刘")
                        // 可使用基础筛选方法
                )
                // 且逻辑拼接
                .and(w -> w
                        .like(Student::getName, "刘")
                        // 可使用基础筛选方法
                )
                .toList();
```

#### 排序
```java
// 按年龄升序
List<Student> list = Ztream.of(list).asc(Student::getAge).toList();
// 按年龄逆序
List<Student> list = Ztream.of(list).desc(Student::getAge).toList();
// 洗牌
List<Student> list = Ztream.of(list).shuffle().toList();

// 先按年龄降序(null排前)，年龄相同按名称升序(null排后)
List<Student> list = Ztream.of(list)
        .sort(r -> r
                    .desc(flag, Student::getAge, true) // 当某条件为真时才应用该排序操作
                    .asc(Student::getName, false)
        ).toList();
```

其他更多方法尝试Ztream.of(obj)点出来看看吧


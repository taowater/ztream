package com.taowater.ztream;

public final class TestFixtures {

    private TestFixtures() {
    }

    public static Student student(String name, Integer age) {
        return new Student(name, age);
    }

    public static class Person {
        private String sex;
        private Boolean flag;

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public Boolean getFlag() {
            return flag;
        }

        public void setFlag(Boolean flag) {
            this.flag = flag;
        }
    }

    public static class Student extends Person {
        private String name;
        private Integer age;

        public Student() {
        }

        public Student(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    public static class Teacher extends Person {
        private String name;
        private Long money;

        public Teacher() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getMoney() {
            return money;
        }

        public void setMoney(Long money) {
            this.money = money;
        }
    }
}

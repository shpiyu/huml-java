package com.github.shpiyu.huml.sample;

import java.util.Map;
import java.util.Objects;
import java.io.IOException;
import com.github.shpiyu.huml.HUMLReader;
import com.github.shpiyu.huml.HUMLWriter;
import com.github.shpiyu.huml.HUMLAdapter;

public class Person {
    private String name;
    private int age;
    private String city;

    public Person() {
    }

    public Person(String name, int age, String city) {
        this.name = name;
        this.age = age;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getCity() {
        return city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", city='" + city + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name) && Objects.equals(city, person.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, city);
    }

    public static class PersonAdapter extends HUMLAdapter<Person> {
        @Override
        public Person fromHUML(HUMLReader reader) throws IOException {
            Map<String, Object> data = reader.readDocument();
            Person p = new Person();
            p.setName((String) data.get("name"));
            p.setAge(Integer.parseInt((String) data.get("age")));
            p.setCity((String) data.get("city"));
            return p;
        }

        @Override
        public void toHUML(HUMLWriter writer, Person value) {
            writer.writeField("name", value.getName());
            writer.writeField("age", String.valueOf(value.getAge()));
            writer.writeField("city", value.getCity());
        }   
    }
}
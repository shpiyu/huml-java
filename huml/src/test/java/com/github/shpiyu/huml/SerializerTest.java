package com.github.shpiyu.huml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SerializerTest {

    private Person person;
    private HUMLMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HUMLMapper();
        mapper.registerAdapter(Person.class, new PersonHUMLAdapter());
        person = new Person();
    }
    
    @Test
    void testNonNullSerialization() throws IOException {
        person.name = "piyush";
        person.age = 29;
        person.points = 32.5;
        person.empty = "";
        person.nullString = "notNUll";
        person.nullDouble = 0.0;

        String huml = mapper.writeValueAsString(person);

        assertEquals("""
                name: "piyush"
                age: 29
                points: 32.5
                empty: ""
                nullString: "notNUll"
                nullDouble: 0.0
                """, huml);
    }

    @Test
    void testNullSerialization() throws IOException {
        person.name = null;
        person.age = null;
        person.points = null;
        person.empty = null;
        person.nullString = null;
        person.nullDouble = null;

        String huml = mapper.writeValueAsString(person);

        assertEquals("""
                name: null
                age: null
                points: null
                empty: null
                nullString: null
                nullDouble: null
                """, huml);
    }

    @Test
    void testEmptySerialization() throws IOException {
        person.name = "";
        person.age = 0;
        person.points = 0.0;
        person.empty = "";
        person.nullString = "";
        person.nullDouble = 0.0;

        String huml = mapper.writeValueAsString(person);

        assertEquals("""
                name: ""
                age: 0
                points: 0.0
                empty: ""
                nullString: ""
                nullDouble: 0.0
                """, huml);
    }

}

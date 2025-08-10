package com.github.shpiyu.huml;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeserializerTest {
    
    private HumlMapper mapper;
    
    @BeforeEach
    void setUp() {
        mapper = new HumlMapper();
        mapper.registerAdapter(Person.class, new PersonHumlAdapter());
    }

    @Test
    void testNonNullDeserialization() throws IOException {
        String huml = """
                name: "piyush"
                age: 29
                points: 32.5
                empty: ""
                nullString: "not null"
                nullDouble: 0.0
                """;

        Person person = mapper.readValue(huml, Person.class);

        assertEquals("piyush", person.name);
        assertEquals(29, person.age);
        assertEquals(32.5, person.points);
        assertTrue(person.empty.isEmpty());
        assertEquals("not null", person.nullString);
        assertEquals(0.0, person.nullDouble, 0.001);
    }
    
    @Test
    void testNullValues() throws IOException {
        String huml = """
                name: null
                age: null
                points: null
                empty: null
                nullString: null
                nullDouble: null
                """;

        Person person = mapper.readValue(huml, Person.class);

        assertNull(person.name);
        assertNull(person.age);
        assertNull(person.points);
        assertNull(person.empty);
        assertNull(person.nullString);
        assertNull(person.nullDouble);
    }
    
    @Test
    void testEmptyValues() throws IOException {
        String huml = """
                name: ""
                age: 0
                points: 0.0
                empty: ""
                nullString: ""
                nullDouble: 0.0
                """;

        Person person = mapper.readValue(huml, Person.class);

        assertEquals("", person.name);
        assertEquals(0, person.age);
        assertEquals(0.0, person.points);
        assertEquals("", person.empty);
        assertEquals("", person.nullString);
        assertEquals(0.0, person.nullDouble);
    }
    
    @Test
    void testMissingFields() throws IOException {
        String huml = """
                name: "test"
                """;

        Person person = mapper.readValue(huml, Person.class);

        assertEquals("test", person.name);
        assertNull(person.age);
        assertNull(person.points);
        assertNull(person.empty);
        assertNull(person.nullString);
        assertNull(person.nullDouble);
    }
    
    @Test
    void testNumericEdgeCases() throws IOException {
        String huml = """
                age: 2147483647
                points: 1.7976931348623157E308
                """;

        Person person = mapper.readValue(huml, Person.class);

        assertEquals(Integer.MAX_VALUE, person.age);
        assertEquals(Double.MAX_VALUE, person.points);
    }
}

package com.github.shpiyu.huml.parser;

import org.junit.jupiter.api.Test;
import com.github.shpiyu.huml.HumlDocument;
import com.github.shpiyu.huml.HumlType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ParserTest {

    @Test
    void testEmptyDocument() {
        assertThrows(ParserException.class, () -> Parser.parse(""),
        "Empty document is undefined");
    }

    @Test
    void testStringScalar() {
        String huml = "greeting: \"Hello\"";
        HumlDocument doc = Parser.parse(huml);
        assertEquals("Hello", doc.get("greeting").asString());
    }

    @Test
    void testBoolean() {
        String huml = "valid: true";
        HumlDocument doc = Parser.parse(huml);
        assertEquals(true, doc.get("valid").asBoolean());
    }

    @Test
    void testNumberScalar() {
        String huml = "age: 29";
        HumlDocument doc = Parser.parse(huml);
        assertEquals(29, doc.get("age").asInteger());
        assertEquals(29.0, doc.get("age").asDouble());
        assertEquals(29L, doc.get("age").asLong());
        assertEquals(29.0f, doc.get("age").asFloat());
    }

    @Test
    void testMaxDouble() {
        String huml = "maxDouble: 1.7976931348623157E308";
        HumlDocument doc = Parser.parse(huml);
        assertEquals(Double.MAX_VALUE, doc.get("maxDouble").asDouble());
    }

    @Test
    void testNull() {
        String huml = "age: null";
        HumlDocument doc = Parser.parse(huml);
        assertEquals(null, doc.get("age").asInteger());
    }

    @Test
    void testStringList() {
        String huml = """
                names:: "Ram", "Lakshman", "Seeta"
                """;
        HumlDocument doc = Parser.parse(huml);
        assertEquals(3, doc.get("names").asList().size());
        assertEquals("Ram", doc.get("names").asList().get(0).asString());
        assertEquals("Lakshman", doc.get("names").asList().get(1).asString());
        assertEquals("Seeta", doc.get("names").asList().get(2).asString());
    }

    @Test
    void testNumberList() {
        String huml = """
                numbers:: 1, 2, 3
                """;
        HumlDocument doc = Parser.parse(huml);
        assertEquals(3, doc.get("numbers").asList().size());
        assertEquals(1, doc.get("numbers").asList().get(0).asInteger());
        assertEquals(2, doc.get("numbers").asList().get(1).asInteger());
        assertEquals(3, doc.get("numbers").asList().get(2).asInteger());
    }

    @Test
    void testBooleanList() {
        String huml = """
                booleans:: true, false, true
                """;
        HumlDocument doc = Parser.parse(huml);
        assertEquals(3, doc.get("booleans").asList().size());
        assertEquals(true, doc.get("booleans").asList().get(0).asBoolean());
        assertEquals(false, doc.get("booleans").asList().get(1).asBoolean());
        assertEquals(true, doc.get("booleans").asList().get(2).asBoolean());
    }

    @Test
    void testNullList() {
        String huml = """
                nulls:: null, null, null
                """;
        HumlDocument doc = Parser.parse(huml);
        assertEquals(3, doc.get("nulls").asList().size());
        assertEquals(null, doc.get("nulls").asList().get(0).asInteger());
        assertEquals(null, doc.get("nulls").asList().get(1).asInteger());
        assertEquals(null, doc.get("nulls").asList().get(2).asInteger());
    }

    @Test
    void testMixedList() {
        String huml = """
                mixed:: "Ram", 29, true, null
                """;
        HumlDocument doc = Parser.parse(huml);
        assertEquals(4, doc.get("mixed").asList().size());
        assertEquals("Ram", doc.get("mixed").asList().get(0).asString());
        assertEquals(29, doc.get("mixed").asList().get(1).asInteger());
        assertEquals(true, doc.get("mixed").asList().get(2).asBoolean());
        assertEquals(null, doc.get("mixed").asList().get(3).asInteger());
    }

    @Test
    void testMultilineList() {
        String huml = """
                multiline_list::
                  - 1
                  - 2
                  - "three"
                """;
        HumlDocument doc = Parser.parse(huml);
        assertEquals(3, doc.get("multiline_list").asList().size());
        assertEquals(1, doc.get("multiline_list").asList().get(0).asInteger());
        assertEquals(2, doc.get("multiline_list").asList().get(1).asInteger());
        assertEquals("three", doc.get("multiline_list").asList().get(2).asString());
    }

    @Test
    void testMultilineListExtraIndentation() {
        String huml = """
                multiline_list::
                  - 1
                    - 2
                  - "three"
                """;
        assertThrows(ParserException.class, () -> Parser.parse(huml),
        "Invalid indentation at line 3");
    }

    @Test
    void testMultilineListLessIndentaion() {
        String huml = """
                multiline_list::
                  - 1
                - 2
                - "three"
                """;
        assertThrows(ParserException.class, () -> Parser.parse(huml),
        "Invalid indentation at line 3");
    }

    @Test
    void testNestedList() {
        String huml = """
                nested_list::
                  - 1
                  - 2
                  - "three"
                  - ::
                    - 1
                    - 2
                    - 3
                """;
        HumlDocument doc = Parser.parse(huml);
        assertEquals(4, doc.get("nested_list").asList().size());
        assertEquals(1, doc.get("nested_list").asList().get(0).asInteger());
        assertEquals(2, doc.get("nested_list").asList().get(1).asInteger());
        assertEquals("three", doc.get("nested_list").asList().get(2).asString());
        assertEquals(3, doc.get("nested_list").asList().get(3).asList().size());
        assertEquals(1, doc.get("nested_list").asList().get(3).asList().get(0).asInteger());
        assertEquals(2, doc.get("nested_list").asList().get(3).asList().get(1).asInteger());
        assertEquals(3, doc.get("nested_list").asList().get(3).asList().get(2).asInteger());
    }

    @Test
    void testEmptyList() {
        String huml = "empty_list:: []";
        HumlDocument doc = Parser.parse(huml);
        assertEquals(HumlType.LIST, doc.get("empty_list").getType());
        assertEquals(0, doc.get("empty_list").asList().size());
    }
        
    
    @Test
    void testInlineDict() {
        String huml = "person:: name: \"John\", age: 30";
        HumlDocument doc = Parser.parse(huml);
        assertEquals("John", doc.get("person").asDict().get("name").asString());
        assertEquals(30, doc.get("person").asDict().get("age").asInteger());
    }

    @Test
    void testMultilineDict() {
        String huml = """
                multiline_dict::
                  one: 1
                  foo: "bar"
                """;
        HumlDocument doc = Parser.parse(huml);
        assertEquals(1, doc.get("multiline_dict").asDict().get("one").asInteger());
        assertEquals("bar", doc.get("multiline_dict").asDict().get("foo").asString());
    }

    @Test
    void testNestedDict() {
        String huml = """
                nested_dict::
                  one: 1
                  foo: "bar"
                  nested::
                    two: 2
                    foo: "baz"
                """;
        HumlDocument doc = Parser.parse(huml);
        assertEquals(1, doc.get("nested_dict").asDict().get("one").asInteger());
        assertEquals("bar", doc.get("nested_dict").asDict().get("foo").asString());     
        assertEquals(2, doc.get("nested_dict").asDict().get("nested").asDict().get("two").asInteger());
        assertEquals("baz", doc.get("nested_dict").asDict().get("nested").asDict().get("foo").asString());
    }

    @Test
    void testEmptyDict() {
        String huml = "empty_dict:: {}";
        HumlDocument doc = Parser.parse(huml);
        assertEquals(HumlType.DICT, doc.get("empty_dict").getType());
        assertEquals(0, doc.get("empty_dict").asDict().size());
    }

    @Test
    void testListOfDicts() {
        String huml = """
                list_of_dicts::
                  - ::
                    one: 1
                    foo: "bar"
                  - ::
                    two: 2
                    foo: "baz"
                """;
        
        HumlDocument doc = Parser.parse(huml);
        assertEquals(2, doc.get("list_of_dicts").asList().size());
        assertEquals(1, doc.get("list_of_dicts").asList().get(0).asDict().get("one").asInteger());
        assertEquals("bar", doc.get("list_of_dicts").asList().get(0).asDict().get("foo").asString());
        assertEquals(2, doc.get("list_of_dicts").asList().get(1).asDict().get("two").asInteger());
        assertEquals("baz", doc.get("list_of_dicts").asList().get(1).asDict().get("foo").asString());
    }
        
    
}

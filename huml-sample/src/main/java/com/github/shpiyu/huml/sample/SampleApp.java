package com.github.shpiyu.huml.sample;

import java.io.IOException;

import com.github.shpiyu.huml.HUMLMapper;

/**
 * Sample application demonstrating the use of HUML core functionality.
 */
public class SampleApp {
    
    /**
     * Main method that demonstrates basic HUML functionality.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) throws IOException{
        // Create HUMLMapper and register custom adapter
        HUMLMapper mapper = new HUMLMapper();
        mapper.registerAdapter(Person.class, new Person.PersonAdapter());

        // Sample HUML document
        String huml = """
                name: John Doe
                age: 30
                city: New York
                """;

        Person person = mapper.readValue(huml, Person.class);
        String serialized = mapper.writeValueAsString(person);
        System.out.println("Parsed person: " + person);
        System.out.println("Serialized: " + serialized);
    }
}

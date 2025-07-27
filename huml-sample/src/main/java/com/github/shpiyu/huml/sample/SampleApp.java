package com.github.shpiyu.huml.sample;

import java.io.IOException;
import java.util.List;

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
    public static void main(String[] args) throws IOException {
        // Create HUMLMapper and register generated adapter
        HUMLMapper mapper = new HUMLMapper();
        mapper.registerAdapter(Student.class, new StudentHUMLAdapter());

        Student s = new Student();
        s.name = "John Doe";
        s.age = 20;
        s.totalMarks = 95.5;
        s.school = "ABC School";
        s.hasCompletedMedicalCheck = true;
        s.skills = List.of("Java", "Python", "C++");
        s.powers = List.of(1,50,100);
        s.nothing = List.of();
        

        String serialized = mapper.writeValueAsString(s);
        System.out.println("Serialized: " + serialized);
    }
}

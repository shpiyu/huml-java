package com.github.shpiyu.huml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HUMLProcessorTest {
    
    @TempDir
    Path tempDir;

    @Test
    void testProcessorGeneratesAdapter() throws IOException {
        // Create a simple test class with @HUML annotation
        String testClass = """
            package com.example.test;
            
            import com.github.shpiyu.huml.HUML;
            
            @HUML
            public class TestPerson {
                String name;
                int age;
            }
            """;

        // Set up the Java compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        
        // Create source file in temp directory
        Path sourceDir = tempDir.resolve("sources");
        Files.createDirectories(sourceDir);
        Path sourceFile = sourceDir.resolve("TestPerson.java");
        Files.writeString(sourceFile, testClass);
        
        // Set up compilation task
        Iterable<? extends JavaFileObject> compilationUnits = 
            fileManager.getJavaFileObjects(sourceFile.toFile());
            
        // Configure compilation options
        List<String> options = Arrays.asList(
            "-d", tempDir.resolve("classes").toString(),
            "-s", tempDir.resolve("generated").toString(),
            "-processor", "com.github.shpiyu.huml.HUMLProcessor"
        );
        
        // Run the compilation
        boolean success = compiler.getTask(
            null, 
            fileManager, 
            null, 
            options, 
            null, 
            compilationUnits
        ).call();
        
        // Verify compilation was successful
        assertTrue(success, "Compilation failed");
        
        // Verify the adapter was generated
        Path generatedFile = tempDir.resolve("generated/com/example/test/TestPersonHUMLAdapter.java");
        assertTrue(Files.exists(generatedFile), "Adapter class was not generated");
        
        // Verify the content of the generated file
        String generatedContent = Files.readString(generatedFile);
        assertTrue(generatedContent.contains("public class TestPersonHUMLAdapter"), 
                 "Generated class has wrong name");
        assertTrue(generatedContent.contains("extends HUMLAdapter<TestPerson>"), 
                 "Generated class should extend HUMLAdapter");
        assertTrue(generatedContent.contains("public TestPerson fromHUML"),
                 "Generated class should have fromHUML method");
        assertTrue(generatedContent.contains("public void toHUML"),
                 "Generated class should have toHUML method");
    }
}

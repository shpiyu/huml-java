# HUML (Human-oriented Markup Language)

A Java library for working with Human-oriented Markup Language (HUML), providing utilities for reading and writing structured data in a human-readable format.

## What is HUML?
HUML is a simple, strict, serialization language for documents, datasets, and configuration. It prioritizes strict form for human-readability. It looks like YAML, but tries to avoid its complexity, ambiguity, and pitfalls. Read more about it here - [huml.io](https://huml.io/).

## About this library
This library is a Java implementation of HUML. It is in active development mode and is not yet ready for production use.

## Usage

### Basic Writing

```java
HUMLWriter writer = new HUMLWriter();
writer.writeField("name", "John Doe");
writer.writeField("age", "30");
String humlOutput = writer.getOutput();
```

### Using Custom Adapters

```java
public class Person {
    private String name;
    private int age;
    
    // Getters and setters
}

public class PersonAdapter extends HUMLAdapter<Person> {
    @Override
    public Person fromHUML(HUMLReader reader) throws IOException {
        Person person = new Person();
        person.setName(reader.readString("name"));
        person.setAge(reader.readInt("age"));
        return person;
    }

    @Override
    public void toHUML(HUMLWriter writer, Person value) throws IOException {
        writer.writeField("name", value.getName());
        writer.writeField("age", String.valueOf(value.getAge()));
    }
}
```

## Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/shpiyu/huml-java.git
   cd huml-java
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

## Project Structure

- `huml-core/`: Core library implementation
  - `src/main/java/com/github/shpiyu/huml/`: Core classes
    - `HUMLWriter.java`: For writing HUML format
    - `HUMLAdapter.java`: Base class for custom adapters
    - `HUMLReader.java`: For reading HUML format
- `huml-sample/`: Sample usage and examples

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

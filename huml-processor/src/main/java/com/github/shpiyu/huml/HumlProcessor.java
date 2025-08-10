package com.github.shpiyu.huml;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * HumlProcessor creates HumlAdapter classes for classes annotated with @Huml.
 */
@SupportedAnnotationTypes("com.github.shpiyu.huml.Huml")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class HumlProcessor extends AbstractProcessor {

    // Functional interface for type conversion
    @FunctionalInterface
    private interface TypeHandler {
        String convert(String fieldName);

        default String apply(String fieldName) {
            return convert(fieldName);
        }
    }

    // Functional interface for type serialization
    @FunctionalInterface
    private interface SerializationHandler {
        String generate(String fieldName);

        default String apply(String fieldName) {
            return generate(fieldName);
        }
    }

    // Map to store type handlers
    private final Map<String, TypeHandler> typeHandlers = new HashMap<>();
    private final Map<String, SerializationHandler> serializationHandlers = new HashMap<>();

    private Filer filer;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.elementUtils = processingEnv.getElementUtils();
        initTypeHandlers();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element e : roundEnv.getElementsAnnotatedWith(Huml.class)) {
            if (e.getKind() != ElementKind.CLASS)
                continue;
            TypeElement classElement = (TypeElement) e;
            generateAdapter(classElement);
        }
        return true;
    }

    // Initialize type handlers
    private void initTypeHandlers() {

        // Wrapper type handlers
        // todo: handle Character
        // todo: handle Strings with special characters \t, \n, etc
        typeHandlers.put("java.lang.Integer", field -> String.format("parseInt(map.get(\"%s\"));", field));
        typeHandlers.put("java.lang.Double", field -> String.format("parseDouble(map.get(\"%s\"));", field));
        typeHandlers.put("java.lang.Float", field -> String.format("parseFloat(map.get(\"%s\"));", field));
        typeHandlers.put("java.lang.Long", field -> String.format("parseLong(map.get(\"%s\"));", field));
        typeHandlers.put("java.lang.Short", field -> String.format("parseShort(map.get(\"%s\"));", field));
        typeHandlers.put("java.lang.Byte", field -> String.format("parseByte(map.get(\"%s\"));", field));
        typeHandlers.put("java.lang.String", field -> String.format("parseString(map.get(\"%s\"));", field));

        // Primitives handlers
        typeHandlers.put("int", field -> String.format("parsePrimitiveInt(map.get(\"%s\"));", field));
        typeHandlers.put("double", field -> String.format("parsePrimitiveDouble(map.get(\"%s\"));", field));
        typeHandlers.put("float", field -> String.format("parsePrimitiveFloat(map.get(\"%s\"));", field));
        typeHandlers.put("long", field -> String.format("parsePrimitiveLong(map.get(\"%s\"));", field));
        typeHandlers.put("short", field -> String.format("parsePrimitiveShort(map.get(\"%s\"));", field));
        typeHandlers.put("byte", field -> String.format("parsePrimitiveByte(map.get(\"%s\"));", field));

        // Initialize serialization handlers
        // For types that need String.valueOf()
        String[] valueOfTypes = {
                "int", "java.lang.Integer",
                "boolean", "java.lang.Boolean",
                "double", "java.lang.Double",
                "float", "java.lang.Float",
                "long", "java.lang.Long",
                "short", "java.lang.Short",
                "byte", "java.lang.Byte",
                "char", "java.lang.Character"
        };

        for (String type : valueOfTypes) {
            serializationHandlers.put(type,
                    fieldName -> "writer.writeField(\"" + fieldName + "\", String.valueOf(value." + fieldName + "));");
        }

        // String type
        serializationHandlers.put("java.lang.String",
                fieldName -> "writer.writeField(\"" + fieldName + "\", handleNullString(value." + fieldName + "));");

        // Default handler for unsupported types
        serializationHandlers.put("__DEFAULT__",
                fieldName -> "writer.writeField(\"" + fieldName + "\", String.valueOf(value." + fieldName
                        + ")); // Unsupported type");
    }

    // Generates HumlAdapter class file
    private void generateAdapter(TypeElement classElement) {
        String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
        String className = classElement.getSimpleName().toString();
        String adapterClassName = className + "HumlAdapter";

        StringBuilder code = new StringBuilder();
        code.append("package ").append(packageName).append(";\n")
                .append("import com.github.shpiyu.huml.HumlAdapter;\n")
                .append("import com.github.shpiyu.huml.HumlReader;\n")
                .append("import com.github.shpiyu.huml.HumlWriter;\n")
                .append("import java.io.IOException;\n")
                .append("import static com.github.shpiyu.huml.HumlParserUtils.*;\n")
                .append("public class ").append(adapterClassName).append(" extends HumlAdapter\u003c").append(className)
                .append("\u003e {\n");

        fromHUML(code, classElement);

        toHUML(code, classElement);

        code.append("}");

        try {
            JavaFileObject file = filer.createSourceFile(packageName + "." + adapterClassName);
            try (Writer writer = file.openWriter()) {
                writer.write(code.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Code generation failed: " + e.getMessage(), e);
        }
    }

    // Generates the fromHUML method which is used to parse HUML to Java object
    private void fromHUML(StringBuilder code, Element classElement) {
        String className = classElement.getSimpleName().toString();

        code.append("    @Override\n")
                .append("    public ").append(className).append(" fromHUML(HumlReader reader) throws IOException {\n")
                .append("        ").append(className).append(" instance = new ").append(className).append("();\n")
                .append("        java.util.Map<String, Object> map = reader.readDocument();\n");

        for (Element field : classElement.getEnclosedElements()) {
            if (field.getKind() == ElementKind.FIELD) {
                String fieldName = field.getSimpleName().toString();
                String fieldType = field.asType().toString();
                code.append("        instance.").append(fieldName).append(" = ");

                // Get the appropriate type handler or use the unknown type handler
                TypeHandler handler = typeHandlers.getOrDefault(fieldType,
                        type -> "null; // " + type + " not supported");

                // Apply the handler to get the conversion code
                String conversionCode = handler.apply(fieldName);
                code.append(conversionCode).append("\n");
            }
        }

        code.append("        return instance;\n");
        code.append("    }\n\n");

    }

    // Generates the toHUML method which is used to serialize Java object to HUML
    private void toHUML(StringBuilder code, Element classElement) {
        String className = classElement.getSimpleName().toString();
        code.append("    @Override public void toHUML(HumlWriter writer, ").append(className)
                .append(" value) throws IOException {\n");

        for (Element field : classElement.getEnclosedElements()) {
            if (field.getKind() == ElementKind.FIELD) {
                String fieldName = field.getSimpleName().toString();
                String fieldType = field.asType().toString();

                // Get the appropriate serialization handler or use the default one
                SerializationHandler handler = serializationHandlers.getOrDefault(
                        fieldType,
                        serializationHandlers.get("__DEFAULT__"));

                // Generate and append the serialization code
                String serializationCode = handler.apply(fieldName);
                code.append("        ").append(serializationCode).append("\n");
            }
        }

        code.append("    }\n");
    }
}

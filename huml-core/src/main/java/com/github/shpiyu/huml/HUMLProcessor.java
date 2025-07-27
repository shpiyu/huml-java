package com.github.shpiyu.huml;

import java.io.IOException;
import java.io.Writer;
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
import java.util.Map;
import java.util.HashMap;

@SupportedAnnotationTypes("com.github.shpiyu.huml.HUML")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class HUMLProcessor extends AbstractProcessor {

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

    // Initialize type handlers
    private void initTypeHandlers() {
        // Primitive types and their wrapper classes
        typeHandlers.put("int", fieldName -> "Integer.parseInt((String) map.get(\"" + fieldName + "\"));");
        typeHandlers.put("java.lang.Integer", typeHandlers.get("int"));

        typeHandlers.put("boolean", fieldName -> "Boolean.parseBoolean((String) map.get(\"" + fieldName + "\"));");
        typeHandlers.put("java.lang.Boolean", typeHandlers.get("boolean"));

        typeHandlers.put("double", fieldName -> "Double.parseDouble((String) map.get(\"" + fieldName + "\"));");
        typeHandlers.put("java.lang.Double", typeHandlers.get("double"));

        typeHandlers.put("float", fieldName -> "Float.parseFloat((String) map.get(\"" + fieldName + "\"));");
        typeHandlers.put("java.lang.Float", typeHandlers.get("float"));

        typeHandlers.put("long", fieldName -> "Long.parseLong((String) map.get(\"" + fieldName + "\"));");
        typeHandlers.put("java.lang.Long", typeHandlers.get("long"));

        typeHandlers.put("short", fieldName -> "Short.parseShort((String) map.get(\"" + fieldName + "\"));");
        typeHandlers.put("java.lang.Short", typeHandlers.get("short"));

        typeHandlers.put("byte", fieldName -> "Byte.parseByte((String) map.get(\"" + fieldName + "\"));");
        typeHandlers.put("java.lang.Byte", typeHandlers.get("byte"));

        typeHandlers.put("char", fieldName -> "Character.getNumericValue((String) map.get(\"" + fieldName + "\"));");
        typeHandlers.put("java.lang.Character", typeHandlers.get("char"));

        // String type
        typeHandlers.put("java.lang.String", fieldName -> "(String) map.get(\"" + fieldName + "\");");

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

        // String type doesn't need String.valueOf()
        serializationHandlers.put("java.lang.String",
                fieldName -> "writer.writeField(\"" + fieldName + "\", value." + fieldName + ");");

        // Default handler for unsupported types
        serializationHandlers.put("__DEFAULT__",
                fieldName -> "writer.writeField(\"" + fieldName + "\", String.valueOf(value." + fieldName
                        + ")); // Unsupported type");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element e : roundEnv.getElementsAnnotatedWith(HUML.class)) {
            if (e.getKind() != ElementKind.CLASS)
                continue;
            TypeElement classElement = (TypeElement) e;
            generateAdapter(classElement);
        }
        return true;
    }

    private void generateAdapter(TypeElement classElement) {
        String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
        String className = classElement.getSimpleName().toString();
        String adapterClassName = className + "HUMLAdapter";

        StringBuilder code = new StringBuilder();
        code.append("package ").append(packageName).append(";\n")
                .append("import com.github.shpiyu.huml.HUMLAdapter;\n")
                .append("import com.github.shpiyu.huml.HUMLReader;\n")
                .append("import com.github.shpiyu.huml.HUMLWriter;\n")
                .append("import java.io.IOException;\n")
                .append("public class ").append(adapterClassName).append(" extends HUMLAdapter\u003c").append(className)
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

    private void fromHUML(StringBuilder code, Element classElement) {
        String className = classElement.getSimpleName().toString();

        code.append("    @Override\n")
                .append("    public ").append(className).append(" fromHUML(HUMLReader reader) throws IOException {\n")
                .append("        ").append(className).append(" instance = new ").append(className).append("();\n")
                .append("        var map = reader.readDocument();\n");

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

    private void toHUML(StringBuilder code, Element classElement) {
        String className = classElement.getSimpleName().toString();
        code.append("    @Override public void toHUML(HUMLWriter writer, ").append(className)
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

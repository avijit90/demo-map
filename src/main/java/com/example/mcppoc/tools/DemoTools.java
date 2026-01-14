package com.example.mcppoc.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DemoTools {

    @Tool(description = "Greets a person by name with a welcome message")
    public String greet(@ToolParam(description = "The name of the person to greet") String name) {
        return "Hello, " + name + "! Welcome to the MCP server.";
    }

    @Tool(description = "Adds two numbers together and returns the sum")
    public int addNumbers(
            @ToolParam(description = "First number to add") int a,
            @ToolParam(description = "Second number to add") int b) {
        return a + b;
    }

    @Tool(description = "Returns the current server time in a readable format")
    public String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm:ss a");
        return "Current server time: " + now.format(formatter);
    }

    @Tool(description = "Reverses the given text string")
    public String reverseString(@ToolParam(description = "The text to reverse") String text) {
        return new StringBuilder(text).reverse().toString();
    }
}

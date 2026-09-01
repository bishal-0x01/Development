package com.springAI.chatApp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class TravellingTools {

    @Tool(description = "Get the details of weather of a city")
    public String getWeather(@ToolParam(description = "city for which to get the weather Information") String city){
        return switch (city) {
            case "Delhi" -> "Sunny, 26 Degree";
            case "London" -> "Cloudy, 2 Degree";
            default -> "cannot Identify the city";
        };
    }
}

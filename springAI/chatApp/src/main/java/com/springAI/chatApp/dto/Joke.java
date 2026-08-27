package com.springAI.chatApp.dto;

public record Joke(
        String text,
        String category,
        Double laughScore,
        Boolean isNSFW
) {
}

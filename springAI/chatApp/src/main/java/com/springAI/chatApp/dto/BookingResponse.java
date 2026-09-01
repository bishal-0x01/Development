package com.springAI.chatApp.dto;

import com.springAI.chatApp.entity.BookingStatus;

import java.time.Instant;

public record BookingResponse(
        Long id,
        String destination,
        Instant departureTime,
        BookingStatus status
) {
}

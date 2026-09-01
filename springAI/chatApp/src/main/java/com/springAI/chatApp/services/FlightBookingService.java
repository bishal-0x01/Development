package com.springAI.chatApp.services;

import com.springAI.chatApp.entity.BookingStatus;
import com.springAI.chatApp.entity.FlightBooking;
import com.springAI.chatApp.repository.FlightBookingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FlightBookingService {

    private final FlightBookingRepository repository;

    public FlightBooking createBooking(String userId, String destination, Instant departureTime){

        boolean exists = repository.existsByUserIdAndDestinationAndDepartureTime(userId,destination,departureTime);

        if(exists){
            throw new IllegalArgumentException(
                    "You already have booking to "+ destination +" on that date."
            );
        }

        FlightBooking booking = FlightBooking.builder()
                .userId(userId)
                .departureTime(departureTime)
                .destination(destination)
                .bookingStatus(BookingStatus.CONFIRMED)
                .build();

        return repository.save(booking);
    }


    public List<FlightBooking> getUserBookings(String userId){
        return repository.findByUserIdOrderByDepartureTimeDesc(userId);
    }

    public FlightBooking updateBookingStatus(Long bookingId, String userId, BookingStatus newStatus){

        FlightBooking booking = repository.findById(bookingId).orElseThrow(()->
                new IllegalArgumentException("Booking not found.")
        );

        if(!booking.getUserId().equals(userId)){
            throw new IllegalArgumentException("You can modify only your bookings.");
        }

        booking.setBookingStatus(newStatus);

        return repository.save(booking);
    }

}

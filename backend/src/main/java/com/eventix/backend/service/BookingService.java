package com.eventix.backend.service;

import com.eventix.backend.entity.Booking;
import com.eventix.backend.entity.Ticket;
import com.eventix.backend.entity.User;
import com.eventix.backend.repository.BookingRepository;
import com.eventix.backend.repository.TicketRepository;
import com.eventix.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, TicketRepository ticketRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    // The Magic ACID Transaction! If anything fails here, the whole thing undoes itself automatically.
    @Transactional
    public Booking createBooking(Long userId, List<Long> ticketIds) {
        // 1. Find the user making the purchase
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Find all the specific tickets they want to buy
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);

        // 3. Check if they are actually available and calculate the total price
        double totalAmount = 0.0;
        for (Ticket ticket : tickets) {
            if ("SOLD".equals(ticket.getStatus())) {
                throw new RuntimeException("Sorry, seat " + ticket.getSeatNumber() + " is already sold!");
            }
            totalAmount += ticket.getPrice();
        }

        // 4. Create the receipt (Booking)
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTotalAmount(totalAmount);
        Booking savedBooking = bookingRepository.save(booking);

        // 5. Mark the tickets as SOLD and attach them to the receipt
        for (Ticket ticket : tickets) {
            ticket.setStatus("SOLD");
            ticket.setBooking(savedBooking);
        }
        
        // This is where Optimistic Locking kicks in! If someone else bought the ticket a millisecond before this, 
        // the system will detect the version change and crash right here, triggering the @Transactional UNDO!
        ticketRepository.saveAll(tickets);

        return savedBooking;
    }
}
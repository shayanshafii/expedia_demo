package com.expedia.demo.storage;

import com.expedia.demo.model.Booking;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BookingStorageTest {
    private BookingStorage bookingStorage;
    private Path testBookingsPath;

    @BeforeEach
    void setUp() throws IOException {
        bookingStorage = new BookingStorage();
        testBookingsPath = Paths.get("src/main/resources/bookings.json");
        
        if (Files.exists(testBookingsPath)) {
            Files.delete(testBookingsPath);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(testBookingsPath)) {
            Files.delete(testBookingsPath);
        }
    }

    @Test
    void testReadBookings_EmptyFile() {
        List<Booking> bookings = bookingStorage.readBookings();
        
        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void testSaveBooking_Success() {
        Booking booking = new Booking();
        booking.setUserId("user-123");
        booking.setFlightId("flight-123");
        booking.setPassengerName("John Doe");
        booking.setPassengerEmail("john@example.com");
        booking.setStatus("PENDING");
        booking.setCreatedAt("2025-11-20T00:00:00");

        bookingStorage.saveBooking(booking);

        List<Booking> bookings = bookingStorage.readBookings();
        assertEquals(1, bookings.size());
        assertEquals("user-123", bookings.get(0).getUserId());
        assertEquals("flight-123", bookings.get(0).getFlightId());
    }

    @Test
    void testSaveBooking_Multiple() {
        Booking booking1 = new Booking();
        booking1.setUserId("user-1");
        booking1.setFlightId("flight-1");
        booking1.setPassengerName("John Doe");
        booking1.setPassengerEmail("john@example.com");
        booking1.setStatus("PENDING");
        booking1.setCreatedAt("2025-11-20T00:00:00");

        Booking booking2 = new Booking();
        booking2.setUserId("user-2");
        booking2.setFlightId("flight-2");
        booking2.setPassengerName("Jane Smith");
        booking2.setPassengerEmail("jane@example.com");
        booking2.setStatus("PENDING");
        booking2.setCreatedAt("2025-11-20T01:00:00");

        bookingStorage.saveBooking(booking1);
        bookingStorage.saveBooking(booking2);

        List<Booking> bookings = bookingStorage.readBookings();
        assertEquals(2, bookings.size());
    }

    @Test
    void testFindBooking_Success() {
        Booking booking = new Booking();
        booking.setUserId("user-123");
        booking.setFlightId("flight-123");
        booking.setPassengerName("John Doe");
        booking.setPassengerEmail("john@example.com");
        booking.setStatus("PENDING");
        booking.setCreatedAt("2025-11-20T00:00:00");

        bookingStorage.saveBooking(booking);

        Optional<Booking> found = bookingStorage.findBooking("user-123", "flight-123");

        assertTrue(found.isPresent());
        assertEquals("user-123", found.get().getUserId());
        assertEquals("flight-123", found.get().getFlightId());
    }

    @Test
    void testFindBooking_NotFound() {
        Optional<Booking> found = bookingStorage.findBooking("user-999", "flight-999");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindBooking_WrongUserId() {
        Booking booking = new Booking();
        booking.setUserId("user-123");
        booking.setFlightId("flight-123");
        booking.setPassengerName("John Doe");
        booking.setPassengerEmail("john@example.com");
        booking.setStatus("PENDING");
        booking.setCreatedAt("2025-11-20T00:00:00");

        bookingStorage.saveBooking(booking);

        Optional<Booking> found = bookingStorage.findBooking("user-456", "flight-123");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindBooking_WrongFlightId() {
        Booking booking = new Booking();
        booking.setUserId("user-123");
        booking.setFlightId("flight-123");
        booking.setPassengerName("John Doe");
        booking.setPassengerEmail("john@example.com");
        booking.setStatus("PENDING");
        booking.setCreatedAt("2025-11-20T00:00:00");

        bookingStorage.saveBooking(booking);

        Optional<Booking> found = bookingStorage.findBooking("user-123", "flight-456");

        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateBooking_Success() {
        Booking booking = new Booking();
        booking.setUserId("user-123");
        booking.setFlightId("flight-123");
        booking.setPassengerName("John Doe");
        booking.setPassengerEmail("john@example.com");
        booking.setStatus("PENDING");
        booking.setCreatedAt("2025-11-20T00:00:00");

        bookingStorage.saveBooking(booking);

        booking.setStatus("CONFIRMED");
        bookingStorage.updateBooking(booking);

        Optional<Booking> updated = bookingStorage.findBooking("user-123", "flight-123");

        assertTrue(updated.isPresent());
        assertEquals("CONFIRMED", updated.get().getStatus());
    }

    @Test
    void testUpdateBooking_MultipleBookings() {
        Booking booking1 = new Booking();
        booking1.setUserId("user-1");
        booking1.setFlightId("flight-1");
        booking1.setPassengerName("John Doe");
        booking1.setPassengerEmail("john@example.com");
        booking1.setStatus("PENDING");
        booking1.setCreatedAt("2025-11-20T00:00:00");

        Booking booking2 = new Booking();
        booking2.setUserId("user-2");
        booking2.setFlightId("flight-2");
        booking2.setPassengerName("Jane Smith");
        booking2.setPassengerEmail("jane@example.com");
        booking2.setStatus("PENDING");
        booking2.setCreatedAt("2025-11-20T01:00:00");

        bookingStorage.saveBooking(booking1);
        bookingStorage.saveBooking(booking2);

        booking1.setStatus("CONFIRMED");
        bookingStorage.updateBooking(booking1);

        Optional<Booking> updated1 = bookingStorage.findBooking("user-1", "flight-1");
        Optional<Booking> updated2 = bookingStorage.findBooking("user-2", "flight-2");

        assertTrue(updated1.isPresent());
        assertEquals("CONFIRMED", updated1.get().getStatus());
        assertTrue(updated2.isPresent());
        assertEquals("PENDING", updated2.get().getStatus());
    }

    @Test
    void testUpdateBooking_NonExistent() {
        Booking booking = new Booking();
        booking.setUserId("user-999");
        booking.setFlightId("flight-999");
        booking.setPassengerName("John Doe");
        booking.setPassengerEmail("john@example.com");
        booking.setStatus("CONFIRMED");
        booking.setCreatedAt("2025-11-20T00:00:00");

        bookingStorage.updateBooking(booking);

        List<Booking> bookings = bookingStorage.readBookings();
        assertTrue(bookings.isEmpty());
    }

    @Test
    void testReadBookings_AfterMultipleSaves() {
        for (int i = 0; i < 5; i++) {
            Booking booking = new Booking();
            booking.setUserId("user-" + i);
            booking.setFlightId("flight-" + i);
            booking.setPassengerName("Passenger " + i);
            booking.setPassengerEmail("passenger" + i + "@example.com");
            booking.setStatus("PENDING");
            booking.setCreatedAt("2025-11-20T0" + i + ":00:00");
            bookingStorage.saveBooking(booking);
        }

        List<Booking> bookings = bookingStorage.readBookings();
        assertEquals(5, bookings.size());
    }

    @Test
    void testSaveBooking_WithSpecialCharacters() {
        Booking booking = new Booking();
        booking.setUserId("user-123");
        booking.setFlightId("flight-123");
        booking.setPassengerName("José García");
        booking.setPassengerEmail("jose.garcia@example.com");
        booking.setStatus("PENDING");
        booking.setCreatedAt("2025-11-20T00:00:00");

        bookingStorage.saveBooking(booking);

        Optional<Booking> found = bookingStorage.findBooking("user-123", "flight-123");

        assertTrue(found.isPresent());
        assertEquals("José García", found.get().getPassengerName());
    }
}

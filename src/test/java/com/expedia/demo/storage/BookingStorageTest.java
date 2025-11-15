package com.expedia.demo.storage;

import com.expedia.demo.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BookingStorageTest {
    @TempDir
    Path tempDir;

    private BookingStorage bookingStorage;

    @BeforeEach
    void setUp() throws Exception {
        bookingStorage = new BookingStorage();
        
        Path testBookingsPath = tempDir.resolve("bookings.json");
        Field bookingsPathField = BookingStorage.class.getDeclaredField("bookingsPath");
        bookingsPathField.setAccessible(true);
        bookingsPathField.set(bookingStorage, testBookingsPath);
        
        bookingStorage.writeBookings(List.of());
    }

    @Test
    void testReadBookings_EmptyFile_ReturnsEmptyList() {
        List<Booking> bookings = bookingStorage.readBookings();
        
        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void testWriteBookings_ThenReadBookings_RoundTrip() {
        Booking booking1 = new Booking();
        booking1.setUserId("user-1");
        booking1.setFlightId("flight-1");
        booking1.setPassengerName("John Doe");
        booking1.setPassengerEmail("john@example.com");
        booking1.setStatus("PENDING");
        booking1.setCreatedAt("2025-01-01T10:00:00");

        Booking booking2 = new Booking();
        booking2.setUserId("user-2");
        booking2.setFlightId("flight-2");
        booking2.setPassengerName("Jane Smith");
        booking2.setPassengerEmail("jane@example.com");
        booking2.setStatus("CONFIRMED");
        booking2.setCreatedAt("2025-01-02T11:00:00");

        bookingStorage.writeBookings(List.of(booking1, booking2));
        
        List<Booking> bookings = bookingStorage.readBookings();
        
        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals("user-1", bookings.get(0).getUserId());
        assertEquals("user-2", bookings.get(1).getUserId());
    }

    @Test
    void testSaveBooking_AppendsToList() {
        Booking booking1 = new Booking();
        booking1.setUserId("user-1");
        booking1.setFlightId("flight-1");
        booking1.setPassengerName("John Doe");
        booking1.setPassengerEmail("john@example.com");
        booking1.setStatus("PENDING");
        booking1.setCreatedAt("2025-01-01T10:00:00");

        bookingStorage.saveBooking(booking1);
        
        Booking booking2 = new Booking();
        booking2.setUserId("user-2");
        booking2.setFlightId("flight-2");
        booking2.setPassengerName("Jane Smith");
        booking2.setPassengerEmail("jane@example.com");
        booking2.setStatus("PENDING");
        booking2.setCreatedAt("2025-01-02T11:00:00");

        bookingStorage.saveBooking(booking2);
        
        List<Booking> bookings = bookingStorage.readBookings();
        
        assertEquals(2, bookings.size());
        assertEquals("user-1", bookings.get(0).getUserId());
        assertEquals("user-2", bookings.get(1).getUserId());
    }

    @Test
    void testUpdateBooking_ReplacesMatchingBooking() {
        Booking booking1 = new Booking();
        booking1.setUserId("user-1");
        booking1.setFlightId("flight-1");
        booking1.setPassengerName("John Doe");
        booking1.setPassengerEmail("john@example.com");
        booking1.setStatus("PENDING");
        booking1.setCreatedAt("2025-01-01T10:00:00");

        Booking booking2 = new Booking();
        booking2.setUserId("user-2");
        booking2.setFlightId("flight-2");
        booking2.setPassengerName("Jane Smith");
        booking2.setPassengerEmail("jane@example.com");
        booking2.setStatus("PENDING");
        booking2.setCreatedAt("2025-01-02T11:00:00");

        bookingStorage.writeBookings(List.of(booking1, booking2));
        
        booking1.setStatus("CONFIRMED");
        bookingStorage.updateBooking(booking1);
        
        List<Booking> bookings = bookingStorage.readBookings();
        
        assertEquals(2, bookings.size());
        assertEquals("CONFIRMED", bookings.get(0).getStatus());
        assertEquals("PENDING", bookings.get(1).getStatus());
    }

    @Test
    void testUpdateBooking_LeavesOthersIntact() {
        Booking booking1 = new Booking();
        booking1.setUserId("user-1");
        booking1.setFlightId("flight-1");
        booking1.setPassengerName("John Doe");
        booking1.setPassengerEmail("john@example.com");
        booking1.setStatus("PENDING");
        booking1.setCreatedAt("2025-01-01T10:00:00");

        Booking booking2 = new Booking();
        booking2.setUserId("user-2");
        booking2.setFlightId("flight-2");
        booking2.setPassengerName("Jane Smith");
        booking2.setPassengerEmail("jane@example.com");
        booking2.setStatus("PENDING");
        booking2.setCreatedAt("2025-01-02T11:00:00");

        Booking booking3 = new Booking();
        booking3.setUserId("user-3");
        booking3.setFlightId("flight-3");
        booking3.setPassengerName("Bob Johnson");
        booking3.setPassengerEmail("bob@example.com");
        booking3.setStatus("PENDING");
        booking3.setCreatedAt("2025-01-03T12:00:00");

        bookingStorage.writeBookings(List.of(booking1, booking2, booking3));
        
        booking2.setStatus("CONFIRMED");
        bookingStorage.updateBooking(booking2);
        
        List<Booking> bookings = bookingStorage.readBookings();
        
        assertEquals(3, bookings.size());
        assertEquals("PENDING", bookings.get(0).getStatus());
        assertEquals("CONFIRMED", bookings.get(1).getStatus());
        assertEquals("PENDING", bookings.get(2).getStatus());
    }

    @Test
    void testFindBooking_Found_ReturnsOptionalWithBooking() {
        Booking booking = new Booking();
        booking.setUserId("user-1");
        booking.setFlightId("flight-1");
        booking.setPassengerName("John Doe");
        booking.setPassengerEmail("john@example.com");
        booking.setStatus("PENDING");
        booking.setCreatedAt("2025-01-01T10:00:00");

        bookingStorage.saveBooking(booking);
        
        Optional<Booking> found = bookingStorage.findBooking("user-1", "flight-1");
        
        assertTrue(found.isPresent());
        assertEquals("user-1", found.get().getUserId());
        assertEquals("flight-1", found.get().getFlightId());
    }

    @Test
    void testFindBooking_NotFound_ReturnsEmptyOptional() {
        Booking booking = new Booking();
        booking.setUserId("user-1");
        booking.setFlightId("flight-1");
        booking.setPassengerName("John Doe");
        booking.setPassengerEmail("john@example.com");
        booking.setStatus("PENDING");
        booking.setCreatedAt("2025-01-01T10:00:00");

        bookingStorage.saveBooking(booking);
        
        Optional<Booking> found = bookingStorage.findBooking("user-2", "flight-2");
        
        assertFalse(found.isPresent());
    }
}

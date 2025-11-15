package com.expedia.demo.storage;

import com.expedia.demo.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BookingStorageTest {
    @TempDir
    Path tempDir;

    private BookingStorage bookingStorage;
    private Path testBookingsPath;

    @BeforeEach
    void setUp() {
        testBookingsPath = tempDir.resolve("test-bookings.json");
        bookingStorage = new BookingStorage(testBookingsPath);
    }

    @Test
    void testInitialization_CreatesFileIfMissing() {
        assertTrue(Files.exists(testBookingsPath));
        
        List<Booking> bookings = bookingStorage.readBookings();
        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void testSaveBooking_AppendsToFile() {
        Booking booking = new Booking();
        booking.setUserId("user-1");
        booking.setFlightId("flight-1");
        booking.setPassengerName("John Doe");
        booking.setPassengerEmail("john@example.com");
        booking.setStatus("PENDING");
        booking.setCreatedAt("2025-01-01T10:00:00");

        bookingStorage.saveBooking(booking);

        List<Booking> bookings = bookingStorage.readBookings();
        assertEquals(1, bookings.size());
        assertEquals("user-1", bookings.get(0).getUserId());
        assertEquals("flight-1", bookings.get(0).getFlightId());
        assertEquals("John Doe", bookings.get(0).getPassengerName());
        assertEquals("john@example.com", bookings.get(0).getPassengerEmail());
        assertEquals("PENDING", bookings.get(0).getStatus());
    }

    @Test
    void testReadBookings_ReturnsAllBookings() {
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

        bookingStorage.saveBooking(booking1);
        bookingStorage.saveBooking(booking2);

        List<Booking> bookings = bookingStorage.readBookings();
        assertEquals(2, bookings.size());
    }

    @Test
    void testFindBooking_FindsByUserIdAndFlightId() {
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

        bookingStorage.saveBooking(booking1);
        bookingStorage.saveBooking(booking2);

        Optional<Booking> found = bookingStorage.findBooking("user-1", "flight-1");
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getPassengerName());

        Optional<Booking> notFound = bookingStorage.findBooking("user-999", "flight-999");
        assertFalse(notFound.isPresent());
    }

    @Test
    void testUpdateBooking_UpdatesCorrectRecord() {
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

        bookingStorage.saveBooking(booking1);
        bookingStorage.saveBooking(booking2);

        booking1.setStatus("CONFIRMED");
        bookingStorage.updateBooking(booking1);

        List<Booking> bookings = bookingStorage.readBookings();
        assertEquals(2, bookings.size());

        Optional<Booking> updated = bookingStorage.findBooking("user-1", "flight-1");
        assertTrue(updated.isPresent());
        assertEquals("CONFIRMED", updated.get().getStatus());

        Optional<Booking> unchanged = bookingStorage.findBooking("user-2", "flight-2");
        assertTrue(unchanged.isPresent());
        assertEquals("PENDING", unchanged.get().getStatus());
    }

    @Test
    void testReadBookings_EmptyFile() throws IOException {
        Files.writeString(testBookingsPath, "");

        List<Booking> bookings = bookingStorage.readBookings();
        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void testReadBookings_InvalidJson() throws IOException {
        Files.writeString(testBookingsPath, "{invalid json content}");

        List<Booking> bookings = bookingStorage.readBookings();
        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void testSaveMultipleBookings_MaintainsOrder() {
        for (int i = 1; i <= 5; i++) {
            Booking booking = new Booking();
            booking.setUserId("user-" + i);
            booking.setFlightId("flight-" + i);
            booking.setPassengerName("Passenger " + i);
            booking.setPassengerEmail("passenger" + i + "@example.com");
            booking.setStatus("PENDING");
            booking.setCreatedAt("2025-01-0" + i + "T10:00:00");
            bookingStorage.saveBooking(booking);
        }

        List<Booking> bookings = bookingStorage.readBookings();
        assertEquals(5, bookings.size());
        
        for (int i = 0; i < 5; i++) {
            assertEquals("user-" + (i + 1), bookings.get(i).getUserId());
        }
    }
}

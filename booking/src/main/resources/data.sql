INSERT INTO Booking (user_id, schedule_id, booking_status, seat_number, pnr, created_at, updated_at)
VALUES
(1, 5001, 'CONFIRMED', 34, 'NF92HUNF', NOW(), NOW());

ALTER TABLE Booking ALTER COLUMN booking_id RESTART WITH
(
    SELECT COALESCE(MAX(booking_id), 0) + 1 FROM Booking
);
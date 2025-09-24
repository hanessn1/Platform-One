-- Stations
INSERT INTO Station (station_id, name, code, city, state)
VALUES
(1, 'New Delhi', 'NDLS', 'Delhi', 'Delhi'),
(2, 'Kanpur Central', 'CNB', 'Kanpur', 'Uttar Pradesh'),
(3, 'Patna Junction', 'PNBE', 'Patna', 'Bihar'),
(4, 'Howrah Junction', 'HWH', 'Kolkata', 'West Bengal'),
(5, 'Mumbai Central', 'BCT', 'Mumbai', 'Maharashtra'),
(6, 'Chennai Central', 'MAS', 'Chennai', 'Tamil Nadu');

-- Trains
INSERT INTO Train (train_id, name, type, created_at, updated_at)
VALUES
(101, 'Rajdhani Express', 'EXPRESS', NOW(), NOW()),
(102, 'Duronto Express', 'EXPRESS', NOW(), NOW()),
(103, 'Shatabdi Express', 'SUPERFAST', NOW(), NOW());

-- Routes for Rajdhani (NDLS → HWH)
INSERT INTO Route (route_id, train_id, station_id, sequence_num, arrival_time, departure_time)
VALUES
(1001, 101, 1, 1, '2025-09-12 20:00:00', '2025-09-12 20:30:00'),
(1002, 101, 2, 2, '2025-09-13 02:00:00', '2025-09-13 02:10:00'),
(1003, 101, 3, 3, '2025-09-13 07:00:00', '2025-09-13 07:10:00'),
(1004, 101, 4, 4, '2025-09-13 14:00:00', '2025-09-13 14:15:00');

-- Routes for Duronto (BCT → MAS)
INSERT INTO Route (route_id, train_id, station_id, sequence_num, arrival_time, departure_time)
VALUES
(2001, 102, 5, 1, '2025-09-12 22:00:00', '2025-09-12 22:30:00'),
(2002, 102, 6, 2, '2025-09-13 12:00:00', '2025-09-13 12:15:00');

-- Routes for Shatabdi (NDLS → CNB)
INSERT INTO Route (route_id, train_id, station_id, sequence_num, arrival_time, departure_time)
VALUES
(3001, 103, 1, 1, '2025-09-12 06:00:00', '2025-09-12 06:15:00'),
(3002, 103, 2, 2, '2025-09-12 10:00:00', '2025-09-12 10:15:00');

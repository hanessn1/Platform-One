-- Stations
INSERT INTO Station (name, code, city, state)
VALUES
('New Delhi', 'NDLS', 'Delhi', 'Delhi'),
('Kanpur Central', 'CNB', 'Kanpur', 'Uttar Pradesh'),
('Patna Junction', 'PNBE', 'Patna', 'Bihar'),
('Howrah Junction', 'HWH', 'Kolkata', 'West Bengal'),
('Mumbai Central', 'BCT', 'Mumbai', 'Maharashtra'),
('Chennai Central', 'MAS', 'Chennai', 'Tamil Nadu');

-- Trains
INSERT INTO Train (name, type, created_at, updated_at)
VALUES
('Rajdhani Express', 'EXPRESS', NOW(), NOW()),
('Duronto Express', 'EXPRESS', NOW(), NOW()),
('Shatabdi Express', 'SUPERFAST', NOW(), NOW());

-- Routes for Rajdhani (NDLS → HWH)
INSERT INTO Route (train_id, station_id, sequence_num, arrival_time, departure_time)
VALUES
(1, 1, 1, '2025-09-12 20:00:00', '2025-09-12 20:30:00'),
(1, 2, 2, '2025-09-13 02:00:00', '2025-09-13 02:10:00'),
(1, 3, 3, '2025-09-13 07:00:00', '2025-09-13 07:10:00'),
(1, 4, 4, '2025-09-13 14:00:00', '2025-09-13 14:15:00');

-- Routes for Duronto (BCT → MAS)
INSERT INTO Route (train_id, station_id, sequence_num, arrival_time, departure_time)
VALUES
(2, 5, 1, '2025-09-12 22:00:00', '2025-09-12 22:30:00'),
(2, 6, 2, '2025-09-13 12:00:00', '2025-09-13 12:15:00');

-- Routes for Shatabdi (NDLS → CNB)
INSERT INTO Route (train_id, station_id, sequence_num, arrival_time, departure_time)
VALUES
(3, 1, 1, '2025-09-12 06:00:00', '2025-09-12 06:15:00'),
(3, 2, 2, '2025-09-12 10:00:00', '2025-09-12 10:15:00');

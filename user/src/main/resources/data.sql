INSERT INTO Users (name, email, hashed_password, role, created_at, updated_at)
VALUES
('Sagnik Barman', 'sagnik@example.com', '$2a$10$abc123hashSagnik', 'USER', NOW(), NOW()),
('Amit Sharma', 'amit.sharma@example.com', '$2a$10$def456hashAmit', 'USER', NOW(), NOW()),
('Priya Gupta', 'priya.gupta@example.com', '$2a$10$ghi789hashPriya', 'USER', NOW(), NOW()),
('Rahul Verma', 'rahul.verma@example.com', '$2a$10$jkl012hashRahul', 'USER', NOW(), NOW()),
('Sneha Patel', 'sneha.patel@example.com', '$2a$10$mno345hashSneha', 'USER', NOW(), NOW()),
('Arjun Iyer', 'arjun.iyer@example.com', '$2a$10$pqr678hashArjun', 'USER', NOW(), NOW()),
('Meera Khan', 'meera.khan@example.com', '$2a$10$stu901hashMeera', 'USER', NOW(), NOW()),
('admin', 'admin@platformone.com', '$2a$10$0IiLDJNNH41muQhhIRgbt.OPebGS7HgUTpwurWij5T0VSnPeSVgz.', 'ADMIN', now(), now());
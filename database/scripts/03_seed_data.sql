INSERT INTO users (username, password, email) VALUES
('admin', '$2a$10$EIXZ5Y8F5Z1u6Z1F1Z1F1eO1Z1F1Z1F1Z1F1Z1F1Z1F1Z1F1Z1F1', 'admin@banco.local'),
('user1', '$2a$10$EIXZ5Y8F5Z1u6Z1F1Z1F1eO1Z1F1Z1F1Z1F1Z1F1Z1F1Z1F1Z1F1', 'user1@banco.local'),
('user2', '$2a$10$EIXZ5Y8F5Z1u6Z1F1Z1F1eO1Z1F1Z1F1Z1F1Z1F1Z1F1Z1F1Z1F1', 'user2@banco.local');

INSERT INTO accounts (user_id, account_number, balance) VALUES
(1, '1234567890', 1000.00),
(1, '0987654321', 5000.00),
(2, '1122334455', 1500.00),
(3, '5566778899', 3000.00);

INSERT INTO transactions (account_id, amount, transaction_type) VALUES
(1, 200.00, 'DEPOSIT'),
(1, 50.00, 'WITHDRAWAL'),
(2, 1000.00, 'DEPOSIT'),
(3, 300.00, 'WITHDRAWAL'),
(4, 1500.00, 'DEPOSIT');
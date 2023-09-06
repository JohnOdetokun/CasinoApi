-- Players
INSERT INTO player (player_id,username,balance) VALUES (1234,'BobTheBuilder22',550.50);
INSERT INTO `player` (player_id,username,balance) VALUES (12345,'Jackson23',1550.50);
INSERT INTO `player` (player_id,username,balance) VALUES (123456,'Honey15',100);
INSERT INTO `player` (player_id,username,balance) VALUES (1234567,'User21',450550.50);
INSERT INTO `player` (player_id,username,balance) VALUES (12345678,'TheRealOne',55550.50);

-- Transactions
INSERT INTO `transaction` (transaction_id,player_id,amount,running_balance,transaction_type,transaction_date_time) VALUES (55432,12345678,500.0,1550.50,'WAGER','2023-09-05T19:44:35.377579Z');
INSERT INTO `transaction` (transaction_id,player_id,amount,running_balance,transaction_type,transaction_date_time) VALUES (57732,12345678,200.0,1000.00,'WIN','2023-08-05T19:44:35.377579Z');
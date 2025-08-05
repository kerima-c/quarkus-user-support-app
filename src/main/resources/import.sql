-- Create test operator user (password = operator_pass)
INSERT INTO user (DTYPE, username, password, userType)
VALUES ('OPERATOR', 'operator', '$2y$10$0jrq2cYApMrpyeHDcuEQX.0YCNcX2UoXDAYuI0aVQd119XWQaBoB2', 'OPERATOR');

-- Create test customer user (password = customer_pass)
INSERT INTO user (DTYPE, username, password, userType)
VALUES ('CUSTOMER', 'customer', '$2y$10$rbrfB6n.UhVNPfwx3vEO.eyLFQPE7Y0KjHQOPb.xBN.O3TBVEecmi', 'CUSTOMER');

--пароль для всех пользователей один и тот же: '12345'
INSERT INTO users (full_name,birth_day,email,username,password,roles)
VALUES ('Anna Borisovna Shtern', '1997-12-17','anna@mail.ru', 'anna','$2a$10$invkJ08RVQGE1gC2xZTIsemr2kzG7mVgZCEXBLq/oEAmH4d14fyBy','CLIENT'),
       ('Boris Nikolaevich Ivanov', '1967-10-01','boris@mail.ru', 'boris','$2a$10$invkJ08RVQGE1gC2xZTIsemr2kzG7mVgZCEXBLq/oEAmH4d14fyBy','CLIENT'),
       ('Ivanov I.I. ','1977-02-03','ivanov@bank.ru','ivanov','$2a$10$invkJ08RVQGE1gC2xZTIsemr2kzG7mVgZCEXBLq/oEAmH4d14fyBy','MANAGER')
ON CONFLICT DO NOTHING;

--INSERT INTO accounts(number,balance,currency_string_code_iso4217,user_id,updated_at)
--VALUES ('номер1',100.2,'RUB',(SELECT id FROM users WHERE username='anna'),now())
--ON CONFLICT DO NOTHING;
--
--INSERT INTO accounts(number,balance,currency_string_code_iso4217,user_id,updated_at)
--VALUES ('номер2',340.6,'USD',(SELECT id FROM users WHERE username='anna'),now())
--ON CONFLICT DO NOTHING;
--
--INSERT INTO accounts(number,balance,currency_string_code_iso4217,user_id,updated_at)
--VALUES ('номер3',0,'CNY',(SELECT id FROM users WHERE username='anna'),now())
--ON CONFLICT DO NOTHING;
--
--INSERT INTO accounts(number,balance,currency_string_code_iso4217,user_id,updated_at)
--VALUES ('номер4',100,'RUB',(SELECT id FROM users WHERE username='boris'),now())
--ON CONFLICT DO NOTHING;


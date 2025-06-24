INSERT INTO currencies (id, short_name, full_name)
VALUES (1,'RUR','рубль'), (2,'USD', 'доллар США'), (3,'CNY','юань');

INSERT INTO users (full_name,birth_day,email,username,password,roles)
VALUES ('Anna Borisovna Shtern', '1997-12-17','anna@mail.ru', 'anna','anna','USER'),
       ('Boris Nikolaevich Ivanov', '1967-10-01','boris@mail.ru', 'boris','boris','USER;ADMIN');
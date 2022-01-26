INSERT IGNORE INTO ingredients(name)
VALUES ('White garlic sauce'), ('Mozzarella'), ('Cheddar'), ('Ingredient');

INSERT IGNORE INTO products(name, type, price, img_data)
VALUES ('BBQ CHICKEN BACON FEAST', 'pizza', 14, ''),
       ('BUTTER CHICKEN FEAST', 'pizza', 14, ''),
       ('COMBO FEAST', 'pizza', 14, '');

INSERT IGNORE INTO products(name, type, price, img_data, size)
VALUES ('WATER', 'drink', 2, '', 250),
       ('PEPSI', 'drink', 2, '', 250),
       ('7UP', 'drink', 5, '', 1000);

INSERT IGNORE INTO product_ingredients(product_id, ingredient_id)
VALUES (1, 1), (1, 2), (2, 3), (2, 2), (3, 1), (3, 2), (3, 3);

INSERT IGNORE INTO users(email, password, enabled, role, name, phone, country, city, street, house)
 VALUES ('atolepko@gmail.com', '12345678', TRUE, 'ADMIN', 'Alexander', '375334455785', 'Belarus',
        'Minsk', 'Nemiga', '3'),
        ('altolpeko@gmail.com', '12345678', TRUE, 'USER', 'Alexander', '375334455445', 'Belarus',
         'Minsk', 'Nemiga', 3),
        ('alexandertolpeko@gmail.com', '12345678', TRUE, 'USER', 'Alexander', '375334455445', 'Belarus',
         'Minsk', 'Nemiga', 3);


INSERT IGNORE INTO orders(user_id, creation_time, price, message)
VALUES (2, TIMESTAMP('2021-01-25',  '13:10:00'), 100, NULL);

INSERT IGNORE INTO order_products(order_id, product_id, count)
VALUES (1, 1, 1), (1, 2, 1), (1, 5, 2)

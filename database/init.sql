CREATE TABLE catalogs ( 
 id_catalog serial4 NOT NULL, 
 name_catalog varchar NULL, 
 CONSTRAINT catalogs_pk PRIMARY KEY (id_catalog) 
); 

CREATE TABLE manufacturers ( 
 id_manufacturer serial4 NOT NULL, 
 name_manufacturer varchar NULL, 
 CONSTRAINT manufacturers_pk PRIMARY KEY (id_manufacturer) 
); 

CREATE TABLE order_status ( 
 id_status serial4 NOT NULL, 
 name_status varchar NOT NULL, 
 CONSTRAINT order_status_pk PRIMARY KEY (id_status) 
); 

CREATE TABLE pick_up_points ( 
 id_pick_up_point serial4 NOT NULL, 
 name_pick_up_point varchar NULL, 
 CONSTRAINT pick_up_points_pk PRIMARY KEY (id_pick_up_point) 
); 

CREATE TABLE providers ( 
 id_provider serial4 NOT NULL, 
 name_provider varchar NULL, 
 CONSTRAINT providers_pk PRIMARY KEY (id_provider) 
); 

CREATE TABLE roles ( 
 id_role int4 NOT NULL, 
 name_role varchar NULL, 
 CONSTRAINT roles_pk PRIMARY KEY (id_role) 
); 

CREATE TABLE products ( 
 id_product serial4 NOT NULL, 
 name_product varchar NULL, 
 quantity_product int4 NULL, 
 id_manufacturer serial4 NOT NULL, 
 id_provider serial4 NOT NULL, 
 id_catalog serial4 NOT NULL, 
 price int4 NULL, 
 image varchar NULL, 
 CONSTRAINT products_pk PRIMARY KEY (id_product), 
 CONSTRAINT products_fk FOREIGN KEY (id_manufacturer) REFERENCES manufacturers(id_manufacturer) ON DELETE CASCADE ON UPDATE CASCADE, 
 CONSTRAINT products_fk_1 FOREIGN KEY (id_provider) REFERENCES providers(id_provider) ON DELETE CASCADE ON UPDATE CASCADE, 
 CONSTRAINT products_fk_2 FOREIGN KEY (id_catalog) REFERENCES catalogs(id_catalog) ON DELETE CASCADE ON UPDATE CASCADE 
); 

CREATE TABLE users ( 
 id_user serial4 NOT NULL, 
 name_user varchar NULL, 
 surname_user varchar NULL, 
 login varchar NULL, 
 "password" varchar NULL, 
 id_role int4 NOT NULL, 
 CONSTRAINT users_pk PRIMARY KEY (id_user), 
 CONSTRAINT users_fk FOREIGN KEY (id_role) REFERENCES roles(id_role) ON DELETE CASCADE ON UPDATE CASCADE 
); 

CREATE TABLE orders ( 
 id_order serial4 NOT NULL, 
 id_user serial4 NOT NULL, 
 id_pick_up_point serial4 NOT NULL, 
 id_status serial4 NOT NULL, 
 CONSTRAINT order_pk PRIMARY KEY (id_order), 
 CONSTRAINT order_fk FOREIGN KEY (id_user) REFERENCES users(id_user) ON DELETE CASCADE ON UPDATE CASCADE, 
 CONSTRAINT order_fk_1 FOREIGN KEY (id_pick_up_point) REFERENCES pick_up_points(id_pick_up_point) ON DELETE CASCADE ON UPDATE CASCADE, 
 CONSTRAINT orders_fk FOREIGN KEY (id_status) REFERENCES order_status(id_status) ON DELETE CASCADE ON UPDATE CASCADE 
); 

CREATE TABLE order_details ( 
 id_order serial4 NOT NULL, 
 order_details_date date NULL, 
 order_details_id_product serial4 NOT NULL, 
 order_details_quantity_product int4 NULL, 
 CONSTRAINT order_details_fk FOREIGN KEY (id_order) REFERENCES orders(id_order) ON DELETE CASCADE ON UPDATE CASCADE, 
 CONSTRAINT order_details_fk_1 FOREIGN KEY (order_details_id_product) REFERENCES products(id_product) ON DELETE CASCADE ON UPDATE CASCADE 
); 

CREATE OR REPLACE FUNCTION get_order_status_counts() 
 RETURNS TABLE(processing_count integer, completed_count integer, cancelled_count integer) 
 LANGUAGE plpgsql 
AS $function$ 
BEGIN 
 RETURN QUERY 
     SELECT  
         (SELECT COUNT(*)::INT FROM orders WHERE id_status = (SELECT id_status FROM order_status WHERE name_status = 'В обработке')) AS processing_count, 
         (SELECT COUNT(*)::INT FROM orders WHERE id_status = (SELECT id_status FROM order_status WHERE name_status = 'Выполнен')) AS completed_count, 
         (SELECT COUNT(*)::INT FROM orders WHERE id_status = (SELECT id_status FROM order_status WHERE name_status = 'Отменен')) AS cancelled_count; 
END; $function$ 
; 

CREATE OR REPLACE FUNCTION check_product_quantity_func() 
RETURNS TRIGGER AS $$ 
BEGIN 
 IF NEW.quantity_product = 0 THEN 
   DELETE FROM products WHERE id_product = NEW.id_product; 
 END IF; 
 RETURN NEW; 
END; 
$$ LANGUAGE plpgsql; 
 
CREATE TRIGGER check_product_quantity 
AFTER UPDATE ON products 
FOR EACH ROW 
EXECUTE PROCEDURE check_product_quantity_func(); 

-- Заполнение таблицы catalogs 
INSERT INTO catalogs (name_catalog) VALUES 
('Каталог 1'), 
('Каталог 2'), 
('Каталог 3'), 
('Каталог 4'), 
('Каталог 5'); 
 
-- Заполнение таблицы manufacturers 
INSERT INTO manufacturers (name_manufacturer) VALUES 
('Производитель 1'), 
('Производитель 2'), 
('Производитель 3'), 
('Производитель 4'), 
('Производитель 5'); 
 
-- Заполнение таблицы order_status 
INSERT INTO order_status (name_status) VALUES 
('В обработке'), 
('Выполнен'), 
('Отменен'); 
 
-- Заполнение таблицы pick_up_points 
INSERT INTO pick_up_points (name_pick_up_point) VALUES 
('Пункт выдачи 1'), 
('Пункт выдачи 2'), 
('Пункт выдачи 3'), 
('Пункт выдачи 4'), 
('Пункт выдачи 5'); 
 
-- Заполнение таблицы providers 
INSERT INTO providers (name_provider) VALUES 
('Поставщик 1'), 
('Поставщик 2'), 
('Поставщик 3'), 
('Поставщик 4'), 
('Поставщик 5'); 
 
-- Заполнение таблицы roles 
INSERT INTO roles (id_role, name_role) VALUES 
(1,'Админ'), 
(2,'Пользователь'); 
 
-- Заполнение таблицы products 
INSERT INTO products (name_product, quantity_product, id_manufacturer, id_provider, id_catalog, price, image) VALUES 
('Продукт 1', 10, 1, 1, 1, 100, 'image1.jpg'), 
('Продукт 2', 20, 2, 2, 2, 200, 'image2.jpg'), 
('Продукт 3', 30, 3, 3, 3, 300, 'image3.jpg'), 
('Продукт 4', 40, 4, 4, 4, 400, 'image4.jpg'), 
('Продукт 5', 50, 5, 5, 5, 500, 'image5.jpg'); 
 
-- Заполнение таблицы users 
INSERT INTO users (name_user, surname_user, login, "password", id_role) VALUES 
('Имя 1', 'Фамилия 1', 'login1', 'password1', 1), 
('Имя 2', 'Фамилия 2', 'login2', 'password2', 2), 
('Имя 3', 'Фамилия 3', 'login3', 'password3', 1), 
('Имя 4', 'Фамилия 4', 'login4', 'password4', 2), 
('Имя 5', 'Фамилия 5', 'login5', 'password5', 1); 
 
-- Заполнение таблицы orders 
INSERT INTO orders (id_user, id_pick_up_point, id_status) VALUES 
(1, 1, 1), 
(2, 2, 2), 
(3, 3, 3), 
(4, 4, 1), 
(5, 5, 2); 
 
-- Заполнение таблицы order_details 
INSERT INTO order_details (id_order, order_details_date, order_details_id_product, order_details_quantity_product) VALUES 
(1, '2023-11-25', 1, 2), 
(2, '2023-11-24', 2, 3), 
(3, '2023-11-23', 3, 4), 
(4, '2023-11-22', 4, 5), 
(5, '2023-11-21', 5, 6);
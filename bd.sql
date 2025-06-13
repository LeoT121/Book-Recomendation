-- Eliminar la base de datos "practica2" si ya existe
DROP DATABASE IF EXISTS books;

-- Crear la base de datos "practica2" con codificación UTF-8
CREATE DATABASE books CHARACTER SET utf8 COLLATE utf8_general_ci;

-- Usar la base de datos "practica2"
USE books;

-- Crear la tabla de usuarios
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL
);

-- Crear la tabla de roles
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL UNIQUE
);

-- Crear la tabla intermedia para la relación muchos a muchos entre usuarios y roles
CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Crear la tabla de libros favoritos
CREATE TABLE favorite_books (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    book_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    image_url VARCHAR(1024),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_book (user_id, book_id)
);

-- Insertar roles en la tabla roles
INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

-- Eliminar el usuario 'admin' si ya existe
DROP USER IF EXISTS 'admin'@'localhost';
FLUSH PRIVILEGES;

-- Crear el usuario 'admin' con la contraseña 'admin'
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';

-- Otorgar todos los permisos sobre la base de datos "practica2" al usuario 'admin'
GRANT ALL PRIVILEGES ON practica2.* TO 'admin'@'localhost';

-- Aplicar los cambios
FLUSH PRIVILEGES;

-- Para el BLOB:
ALTER TABLE users ADD COLUMN image LONGBLOB;

-- Crear usuario administrador (la contraseña 'secreto' está encriptada con BCrypt)
INSERT INTO users (name, email, password) 
VALUES ('administrador', 'admin@sistema.com', '$2a$10$TRQFRRFbVMQGUvEZ.gE07OXnCHcr0nFbWO6CXk4QTg7A8QyGb3RMO');

-- Obtener el ID del usuario recién creado
SET @admin_id = LAST_INSERT_ID();

-- Asignar el rol de administrador al usuario
INSERT INTO user_roles (user_id, role_id) 
SELECT @admin_id, id FROM roles WHERE name = 'ROLE_ADMIN';

-- Agregar un libro favorito para el administrador
INSERT INTO favorite_books (user_id, book_id, title, author, image_url, date) 
VALUES (@admin_id, '/works/OL26365569W', 'Yzklerin Efendisi 1', 'J.R.R. Tolkien', 
        'https://covers.openlibrary.org/b/id/12308941-M.jpg', NOW());
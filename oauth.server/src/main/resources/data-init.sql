-- Script de inicialización para PostgreSQL
-- Base de datos: oauth_db

-- Crear roles básicos
INSERT INTO roles (name, description) VALUES ('ADMIN', 'Administrador del sistema con acceso completo');
INSERT INTO roles (name, description) VALUES ('USER', 'Usuario estándar con permisos de lectura');
INSERT INTO roles (name, description) VALUES ('MANAGER', 'Gestor con permisos de escritura');

CREATE DATABASE IF NOT EXISTS taller_mecanico CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE taller_mecanico;
-- IDs supplied by the Java application as RFC 9562 UUIDv7, stored as BINARY(16).
-- No AUTO_INCREMENT and no physical DELETE operations.
CREATE TABLE usuarios (
 id BINARY(16) NOT NULL PRIMARY KEY, activo BOOLEAN NOT NULL DEFAULT TRUE,
 email VARCHAR(254) NOT NULL UNIQUE, password_hash VARCHAR(255) NOT NULL,
 rol ENUM('ADMINISTRADOR_TALLER','RECEPCIONISTA','MECANICO') NULL
);
CREATE TABLE talleres (
 id BINARY(16) NOT NULL PRIMARY KEY, activo BOOLEAN NOT NULL DEFAULT TRUE,
 rfc VARCHAR(13) NOT NULL UNIQUE, nombre_comercial VARCHAR(150) NOT NULL,
 calle VARCHAR(250) NOT NULL, codigo_postal VARCHAR(10) NOT NULL,
 colonia VARCHAR(100) NOT NULL, municipio VARCHAR(100) NOT NULL, estado VARCHAR(100) NOT NULL,
 fotografia VARCHAR(500) NULL
);
CREATE TABLE clientes (
 id BINARY(16) NOT NULL PRIMARY KEY, activo BOOLEAN NOT NULL DEFAULT TRUE,
 taller_id BINARY(16) NOT NULL,
 nombre VARCHAR(100) NOT NULL, apellido VARCHAR(100) NOT NULL,
 email VARCHAR(254) NOT NULL UNIQUE, telefono VARCHAR(20) NOT NULL UNIQUE,
 CONSTRAINT fk_cliente_taller FOREIGN KEY (taller_id) REFERENCES talleres(id)
);
CREATE TABLE vehiculos (
 id BINARY(16) NOT NULL PRIMARY KEY, activo BOOLEAN NOT NULL DEFAULT TRUE,
 cliente_id BINARY(16) NOT NULL, vin VARCHAR(17) NOT NULL UNIQUE, placa VARCHAR(20) NOT NULL UNIQUE,
 marca VARCHAR(60) NOT NULL, modelo VARCHAR(60) NOT NULL, anio SMALLINT NOT NULL,
 version VARCHAR(60) NOT NULL, color VARCHAR(40) NOT NULL,
 CONSTRAINT fk_vehiculo_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
 CONSTRAINT ck_vehiculo_anio CHECK (anio BETWEEN 1886 AND 2100)
);
CREATE TABLE fotos_vehiculo (
 id BINARY(16) NOT NULL PRIMARY KEY, activo BOOLEAN NOT NULL DEFAULT TRUE,
 vehiculo_id BINARY(16) NOT NULL, ruta VARCHAR(500) NOT NULL, tipo VARCHAR(80) NOT NULL,
 CONSTRAINT fk_foto_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id)
);
CREATE INDEX ix_clientes_activo_nombre ON clientes(activo,nombre);
CREATE INDEX ix_vehiculos_activo_marca ON vehiculos(activo,marca);

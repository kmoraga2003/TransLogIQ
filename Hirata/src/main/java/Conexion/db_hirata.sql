create database bd_hirata;
use bd_hirata;

CREATE TABLE conductor (
    id_conductor INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100)
);

CREATE TABLE camion (
    patente VARCHAR(10) PRIMARY KEY,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    anio INT,
    kilometraje_actual INT,
    id_conductor INT,
    FOREIGN KEY (id_conductor) REFERENCES conductor(id_conductor)
);

CREATE TABLE mantenimiento (
    id_mantenimiento INT PRIMARY KEY AUTO_INCREMENT,
    patente_camion VARCHAR(10),
    fecha DATE,
    descripcion VARCHAR(200),
    FOREIGN KEY (patente_camion) REFERENCES camion(patente)
);




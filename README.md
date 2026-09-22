# 🚛 TransLogIQ — Sistema de Gestión de Flotas, Telemetría GPS y Mantenimiento

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/)
[![Build Status](https://img.shields.io/badge/Build-Success-brightgreen.svg)]()
[![Testing](https://img.shields.io/badge/Tests-19%20Passed%20%2F%20JUnit%205-green.svg)]()
[![Security](https://img.shields.io/badge/Security-AES%20%2F%20SHA--256-orange.svg)]()

**TransLogIQ** es una solución tecnológica integral de **Gestión de Flotas (FMS) y Telemetría en Tiempo Real** desarrollada en **Java puro**. Permite a las empresas de logística y transporte monitorear vehículos en vivo, controlar kilometrajes, automatizar alertas de mantenimiento preventivo y gestionar conductores desde una interfaz centralizada y segura.

---

## 📌 Características Principales

### 📡 1. Monitoreo GPS & Telemetría 2D en Tiempo Real
- **Mapa Canvas Interactivo:** Renderizado visual 2D (`Graphics2D`) con mapa de red vial, escala y brújula.
- **Telemetría en Vivo:** Marcadores dinámicos que indican velocidad, dirección (rumbo), coordenadas y estado operativo del vehículo:
  - 🟢 **En Ruta** (Operación normal)
  - 🟠 **Detenido** (Vehículo estacionado o en descanso)
  - 🔴 **Alerta Velocidad** (Exceso de velocidad > 90 km/h)
- **Simulador Integrado:** Controles interactivas para iniciar/pausar la simulación GPS y detonar alertas de velocidad de prueba.

### 🛠 2. Control de Mantenimiento Preventivo & Alertas
- **Detección Automática de Mantenimiento:** Genera alertas automáticas para vehículos al superar los **5.000 km**.
- **Historial de Servicios:** Registro completo de mantenimientos mecánicos, cambios de lubricantes y reparaciones.

### 🚛 3. Gestión Integral de Vehículos y Conductores (CRUD Completo)
- **Gestión de Camiones:** Alta, edición, eliminación y consulta de vehículos (Patente, Marca, Modelo, Año, Km).
- **Gestión de Conductores:** Registro de choferes autorizados y asignación dinámica a cada unidad de la flota.
- **Control de Kilometraje:** Actualización rápida de kilometrajes por recorrido.

### 🔒 4. Seguridad de Grado Empresarial (`.env` + AES/SHA-256)
- **Archivo de Entorno Cifrado (`.env`):** Almacena claves secretas y contraseñas de base de datos cifradas mediante **AES-128**.
- **Hashing SHA-256:** Verificación segura de contraseñas de usuarios.
- **Acceso Controlado:** Pantalla de autenticación previa al acceso a la interfaz principal.

### ⚡ 5. Arquitectura Híbrida y Alta Disponibilidad
- **Modo Dual (MySQL + Fallback Autónomo):** Se conecta a una base de datos MySQL local o remota. En caso de falta de conectividad, conmuta automáticamente a almacenamiento local en memoria para garantizar que el sistema nunca deje de funcionar.

---

## 🔑 Credenciales de Acceso Estándar (Entorno de Pruebas)

Al iniciar la aplicación se presentará la pantalla de inicio de sesión:

| Campo | Credencial |
| :--- | :--- |
| **Usuario** | `admin` |
| **Contraseña** | `1234` |

---

## 🏗 Estructura del Proyecto

```text
TransLogIQ/
├── .env                       # Archivo de configuración cifrado
├── pom.xml                    # Configuración de Maven y dependencias
├── src/
│   ├── main/java/
│   │   ├── Conexion/          # Gestor de conexión a MySQL y Fallback
│   │   ├── dao/               # Objetos de Acceso a Datos (CamionDAO, ConductorDAO, MantenimientoDAO)
│   │   ├── modulo/            # Modelos de Dominio (Camion, Conductor, Mantenimiento, UbicacionGPS)
│   │   ├── seguridad/         # Módulo de Cifrado (CryptoUtil, EnvConfig, AuthService)
│   │   ├── service/           # Servicio de Simulación y Telemetría (GpsService)
│   │   ├── st/hirata/         # Punto de entrada principal (Hirata.java)
│   │   └── vista/             # Interfaz Gráfica Swing (VistaLogin, VistaFlota, PanelGPS)
│   └── test/java/             # Suite de Pruebas Unitarias JUnit 5 (19 Tests)
```

---

## 🚀 Requisitos e Instalación

### Prerrequisitos
- **Java JDK 17** o superior.
- **Apache Maven 3.8+**

### 1. Clonar o Descargar el Proyecto
```bash
cd TransLogIQ/Hirata
```

### 2. Compilar la Aplicación
```bash
mvn clean compile
```

### 3. Ejecutar la Aplicación
```bash
mvn exec:java
```

---

## 🧪 Pruebas UnitariasAutomatizadas

El proyecto incluye **19 pruebas unitarias** desarrolladas con **JUnit 5**, cubriendo modelos de datos, servicios de telemetría, seguridad de contraseñas y operaciones CRUD.

Para ejecutar la suite completa de pruebas:
```bash
mvn test
```

### Resultados Esperados:
```text
[INFO] Running seguridad.SecurityTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running dao.CamionDAOTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running service.GpsServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] Results:
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 🗄 Base de Datos (Opcional - Script SQL)

Si deseas conectar el proyecto a una base de datos MySQL física, ejecuta el siguiente script en tu servidor MySQL:

```sql
CREATE DATABASE bd_hirata;
USE bd_hirata;

CREATE TABLE conductor (
    id_conductor INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE camion (
    patente VARCHAR(10) PRIMARY KEY,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    anio INT NOT NULL,
    kilometraje_actual INT NOT NULL,
    id_conductor INT,
    FOREIGN KEY (id_conductor) REFERENCES conductor(id_conductor) ON DELETE SET NULL
);

CREATE TABLE mantenimiento (
    id_mantenimiento INT PRIMARY KEY AUTO_INCREMENT,
    patente_camion VARCHAR(10) NOT NULL,
    fecha DATE NOT NULL,
    descripcion VARCHAR(250) NOT NULL,
    FOREIGN KEY (patente_camion) REFERENCES camion(patente) ON DELETE CASCADE
);
```

---

## 📄 Licencia

Este proyecto está disponible para distribución B2B y comercialización de software empresarial.

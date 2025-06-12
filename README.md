TelasTech360 - API Backend
API de backend para el sistema de gestión de producción textil TelasTech360. Este proyecto proporciona una base robusta para la administración de clientes, proveedores, insumos, productos, inventarios y todo el ciclo de producción y ventas.

🚀 Características Principales
Autenticación y Seguridad: Sistema seguro basado en tokens JWT para la autenticación y autorización basada en roles y permisos específicos para cada endpoint.
Gestión de Clientes: Operaciones CRUD completas para clientes y sus contactos asociados.
Gestión de Empleados y Usuarios: Administración de empleados y sus respectivas cuentas de usuario en el sistema.
Gestión de Proveedores: CRUD para la administración de proveedores de insumos.
Manejo de Catálogos: Gestión de Insumos (materias primas) y Productos terminados, incluyendo la Lista de Materiales (BOM) para cada producto.
Ciclo de Ventas: Creación, consulta, actualización y anulación de Órdenes de Venta.
Ciclo de Compras: Creación, consulta, actualización y anulación de Órdenes de Compra a proveedores.
Ciclo de Producción: Generación de Órdenes de Producción a partir de las ventas, con gestión detallada de tareas por empleado.
Control de Inventario: Manejo de stock para insumos y productos terminados, con registro de entradas y salidas.
Alertas de Stock: Sistema automatizado que genera alertas cuando el inventario de un ítem cae por debajo de un umbral predefinido.
Documentación de API: Documentación interactiva y completa generada con Springdoc (Swagger).
🛠️ Tecnologías Utilizadas
Java 21
Spring Boot 3.4.5
Spring Data JPA (Hibernate): Para la persistencia de datos.
Spring Security: Para la autenticación y autorización.
PostgreSQL: Base de datos relacional.
Maven: Para la gestión de dependencias y construcción del proyecto.
JSON Web Tokens (JWT): Para la seguridad de la API.
Springdoc OpenAPI (Swagger): Para la documentación de la API.
📋 Prerrequisitos
Antes de comenzar, asegúrate de tener instalado lo siguiente:

JDK 21 o superior.
Maven 3.x o superior.
Una instancia de PostgreSQL en ejecución.
⚙️ Configuración
Crear la Base de Datos:
Abre tu cliente de PostgreSQL (como pgAdmin o psql) y crea una nueva base de datos. El nombre configurado por defecto es TelasTech360.

SQL

CREATE DATABASE "TelasTech360";
Configurar la Conexión:
Abre el archivo src/main/resources/application.properties y ajusta las siguientes propiedades para que coincidan con tu configuración local de PostgreSQL:

Properties

spring.datasource.url=jdbc:postgresql://localhost:5432/TelasTech360
spring.datasource.username=postgres
spring.datasource.password=1234
Reemplaza postgres y 1234 con tu usuario y contraseña de PostgreSQL si son diferentes.

Clave Secreta JWT:
Por seguridad, se recomienda cambiar la clave secreta de JWT en el mismo archivo application.properties. Busca la propiedad jwt.secret y reemplaza el valor por una cadena de caracteres larga y segura.

Properties

jwt.secret=TuNuevaClaveSuperSecretaLargaYComplejaQueNadieAdivinara
🚀 Instalación y Ejecución
Sigue estos pasos para construir y ejecutar el backend:

Clonar el Repositorio (si aplica):

Bash

git clone https://<tu-repositorio>/CRM_Backend_Definitive.git
cd CRM_Backend_Definitive
Construir el Proyecto:
Usa Maven para compilar el proyecto y descargar todas las dependencias necesarias. Esto validará que el proyecto esté correctamente configurado.

Bash

mvn clean install
Este comando compilará el código, ejecutará las pruebas (como Crmtech360BackendApplicationTests) y empaquetará la aplicación en un archivo .jar dentro del directorio target/.

Ejecutar la Aplicación:
Una vez que la construcción sea exitosa (BUILD SUCCESS), puedes iniciar el servidor con el siguiente comando de Spring Boot:

Bash

mvn spring-boot:run
El servidor se iniciará y estará escuchando en el puerto 8080 por defecto. Verás en la consola un mensaje similar a este, indicando que la aplicación está lista:

...  INFO ... --- [crmtech360-backend] [           main] c.c.c.Crmtech360BackendApplication       : Started Crmtech360BackendApplication in X.XXX seconds...
📚 Documentación de la API (Swagger)
Una vez que la aplicación esté en ejecución, puedes acceder a la documentación interactiva de la API a través de Swagger UI.

Abre tu navegador y ve a la siguiente URL:

http://localhost:8080/swagger-ui.html

Desde esta interfaz podrás:

Ver todos los endpoints disponibles.
Probar cada endpoint directamente desde el navegador.
Ver los esquemas de los DTOs de solicitud (Request) y respuesta (Response).
Autenticarte usando el botón Authorize para acceder a los endpoints protegidos. Deberás iniciar sesión primero a través del endpoint /api/v1/auth/login para obtener un token JWT.
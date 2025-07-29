# Usa una imagen base con JDK para compilar la aplicación.
FROM eclipse-temurin:17-jdk-focal AS build

# Establece el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Copia el archivo pom.xml para que Maven pueda descargar las dependencias.
# Esto permite que Docker cachee las capas de dependencias si el pom.xml no cambia.
COPY pom.xml .

# Descarga las dependencias de Maven.
# El comando 'dependency:go-offline' descarga todas las dependencias sin construir el proyecto.
# Esto es útil para separar la capa de dependencias de la capa de código fuente.
RUN mvn dependency:go-offline -B

# Copia el código fuente de la aplicación.
COPY src ./src

# Empaqueta la aplicación Spring Boot en un archivo JAR.
# '-DskipTests' omite la ejecución de las pruebas para acelerar la construcción de la imagen.
# 'clean install' asegura una construcción limpia.
RUN mvn clean install -DskipTests

# --- Etapa de Ejecución (Run Stage) ---
# Usa una imagen base más ligera con solo JRE para la ejecución de la aplicación.
# Esto resulta en una imagen final más pequeña.
FROM eclipse-temurin:17-jre-focal

# Establece el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Copia el archivo JAR generado desde la etapa de construcción.
# El nombre del JAR debe coincidir con el nombre final de tu artefacto Spring Boot.
COPY --from=build /app/target/microservicio-producto.jar microservicio-producto.jar

# Expone el puerto en el que se ejecutará la aplicación.
# Asegúrate de que este puerto coincida con 'server.port' en tu application.properties.
EXPOSE 8084

# Define el comando de entrada para ejecutar la aplicación JAR.
# 'java -jar' ejecuta el archivo JAR.
ENTRYPOINT ["java", "-jar", "microservicio-producto.jar"]

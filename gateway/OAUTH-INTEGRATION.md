# Integración Gateway con OAuth2 Server

## Configuración Completada

Se ha integrado exitosamente el OAuth2 Server con el Gateway. Ahora todas las peticiones al Gateway requieren autenticación OAuth2.

## Arquitectura

```
Cliente (Browser/App)
    ↓
Gateway (Puerto 8123) - Docker Container - Requiere OAuth2
    ↓ (redirige si no autenticado)
OAuth Server (Puerto 9000) - Host Machine (fuera de Docker) - Login
    ↓ (retorna con token)
Gateway - Autorizado
    ↓
Microservicios (8081-8084) - Docker Containers en pharmacy-network
```

**Nota Importante**: El OAuth Server corre en la máquina host (fuera de Docker), mientras que el Gateway y los microservicios corren en contenedores Docker conectados por la red `pharmacy-network`.

## Flujo de Autenticación

1. **Usuario accede a cualquier ruta del Gateway** (ej: `http://localhost:8123/api/catalog/productos`)
2. **Gateway detecta que no hay sesión autenticada**
3. **Gateway redirige automáticamente a** `http://localhost:9000/login`
4. **Usuario ingresa credenciales** en el OAuth Server
5. **OAuth Server genera Access Token JWT**
6. **Gateway recibe el token** y crea sesión
7. **Gateway permite el acceso** a los recursos protegidos
8. **Gateway propaga el token** a los microservicios downstream

## Configuración Realizada

### Gateway (`gateway/pom.xml`)
- ✅ Agregada dependencia `spring-boot-starter-oauth2-client`
- ✅ Agregada dependencia `spring-boot-starter-security`
- ✅ Agregada dependencia `spring-boot-starter-oauth2-resource-server`

### Gateway (`gateway/src/main/resources/application.yaml`)
- ✅ Configurado OAuth2 Client con `client-id: test` y `client-secret: 123456`
- ✅ Configurados endpoints del OAuth Server (puerto 9000)
- ✅ Agregado CORS para permitir comunicación con OAuth Server

### Gateway - Clases Java Creadas
- ✅ `SecurityConfig.java` - Protege todas las rutas excepto `/actuator/**`
- ✅ `TokenRelayFilter.java` - Propaga el Access Token a microservicios
- ✅ `AuthController.java` - Endpoints para verificar autenticación y obtener token

### OAuth Server (`oauth.server/SecurityConfig.java`)
- ✅ Agregado redirect URI para el Gateway: `http://localhost:8123/login/oauth2/code/gateway-client`
- ✅ Agregado post-logout redirect URI: `http://localhost:8123`

## Cómo Usar

### 1. Iniciar OAuth Server (en la máquina host)

```bash
# El OAuth Server debe correr FUERA de Docker
cd oauth.server
mvn spring-boot:run

# Verifica que esté corriendo en http://localhost:9000
```

### 2. Iniciar servicios con Docker Compose

```bash
# Desde la raíz del proyecto
docker-compose up -d

# O rebuild si hiciste cambios
docker-compose up -d --build

# Ver logs del gateway
docker-compose logs -f gateway
```

### 3. Verificar que todos los servicios estén corriendo

```bash
docker-compose ps
```

Deberías ver:
- gateway (8123)
- productos-service (8081)
- almacen-service (8082)
- ventas-service (8083)
- reportes-service (8084)
- 4 bases de datos PostgreSQL

Cuando accedas a cualde los usuarios que tienes configurados en la base de datos PostgreSQL del OAuth Server:
- Base de datos: `oauth_db` (puerto 5433)
- Los usuarios están en la tabla de usuarios gestionada por el `CustomUserDetailsService`

### 4. Acceder a
http://localhost:8123/api/catalog/productos
    ↓ (redirige a)
http://localhost:9000/login
```

### 3. Credenciales de Login

Usa las credenciales configuradas en tu OAuth Server (base de datos PostgreSQL).

### 4. Endpoints Útiles del Gateway

- **Home (verificar sesión)**: `http://localhost:8123/`
- **Obtener token actual**: `http://localhost:8123/token`
- **Productos**: `http://localhost:8123/api/catalog/productos`
- **Almacén**: `http://localhost:8123/api/inventory/...`
- **Ventas**: `http://localhost:8123/api/sales/...`
- **Reportes**: `http://localhost:8123/api/reporting/...`

## Verificar Funcionamiento

1. **Intenta acceder sin autenticación**:
   ```bash
   curl http://localhost:8123/api/catalog/productos
   ```
   Respuesta: Redirección 302 a `/oauth2/authorization/gateway-client`

2. **Accede desde el navegador**:
   - Abre `http://localhost:8123/api/catalog/productos`
   - Serás redirigido al login del OAuth Server
   - Ingresa credenciales
   - Serás redirigido de vuelta al Gateway con sesión activa
   - Verás los productos

3. **Obtener información del token**:
   ```bash
   # Después de autenticarte en el navegador
   curl http://localhost:8123/token --cookie "JSESSIONID=..."
   ```

## Configuración para Docker

La configuración ya está lista para Docker. El Gateway está configurado para:

### Comunicación desde el contenedor al OAuth Server (host):
- Usa `host.docker.internal:9000` para todas las llamadas internas (token, jwks, userinfo)
- `host.docker.internal` es un DNS especial que Docker proporciona para acceder al host desde contenedores

### Redirecciones del navegador:
- Usa `localhost:9000` para `authorization-uri` porque el navegador del cliente necesita acceder al OAuth Server directamente

### Variables de Entorno en docker-compose.yml:
```yaml
environment:
  # OAuth2 Provider usa host.docker.internal para llamadas internas
  SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_GATEWAY-CLIENT_ISSUER-URI: http://host.docker.internal:9000
  SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_GATEWAY-CLIENT_TOKEN-URI: http://host.docker.internal:9000/oauth2/token
  # authorization-uri usa localhost para redirección del navegador
  SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_GATEWAY-CLIENT_AUTHORIZATION-URI: http://localhost:9000/oauth2/authorize
```

### Usuarios de PostgreSQL:
- Los usuarios ya están configurados en la base de datos PostgreSQL del OAuth Server (puerto 5433)
- El OAuth Server consume esos usuarios automáticamente
- No necesitas cambiar nada en la configuración de usuarios

## Troubleshooting

### Error: "Invalid redirect URI"
- Verifica que el redirect URI en el OAuth Server incluya: `http://localhost:8123/login/oauth2/code/gateway-client`

### Error: "No autorizado después del login"
- Verifica que el OAuth Server esté corriendo en el puerto 9000
- Verifica que las credenciales sean correctas
- Revisa los logs del OAuth Server

### Error: "CORS"
- Verifica que el Gateway esté en la lista de allowed origins del OAuth Server
- Verifica la configuración de CORS en `application.yaml` del Gateway

## Propagación del Token a Microservicios

El `TokenRelayFilter` automáticamente agrega el Access Token a los requests hacia los microservicios. Los microservicios pueden validar este token usando el endpoint de introspección del OAuth Server o configurando OAuth2 Resource Server.

Para configurar un microservicio como Resource Server:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:9000
```

## Notas de Seguridad

- El client secret está en texto plano (`{noop}123456`). En producción, usa BCrypt.
- Las URLs usan `http://localhost`. En producción, usa HTTPS.
- Configura timeouts adecuados para las sesiones.
- Implementa refresh token rotation para mayor seguridad.

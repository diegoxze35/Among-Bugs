# 📚 Documentación de la API - Among Bugs

**Base URL (Local):** `http://localhost:8080/api`

## 🔐 Autenticación y Headers
Los endpoints que requieren autenticación deben de colocar el header de **Authorization**.
* **Header Key:** `Authorization`
* **Header Value:** `Bearer <el_token_jwt>`

Para **TODOS** los end points se puede incluir el header de **Accept-language** con el valor **es-MX** para que la API
responda con mensajes en español, si no se coloca responderá con mensajes en inglés.

* **Accept-Language:** `es-MX`

## Indice
1. [Servicio de autenticación](#servicio-de-autenticación)
   - [Registrar a un jugador](#1-registrar-nuevo-jugador)
   - [Verificar el correo electrónico](#2-verificar-correo-electrónico)
   - [Iniciar sesión](#3-inicio-de-sesión)


## Servicio de autenticación
### 1. Registrar nuevo jugador
Crea un usuario en estado inactivo y envía un correo de verificación.

* **URL:** `/auth/register`
* **Método:** `POST`
* **Autenticación Requerida:** No

**Body (Request):**
```json
{
  "username": "diegoxze35",
  "email": "dmorenom2002@alumno.ipn.mx",
  "password": "TheDiesgo'sPassword"
}
```
**Body (Response)**
```json
{
    "email": "dmorenom2002@alumno.ipn.mx",
    "username": "diegoxze35",
    "message": "If dmorenom2002@alumno.ipn.mx is a valid email, well send you a verification link"
}
```

## 2. Verificar correo electrónico

Este endpoint es enviado al correo electrónico del usuario que se registró,
tiene un tiempo de vida de 15 minutos **por favor vean el código de este método en el controlador AuthController 
que está en el paquete org.ipn.mx.among.bugs.controller hay un comentario importante**

* **URL:** `/auth/verify?token=<token-generado-por-el-servidor>`
* **Método:** `GET`
* **Autenticación Requerida:** No
**Response**: Redirige a una URL, debe de redirigir a una URL del frontend

## 3. Inicio de sesión

Endpoint para que el jugador inicie sesión una vez que haya verificado su correo.

* **URL:** `auth/login`
* **Método:** `POST`
* **Autenticación Requerida:** No
**Response**: Redirige a una URL, debe de redirigir a una URL del frontend

**Body (Request):**
```json
{
  "email": "diegoalv1217@gmail.com",
  "password": "TheDiesgo'sPassword"
}
```
**Body (Response)**

**Si el usuario aún no ha verificado su correo y su token no ha expirado**
```json
{
    "message": "No has verificado tu cuenta, por favor verifica tu cuenta haciendo clic en el enlace de tu correo electrónico"
}
```
**Si el usuario aún no ha verificado su correo y su token ha expirado**
```json
{
    "message": "No verificaste tu cuenta haciendo clic en el enlace que te hemos enviado, te hemos enviado otro correo electrónico"
}
```

**Si el usuario ya verificó su correo electrónico**
La funcionalidad del token de refresco está en construcción
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImRpZWdveHplMzUiLCJyb2xlcyI6Ilt7XCJhdXRob3JpdHlcIjpcIlJPTEVfUExBWUVSXCJ9XSIsInN1YiI6IjEiLCJpYXQiOjE3NjY0ODQxNDAsImV4cCI6MTc2NjQ4Nzc0MH0.2-jQh60D0Gw9flWheapj_7PAOdH3NOjBi6mq9zFcp74",
    "refreshToken": null
}
```

**Si algo está mal en las credenciales**
````json
{
    "error": "Bad credentials"
}
````

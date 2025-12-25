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
2. [Servicio de jugadores](#servicio-de-jugadores)
   - [Obtener información de un jugador](#1-obtener-información-de-un-jugador)
3. [Servicio de juegos de Trivia](#servicio-de-trivia)
   - [Crear una trivia](#1-crear-una-trivia)
   - [Actualizar una trivia](#2-actualizar-una-trivia)
   - [Generar un reporte PDF de los juegos de trivia de un jugador](#3-generar-un-reporte-pdf-de-todos-los-juegos-trivia-de-un-jugador)


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
**Body (Response) Si todo sale bien (el usuario se registra correctamente)** 
```json
{
    "email": "dmorenom2002@alumno.ipn.mx",
    "username": "diegoxze35",
    "message": "If dmorenom2002@alumno.ipn.mx is a valid newEmail, well send you a verification link"
}
```
**Status Code: 201**

**Body (Response) Si el nombre de usuario ya existe** 
```json
{
    "message": "This newUsername is already in use, please try another one"
}
```

**Body (Response) Si el correo electrónico ya está en uso** 
```json
{
    "message": "This newEmail is already registered, please type another one"
}
```

### 2. Verificar correo electrónico

Este endpoint es enviado al correo electrónico del usuario que se registró,
tiene un tiempo de vida de 15 minutos **por favor vean el código de este método en el controlador AuthController 
que está en el paquete org.ipn.mx.among.bugs.controller hay un comentario importante**

* **URL:** `/auth/verify?token=<token-generado-por-el-servidor>`
* **Método:** `GET`
* **Autenticación Requerida:** No
**Response**: Redirige a una URL, debe de redirigir a una URL del frontend

### 3. Inicio de sesión

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
**Status Code: 200**

**Si algo está mal en las credenciales**
````json
{
    "error": "Bad credentials"
}
````

## Servicio de jugadores

### 1. Obtener información de un jugador
Obtener la información registrada de un jugador (excepto el password)

* **URL:** `/player`
* **Método:** `GET`
* **Autenticación Requerida:** Sí

**Response (Body)**
```json
{
    "username": "diegoxze35",
    "email": "dmorenom2002@alumno.ipn.mx"
}
```
**Status Code: 200**

## Servicio de Trivia

### 1. Crear una trivia
Crea una trivia, se obtiene el ID del jugador a través del token de autenticación

* **URL:** `/trivia`
* **Método:** `POST`
* **Autenticación Requerida:** Sí

**Body (Request) - Ejemplo**
````json
{
  "title": "Informática",
  "targetScore": 100,
  "description": "Preguntas básicas de informática",
  "isPublic": false,
  "questions": [
    {
      "questionText": "¿Qué significa CPU?",
      "options": [
        {
          "text": "Unidad Central de procesamiento",
          "isCorrect": true
        },
        {
          "text": "Control Parental Uniforme",
          "isCorrect": false
        },
        {
          "text": "Centro de Pedidos Universal",
          "isCorrect": false
        }
      ]
    },
    {
      "questionText": "¿Qué significa RAM?",
      "options": [
        {
          "text": "Refrescos Anónimos Maestros",
          "isCorrect": false
        },
        {
          "text": "Memoria de acceso aleatorio",
          "isCorrect": true
        },
        {
          "text": "Mercado Anual de Recompensas",
          "isCorrect": false
        }
      ]
    }
  ]
}
````

**Body (response)**
````json
{
    "id": 1,
    "targetScore": 100,
    "title": "Informática",
    "description": "Preguntas básicas de informática",
    "isPublic": false,
    "questions": [{
        "id": 1,
        "questionText": "¿Qué significa RAM?",
        "options": [{
            "text": "Refrescos Anónimos Maestros",
            "isCorrect": false
        }, {
            "text": "Memoria de acceso aleatorio",
            "isCorrect": true
        }, {
            "text": "Mercado Anual de Recompensas",
            "isCorrect": false
        }]
    }, {
        "id": 2,
        "questionText": "¿Qué significa CPU?",
        "options": [{
            "text": "Unidad Central de procesamiento",
            "isCorrect": true
        }, {
            "text": "Control Parental Uniforme",
            "isCorrect": false
        }, {
            "text": "Centro de Pedidos Universal",
            "isCorrect": false
        }]
    }]
}
````
**Status Code: 201**

### 2. Actualizar una trivia
Actualiza una trivia existente del jugador, se debe de modificar los campos existentes enviando el id en el request 
por cada trivia y question, para añadir nuevas preguntas, el id de las question debe ser null,
para eliminarlas, se debe de quitar la question existente de la request.

**Body (Request) - Ejemplo**
````json
{
    "id": 1,
    "targetScore": 80,
    "title": "Informática",
    "description": "Preguntas básicas (muy fáciles) de informática",
    "isPublic": false,
    "questions": [{
        "id": 1,
        "questionText": "¿Qué significa ram?",
        "options": [{
            "text": "Refrescos Anónimos Maestros",
            "isCorrect": false
        }, {
            "text": "Memoria de acceso aleatorio",
            "isCorrect": true
        }, {
            "text": "Mercado Anual de Recompensas",
            "isCorrect": false
        }, {
            "text": "Random Access Memory",
            "isCorrect": true
        }]
    }, {
        "id": 2,
        "questionText": "¿Qué significa CPU?",
        "options": [{
            "text": "Unidad Central de procesamiento",
            "isCorrect": true
        }, {
            "text": "Control Parental Uniforme",
            "isCorrect": false
        }, {
            "text": "Centro de Pedidos Universal",
            "isCorrect": false
        }]
    }, {
        "id": null,
        "questionText": "¿Qué significa SSD?",
        "options": [{
            "text": "Unidad de estado sólido",
            "isCorrect": true
        }, {
            "text": "Unidad de almacenamiento persistente",
            "isCorrect": false
        }]
    }]
}
````

**Body (response)**
````json
{
    "id": 1,
    "targetScore": 80,
    "title": "Informática",
    "description": "Preguntas básicas (muy fáciles) de informática",
    "isPublic": false,
    "questions": [{
        "id": 1,
        "questionText": "¿Qué significa ram?",
        "options": [{
            "text": "Refrescos Anónimos Maestros",
            "isCorrect": false
        }, {
            "text": "Memoria de acceso aleatorio",
            "isCorrect": true
        }, {
            "text": "Mercado Anual de Recompensas",
            "isCorrect": false
        }, {
            "text": "Random Access Memory",
            "isCorrect": true
        }]
    }, {
        "id": 2,
        "questionText": "¿Qué significa CPU?",
        "options": [{
            "text": "Unidad Central de procesamiento",
            "isCorrect": true
        }, {
            "text": "Control Parental Uniforme",
            "isCorrect": false
        }, {
            "text": "Centro de Pedidos Universal",
            "isCorrect": false
        }]
    }, {
        "id": 3,
        "questionText": "¿Qué significa SSD?",
        "options": [{
            "text": "Unidad de estado sólido",
            "isCorrect": true
        }, {
            "text": "Unidad de almacenamiento persistente",
            "isCorrect": false
        }]
    }]
}
````

### 3. Generar un reporte PDF de todos los juegos trivia de un jugador
Genera un PDF de todos los juegos de trivia de un jugador.

* **URL:** `/trivia/report`
* **Método:** `GET`
* **Autenticación Requerida:** Sí

**Response (application/pdf)**
<img width="924" height="640" alt="image" src="https://github.com/user-attachments/assets/9bf0fd3f-7807-4cce-9114-166b10483f22" />


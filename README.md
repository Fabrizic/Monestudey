# Monestudey - App de Finanzas Personales

## 🚀 Configuración del Proyecto

### 📋 Requisitos Previos
- Android Studio
- JDK 11 o superior
- Dispositivo Android o emulador

### 🔧 Configuración de Dialogflow

1. **Obtener credenciales:**
   - Ve a [Google Cloud Console](https://console.cloud.google.com/)
   - Selecciona tu proyecto de Dialogflow
   - Ve a "IAM & Admin" > "Service Accounts"
   - Descarga el archivo JSON de credenciales

2. **Configurar Project ID:**
   - Abre `app/src/main/java/com/example/monestudey/data/chatbot/DialogflowConfig.kt`
   - Cambia `TU_PROJECT_ID_AQUI` por tu Project ID real
   - Ejemplo: `const val PROJECT_ID = "mi-proyecto-dialogflow"`

3. **Colocar el archivo:**
   - Copia tu archivo JSON descargado
   - Renómbralo a `dialogflow_credentials.json`
   - Colócalo en `app/src/main/res/raw/`

4. **Verificar:**
   - El archivo debe estar en: `app/src/main/res/raw/dialogflow_credentials.json`
   - **NO** subir este archivo a Git (ya está en .gitignore)

### 🏗️ Compilación

```bash
./gradlew build
```

### 📱 Instalación

```bash
./gradlew installDebug
```

## 🎯 Funcionalidades

- ✅ **Seguimiento de gastos** con categorías
- ✅ **Chatbot inteligente** con Dialogflow
- ✅ **Interfaz moderna** con Material Design 3
- ✅ **Persistencia de datos** con Room Database

## 🔒 Seguridad

- Las credenciales de Dialogflow están protegidas en `.gitignore`
- Cada desarrollador debe usar sus propias credenciales
- Nunca subir archivos JSON de credenciales a Git

## 📞 Soporte

Para problemas con Dialogflow, verificar:
1. Archivo JSON en la ubicación correcta
2. Permisos de internet en AndroidManifest
3. Configuración del proyecto en Google Cloud Console 
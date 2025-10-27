# 💸 YapeListener - Monitor de Transacciones en Tiempo Real

Una aplicación Android que **escucha notificaciones de Yape** y registra automáticamente tus transacciones en **Firebase Realtime Database**.
¡Sin intervención manual, sin registros manuales! Todo se sincroniza automáticamente para que lleves un control preciso de tus finanzas.

---

![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple)
![Android](https://img.shields.io/badge/Platform-Android-green)
![Firebase](https://img.shields.io/badge/Backend-Firebase%20Realtime%20Database-orange)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue)

---

## 🚀 Características

- 📲 **Captura automática** de notificaciones de Yape.
- 💰 **Extracción inteligente** de montos, nombres y tipo de transacción.
- ⚡ **Sincronización en tiempo real** con Firebase Realtime Database.
- 🔐 **Identificación única** por dispositivo para múltiples usuarios.
- 🎯 **Detección precisa** del tipo de transacción (Ingreso, Egreso, Pago, Retiro).
- 🧹 **Procesamiento limpio** de nombres (solo primer nombre y primer apellido).
- 🎨 **UI moderna** con Jetpack Compose y Material Design 3.
- 🌗 **Tema claro y oscuro** adaptativo.
- ⏯️ **Control del servicio** - Activa/desactiva la captura de notificaciones.

---

## 🎥 Demo

📹 **Video de demostración:**

<div align="center">
  <video width="80%" controls>
    <source src="presentation/demo.mov" type="video/quicktime">
    Tu navegador no soporta la reproducción de video. <a href="presentation/demo.mov">Descargar video</a>
  </video>
</div>

---

## 📋 Requisitos Previos

- **Android 7.0 (API 24)** o superior
- **Permisos de acceso a notificaciones** habilitados
- **Cuenta de Firebase** con Realtime Database configurada
- **Android Studio Iguana** o superior (recomendado)

---

## 🛠 Instalación

### 1. Clona el repositorio

```bash
git clone https://github.com/pierre-juarez/yapelistener.git
```

### 2. Entra al proyecto

```bash
cd yapelistener
```

### 3. Configura Firebase

1. Ve a la [Consola de Firebase](https://console.firebase.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. Añade una aplicación Android con el package name: `dev.pierrejuarez.yapelistener` o cambia a uno existente
4. Descarga el archivo `google-services.json`
5. Coloca el archivo en la carpeta `app/`
6. Habilita **Firebase Realtime Database** en tu proyecto

### 4. Sincroniza el proyecto

Abre el proyecto en Android Studio y sincroniza las dependencias:

```bash
./gradlew build
```

### 5. Ejecuta la aplicación

Conecta tu dispositivo Android o usa un emulador y ejecuta:

```bash
./gradlew installDebug
```

### 6. Habilita los permisos de notificación

1. Abre la app
2. Ve a **Configuración → Notificaciones → Acceso a notificaciones**
3. Activa **YapeListener**

---

## 📱 Uso

1. **Instala y configura** la aplicación siguiendo los pasos anteriores
2. **Abre la app** y activa el servicio desde el toggle principal
3. **Recibe o envía un Yape** - La transacción se registrará automáticamente
4. **Verifica en Firebase** - Los datos aparecerán en `transacciones_yape/`

### Estructura de datos en Firebase

Cada transacción se almacena con el siguiente formato:

```json
{
  "transacciones_yape": {
    "push_id_generado": {
      "id_envio": "uuid-generado",
      "monto": "100.50",
      "persona": "Juan Perez",
      "tipo": "INGRESO",
      "fecha": "26/10/2025 15:30:45",
      "timestamp": 1730000000000,
      "mensaje_completo": "Yape! Juan Perez te envió S/ 100.50",
      "dispositivo_id": "android_id_del_dispositivo"
    }
  }
}
```

### Tipos de transacción detectados

- **INGRESO**: Cuando recibes dinero

---

## 🏗 Arquitectura del Proyecto

```
app/src/main/
├── java/dev/pierrejuarez/yapelistener/
│   ├── MainActivity.kt                    # Actividad principal con UI
│   ├── YapeNotificationListener.kt        # Servicio de escucha de notificaciones
│   └── ui/theme/                          # Tema y estilos de Compose
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── AndroidManifest.xml                    # Configuración de permisos y servicios
```

---

## 🔑 Permisos Requeridos

El proyecto utiliza los siguientes permisos:

```xml
<uses-permission android:name="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" />
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 🧪 Tecnologías Utilizadas

| Tecnología               | Versión/Descripción                         |
| ------------------------ | ------------------------------------------- |
| **Kotlin**               | Lenguaje principal                          |
| **Jetpack Compose**      | UI moderna y declarativa                    |
| **Material Design 3**    | Sistema de diseño                           |
| **Firebase Realtime DB** | Base de datos en tiempo real                |
| **NotificationListener** | API de Android para escuchar notificaciones |
| **SharedPreferences**    | Almacenamiento local de configuración       |

---

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Si deseas mejorar este proyecto:

1. Haz un fork del repositorio
2. Crea una rama para tu feature:

```bash
git checkout -b feature/nueva-caracteristica
```

3. Realiza tus cambios y haz commit:

```bash
git commit -m 'Agrega nueva característica'
```

4. Sube tus cambios:

```bash
git push origin feature/nueva-caracteristica
```

5. Abre un Pull Request 🚀

---

## ⚠️ Importante

- Esta aplicación está diseñada **únicamente con fines educativos y de gestión personal**.
- Respeta la privacidad y los términos de servicio de Yape.
- Los datos se almacenan en tu propia base de datos de Firebase.
- **No compartas tu archivo `google-services.json`** públicamente.

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.

---

## 👨‍💻 Autor

Desarrollado con ♥️ por [Pierre Juarez](https://www.linkedin.com/in/pierre-juarez/) 😊

---

## 📞 Contacto

¿Tienes preguntas o sugerencias? ¡Contáctame!

- **LinkedIn**: [Pierre Juarez](https://www.linkedin.com/in/pierre-juarez/)
- **GitHub**: [@pierre-juarez](https://github.com/pierre-juarez)

---

## ⭐ ¿Te gustó el proyecto?

Si este proyecto te fue útil, no olvides darle una ⭐ en GitHub. ¡Tu apoyo motiva a seguir creando!

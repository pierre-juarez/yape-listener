package dev.pierrejuarez.yapelistener

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.app.Notification
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class YapeNotificationListener: NotificationListenerService() {
    private val TAG = "YapeListener"
    private lateinit var database: FirebaseDatabase

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance()
        Log.d(TAG, "Servicio iniciado")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // ⚠️ VERIFICAR SI EL SERVICIO ESTÁ ACTIVADO
        val prefs = getSharedPreferences("yape_config", MODE_PRIVATE)
        val serviceEnabled = prefs.getBoolean("service_enabled", true)

        if (!serviceEnabled) {
            Log.d(TAG, "⏸️ Servicio pausado - Notificación ignorada")
            return
        }

        // Filtrar solo notificaciones de Yape
        if (sbn.packageName == "com.bcp.innovacxion.yapeapp") {

            val notification = sbn.notification
            val extras = notification.extras

            // Extraer información básica
            val titulo = extras.getString(Notification.EXTRA_TITLE) ?: ""
            val mensaje = extras.getString(Notification.EXTRA_TEXT) ?: ""
            val timestamp = sbn.postTime

            Log.d(TAG, "📱 Yape detectado - Título: $titulo | Mensaje: $mensaje")

            // Extraer datos específicos
            val monto = extraerMonto(mensaje)
            val persona = extraerPersona(mensaje)
            val tipo = detectarTipo(mensaje)

            // Crear objeto limpio para enviar
            val transaccion = hashMapOf<String, Any>(
                "id_envio" to UUID.randomUUID().toString(),
                "monto" to monto,
                "persona" to persona,
                "tipo" to tipo,
                "fecha" to formatearFecha(timestamp),
                "timestamp" to timestamp,
                "mensaje_completo" to mensaje,
                "dispositivo_id" to getCustomDeviceId()
            )

            Log.d(TAG, "💰 Monto: $monto | 👤 Persona: $persona | 📊 Tipo: $tipo")

            // Enviar a Firebase
            enviarAFirebase(transaccion)
        }
    }

    private fun enviarAFirebase(transaccion: HashMap<String, Any>) {
        database.getReference("transacciones_yape")
            .push()
            .setValue(transaccion)
            .addOnSuccessListener {
                Log.d(TAG, "✅ Transacción enviada a Firebase")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Error al enviar: ${e.message}")
            }
    }

    private fun extraerMonto(mensaje: String): String {
        val regex = """S/\s*([0-9,]+\.?[0-9]*)""".toRegex()
        val match = regex.find(mensaje)
        return match?.groupValues?.get(1)?.replace(",", "") ?: "0.00"
    }

    private fun extraerPersona(mensaje: String): String {
        var nombreCompleto: String? = null

        // Patrón: "NOMBRE te envió" (captura todo hasta "te envió")
        // Primero removemos "Yape!" si existe al inicio
        val mensajeLimpio = mensaje.replace("""^Yape!\s+""".toRegex(RegexOption.IGNORE_CASE), "")

        val regexEnvio = """^(.+?)\s+te\s+envi[oó]""".toRegex(RegexOption.IGNORE_CASE)
        val matchEnvio = regexEnvio.find(mensajeLimpio)
        if (matchEnvio != null) {
            nombreCompleto = matchEnvio.groupValues[1].trim()
        }

        // Patrón: "Enviaste a NOMBRE" o "Pagaste a NOMBRE"
        if (nombreCompleto == null) {
            val regexA = """(?:Enviaste|Pagaste)\s+(?:a\s+)?(.+?)(?:\s+por\s+S/)""".toRegex(RegexOption.IGNORE_CASE)
            val matchA = regexA.find(mensajeLimpio)
            if (matchA != null) {
                nombreCompleto = matchA.groupValues[1].trim()
            }
        }

        // Patrón: "Recibiste de NOMBRE"
        if (nombreCompleto == null) {
            val regexDe = """Recibiste\s+(?:de\s+)?(.+?)(?:\s+por\s+S/|\s*$)""".toRegex(RegexOption.IGNORE_CASE)
            val matchDe = regexDe.find(mensajeLimpio)
            if (matchDe != null) {
                nombreCompleto = matchDe.groupValues[1].trim()
            }
        }

        // Formatear nombre: extraer primer nombre y primer apellido principal
        return if (nombreCompleto != null) {
            formatearNombre(nombreCompleto)
        } else {
            "Desconocido"
        }
    }

    private fun formatearNombre(nombreCompleto: String): String {
        // Dividir el nombre en partes
        val partes = nombreCompleto.split("""\s+""".toRegex())

        if (partes.isEmpty()) return "Desconocido"

        // Buscar el primer nombre completo (no inicial) y primer apellido completo (no inicial)
        var primerNombre: String? = null
        var primerApellido: String? = null

        for (parte in partes) {
            val esInicial = parte.matches("""[A-Z]\.?""".toRegex())

            if (primerNombre == null && !esInicial) {
                primerNombre = parte
            } else if (primerNombre != null && primerApellido == null && !esInicial) {
                primerApellido = parte
                break
            }
        }

        return when {
            primerNombre != null && primerApellido != null -> "$primerNombre $primerApellido"
            primerNombre != null -> primerNombre
            else -> partes[0] // Fallback al primer elemento
        }
    }

    private fun detectarTipo(mensaje: String): String {
        return when {
            mensaje.contains("Recibiste", ignoreCase = true) -> "INGRESO"
            mensaje.contains("te envió un pago", ignoreCase = true) -> "INGRESO"
            mensaje.contains("te enviaron", ignoreCase = true) -> "INGRESO"
            mensaje.contains("Enviaste", ignoreCase = true) -> "EGRESO"
            mensaje.contains("Pagaste", ignoreCase = true) -> "PAGO"
            mensaje.contains("Retiraste", ignoreCase = true) -> "RETIRO"
            else -> "OTRO"
        }
    }

    private fun formatearFecha(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun getCustomDeviceId(): String {
        return android.provider.Settings.Secure.getString(
            contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // No hacemos nada cuando se elimina una notificación
    }
}
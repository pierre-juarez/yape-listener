package dev.pierrejuarez.yapelistener

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {

    private lateinit var switchService: Switch
    private lateinit var tvEstado: TextView
    private lateinit var tvEstadoCaptura: TextView
    private lateinit var tvEstadoBateria: TextView
    private lateinit var btnPermiso: Button
    private lateinit var btnBateria: Button
    private lateinit var btnAppSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Vincular vistas
        switchService = findViewById(R.id.switchService)
        tvEstado = findViewById(R.id.tvEstado)
        tvEstadoCaptura = findViewById(R.id.tvEstadoCaptura)
        tvEstadoBateria = findViewById(R.id.tvEstadoBateria)
        btnPermiso = findViewById(R.id.btnPermiso)
        btnBateria = findViewById(R.id.btnBateria)
        btnAppSettings = findViewById(R.id.btnAppSettings)

        // Cargar estado guardado
        val prefs = getSharedPreferences("yape_config", MODE_PRIVATE)
        val serviceEnabled = prefs.getBoolean("service_enabled", true)
        switchService.isChecked = serviceEnabled

        // Actualizar UI
        actualizarEstadoUI()

        // Listener del Switch
        switchService.setOnCheckedChangeListener { _, isChecked ->
            // Guardar estado
            prefs.edit()
                .putBoolean("service_enabled", isChecked)
                .apply()

            // Actualizar UI
            actualizarEstadoUI()

            // Mostrar mensaje
            if (isChecked) {
                Toast.makeText(this, "✅ Captura activada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "⏸️ Captura pausada", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón de permisos
        btnPermiso.setOnClickListener {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(intent)
        }

        // Botón de optimización de batería
        btnBateria.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: Exception) {
                    // Si falla, abrir la configuración general
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    startActivity(intent)
                    Toast.makeText(this, "Busca 'Yape Listener' y desactiva la optimización", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "No disponible en esta versión de Android", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón de configuración de la app (para segundo plano)
        btnAppSettings.setOnClickListener {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
                Toast.makeText(this, "Revisa los permisos y la ejecución en segundo plano", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la configuración", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarEstadoUI()
    }

    private fun actualizarEstadoUI() {
        val hasPermission = isNotificationServiceEnabled()
        val prefs = getSharedPreferences("yape_config", MODE_PRIVATE)
        val serviceEnabled = prefs.getBoolean("service_enabled", true)
        val isBatteryOptimized = isBatteryOptimizationEnabled()

        // Estado del permiso
        if (hasPermission) {
            tvEstado.text = "✅ Permiso otorgado"
            tvEstado.setTextColor(getColor(android.R.color.holo_green_dark))
            btnPermiso.isEnabled = false
            btnPermiso.alpha = 0.5f
        } else {
            tvEstado.text = "❌ Sin permiso"
            tvEstado.setTextColor(getColor(android.R.color.holo_red_dark))
            btnPermiso.isEnabled = true
            btnPermiso.alpha = 1f
        }

        // Estado de optimización de batería
        if (isBatteryOptimized) {
            tvEstadoBateria.text = "⚠️ Optimización de batería activa"
            tvEstadoBateria.setTextColor(getColor(android.R.color.holo_orange_dark))
            btnBateria.isEnabled = true
        } else {
            tvEstadoBateria.text = "✅ Optimización desactivada"
            tvEstadoBateria.setTextColor(getColor(android.R.color.holo_green_dark))
            btnBateria.isEnabled = false
            btnBateria.alpha = 0.5f
        }

        // Estado de captura
        if (hasPermission && serviceEnabled) {
            tvEstadoCaptura.text = "🟢 CAPTURANDO\nLas transacciones de Yape se están registrando"
            tvEstadoCaptura.setTextColor(getColor(android.R.color.holo_green_dark))
        } else if (hasPermission && !serviceEnabled) {
            tvEstadoCaptura.text = "⏸️ PAUSADO\nActiva el switch para comenzar a capturar"
            tvEstadoCaptura.setTextColor(getColor(android.R.color.holo_orange_dark))
        } else {
            tvEstadoCaptura.text = "⭕ INACTIVO\nDa permiso para poder capturar"
            tvEstadoCaptura.setTextColor(getColor(android.R.color.darker_gray))
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val packageName = packageName
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(packageName)
    }

    private fun isBatteryOptimizationEnabled(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
            return if (powerManager != null) {
                // Devuelve true si ESTÁ siendo optimizada (queremos que sea false)
                !powerManager.isIgnoringBatteryOptimizations(packageName)
            } else {
                false
            }
        }
        // En versiones antiguas, no hay optimización de batería
        return false
    }

}


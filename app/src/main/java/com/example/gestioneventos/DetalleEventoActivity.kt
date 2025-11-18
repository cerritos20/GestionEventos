package com.example.gestioneventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetalleEventoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_evento)

        // Recuperamos datos
        val idEvento = intent.getStringExtra("id") ?: "0"
        val titulo = intent.getStringExtra("titulo")
        val fecha = intent.getStringExtra("fecha")
        val ubicacion = intent.getStringExtra("ubicacion")
        val descripcion = intent.getStringExtra("descripcion")

        // Vinculamos controles
        val tvTitulo = findViewById<TextView>(R.id.tvTituloDetalle)
        val tvFecha = findViewById<TextView>(R.id.tvFechaDetalle)
        val tvUbicacion = findViewById<TextView>(R.id.tvUbicacionDetalle)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescripcionDetalle)
        val btnAsistir = findViewById<Button>(R.id.btnAsistir)
        val btnCompartir = findViewById<Button>(R.id.btnCompartir) // <--- NUEVO

        tvTitulo.text = titulo
        tvFecha.text = "📅 $fecha"
        tvUbicacion.text = "📍 $ubicacion"
        tvDescripcion.text = descripcion

        // Configurar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.14/backend/") // TU IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        // ACCIÓN 1: ASISTIR
        btnAsistir.setOnClickListener {
            val request = AsistenciaRequest(usuarioId = "1", eventoId = idEvento)

            api.confirmarAsistencia(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val mensaje = response.body()?.mensaje
                        Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()

                        if (response.body()?.exito == true) {
                            btnAsistir.text = "¡Asistencia Confirmada!"
                            btnAsistir.isEnabled = false
                            btnAsistir.setBackgroundColor(android.graphics.Color.GRAY)
                        }
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error conexión", Toast.LENGTH_LONG).show()
                }
            })
        }

        // ACCIÓN 2: COMPARTIR (NUEVO CÓDIGO) 🚀
        btnCompartir.setOnClickListener {
            // Crear el mensaje que se va a enviar
            val mensaje = "¡Hola! Te invito a este evento:\n\n" +
                    "🎉 *$titulo*\n" +
                    "📅 $fecha\n" +
                    "📍 $ubicacion\n\n" +
                    "Descubre más en nuestra App de Eventos Comunitarios."

            // Crear el Intent de compartir
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, mensaje)
                type = "text/plain"
            }

            // Lanzar el menú de compartir de Android
            val shareIntent = Intent.createChooser(intent, "Compartir evento vía...")
            startActivity(shareIntent)
        }
    }
}
package com.example.gestioneventos

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 👇 ¡ESTA ES LA LÍNEA QUE BUSCA TU HOME! TIENE QUE LLAMARSE ASÍ:
class CrearEventoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_evento)

        val etTitulo = findViewById<TextInputEditText>(R.id.etTituloEvento)
        val etDescripcion = findViewById<TextInputEditText>(R.id.etDescripcionEvento)
        val etUbicacion = findViewById<TextInputEditText>(R.id.etUbicacionEvento)
        val etFecha = findViewById<TextInputEditText>(R.id.etFechaEvento)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarEvento)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.25/backend/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val desc = etDescripcion.text.toString()
            val ubicacion = etUbicacion.text.toString()
            val fecha = etFecha.text.toString()

            if (titulo.isEmpty() || desc.isEmpty() || fecha.isEmpty()) {
                Toast.makeText(this, "Faltan datos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = CrearEventoRequest(titulo, desc, fecha, ubicacion)

            api.crearEvento(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body()?.exito == true) {
                        Toast.makeText(applicationContext, "¡Evento Creado!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Error: ${response.body()?.mensaje}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
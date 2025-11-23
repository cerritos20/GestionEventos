package com.example.gestioneventos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetalleEventoActivity : AppCompatActivity() {

    private lateinit var api: ApiService
    private lateinit var rvComentarios: RecyclerView
    private lateinit var etComentario: TextInputEditText
    private lateinit var ratingBar: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_evento)

        // 👇 RECUPERAR EL ID DEL USUARIO DE LA MEMORIA
        val prefs = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        val idUsuarioLogueado = prefs.getString("id_usuario", "1") ?: "1" // Si falla, usa 1 por defecto

        val idEvento = intent.getStringExtra("id") ?: "0"
        val titulo = intent.getStringExtra("titulo")
        val fecha = intent.getStringExtra("fecha")
        val ubicacion = intent.getStringExtra("ubicacion")
        val descripcion = intent.getStringExtra("descripcion")

        val tvTitulo = findViewById<TextView>(R.id.tvTituloDetalle)
        val tvFecha = findViewById<TextView>(R.id.tvFechaDetalle)
        val tvUbicacion = findViewById<TextView>(R.id.tvUbicacionDetalle)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescripcionDetalle)
        val btnAsistir = findViewById<Button>(R.id.btnAsistir)
        val btnCompartir = findViewById<Button>(R.id.btnCompartir)
        val btnEnviar = findViewById<Button>(R.id.btnEnviarComentario)

        rvComentarios = findViewById(R.id.rvComentarios)
        etComentario = findViewById(R.id.etNuevoComentario)
        ratingBar = findViewById(R.id.ratingNuevo)

        tvTitulo.text = titulo
        tvFecha.text = "📅 $fecha"
        tvUbicacion.text = "📍 $ubicacion"
        tvDescripcion.text = descripcion

        rvComentarios.layoutManager = LinearLayoutManager(this)

        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.25/backend/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        api = retrofit.create(ApiService::class.java)

        cargarComentarios(idEvento)

        // Botón Asistir (Con ID REAL)
        btnAsistir.setOnClickListener {
            val request = AsistenciaRequest(usuarioId = idUsuarioLogueado, eventoId = idEvento)
            api.confirmarAsistencia(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, response.body()?.mensaje, Toast.LENGTH_SHORT).show()
                        if (response.body()?.exito == true) {
                            btnAsistir.text = "¡Asistencia Confirmada!"
                            btnAsistir.isEnabled = false
                            btnAsistir.setBackgroundColor(android.graphics.Color.GRAY)
                        }
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Fallo conexión", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Botón Compartir
        btnCompartir.setOnClickListener {
            val msj = "¡Mira este evento!: $titulo en $ubicacion el $fecha"
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, msj)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, "Compartir"))
        }

        // Botón Enviar Comentario (Con ID REAL)
        btnEnviar.setOnClickListener {
            val texto = etComentario.text.toString()
            val estrellas = ratingBar.rating

            if (texto.isEmpty()) {
                Toast.makeText(this, "Escribe algo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = ComentarioRequest(usuarioId = idUsuarioLogueado, eventoId = idEvento, comentario = texto, calificacion = estrellas)

            api.guardarComentario(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        if (response.body()?.exito == true) {
                            Toast.makeText(applicationContext, "Comentario publicado", Toast.LENGTH_SHORT).show()
                            etComentario.text?.clear()
                            ratingBar.rating = 0f
                            cargarComentarios(idEvento)
                        } else {
                            Toast.makeText(applicationContext, "${response.body()?.mensaje}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun cargarComentarios(idEvento: String) {
        api.verComentarios(idEvento).enqueue(object : Callback<List<Comentario>> {
            override fun onResponse(call: Call<List<Comentario>>, response: Response<List<Comentario>>) {
                if (response.isSuccessful && response.body() != null) {
                    rvComentarios.adapter = ComentarioAdapter(response.body()!!)
                }
            }
            override fun onFailure(call: Call<List<Comentario>>, t: Throwable) {}
        })
    }
}
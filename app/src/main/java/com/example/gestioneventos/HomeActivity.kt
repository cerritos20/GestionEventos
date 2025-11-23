package com.example.gestioneventos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabCrear: FloatingActionButton
    private lateinit var fabHistorial: FloatingActionButton
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Vinculamos las vistas
        recyclerView = findViewById(R.id.rvEventos)
        fabCrear = findViewById(R.id.fabCrear)
        fabHistorial = findViewById(R.id.fabHistorial) // Este es el botón naranja nuevo

        // Configurar la lista
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar Retrofit (Con modo relajado y tu IP)
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.25/backend/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Cargar los eventos al abrir
        cargarEventos()

        // ACCIÓN 1: Ir a Crear Evento
        fabCrear.setOnClickListener {
            val intent = Intent(this, CrearEventoActivity::class.java)
            startActivity(intent)
        }

        // ACCIÓN 2: Ir al Historial
        fabHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }
    }

    // Esto hace que la lista se actualice sola si agregas un evento y regresas
    override fun onResume() {
        super.onResume()
        cargarEventos()
    }

    private fun cargarEventos() {
        apiService.obtenerEventos().enqueue(object : Callback<List<Evento>> {
            override fun onResponse(call: Call<List<Evento>>, response: Response<List<Evento>>) {
                if (response.isSuccessful) {
                    val eventos = response.body()
                    if (eventos != null && eventos.isNotEmpty()) {
                        val adapter = EventoAdapter(eventos)
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(applicationContext, "No hay eventos próximos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Error del servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Evento>>, t: Throwable) {
                Log.e("HomeActivity", "Error: ${t.message}")
                Toast.makeText(applicationContext, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
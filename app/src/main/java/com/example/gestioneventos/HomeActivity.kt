package com.example.gestioneventos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabCrear: FloatingActionButton
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.rvEventos)
        fabCrear = findViewById(R.id.fabCrear)

        // Configurar la lista
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ✅ AQUÍ ESTÁ TU IP DIRECTA (192.168.1.14)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.14/backend/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Cargar los eventos automáticamente
        cargarEventos()

        fabCrear.setOnClickListener {
            Toast.makeText(this, "Crear Evento (Próximamente)", Toast.LENGTH_SHORT).show()
        }
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
                        Toast.makeText(applicationContext, "No hay eventos aún", Toast.LENGTH_SHORT).show()
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
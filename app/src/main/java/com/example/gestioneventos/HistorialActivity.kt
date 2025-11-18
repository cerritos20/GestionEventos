package com.example.gestioneventos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HistorialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val recycler = findViewById<RecyclerView>(R.id.rvHistorial)
        recycler.layoutManager = LinearLayoutManager(this)

        // Configurar Retrofit (TU IP)
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.14/backend/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val api = retrofit.create(ApiService::class.java)

        // Pedir historial
        api.verHistorial().enqueue(object : Callback<List<Evento>> {
            override fun onResponse(call: Call<List<Evento>>, response: Response<List<Evento>>) {
                if (response.isSuccessful) {
                    val lista = response.body()
                    if (lista != null && lista.isNotEmpty()) {
                        // ¡Reusamos el mismo adaptador! Magia de la programación
                        recycler.adapter = EventoAdapter(lista)
                    } else {
                        Toast.makeText(applicationContext, "No hay eventos pasados", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Evento>>, t: Throwable) {
                Toast.makeText(applicationContext, "Error conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
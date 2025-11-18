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

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val etNombre = findViewById<TextInputEditText>(R.id.etNombreRegistro)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmailRegistro)
        val etPass = findViewById<TextInputEditText>(R.id.etPassRegistro)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        // Configurar Retrofit (TU IP)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.14/backend/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegistroRequest(nombre, email, pass)

            api.registrarUsuario(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body()?.exito == true) {
                        Toast.makeText(applicationContext, "Registro exitoso. Inicia sesión.", Toast.LENGTH_LONG).show()
                        finish() // Cierra el registro y vuelve al Login
                    } else {
                        Toast.makeText(applicationContext, "Error: ${response.body()?.mensaje}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Fallo de conexión", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
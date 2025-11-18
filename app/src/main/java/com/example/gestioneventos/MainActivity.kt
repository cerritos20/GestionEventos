package com.example.gestioneventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // 👇 NUEVO: Vinculamos el texto de "Registrarse"
        val tvRegistrar = findViewById<TextView>(R.id.tvRegistrar)

        // ✅ TU IP DIRECTA (192.168.1.14)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.14/backend/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        // Lógica del botón INGRESAR
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Llena los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(email, pass)

            api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val respuesta = response.body()
                        if (respuesta != null && respuesta.exito) {
                            Toast.makeText(applicationContext, "¡Bienvenido!", Toast.LENGTH_SHORT).show()

                            // 🚀 SALTO A LA PANTALLA HOME
                            val intento = Intent(this@MainActivity, HomeActivity::class.java)
                            startActivity(intento)
                            finish()

                        } else {
                            Toast.makeText(applicationContext, "Error: ${respuesta?.mensaje}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Error del servidor", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Fallo de conexión", Toast.LENGTH_LONG).show()
                }
            })
        }

        // 👇 NUEVO: Acción al tocar "¿No tienes cuenta? Regístrate"
        tvRegistrar.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}
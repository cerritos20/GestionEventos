package com.example.gestioneventos // <--- MANTÉN TU NOMBRE DE PAQUETE AQUÍ

import android.os.Bundle
import android.util.Log
import android.widget.Button
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

        // 1. Vinculamos los controles de la pantalla
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // 2. Configuración de Retrofit
        // ✅ IP ACTUALIZADA SEGÚN TU CONEXIÓN WI-FI
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.14/backend/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        // 3. Acción del botón INGRESAR
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            // Validación simple: que no estén vacíos
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Creamos el objeto con los datos
            val loginRequest = LoginRequest(email, pass)

            // Enviamos los datos al servidor PHP
            api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val respuesta = response.body()
                        if (respuesta != null && respuesta.exito) {
                            // --- LOGIN EXITOSO ---
                            Toast.makeText(applicationContext, "¡Bienvenido ${respuesta.mensaje}!", Toast.LENGTH_LONG).show()
                            Log.d("LOGIN", "Usuario entró correctamente")

                            // TODO: Aquí agregaremos el código para cambiar de pantalla
                        } else {
                            // --- CONTRASEÑA INCORRECTA ---
                            Toast.makeText(applicationContext, "Error: ${respuesta?.mensaje}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Error del servidor (404/500)", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // --- ERROR DE CONEXIÓN (Internet o IP) ---
                    Toast.makeText(applicationContext, "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("LOGIN_ERROR", "Error: ${t.message}")
                }
            })
        }
    }
}
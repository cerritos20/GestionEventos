package com.example.gestioneventos

import android.content.Context // Importante para SharedPreferences
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
        val tvRegistrar = findViewById<TextView>(R.id.tvRegistrar)

        // ✅ TU IP (192.168.1.14)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.14/backend/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

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

                            // 👇 AQUÍ GUARDAMOS QUIÉN ERES EN LA MEMORIA DEL TELÉFONO
                            val prefs = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
                            val editor = prefs.edit()
                            editor.putString("id_usuario", respuesta.usuario?.id)
                            editor.putString("nombre_usuario", respuesta.usuario?.nombre)
                            editor.apply() // Guardar cambios

                            Toast.makeText(applicationContext, "¡Hola ${respuesta.usuario?.nombre}!", Toast.LENGTH_SHORT).show()

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

        tvRegistrar.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }
}
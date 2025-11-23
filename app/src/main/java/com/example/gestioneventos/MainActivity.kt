package com.example.gestioneventos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Firebase
        firebaseAuth = FirebaseAuth.getInstance()

        // ✅ Configuración Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Importante
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // ✅ UI
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogle = findViewById<Button>(R.id.btnGoogle)
        val tvRegistrar = findViewById<TextView>(R.id.tvRegistrar)

        // ✅ Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.25/backend/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        // ✅ LOGIN NORMAL
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

                    if (response.isSuccessful && response.body()?.exito == true) {

                        val usuario = response.body()!!.usuario!!

                        guardarSesion(usuario.id, usuario.nombre)

                        Toast.makeText(
                            applicationContext,
                            "¡Hola ${usuario.nombre}!",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Credenciales incorrectas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error de conexión", Toast.LENGTH_LONG).show()
                }
            })
        }

        // ✅ LOGIN GOOGLE
        btnGoogle.setOnClickListener {

            googleSignInClient.signOut().addOnCompleteListener {

                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }

        // ✅ IR A REGISTRO
        tvRegistrar.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    // ✅ RESULTADO GOOGLE
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                val idToken = account.idToken

                if (idToken == null) {
                    Toast.makeText(this, "Token de Google es null", Toast.LENGTH_LONG).show()
                    Log.e("GOOGLE_SIGN_IN", "ID Token es NULL")
                    return
                }

                val credential = GoogleAuthProvider.getCredential(idToken, null)

                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->

                        if (authTask.isSuccessful) {

                            val user = firebaseAuth.currentUser

                            val nombre = user?.displayName ?: "Usuario Google"
                            val email = user?.email ?: ""

                            Toast.makeText(
                                this,
                                "Bienvenido $nombre",
                                Toast.LENGTH_LONG
                            ).show()

                            guardarSesion("google_${user?.uid}", nombre)

                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()

                        } else {

                            val error = authTask.exception?.localizedMessage

                            Toast.makeText(
                                this,
                                "Google Auth Failed: $error",
                                Toast.LENGTH_LONG
                            ).show()

                            Log.e("GOOGLE_AUTH", "Error real de Firebase:", authTask.exception)
                        }
                    }

            } catch (e: ApiException) {

                Toast.makeText(
                    this,
                    "Falló Google: ${e.statusCode} - ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()

                Log.e("GOOGLE_SIGN_IN", "API Exception:", e)
            }
        }
    }

    // ✅ GUARDAR SESIÓN
    private fun guardarSesion(id: String, nombre: String) {

        val prefs = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString("id_usuario", id)
        editor.putString("nombre_usuario", nombre)

        editor.apply()
    }
}

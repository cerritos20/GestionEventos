package com.example.gestioneventos



import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // Para pedir la lista de eventos (lo usaremos después)
    @GET("obtener_eventos.php")
    fun obtenerEventos(): Call<List<Evento>>

    // Para hacer LOGIN
    @POST("login.php")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
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

    // AGREGA ESTO AL FINAL, ANTES DE LA LLAVE DE CIERRE }
    @POST("crear_evento.php")
    fun crearEvento(@Body request: CrearEventoRequest): Call<LoginResponse>

    @POST("registro.php")
    fun registrarUsuario(@Body request: RegistroRequest): Call<LoginResponse>

    @POST("asistir.php")
    fun confirmarAsistencia(@Body request: AsistenciaRequest): Call<LoginResponse>

    // 1. Guardar un comentario nuevo
    @POST("guardar_comentario.php")
    fun guardarComentario(@Body request: ComentarioRequest): Call<LoginResponse>

    // 2. Leer los comentarios de un evento específico
    // Usamos @Query para enviar ?evento_id=1 en la URL
    @GET("ver_comentarios.php")
    fun verComentarios(@retrofit2.http.Query("evento_id") id: String): Call<List<Comentario>>


}
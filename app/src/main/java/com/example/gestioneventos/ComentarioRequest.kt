package com.example.gestioneventos
import com.google.gson.annotations.SerializedName

data class ComentarioRequest(
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("evento_id") val eventoId: String,
    @SerializedName("comentario") val comentario: String,
    @SerializedName("calificacion") val calificacion: Float
)
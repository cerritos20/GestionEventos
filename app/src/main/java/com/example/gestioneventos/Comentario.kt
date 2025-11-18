package com.example.gestioneventos
import com.google.gson.annotations.SerializedName

data class Comentario(
    @SerializedName("nombre") val usuario: String,
    @SerializedName("comentario") val texto: String,
    @SerializedName("calificacion") val rating: Float,
    @SerializedName("fecha") val fecha: String
)
package com.example.gestioneventos

import com.google.gson.annotations.SerializedName

data class Evento(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("fecha_hora") val fechaHora: String,
    @SerializedName("ubicacion") val ubicacion: String,
    @SerializedName("imagen_url") val imagenUrl: String
)
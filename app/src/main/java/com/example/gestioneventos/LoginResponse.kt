package com.example.gestioneventos
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("exito") val exito: Boolean,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("usuario") val usuario: Usuario? // <--- Esto es nuevo
)

data class Usuario(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String
)
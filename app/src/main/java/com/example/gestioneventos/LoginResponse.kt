package com.example.gestioneventos

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("exito") val exito: Boolean,
    @SerializedName("mensaje") val mensaje: String
)
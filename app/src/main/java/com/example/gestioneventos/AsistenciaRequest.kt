package com.example.gestioneventos
import com.google.gson.annotations.SerializedName

data class AsistenciaRequest(
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("evento_id") val eventoId: String
)
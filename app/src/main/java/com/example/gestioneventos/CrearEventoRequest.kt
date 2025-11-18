package com.example.gestioneventos

data class CrearEventoRequest(
    val titulo: String,
    val descripcion: String,
    val fecha_hora: String,
    val ubicacion: String
)
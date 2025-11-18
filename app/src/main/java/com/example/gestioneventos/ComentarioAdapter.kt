package com.example.gestioneventos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ComentarioAdapter(private val lista: List<Comentario>) : RecyclerView.Adapter<ComentarioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usuario: TextView = view.findViewById(R.id.txtUsuarioComentario)
        val texto: TextView = view.findViewById(R.id.txtTextoComentario)
        val fecha: TextView = view.findViewById(R.id.txtFechaComentario)
        val rating: RatingBar = view.findViewById(R.id.ratingItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comentario, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.usuario.text = item.usuario
        holder.texto.text = item.texto
        holder.fecha.text = item.fecha
        holder.rating.rating = item.rating
    }

    override fun getItemCount(): Int = lista.size
}
package com.example.gestioneventos

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventoAdapter(private val listaEventos: List<Evento>) : RecyclerView.Adapter<EventoAdapter.EventoViewHolder>() {

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.txtTitulo)
        val fecha: TextView = itemView.findViewById(R.id.txtFecha)
        val ubicacion: TextView = itemView.findViewById(R.id.txtUbicacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evento, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = listaEventos[position]
        holder.titulo.text = evento.titulo
        holder.fecha.text = evento.fechaHora
        holder.ubicacion.text = evento.ubicacion

        // 👇 ESTO ES LO NUEVO: Detectar el clic en la tarjeta
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleEventoActivity::class.java)

            // Empacamos los datos para enviarlos a la otra pantalla
            intent.putExtra("id", evento.id)
            intent.putExtra("titulo", evento.titulo)
            intent.putExtra("fecha", evento.fechaHora)
            intent.putExtra("ubicacion", evento.ubicacion)
            intent.putExtra("descripcion", evento.descripcion)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listaEventos.size
    }
}
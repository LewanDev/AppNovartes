package com.nmarchelli.appnovartes.data.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nmarchelli.appnovartes.R

class Articulo(
    val id: Int,
    val codigo: String?,
    val descripcion: String,
    val descripcion_larga: String,
    val rubro: String?,
    val subrubro: String?,
    val destacado: Boolean,
    val _borrado: Boolean,
    val actualizacion: String, //DATETIME
    val UxB: Int,
    val cod_bar_unidad: String,
    val cod_bar_pack: String,
    val cod_bar_bulto: String,
    val UxP: Int,
    val creado: String, //TIMESTAMP
    val nodisponible: Boolean,
    val stockmax: Int,
    val principal: Boolean,
    val complemento: Boolean,
    val customizable: Boolean
)

class ArticuloAdapter(private val listaOriginal: List<Articulo>,
                      private val onItemClick: (Articulo) -> Unit) :
    RecyclerView.Adapter<ArticuloAdapter.ArticuloViewHolder>(), Filterable {

    private var listaFiltrada: List<Articulo> = listaOriginal

    class ArticuloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.textNombre)
        val precio: TextView = itemView.findViewById(R.id.textCodigo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_articulo, parent, false)
        return ArticuloViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticuloViewHolder, position: Int) {
        val articulo = listaFiltrada[position]
        holder.nombre.text = articulo.descripcion
        holder.precio.text = "#${articulo.codigo}"

        holder.itemView.setOnClickListener {
            onItemClick(articulo)
        }
    }

    override fun getItemCount(): Int = listaFiltrada.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val filtro = query.toString().lowercase().trim()
                val resultados = if (filtro.isEmpty()) {
                    listaOriginal
                } else {
                    listaOriginal.filter {
                        it.descripcion.lowercase().contains(filtro) || it.descripcion.lowercase().contains(filtro)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = resultados
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listaFiltrada = results?.values as List<Articulo>
                notifyDataSetChanged()
            }
        }
    }
}
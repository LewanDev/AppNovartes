package com.nmarchelli.appnovartes.domain.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nmarchelli.appnovartes.R
import com.nmarchelli.appnovartes.data.local.entities.CartItemEntity

object Cart {
    private val items = mutableListOf<CartItem>()

    fun addItem(item: CartItem) {
        val existingItem = items.find { it.id == item.id }
        if (existingItem != null) {
            existingItem.cantidad += item.cantidad
        } else {
            items.add(item)
        }
    }

    fun removeItem(id: Int) {
        items.removeAll { it.id == id }
    }

    fun clearCart() {
        items.clear()
    }

    fun getItems(): List<CartItem> = items.toList()
}



data class CartItem(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val telas: String,
    val telas2: String,
    val patas: String,
    val patas2: String,
    var cantidad: Int
)
class CartAdapter(
    private var items: List<CartItemEntity>,
    private val onDeleteClick: (CartItemEntity) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtItemCartNombre)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtItemCartCantidad)
        val txtRubro: TextView = itemView.findViewById(R.id.txtItemCartRubro)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnItemCartEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.txtNombre.text = item.nombre
        holder.txtCantidad.text = "Cantidad: ${item.cantidad}"
        holder.txtRubro.text = "${item.patas} / ${item.telas}"

        holder.btnEliminar.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<CartItemEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}

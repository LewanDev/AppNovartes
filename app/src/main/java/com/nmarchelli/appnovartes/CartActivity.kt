package com.nmarchelli.appnovartes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class CartActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var totalText: TextView
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

        listView = findViewById(R.id.listCart)
        totalText = findViewById(R.id.txtTotal)
        btnConfirm = findViewById(R.id.btnBuy)

        updateList()

        btnConfirm.setOnClickListener {
            if (Cart.productos.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, ConfirmBuyActivity::class.java))
            }
        }
    }

    private fun updateList() {
        val adapter = object : ArrayAdapter<Product>(
            this,
            R.layout.item_cart_product,
            Cart.productos
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val inflater = layoutInflater
                val view = convertView ?: inflater.inflate(R.layout.item_cart_product, parent, false)

                val producto = getItem(position)

                val txtNombre = view.findViewById<TextView>(R.id.txtProductoNombre)
                val txtPrecio = view.findViewById<TextView>(R.id.txtProductoPrecio)
                val txtResumen = view.findViewById<TextView>(R.id.txtProductoResumen)

                txtNombre.text = producto?.nombre ?: "Producto"
                "Precio: $${producto?.precio ?: 0}".also { txtPrecio.text = it }

                """
                                Categoría: ${producto?.categoria}
                                Tela: ${producto?.tela} (${producto?.colorTela})
                                Patas: ${producto?.patas} (${producto?.colorPatas})
                                Medidas: ${producto?.medidas}
                                Costura: ${producto?.costura}
                                """.trimIndent().also { txtResumen.text = it }

                return view
            }
        }

        listView.adapter = adapter

        val total = Cart.productos.sumOf { it.precio }
        totalText.text = "Total: $${total}"
    }


}
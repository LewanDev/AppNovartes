package com.nmarchelli.appnovartes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var productosFiltrados: List<String>
    private val productosDisponibles = listOf("Sillón 2 cuerpos", "Silla madera", "Almohadón redondo", "Sillón esquinero", "Silla metálica")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnPerfil = findViewById<Button>(R.id.btnProfile)
        val saludo = findViewById<TextView>(R.id.txtGreeting)
        val listView = findViewById<ListView>(R.id.listProducts)
        val buscador = findViewById<SearchView>(R.id.svProductSearch)
        val btnCarrito = findViewById<Button>(R.id.btnCart)

        saludo.text = getString(R.string.txt_welcome)

        productosFiltrados = productosDisponibles
        actualizarLista(listView, productosFiltrados)

        btnPerfil.setOnClickListener {
            Toast.makeText(this, "Ir a perfil (pantalla en construcción)", Toast.LENGTH_SHORT).show()
        }

        buscador.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                productosFiltrados = productosDisponibles.filter {
                    it.contains(newText ?: "", ignoreCase = true)
                }
                actualizarLista(listView, productosFiltrados)
                return true
            }
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val productoSeleccionado = Catalog.productos[position]
            val intent = Intent(this, CustomProductsActivity::class.java)
            intent.putExtra("producto", productoSeleccionado)
            startActivity(intent)
        }

        btnCarrito.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

    }

    private fun actualizarLista(listView: ListView, items: List<String>) {
        val adapter = object : ArrayAdapter<Product>(
            this,
            android.R.layout.simple_list_item_1,
            Catalog.productos
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                val producto = getItem(position)
                view.text = "${producto?.nombre} - $${producto?.precio}"
                return view
            }
        }

        listView.adapter = adapter
    }
}
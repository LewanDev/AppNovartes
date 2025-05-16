package com.nmarchelli.appnovartes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nmarchelli.appnovartes.data.api.ApiClient
import com.nmarchelli.appnovartes.data.model.Articulo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Filter
import android.widget.Filterable


class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"

    private lateinit var txtGreeting: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArticuloAdapter
    private lateinit var svSearcher: SearchView
    private lateinit var btnUsuario: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        txtGreeting = findViewById(R.id.txtGreeting)
        recyclerView = findViewById(R.id.recyclerArticulos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        svSearcher = findViewById(R.id.svProductSearch)
        btnUsuario = findViewById(R.id.btnUser)
        txtGreeting.text = getString(R.string.txt_welcome)

        getArticulos { articulos ->
            adapter = ArticuloAdapter(articulos)
            recyclerView.adapter=adapter

            svSearcher.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean =false

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })
        }

        svSearcher.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        btnUsuario.setOnClickListener {
            val popup = PopupMenu(this, btnUsuario)
            popup.menuInflater.inflate(R.menu.menu_user, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_profile -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        true
                    }
                    R.id.menu_cart -> {
                        startActivity(Intent(this, CartActivity::class.java))
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        }

    }

    private fun getArticulos(onLoaded: (List<Articulo>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getArticulos()
                if (response.isSuccessful) {
                    val articulos = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        onLoaded(articulos)
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
            }
        }
    }


    class ArticuloAdapter(private val listaOriginal: List<Articulo>) :
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


}
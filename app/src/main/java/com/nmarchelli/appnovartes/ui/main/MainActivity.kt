package com.nmarchelli.appnovartes.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nmarchelli.appnovartes.KEY_CATEGORY
import com.nmarchelli.appnovartes.KEY_CODE
import com.nmarchelli.appnovartes.KEY_DESCRIPTION
import com.nmarchelli.appnovartes.KEY_ID
import com.nmarchelli.appnovartes.KEY_STOCK
import com.nmarchelli.appnovartes.KEY_SUBCATEGORY
import com.nmarchelli.appnovartes.R
import com.nmarchelli.appnovartes.data.local.AppDatabase
import com.nmarchelli.appnovartes.data.remote.ApiClient
import com.nmarchelli.appnovartes.data.repository.ArticuloRepository
import com.nmarchelli.appnovartes.domain.mappers.toDomainList
import com.nmarchelli.appnovartes.domain.models.Articulo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.nmarchelli.appnovartes.domain.models.ArticuloAdapter
import com.nmarchelli.appnovartes.ui.CartActivity
import com.nmarchelli.appnovartes.ui.ProductActivity
import com.nmarchelli.appnovartes.ui.ProfileActivity


class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"

    private lateinit var txtGreeting: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArticuloAdapter
    private lateinit var svSearcher: SearchView
    private lateinit var btnUser: ImageButton
    private lateinit var repo: ArticuloRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = AppDatabase.getInstance(this)
        repo = ArticuloRepository(ApiClient.apiService, db.articuloDao())

        initVariables()
        svSearcher.setIconifiedByDefault(false)
        svSearcher.isIconified = false

        lifecycleScope.launch {
            val cliente = db.clienteDao().getCliente()

            if (cliente != null) {
                txtGreeting.text = cliente.nombreCliente
            }
        }


        getArticulos { articulos ->
            adapter = ArticuloAdapter(articulos) { articuloSeleccionado ->
                val intent = Intent(this, ProductActivity::class.java).apply {
                    putExtra(KEY_ID, articuloSeleccionado.id)
                    putExtra(KEY_DESCRIPTION, articuloSeleccionado.descripcion)
                    putExtra(KEY_CODE, articuloSeleccionado.codigo)
                    putExtra(KEY_CATEGORY, articuloSeleccionado.rubro)
                    putExtra(KEY_SUBCATEGORY, articuloSeleccionado.subrubro)
                    putExtra(KEY_STOCK, articuloSeleccionado.stockmax)
                }
                startActivity(intent)
            }

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

        btnUser.setOnClickListener {
            val popup = PopupMenu(this, btnUser)
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
            val articulos = withContext(Dispatchers.IO) {
                val local = repo.getArticulosLocal().toDomainList()
                if (local.isNotEmpty()) {
                    local
                } else {
                    try {
                        val remote = repo.getArticulos()
                        repo.insertAll(remote)
                        remote
                    } catch (e: Exception) {
                        Log.e(tag, "Error al obtener artículos de la API: ${e.message}")
                        emptyList()
                    }
                }
            }

            withContext(Dispatchers.Main) {
                onLoaded(articulos)
            }
        }
    }

    private fun setupRecycler(articulos: List<Articulo>) {
        adapter = ArticuloAdapter(articulos) { articuloSeleccionado ->
            val intent = Intent(this, ProductActivity::class.java).apply {
                putExtra("id", articuloSeleccionado.id)
                putExtra("descripcion", articuloSeleccionado.descripcion)
                putExtra("codigo", articuloSeleccionado.codigo)
                putExtra("rubro", articuloSeleccionado.rubro)
                putExtra("subrubro", articuloSeleccionado.subrubro)
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        svSearcher.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun initVariables() {
        txtGreeting = findViewById(R.id.txtGreeting)
        recyclerView = findViewById(R.id.recyclerArticulos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        svSearcher = findViewById(R.id.svProductSearch)
        btnUser = findViewById(R.id.btnUser)
    }


}
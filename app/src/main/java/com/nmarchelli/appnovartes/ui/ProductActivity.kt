package com.nmarchelli.appnovartes.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nmarchelli.appnovartes.R
import com.nmarchelli.appnovartes.data.local.AppDatabase
import com.nmarchelli.appnovartes.data.local.entities.CartItemEntity
import com.nmarchelli.appnovartes.data.remote.ApiClient
import com.nmarchelli.appnovartes.domain.models.Articulo
import com.nmarchelli.appnovartes.KEY_CATEGORY
import com.nmarchelli.appnovartes.KEY_CODE
import com.nmarchelli.appnovartes.KEY_DESCRIPTION
import com.nmarchelli.appnovartes.KEY_ID
import com.nmarchelli.appnovartes.KEY_STOCK
import com.nmarchelli.appnovartes.domain.models.Rubro
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductActivity : AppCompatActivity() {

    //region >> Variables
    private lateinit var btnBack: ImageView
    private lateinit var txtTitle: TextView

    private lateinit var txtNombre: TextView
    private lateinit var txtCodigo: TextView
    private lateinit var btnAdd: Button

    private lateinit var spTelas: Spinner
    private lateinit var spTelas2: Spinner

    private lateinit var spPatas: Spinner
    private lateinit var spPatas2: Spinner

    private lateinit var rubros: List<Rubro>
    private lateinit var articulos: List<Articulo>

    private lateinit var btnMenos: ImageView
    private lateinit var btnMas: ImageView
    private lateinit var etCantidad: EditText

    private var idProducto: Int = 0
    private lateinit var nombreProducto: String
    private lateinit var codigoProducto: String
    private lateinit var categoriaProducto: String
    private var stockMaxProducto: Int = 0
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        val db = AppDatabase.getInstance(applicationContext)

        setVariables()
        getArticulo()
        getRubros()
        setProductosActivity()


        btnBack.setOnClickListener {
            this.finish()
        }

        btnAdd.setOnClickListener {
            //Add to cart
            val _cantidad = etCantidad.text.toString().toIntOrNull() ?: 1

            val cartItem = CartItemEntity(
                codigo = codigoProducto,
                nombre = nombreProducto,
                telas = spTelas.selectedItem.toString(),
                telas2 = spTelas2.selectedItem.toString(),
                patas = spPatas.selectedItem.toString(),
                patas2 = spPatas2.selectedItem.toString(),
                cantidad = _cantidad,
                productoId = idProducto
            )

            CoroutineScope(Dispatchers.IO).launch {

                val existing = db.cartDao().getItemByCodigo(codigoProducto)

                if (existing != null) {
                    val nuevaCantidad = existing.cantidad + _cantidad
                    db.cartDao().updateCantidad(codigoProducto, nuevaCantidad)
                } else {
                    db.cartDao().insertItem(cartItem)
                }
                Log.d("CART", "Item insertado con ID: ${cartItem.id}")
            }



            Toast.makeText(this, getString(R.string.txt_product_added_tocart), Toast.LENGTH_SHORT)
                .show()
            finish()
        }

        btnMenos.setOnClickListener {
            val cantidadActual = etCantidad.text.toString().toIntOrNull() ?: 1
            if (cantidadActual > 1) {
                etCantidad.setText((cantidadActual - 1).toString())
            }
        }

        btnMas.setOnClickListener {
            val cantidadActual = etCantidad.text.toString().toIntOrNull() ?: 1
            if (cantidadActual < stockMaxProducto) {
                etCantidad.setText((cantidadActual + 1).toString())
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.txt_max_quantity) + stockMaxProducto,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun setProductosActivity() {
        txtTitle.text = getString(R.string.txt_product_title)
        idProducto = intent.getIntExtra(KEY_ID, -1)
        nombreProducto = intent.getStringExtra(KEY_DESCRIPTION).toString()
        codigoProducto = intent.getStringExtra(KEY_CODE).toString()
        categoriaProducto = intent.getStringExtra(KEY_CATEGORY).toString()
        stockMaxProducto = intent.getIntExtra(KEY_STOCK, 1)

        etCantidad.setText("1")
        txtNombre.text = nombreProducto
        txtCodigo.text = "#$codigoProducto"
        spPatas2.isEnabled = false
        spTelas2.isEnabled = false
        btnAdd.isEnabled = false
    }

    private fun setVariables() {
        btnBack = findViewById(R.id.btnBackNavBar)
        txtTitle = findViewById(R.id.txtTitleNavBar)

        txtNombre = findViewById(R.id.txtNombreProduct)
        txtCodigo = findViewById(R.id.txtCodigoProduct)

        spTelas = findViewById(R.id.spTelasProduct)
        spTelas2 = findViewById(R.id.spTelas2Product)

        spPatas = findViewById(R.id.spPatasProduct)
        spPatas2 = findViewById(R.id.spPatas2Product)

        btnMenos = findViewById(R.id.imgMenosProduct)
        btnMas = findViewById(R.id.imgMasProduct)
        etCantidad = findViewById(R.id.etCantidadProduct)

        btnAdd = findViewById(R.id.btnAddToCartProduct)
    }

    private fun getRubros() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getRubros()
                if (response.isSuccessful) {
                    rubros = response.body() ?: emptyList()
                    Log.i("ProductAct", "$rubros")

                    val rubrosPatas = listOf(getString(R.string.sp_option)) +
                            rubros.filter { it.pata == 1 && it._borrado == 0 }
                                .map { it.descripcion }
                    val rubrosTelas = listOf(getString(R.string.sp_option)) +
                            rubros.filter { it.tela == 1 && it._borrado == 0 }
                                .map { it.descripcion }

                    val adapterPatas =
                        ArrayAdapter(this@ProductActivity, R.layout.item_spinner, rubrosPatas)
                    adapterPatas.setDropDownViewResource(R.layout.item_spinner_dropdown)
                    spPatas.adapter = adapterPatas

                    val adapterTelas =
                        ArrayAdapter(this@ProductActivity, R.layout.item_spinner, rubrosTelas)
                    adapterTelas.setDropDownViewResource(R.layout.item_spinner_dropdown)
                    spTelas.adapter = adapterTelas

                    spPatas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position == 0) {
                                spPatas2.adapter = null
                                checkSpinners()
                                return
                            }
                            spPatas2.isEnabled = true
                            val rubroSeleccionado = rubros.filter { it.pata == 1 }[position - 1]
                            val articulosFiltrados =
                                articulos.filter { it.rubro == rubroSeleccionado.codigo }
                            val nombresArticulos = articulosFiltrados.map { it.descripcion }

                            val adapterArticulos = ArrayAdapter(
                                this@ProductActivity,
                                R.layout.item_spinner,
                                nombresArticulos
                            )
                            adapterArticulos.setDropDownViewResource(R.layout.item_spinner_dropdown)
                            spPatas2.adapter = adapterArticulos

                            checkSpinners()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

                    spTelas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position == 0) {
                                spTelas2.adapter = null
                                checkSpinners()
                                return
                            }
                            spTelas2.isEnabled = true
                            val rubroSeleccionado = rubros.filter { it.tela == 1 }[position - 1]
                            val articulosFiltrados =
                                articulos.filter { it.rubro == rubroSeleccionado.codigo }
                            val nombresArticulos = articulosFiltrados.map { it.descripcion }

                            val adapterArticulos = ArrayAdapter(
                                this@ProductActivity,
                                R.layout.item_spinner,
                                nombresArticulos
                            )
                            adapterArticulos.setDropDownViewResource(R.layout.item_spinner_dropdown)
                            spTelas2.adapter = adapterArticulos

                            checkSpinners()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }


                } else {
                    Toast.makeText(
                        this@ProductActivity,
                        "Error al obtener rubros",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProductActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }

    private fun getArticulo() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getArticulos()
                if (response.isSuccessful) {
                    articulos = response.body() ?: emptyList()
                } else {
                    Toast.makeText(
                        this@ProductActivity,
                        "Error al obtener artículos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProductActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun checkSpinners() {
        val opcionInvalida = getString(R.string.sp_option)
        val valido = spPatas.selectedItem.toString() != opcionInvalida &&
                spTelas.selectedItem.toString() != opcionInvalida

        btnAdd.isEnabled = valido
    }


}
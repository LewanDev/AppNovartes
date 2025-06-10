package com.nmarchelli.appnovartes.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nmarchelli.appnovartes.R
import com.nmarchelli.appnovartes.data.local.AppDatabase
import com.nmarchelli.appnovartes.data.local.entities.CartItemEntity
import com.nmarchelli.appnovartes.domain.models.CartAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartAdapter

    private lateinit var btnBack: ImageView
    private lateinit var btnConfirmBuy: Button
    private lateinit var txtTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getInstance(applicationContext)

        setVariables()
        loadCartItems()
        getClienteData()

        btnConfirmBuy.setOnClickListener {
            //Confirm buy
            CoroutineScope(Dispatchers.IO).launch {
                val configsMap = getConfiguraciones()
                val body = setMailBodyText(db)
                withContext(Dispatchers.Main) {
                    sendMailFromAndroidOS(
                        this@CartActivity,
                        configsMap["email"].toString(), "Pedido App - Cliente Nro: "+configsMap["codigo"], body
                    )
                }
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getClienteData() {
        lifecycleScope.launch {

        }
    }

    private suspend fun getConfiguraciones(): Map<String, String> {
        val list = listOf("email", "nombre", "domicilio", "codigo")
        val configs = db.configuracionDao().getConfiguraciones(list)
        return configs.associate { it.variable to it.valor }

    }

    private fun setVariables() {
        btnConfirmBuy = findViewById(R.id.btnCartConfirmBuy)
        btnBack = findViewById(R.id.btnBackNavBar)
        txtTitle = findViewById(R.id.txtTitleNavBar)
        txtTitle.text = getString(R.string.btn_cart)
    }

    private fun loadCartItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val items = db.cartDao().getAllItems()
            items.forEach {
                Log.d("CART", "ID: ${it.id}, Producto: ${it.nombre}, Cant: ${it.cantidad}")
            }
            withContext(Dispatchers.Main) {
                adapter = CartAdapter(items) { itemToDelete ->
                    deleteItem(itemToDelete)
                }
                recyclerView.adapter = adapter
            }
        }
    }

    private fun deleteItem(item: CartItemEntity) {
        CoroutineScope(Dispatchers.IO).launch {

            if (item.cantidad > 1) {
                db.cartDao().decreaseCantidad(item.id)
            } else {
                db.cartDao().deleteItemById(item.id)
            }

            val updatedItems = db.cartDao().getAllItems()
            withContext(Dispatchers.Main) {
                adapter.updateData(updatedItems)
                Toast.makeText(
                    this@CartActivity,
                    getString(R.string.txt_product_deleted),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    suspend fun setMailBodyText(db: AppDatabase): String {
        val items = db.cartDao().getAllItems()
        val cliente = db.clienteDao().getCliente()

        if (cliente != null) {
            val sbClienteData = StringBuilder()
            sbClienteData.append("Datos del Cliente")
            sbClienteData.append("Nombre: " + cliente.nombreCliente + ", Documento: " + cliente.numeroDto + "\r\n")
            sbClienteData.append("Domicilio: " + cliente.domicilio + "\r\n")
            sbClienteData.append("Email: " + cliente.mail + ", Teléfono: " + cliente.telefono + "\r\n")
            sbClienteData.append("\r\n")
        }

        if (items.isNotEmpty()) {
            val sbItemsCart = StringBuilder()
            sbItemsCart.append("Detalle del pedido:\n\n")
            items.forEach { item ->
                sbItemsCart.append("Producto: ${item.nombre}\n")
                sbItemsCart.append("Código: ${item.codigo}\n")
                sbItemsCart.append("Cantidad: ${item.cantidad}\n")
                sbItemsCart.append("Telas: ${item.telas}, ${item.telas2}\n")
                sbItemsCart.append("Patas: ${item.patas}, ${item.patas2}\n")
                sbItemsCart.append("\n")
            }
            return sbItemsCart.toString()
        } else {
            return "El carrito está vacío."
        }
    }

    fun sendMailFromAndroidOS(
        context: Context,
        destinatario: String,
        asunto: String,
        cuerpo: String
    ) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822" // Solo apps de mail
            putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
            putExtra(Intent.EXTRA_SUBJECT, asunto)
            putExtra(Intent.EXTRA_TEXT, cuerpo)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Enviar mail..."))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No hay cliente de correo instalado", Toast.LENGTH_SHORT).show()
        }
    }


}
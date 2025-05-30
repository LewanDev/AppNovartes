package com.nmarchelli.appnovartes.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        btnConfirmBuy.setOnClickListener {
            //Confirm buy

        }

        btnBack.setOnClickListener {
            finish()
        }
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
                Toast.makeText(this@CartActivity, getString(R.string.txt_product_deleted), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
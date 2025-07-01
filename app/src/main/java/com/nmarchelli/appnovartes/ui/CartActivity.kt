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
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nmarchelli.appnovartes.R
import com.nmarchelli.appnovartes.data.local.AppDatabase
import com.nmarchelli.appnovartes.data.local.entities.CartItemEntity
import com.nmarchelli.appnovartes.data.remote.ApiClient
import com.nmarchelli.appnovartes.data.repository.ArticuloRepository
import com.nmarchelli.appnovartes.data.repository.PedidoCabRepository
import com.nmarchelli.appnovartes.domain.models.CartAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class CartActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: CartAdapter

    private lateinit var btnBack: ImageView
    private lateinit var btnConfirmBuy: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var txtTitle: TextView
    private lateinit var repoPedidoCab: PedidoCabRepository

    private var asuntoMail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        db = AppDatabase.getInstance(this)

        recyclerView = findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(this)
        repoPedidoCab = PedidoCabRepository(ApiClient.apiService, db.pedidoCabDao())

        setVariables()
        loadCartItems()

        btnConfirmBuy.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val configsMap = getConfiguraciones()
                val xlsFile = createXLS(applicationContext)
                val body = setMailBodyText(db, repoPedidoCab)

                withContext(Dispatchers.Main) {
                    sendMailFromAndroidOS(
                        context = this@CartActivity,
                        destinatario = configsMap["email"].toString(),
                        asunto =  asuntoMail,
                        cuerpo = body,
                        archivo = xlsFile
                    )
                }
            }
        }

        btnBack.setOnClickListener {
            finish()
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

    suspend fun createXLS(context: Context): File{
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("Pedido")

        // Cabecera
        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("Código")
        header.createCell(1).setCellValue("Nombre")
        header.createCell(2).setCellValue("Cantidad")

        val items = AppDatabase.getInstance(context).cartDao().getAllItems()

        for ((index, item) in items.withIndex()) {
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(item.codigo)
            row.createCell(1).setCellValue(item.nombre)
            row.createCell(2).setCellValue(item.cantidad.toString())
        }

        // Guardar archivo en almacenamiento temporal
        val file = File(context.cacheDir, "pedido.xls")
        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()

        return file
    }

    suspend fun setMailBodyText(db: AppDatabase, pedidoCabRepository: PedidoCabRepository): String {
        val items = db.cartDao().getAllItems()
        val cliente = db.clienteDao().getCliente()
        val sbMailBodyText = StringBuilder()

        val lastPedidoCab = pedidoCabRepository.getLastPedidoCab()
        val nuevoId = lastPedidoCab.id + 1

        var total = 0

        if (cliente != null) {
            asuntoMail = "Pedido App Nuevo Nro $nuevoId"

            sbMailBodyText.append("Código cliente: " + cliente.codigo + "\r\n")
            sbMailBodyText.append("Domicilio: " + cliente.domicilio + "\r\n")
            sbMailBodyText.append("Email: " + cliente.mail + ", Teléfono: " + cliente.telefono + "\r\n")
            sbMailBodyText.append("\r\n")
        }

        if (items.isNotEmpty()) {
            sbMailBodyText.append("---------------------------------------------------------------------------\r\n")
            sbMailBodyText.append("  Codigo                        Articulo                         Cantidad   \r\n")
            sbMailBodyText.append("---------------------------------------------------------------------------\r\n")

            items.forEach { item ->
                sbMailBodyText.append("${item.codigo}        ${item.nombre}       ${item.cantidad}\r\n")
//                sbMailBodyText.append("${item.nombre}\n")
//                sbMailBodyText.append("${item.cantidad}\n")
            }
            sbMailBodyText.append("\r\n     TOTAL DE LA COMPRA                               $")
            return sbMailBodyText.toString()
        } else {
            return getString(R.string.txt_emptycart)
        }
    }

    fun sendMailFromAndroidOS(
        context: Context,
        destinatario: String,
        asunto: String,
        cuerpo: String,
        archivo: File
    ) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", archivo)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822" // Solo apps de mail
            putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
            putExtra(Intent.EXTRA_SUBJECT, asunto)
            putExtra(Intent.EXTRA_TEXT, cuerpo)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        }
        try {
            context.startActivity(Intent.createChooser(intent, getString(R.string.txt_sendmail)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, getString(R.string.txt_nomailapp), Toast.LENGTH_SHORT).show()
        }
    }


}
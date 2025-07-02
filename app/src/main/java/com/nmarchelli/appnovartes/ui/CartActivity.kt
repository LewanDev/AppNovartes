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
import com.nmarchelli.appnovartes.data.local.entities.ClienteEntity
import com.nmarchelli.appnovartes.data.remote.ApiClient
import com.nmarchelli.appnovartes.data.repository.InformePedidoRepository
import com.nmarchelli.appnovartes.data.repository.PedidoCabRepository
import com.nmarchelli.appnovartes.domain.models.CartAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CartActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: CartAdapter

    private lateinit var btnBack: ImageView
    private lateinit var btnConfirmBuy: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var txtTitle: TextView
    private lateinit var repoPedidoCab: PedidoCabRepository
    private lateinit var repoInformePedido: InformePedidoRepository
    private lateinit var clienteEntity: ClienteEntity

    private var asuntoMail = ""
    private var pedidoCabId = ""
    private var ordenPedido = ""
    private var totalPedido = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        db = AppDatabase.getInstance(this)

        recyclerView = findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(this)
        repoPedidoCab = PedidoCabRepository(ApiClient.apiService, db.pedidoCabDao())
        repoInformePedido = InformePedidoRepository(ApiClient.apiService, db.informePedidoDao())

        setVariables()
        loadCartItems()

        btnConfirmBuy.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val configsMap = getConfiguraciones()
                val body = setMailBodyText(db, repoPedidoCab)
                val xlsFile = createXLS(applicationContext, repoInformePedido)

                withContext(Dispatchers.Main) {
                    sendMailFromAndroidOS(
                        context = this@CartActivity,
                        destinatario = configsMap["email"].toString(),
                        asunto = asuntoMail,
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


    suspend fun setMailBodyText(db: AppDatabase, repository: PedidoCabRepository): String {
        val items = db.cartDao().getAllItems()
        val cliente = db.clienteDao().getCliente()
        val sbMailBodyText = StringBuilder()

        val lastPedidoCab = repository.getLastPedidoCab()
        pedidoCabId = (lastPedidoCab.id.toInt() + 1).toString()



        if (cliente != null) {
            clienteEntity = cliente
            asuntoMail = "Pedido App Nuevo Nro $pedidoCabId"

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
                totalPedido++
//                sbMailBodyText.append("${item.nombre}\n")
//                sbMailBodyText.append("${item.cantidad}\n")
            }
            sbMailBodyText.append("\r\n     TOTAL DE LA COMPRA                               $${totalPedido}.00")
            return sbMailBodyText.toString()
        } else {
            return getString(R.string.txt_emptycart)
        }
    }

    suspend fun createXLS(context: Context, repository: InformePedidoRepository): File {
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("Pedido")

        // Cabecera
        val row1 = sheet.createRow(0)
        row1.createCell(0).setCellValue("Pedido")
        row1.createCell(1).setCellValue("Fecha")
        val row2 = sheet.createRow(1)
        row2.createCell(0).setCellValue(pedidoCabId)
        row2.createCell(1).setCellValue(getCurrentDateTime())

        val row4 = sheet.createRow(3)
        row4.createCell(0).setCellValue("Código")
        row4.createCell(1).setCellValue("Razón Social")
        row4.createCell(2).setCellValue("Documento")
        row4.createCell(3).setCellValue("Email")
        row4.createCell(4).setCellValue("Teléfono")

        val row5 = sheet.createRow(4)
        row5.createCell(0).setCellValue(clienteEntity.codigo.toString())
        row5.createCell(1).setCellValue(clienteEntity.nombreCliente)
        row5.createCell(2).setCellValue(clienteEntity.idDocumento.toString())
        row5.createCell(3).setCellValue(clienteEntity.mail)
        row5.createCell(4).setCellValue(clienteEntity.telefono)

        val row7 = sheet.createRow(6)
        row7.createCell(0).setCellValue("Código Orden")
        row7.createCell(1).setCellValue("Artículo")
        row7.createCell(2).setCellValue("Descripción")
        row7.createCell(3).setCellValue("Precio")
        row7.createCell(4).setCellValue("Cantidad")
        row7.createCell(5).setCellValue("Total")

        val items = AppDatabase.getInstance(context).cartDao().getAllItems()
        val lastInformePedido = repository.getLastInformePedido()
        ordenPedido = (lastInformePedido.orden.toInt() + 1).toString()

        val initRow = 7
        val precio = 1.00
        for ((index, item) in items.withIndex()) {
            val row = sheet.createRow(initRow + index)
            row.createCell(0).setCellValue(ordenPedido)
            row.createCell(1).setCellValue(item.codigo)
            row.createCell(2).setCellValue(item.nombre)
            row.createCell(3).setCellValue("${precio}0") //Precio - todo: ver esto cuando suban los precios a la db
            row.createCell(4).setCellValue(item.cantidad.toString())
            row.createCell(5).setCellValue("${item.cantidad}.00") // todo: mismo que arriba
        }

        val file = File(context.cacheDir, "pedido_${pedidoCabId}.xls")
        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()

        return file
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

    fun getCurrentDateTime(): String {
        // Get the current date and time
        val currentDateTime = LocalDateTime.now()
        println("Current Date and Time: $currentDateTime")

        // Format the date and time for display (optional)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)
        return formattedDateTime
    }

}
package com.nmarchelli.appnovartes

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.*

class CustomProductsActivity : AppCompatActivity() {

    private lateinit var nombreProducto: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_custom_products)

        val txtNombre = findViewById<TextView>(R.id.txtNombreProducto)
        val txtPrecio = findViewById<TextView>(R.id.txtPrecioProducto)

        nombreProducto = intent.getStringExtra("producto") ?: "Producto"

        val producto = intent.getSerializableExtra("producto") as? Product

        txtNombre.text = (buildString {
            append("Personalizando:")
            append(producto?.nombre)
        })
        "Precio: $${producto?.precio ?: 0}".also { txtPrecio.text = it }

        // Spinners
        val spinnerTela = findViewById<Spinner>(R.id.spinnerTela)
        val spinnerColorTela = findViewById<Spinner>(R.id.spinnerColorTela)
        val spinnerPatas = findViewById<Spinner>(R.id.spinnerPatas)
        val spinnerColorPatas = findViewById<Spinner>(R.id.spinnerColorPatas)
        val spinnerMedidas = findViewById<Spinner>(R.id.spinnerMedidas)
        val spinnerCostura = findViewById<Spinner>(R.id.spinnerCostura)

        // Botón
        val btnAgregarCarrito = findViewById<Button>(R.id.btnAgregarAlCarrito)

        fun setSpinner(spinner: Spinner, opciones: List<String>) {
            spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
        }

        setSpinner(spinnerTela, listOf("Lino", "Chenille", "Cuero sintético"))
        setSpinner(spinnerColorTela, listOf("Beige", "Gris", "Azul"))
        setSpinner(spinnerPatas, listOf("Madera", "Metal"))
        setSpinner(spinnerColorPatas, listOf("Natural", "Negro", "Cromado"))
        setSpinner(spinnerMedidas, listOf("1.20 x 0.80", "1.50 x 0.90", "2.00 x 1.00"))
        setSpinner(spinnerCostura, listOf("Simple", "Doble reforzada", "Decorativa"))

        btnAgregarCarrito.setOnClickListener {
            if (producto != null) {
                val productoPersonalizado = Product(
                    id = producto.id,
                    nombre = producto.nombre,
                    categoria = producto.categoria,
                    precio = producto.precio,
                    tela = spinnerTela.selectedItem.toString(),
                    colorTela = spinnerColorTela.selectedItem.toString(),
                    patas = spinnerPatas.selectedItem.toString(),
                    colorPatas = spinnerColorPatas.selectedItem.toString(),
                    medidas = spinnerMedidas.selectedItem.toString(),
                    costura = spinnerCostura.selectedItem.toString()
                )

                Cart.productos.add(productoPersonalizado)
                Toast.makeText(this, "${producto.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error: producto no encontrado", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
package com.nmarchelli.appnovartes

object Catalog {
    val productos = listOf(
        Product(
            id = 1,
            nombre = "Sillón 2 cuerpos",
            categoria = "Sillones",
            precio = 120000,
            tela = "Lino",
            colorTela = "Beige",
            patas = "Madera",
            colorPatas = "Natural",
            medidas = "1.60 x 0.90",
            costura = "Doble"
        ),
        Product(
            id = 2,
            nombre = "Silla nórdica",
            categoria = "Sillas",
            precio = 45000,
            tela = "Chenille",
            colorTela = "Gris",
            patas = "Metal",
            colorPatas = "Negro",
            medidas = "0.50 x 0.50",
            costura = "Simple"
        ),
        Product(
            id = 3,
            nombre = "Almohadón decorativo",
            categoria = "Almohadones",
            precio = 15000,
            tela = "Algodón",
            colorTela = "Azul",
            patas = "Sin patas",
            colorPatas = "N/A",
            medidas = "0.40 x 0.40",
            costura = "Invisible"
        )
    )
}
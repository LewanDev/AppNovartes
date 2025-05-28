package com.nmarchelli.appnovartes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.nmarchelli.appnovartes.R
import com.nmarchelli.appnovartes.data.local.AppDatabase
import com.nmarchelli.appnovartes.data.local.entities.ClienteEntity
import com.nmarchelli.appnovartes.data.remote.ApiClient
import com.nmarchelli.appnovartes.data.remote.LoginRequest
import com.nmarchelli.appnovartes.spSession
import com.nmarchelli.appnovartes.spSessionIsLoggedKey
import com.nmarchelli.appnovartes.spUserNameKey
import com.nmarchelli.appnovartes.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etUser: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUser = findViewById(R.id.edtUsuario)
        etPassword = findViewById(R.id.edtContrasena)
        btnLogin = findViewById(R.id.btnLogin)

        checkIfUserIsLogged()

        btnLogin.setOnClickListener {
            val user = etUser.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                doLogin(user, pass)
            }
        }

    }

    private fun checkIfUserIsLogged() {
        val prefs = getSharedPreferences(spSession, MODE_PRIVATE)
        if (prefs.getBoolean(spSessionIsLoggedKey, false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun doLogin(username: String, password: String) {
        val db = AppDatabase.getInstance(applicationContext)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.login(LoginRequest(username, password))

                if (response.isSuccessful && response.body()?.success == true) {
                    val loginResponse = response.body()!!
                    val clienteResponse = loginResponse.cliente

                    val cliente = ClienteEntity(
                        codigo = clienteResponse.codigo?.toIntOrNull() ?: 0,
                        nombreCliente = clienteResponse.nombre ?: "",
                        idDocumento = clienteResponse.idDocumento ?: 0,
                        numeroDto = clienteResponse.numeroDto?.toString() ?: "",
                        idProvincia = clienteResponse.idProvincia ?: 0,
                        idLocalidad = clienteResponse.idLocalidad ?: 0,
                        codigoPostal = clienteResponse.codigoPostal ?: "",
                        domicilio = clienteResponse.domicilio ?: "",
                        mail = clienteResponse.mail ?: "",
                        telefono = clienteResponse.telefono ?: "",
                        ctacte = clienteResponse.ctacte ?: ""
                    )

                    db.clienteDao().insert(cliente)

                    // Guardar login en SharedPreferences
                    getSharedPreferences(spSession, MODE_PRIVATE).edit {
                        putBoolean(spSessionIsLoggedKey, true)
                        putString(spUserNameKey, username)
                    }

                    // Redirigir al MainActivity
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()

                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        response.body()?.message ?: "Hubo un problema al loguear",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Error de red: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}
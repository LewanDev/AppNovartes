package com.nmarchelli.appnovartes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.nmarchelli.appnovartes.R
import com.nmarchelli.appnovartes.data.local.AppDatabase
import com.nmarchelli.appnovartes.data.local.entities.ClienteEntity
import com.nmarchelli.appnovartes.data.remote.ApiClient
import com.nmarchelli.appnovartes.data.remote.ApiService
import com.nmarchelli.appnovartes.data.remote.LoginRequest
import com.nmarchelli.appnovartes.spSession
import com.nmarchelli.appnovartes.spSessionIsLoggedKey
import com.nmarchelli.appnovartes.spUserNameKey
import com.nmarchelli.appnovartes.ui.main.MainActivity
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var etUser: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val api = Retrofit.Builder()
            .baseUrl("https://novartes.com.ar/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val db = AppDatabase.getInstance(applicationContext)
        //val clienteDao = db.clienteDao()
        //val usuarioDao = db.usuarioDao()

        /*
        val userHard = "admin"
        val passHard = "1"
         */


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


        /*
        btnLogin.setOnClickListener {
            val user = etUser.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                doLogin(user, pass)
            }

            lifecycleScope.launch {
                try {
                    val response = api.login(LoginRequest(user, pass))
                    if (response.isSuccessful && response.body()?.success == true) {
                        val loginResponse = response.body()!!

                        val cliente = ClienteEntity(
                            codigo = loginResponse.codigo!!,
                            nombreCliente = loginResponse.nombre_cliente ?: "",
                            idDocumento = loginResponse.id_documento ?: 0,
                            numeroDto = loginResponse.numero_dto ?: "",
                            idProvincia = loginResponse.id_provincia ?: 0,
                            idLocalidad = loginResponse.id_localidad ?: 0,
                            codigoPostal = loginResponse.codigo_postal ?: "",
                            domicilio = loginResponse.domicilio ?: "",
                            mail = loginResponse.mail ?: "",
                            telefono = loginResponse.telefono ?: "",
                            ctacte = loginResponse.ctacte ?: ""
                        )

                        db.clienteDao().insert(cliente)

                        // Guardar login en SharedPreferences si querés
                        getSharedPreferences(spSession, MODE_PRIVATE).edit {
                            putBoolean(spSessionIsLoggedKey, true)
                            putString(spUserNameKey, user)
                        }

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, response.body()?.message ?: "Error en login", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
                /*
                //val usuarioValido = usuarioDao.login(user, pass)

                //if (usuarioValido != null) {
                if(user == userHard && pass == passHard){
                    val prefs = getSharedPreferences(spSession, MODE_PRIVATE)
                    prefs.edit {
                        putBoolean(spSessionIsLoggedKey, true)
                        putString(spUserNameKey, user)
                    }
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, getString(R.string.wrong_user_passwd), Toast.LENGTH_SHORT).show()
                }

                 */
            }
        }

         */

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
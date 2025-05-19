package com.nmarchelli.appnovartes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.nmarchelli.appnovartes.R
import com.nmarchelli.appnovartes.spSession
import com.nmarchelli.appnovartes.spSessionIsLoggedKey
import com.nmarchelli.appnovartes.spUserNameKey

class LoginActivity : AppCompatActivity() {
    private val adminUser = "admin"
    private val adminPasswd = "123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)


        val etUser = findViewById<EditText>(R.id.edtUsuario)
        val etPassword = findViewById<EditText>(R.id.edtContrasena)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        checkIfUserIsLogged()

        btnLogin.setOnClickListener {
            val user = etUser.text.toString()
            val passwd = etPassword.text.toString()

            //todo: validate with DB
            if (user == adminUser && passwd == adminPasswd) {
                val prefs = getSharedPreferences(spSession, MODE_PRIVATE)
                prefs.edit { putBoolean(spSessionIsLoggedKey, true) }
                prefs.edit { putString(spUserNameKey, user)}
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, R.string.wrong_user_passwd, Toast.LENGTH_SHORT).show()
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
}
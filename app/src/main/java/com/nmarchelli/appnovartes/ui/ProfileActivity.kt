package com.nmarchelli.appnovartes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.nmarchelli.appnovartes.R
import com.nmarchelli.appnovartes.data.local.AppDatabase
import com.nmarchelli.appnovartes.spSession
import com.nmarchelli.appnovartes.spSessionIsLoggedKey
import com.nmarchelli.appnovartes.spUserNameKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var txtTitle: TextView
    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtBalance: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val db = AppDatabase.getInstance(this)

        setVariables()

        txtTitle.text = getString(R.string.txt_profile_title)

        lifecycleScope.launch {
            val cliente = db.clienteDao().getCliente()

            if (cliente != null) {
                txtName.text = cliente.nombreCliente
                txtEmail.text = cliente.mail
                txtBalance.text = "$ " + cliente.ctacte
            }
        }

        btnLogout.setOnClickListener {
            getSharedPreferences(spSession, MODE_PRIVATE).edit {
                putBoolean(spSessionIsLoggedKey, false)
            }

            CoroutineScope(Dispatchers.IO).launch {
                db.clienteDao().deleteAll()

                withContext(Dispatchers.Main) {
                    val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setVariables(){
        txtTitle = findViewById(R.id.txtTitleNavBar)
        txtName = findViewById(R.id.txtProfileName)
        txtEmail = findViewById(R.id.txtProfileMail)
        txtBalance = findViewById(R.id.txtProfileBalance)

        btnBack = findViewById(R.id.btnBack)
        btnLogout = findViewById(R.id.btnLogout)
    }
}
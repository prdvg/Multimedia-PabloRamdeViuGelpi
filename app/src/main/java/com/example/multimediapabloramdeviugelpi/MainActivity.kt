@file:Suppress("UNUSED_EXPRESSION")

package com.example.multimediapabloramdeviugelpi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val STORAGE_AND_CAMERA_PERMISSION_REQUEST_CODE = 1001

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pantalla = findViewById<LinearLayout>(R.id.pantalla)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_AND_CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startMenuMain()
        }
        pantalla.setOnTouchListener { v, event ->
            //Toast.makeText(this@MainActivity, "mensaje", Toast.LENGTH_SHORT).show()
            val intent = Intent (this, MenuMain::class.java)
            startActivity(intent)
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_AND_CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de almacenamiento concedido", Toast.LENGTH_LONG).show()
                    startMenuMain()
                } else {
                    Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startMenuMain() {
        val intent = Intent(this, MenuMain::class.java)
        startActivity(intent)
        finish()
    }
}
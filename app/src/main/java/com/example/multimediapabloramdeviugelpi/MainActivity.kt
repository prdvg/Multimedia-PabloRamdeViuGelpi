package com.example.multimediapabloramdeviugelpi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val PERMISSION_REQUEST_CODE = 1001

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pantalla = findViewById<LinearLayout>(R.id.pantalla)

        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )

        val permissionsToRequest = mutableListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            Log.d("MainActivity", "Solicitando permisos...")
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            Log.d("MainActivity", "Todos los permisos ya están concedidos.")
            //startMenuMain()
        }

        pantalla.setOnTouchListener { v, event ->
            val intent = Intent(this, MenuMain::class.java)
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
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = mutableListOf<String>()
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    PermissionManager.addGrantedPermission(permissions[i])
                } else {
                    deniedPermissions.add(permissions[i])
                }
            }
            if (deniedPermissions.isEmpty()) {
                Toast.makeText(this, "Todos los permisos concedidos", Toast.LENGTH_LONG).show()
                startMenuMain()
            } else {
                val deniedPermissionsString = deniedPermissions.joinToString(", ")
                Toast.makeText(
                    this,
                    "Los siguientes permisos no fueron concedidos: $deniedPermissionsString",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }



    private fun startMenuMain() {
        val intent = Intent(this, MenuMain::class.java)
        startActivity(intent)
        finish()
    }
}

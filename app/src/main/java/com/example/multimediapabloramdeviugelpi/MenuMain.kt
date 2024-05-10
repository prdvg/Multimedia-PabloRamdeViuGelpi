package com.example.multimediapabloramdeviugelpi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MenuMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)
        val texto = findViewById<Button>(R.id.texto)
        val imagen = findViewById<Button>(R.id.imagen)
        val audio = findViewById<Button>(R.id.audio)
        val video = findViewById<Button>(R.id.video)

        texto.setOnClickListener(){
            val intent = Intent(this, TextEdit::class.java)
            startActivity(intent)
        }
        imagen.setOnClickListener(){
            val intent = Intent(this, Pictures::class.java)
            startActivity(intent)
        }
        audio.setOnClickListener{
            val intent = Intent(this, MakeAudio::class.java)
            startActivity(intent)
        }
    }
}
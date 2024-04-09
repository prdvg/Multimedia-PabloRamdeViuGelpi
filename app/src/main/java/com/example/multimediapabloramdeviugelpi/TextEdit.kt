package com.example.multimediapabloramdeviugelpi

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.format.TextStyle

class TextEdit : AppCompatActivity() {
    private var BApply = false
    private var IApply = false
    private var SApply = false
    private var saved: Boolean = false
    private var textSize = 1.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_edit)
        var save = findViewById<Button>(R.id.saveTxt)
        var back = findViewById<Button>(R.id.backTxt)
        var name = findViewById<EditText>(R.id.NombreTxt)
        var negrita = findViewById<TextView>(R.id.negrita)
        var cursiva = findViewById<TextView>(R.id.cursiva)
        var subrrayado = findViewById<TextView>(R.id.subrrayado)
        var incSize = findViewById<TextView>(R.id.incSize)
        var decSize = findViewById<TextView>(R.id.decSize)
        var texto = findViewById<EditText>(R.id.docText)

        back.setOnClickListener(){
            volver()
        }
        save.setOnClickListener(){
            if(name.text!=null){
                val fich = texto.text.toString()
                guardarArchivo(fich)
            }else{
                Toast.makeText(this, "debe de ponerle un nombre al archivo", Toast.LENGTH_LONG).show()
            }
        }
        negrita.setOnClickListener(){
            if(!BApply){
                statusB(negrita)
                applyNegrita(texto)
                BApply = true
            }else{
                defaultB(negrita)
                noNegrita(texto)
                BApply = false
            }
        }
        cursiva.setOnClickListener(){
            if(!IApply){
                statusI(cursiva)
                applyCursiva(texto)
                IApply = true
            }else{
                defautI(cursiva)
                noCursiva(texto)
                IApply = false
            }
        }
        subrrayado.setOnClickListener(){
            if(!SApply){
                statusS(subrrayado)
                applySubrayado(texto)
                SApply = true
            }else{
                defaultS(subrrayado)
                noSubrayado(texto)
                SApply = false
            }
        }
        incSize.setOnClickListener {
            val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation)
            incSize.startAnimation(pulseAnimation)
            increaseTextSize(texto)
        }
        decSize.setOnClickListener {
            val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation)
            decSize.startAnimation(pulseAnimation)
            decreaseTextSize(texto)
        }
    }
    private fun volver(){
        var intent = Intent (this, MenuMain::class.java)
        startActivity(intent)
    }
    private fun statusB(texto: TextView){
        val boton = texto.text.toString()
        val spannable = SpannableStringBuilder(boton)
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, boton.length, 0)
        texto.text = spannable
    }
    private fun defaultB(texto: TextView){
        val boton = texto.text.toString()
        val spannable = SpannableStringBuilder(boton)
        spannable.setSpan(StyleSpan(Typeface.NORMAL), 0, boton.length, 0)
        texto.text = spannable
    }
    private fun statusI(texto: TextView){
        val boton = texto.text.toString()
        val spannable = SpannableStringBuilder(boton)
        spannable.setSpan(StyleSpan(Typeface.ITALIC), 0, boton.length, 0)
        texto.text = spannable
    }
    private fun defautI(texto: TextView){
        val boton = texto.text.toString()
        val spannable = SpannableStringBuilder(boton)
        spannable.setSpan(StyleSpan(Typeface.NORMAL), 0, boton.length, 0)
        texto.text = spannable
    }
    private fun statusS(texto: TextView){
        val boton = texto.text.toString()
        val spannable = SpannableStringBuilder(boton)
        spannable.setSpan(UnderlineSpan(), 0, boton.length, 0)
        texto.text = spannable
    }
    private fun defaultS(texto: TextView){
        val boton = texto.text.toString()
        val spannable = SpannableStringBuilder(boton)
        spannable.setSpan(StyleSpan(Typeface.NORMAL), 0, boton.length, 0)
        texto.text = spannable
    }
    private fun applyNegrita(editText: EditText) {
        val editableText = editText.text
        val spannable = SpannableStringBuilder(editableText)
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, editableText.length, 0)
        editText.text = spannable
    }
    private fun noNegrita(editText: EditText){
        val spannable = SpannableStringBuilder(editText.text)
        val styleSpans = spannable.getSpans(0, spannable.length, StyleSpan::class.java)

        for (span in styleSpans) {
            if (span.style == Typeface.BOLD) {
                val start = spannable.getSpanStart(span)
                val end = spannable.getSpanEnd(span)
                spannable.removeSpan(span)
                spannable.setSpan(StyleSpan(span.style and Typeface.BOLD.inv()), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        editText.text = spannable
    }
    private fun applyCursiva(editText: EditText){
        val editableText = editText.text
        val spannable = SpannableStringBuilder(editableText)
        spannable.setSpan(StyleSpan(Typeface.ITALIC), 0, editableText.length, 0)
        editText.text = spannable
    }
    private fun noCursiva(editText: EditText){
        val spannable = SpannableStringBuilder(editText.text)
        val styleSpans = spannable.getSpans(0, spannable.length, StyleSpan::class.java)

        for (span in styleSpans) {
            if (span.style == Typeface.ITALIC) {
                val start = spannable.getSpanStart(span)
                val end = spannable.getSpanEnd(span)
                spannable.removeSpan(span)
                spannable.setSpan(StyleSpan(span.style and Typeface.ITALIC.inv()), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        editText.text = spannable
    }
    private fun applySubrayado(editText: EditText) {
        val spannable = SpannableStringBuilder(editText.text)
        spannable.setSpan(UnderlineSpan(), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        editText.text = spannable
    }

    private fun noSubrayado(editText: EditText) {
        val spannable = SpannableStringBuilder(editText.text)
        val underlineSpans = spannable.getSpans(0, spannable.length, UnderlineSpan::class.java)

        for (span in underlineSpans) {
            spannable.removeSpan(span)
        }

        editText.text = spannable
    }
    private fun increaseTextSize(editText: EditText) {
        textSize += 0.1f
        updateTextSize(editText)
    }

    private fun decreaseTextSize(editText: EditText) {
        textSize -= 0.1f
        updateTextSize(editText)
    }

    private fun updateTextSize(editText: EditText) {
        val editableText = editText.text
        val spannable = SpannableStringBuilder(editableText)
        spannable.setSpan(RelativeSizeSpan(textSize), 0, editableText.length, 0)
        editText.text = spannable
    }
    private fun guardarArchivo(nombreArchivo: String) {
        val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDirectory, "$nombreArchivo.txt")

        try {
            val fileWriter = FileWriter(file)
            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.write(nombreArchivo)
            bufferedWriter.close()

            Toast.makeText(this, "Archivo guardado correctamente", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Error al guardar el archivo", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
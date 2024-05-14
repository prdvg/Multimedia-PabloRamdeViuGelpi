package com.example.multimediapabloramdeviugelpi

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class MakeAudio : AppCompatActivity() {

    private val PICK_AUDIO_REQUEST_CODE = 1001
    private val RECORD_AUDIO_REQUEST_CODE = 1002
    private var currentAudioUri: Uri? = null

    private lateinit var back: Button
    private lateinit var save: Button
    private lateinit var grabar: Button
    private lateinit var pickAudio: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_audio)

        back = findViewById(R.id.backAudio)
        save = findViewById(R.id.guardarAudio)
        grabar = findViewById(R.id.recordAudio)
        pickAudio = findViewById(R.id.selectAudio)

        createAudioFolderIfNeeded()
        back.setOnClickListener {
            finish()
        }

        save.setOnClickListener {
            saveAudio()
        }

        grabar.setOnClickListener {
            recordAudio()
        }

        pickAudio.setOnClickListener {
            pickAudio()
        }
    }

    private val pickAudioLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val uri = data?.data
            if (uri != null) {
                currentAudioUri = uri
            }
        }
    }

    private fun playAudio(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "audio/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    private fun pickAudio() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "audio/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }
        pickAudioLauncher.launch(Intent.createChooser(intent, "Seleccionar archivo de audio"))
    }

    private fun recordAudio() {
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        startActivityForResult(intent, RECORD_AUDIO_REQUEST_CODE)
    }

    private fun saveAudio() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Guardar audio")
        builder.setMessage("¿Desea guardar el audio?")

        val input = EditText(this)
        input.hint = "Nombre del archivo"
        builder.setView(input)

        builder.setPositiveButton("Sí") { dialog, which ->
            val fileName = input.text.toString().trim()
            if (fileName.isNotEmpty()) {
                saveAudioToFile(fileName)
            } else {
                Toast.makeText(this, "El nombre del archivo no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun saveAudioToFile(fileName: String) {
        currentAudioUri?.let { uri ->
            val inputStream = contentResolver.openInputStream(uri)
            val audioFolder = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Audios")
            if (!audioFolder.exists()) {
                audioFolder.mkdirs()
            }
            val audioFile = File(audioFolder, "$fileName.mp3")
            inputStream?.use { input ->
                FileOutputStream(audioFile).use { output ->
                    input.copyTo(output)
                }
                Toast.makeText(this, "Audio guardado en: ${audioFile.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } ?: run {
            Toast.makeText(this, "No se ha seleccionado ningún audio para guardar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            PICK_AUDIO_REQUEST_CODE -> {
                data?.data?.let { uri ->
                    currentAudioUri = uri
                }
            }
            RECORD_AUDIO_REQUEST_CODE -> {
                data?.data?.let { uri ->
                    currentAudioUri = uri
                }
            }
        }
    }

    private fun createAudioFolderIfNeeded() {
        val audioFolder = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Audios")
        if (!audioFolder.exists()) {
            audioFolder.mkdirs()
        }
    }
}

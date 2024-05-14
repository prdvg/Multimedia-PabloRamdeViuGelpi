package com.example.multimediapabloramdeviugelpi

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
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
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

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
                playAudio(uri) // Reproduce el audio seleccionado
            }
        }
    }

    private fun playAudio(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "audio/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
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
            try {
                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.use { input ->
                    val audioUri = saveAudioToMusicFolder(fileName, input)
                    if (audioUri != null) {
                        Toast.makeText(this, "Audio guardado en: ${audioUri.path}", Toast.LENGTH_LONG).show()
                        playAudio(audioUri) // Reproduce el audio guardado
                    } else {
                        Toast.makeText(this, "Error al guardar el audio", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(this, "Error al guardar el audio", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } ?: run {
            Toast.makeText(this, "No se ha seleccionado ningún audio para guardar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveAudioToMusicFolder(fileName: String, inputStream: InputStream): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, "$fileName.mp3")
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
            put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
        }

        val resolver = contentResolver
        val audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val audioUri = resolver.insert(audioCollection, contentValues)

        if (audioUri != null) {
            resolver.openOutputStream(audioUri)?.use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            return audioUri
        }

        return null
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
                    playAudio(uri) // Reproduce el audio seleccionado
                }
            }
            RECORD_AUDIO_REQUEST_CODE -> {
                data?.data?.let { uri ->
                    currentAudioUri = uri
                    playAudio(uri) // Reproduce el audio grabado
                }
            }
        }
    }
}

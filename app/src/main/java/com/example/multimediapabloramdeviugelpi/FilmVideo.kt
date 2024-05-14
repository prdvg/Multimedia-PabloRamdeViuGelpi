package com.example.multimediapabloramdeviugelpi

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class FilmVideo : AppCompatActivity() {

    private val PICK_VIDEO_REQUEST_CODE = 101
    private val RECORD_VIDEO_REQUEST_CODE = 102
    private var videoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_video)

        val selectVideo = findViewById<ImageView>(R.id.selectVideoButton)
        val recordVideoButton = findViewById<Button>(R.id.recordVideoButton)

        selectVideo.setOnClickListener {
            selectVideoFromGallery()
        }

        recordVideoButton.setOnClickListener {
            recordVideo()
        }
    }

    private fun selectVideoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_VIDEO_REQUEST_CODE)
    }

    private fun recordVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, RECORD_VIDEO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_VIDEO_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        playVideo(uri)
                    }
                }
                RECORD_VIDEO_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        videoUri = uri
                        showSaveVideoDialog(uri)
                    }
                }
            }
        }
    }

    private fun playVideo(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "video/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    private fun showSaveVideoDialog(uri: Uri) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Guardar video")
        builder.setMessage("¿Desea guardar el video?")

        val input = EditText(this)
        input.hint = "Nombre del archivo"
        builder.setView(input)

        builder.setPositiveButton("Sí") { dialog, which ->
            val videoName = input.text.toString()
            if (videoName.isNotBlank()) {
                saveVideoToFile(uri, videoName)
            } else {
                Toast.makeText(this, "El nombre del archivo no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun saveVideoToFile(videoUri: Uri, videoName: String) {
        val contentResolver = contentResolver
        val moviesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val videoFile = File(moviesDirectory, "$videoName.mp4")

        try {
            contentResolver.openInputStream(videoUri)?.use { inputStream ->
                videoFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Toast.makeText(this, "Video guardado en: ${videoFile.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar el video", Toast.LENGTH_SHORT).show()
        }
    }
}

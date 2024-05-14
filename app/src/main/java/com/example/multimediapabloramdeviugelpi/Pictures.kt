package com.example.multimediapabloramdeviugelpi

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class Pictures : AppCompatActivity() {

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_IMAGE_CAPTURE = 2

    private var capturedPhotoFile: File? = null
    private lateinit var photoImageView: ImageView
    private lateinit var outputDirectory: File
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pictures)
        photoImageView = findViewById(R.id.photoImageView)
        val takePhotoButton: Button = findViewById(R.id.takePhotoButton)
        val saveButton = findViewById<Button>(R.id.savePhoto)
        val backButton = findViewById<Button>(R.id.backPhotos)

        outputDirectory = getOutputDirectory()

        takePhotoButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            } else {
                dispatchTakePictureIntent()
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MenuMain::class.java)
            startActivity(intent)
        }

        saveButton.setOnClickListener {
            showSaveConfirmationDialog()
        }

        photoImageView.setOnClickListener {
            openGallery()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            capturedPhotoFile = createImageFile()
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.multimediapabloramdeviugelpi.fileprovider",
                capturedPhotoFile!!
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "No se pudo abrir la aplicación de la cámara: ${e.message}", e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            photoImageView.setImageURI(selectedImageUri)
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(capturedPhotoFile?.absolutePath)
            photoImageView.setImageBitmap(bitmap)
        }
    }

    private fun showSaveConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        builder.setView(input)
        builder.setMessage("¿Desea guardar esta imagen?")
            .setTitle("Confirmación")
            .setPositiveButton("Sí") { _, _ ->
                val imageName = input.text.toString()
                if (imageName.isNotEmpty()) {
                    savePhoto(imageName)
                } else {
                    Toast.makeText(this, "El nombre de la imagen no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun savePhoto(imageName: String) {
        val photoFile = capturedPhotoFile ?: run {
            Log.e(TAG, "No hay foto capturada para guardar")
            Toast.makeText(this, "No hay foto capturada para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val multimediaDirectory = File(picturesDirectory, "Multimedia(PabloRamdeViuGelpi)")

        if (!multimediaDirectory.exists()) {
            if (!multimediaDirectory.mkdirs()) {
                Log.e(TAG, "Error al crear el directorio Multimedia(PabloRamdeViuGelpi)")
                Toast.makeText(this, "Error al crear el directorio Multimedia(PabloRamdeViuGelpi)", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val destination = File(multimediaDirectory, "$imageName.png")
        try {
            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            val outputStream = FileOutputStream(destination)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Toast.makeText(this, "Foto guardada exitosamente en $destination", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "Error al guardar la imagen: ${e.message}")
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", outputDirectory).apply {
            currentPhotoPath = absolutePath
        }
    }

    companion object {
        private const val TAG = "PicturesActivity"
    }
}

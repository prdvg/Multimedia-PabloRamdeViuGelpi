package com.example.multimediapabloramdeviugelpi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class Pictures : AppCompatActivity() {

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var photoImageView: ImageView
    private lateinit var outputDirectory: File
    private lateinit var imageCapture: ImageCapture
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
                // Si el permiso de la cámara no está concedido, solicitarlo
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            } else {
                // Si el permiso de la cámara está concedido, abrir la cámara
                startCamera()
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MenuMain::class.java)
            startActivity(intent)
        }

        saveButton.setOnClickListener {
            // Mostrar un diálogo de confirmación para guardar la imagen
            showSaveConfirmationDialog()
        }

        // Configurar el OnClickListener para photoImageView
        photoImageView.setOnClickListener {
            openGallery()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture)
                takePhoto()
            } catch (exc: Exception) {
                Log.e(TAG, "Error al iniciar la cámara: ${exc.message}", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val photoFile = createImageFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        try {
            imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // Obtener el directorio de imágenes público
                        val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

                        // Construir la ruta del directorio Multimedia(PabloRamdeViuGelpi)
                        val multimediaDirectory = File(picturesDirectory, "Multimedia(PabloRamdeViuGelpi)")

                        // Verificar si el directorio Multimedia(PabloRamdeViuGelpi) existe, si no, crearlo
                        if (!multimediaDirectory.exists()) {
                            if (!multimediaDirectory.mkdirs()) {
                                Log.e(TAG, "Error al crear el directorio Multimedia(PabloRamdeViuGelpi)")
                                Toast.makeText(this@Pictures, "Error al crear el directorio Multimedia(PabloRamdeViuGelpi)", Toast.LENGTH_SHORT).show()
                                return
                            }
                        }

                        // Construir la ruta del archivo de destino
                        val destination = File(multimediaDirectory, photoFile.name)

                        // Mover el archivo de la ubicación actual a la nueva ubicación
                        try {
                            if (photoFile.renameTo(destination)) {
                                val savedUri = Uri.fromFile(destination)
                                val msg = "Foto guardada: $savedUri"
                                Toast.makeText(this@Pictures, msg, Toast.LENGTH_SHORT).show()
                                val bitmap = BitmapFactory.decodeFile(destination.absolutePath)
                                photoImageView.setImageBitmap(bitmap)
                                currentPhotoPath = destination.absolutePath
                            } else {
                                Log.e(TAG, "Error al mover la imagen al directorio Multimedia(PabloRamdeViuGelpi)")
                                Toast.makeText(this@Pictures, "Error al mover la imagen al directorio Multimedia(PabloRamdeViuGelpi)", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.e(TAG, "Error al guardar la imagen: ${e.message}")
                            Toast.makeText(this@Pictures, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e(TAG, "Error al guardar la foto: ${exception.message}", exception)
                        Toast.makeText(this@Pictures, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error al capturar la imagen: ${e.message}", e)
            Toast.makeText(this@Pictures, "Error al capturar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSaveConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Desea guardar esta imagen?")
            .setTitle("Confirmación")
            .setPositiveButton("Sí") { _, _ ->
                savePhoto()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun savePhoto() {
        if (currentPhotoPath.isNotEmpty()) {
            val source = File(currentPhotoPath)

            // Verificar si el archivo de origen existe y se puede leer
            if (!source.exists() || !source.canRead()) {
                Log.e(TAG, "Error: El archivo de origen no existe o no se puede leer")
                Toast.makeText(this, "Error: El archivo de origen no existe o no se puede leer", Toast.LENGTH_SHORT).show()
                return
            }

            val destinationDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Multimedia(PabloRamdeViuGelpi)")

            // Verificar si la carpeta de destino existe o crearla si no
            if (!destinationDir.exists()) {
                if (!destinationDir.mkdirs()) {
                    // Si no se pudo crear la carpeta, mostrar un mensaje de error y salir
                    Log.e(TAG, "Error al crear la carpeta de destino")
                    Toast.makeText(this, "Error al crear la carpeta de destino", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            val destination = File(destinationDir, source.name)
            try {
                // Copiar el archivo de origen al archivo de destino
                source.copyTo(destination)
                Toast.makeText(this, "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "Error al guardar la imagen: ${e.message}")
                Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e(TAG, "Error: No hay ninguna imagen para guardar")
            Toast.makeText(this, "Error: No hay ninguna imagen para guardar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            photoImageView.setImageURI(selectedImageUri)
        }
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
            // Guarda la ruta del archivo para usarla con Intents de compartir
            currentPhotoPath = absolutePath
        }
    }

    companion object {
        private const val TAG = "PicturesActivity"
    }
}

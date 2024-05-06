package com.example.multimediapabloramdeviugelpi

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TakePhoto : AppCompatActivity() {
    private lateinit var imageCapture: ImageCapture
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()

        val back = findViewById<Button>(R.id.backJPG)
        val take = findViewById<ImageButton>(R.id.buttonCapture)

        back.setOnClickListener {
            val intent = Intent(this, MenuMain::class.java)
            startActivity(intent)
        }
        take.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Desea guardar esta imagen?")
                .setTitle("Confirmación")
                .setPositiveButton("Sí") { dialog, which ->
                    showNameDialog()
                }
                .setNegativeButton("No") { dialog, which -> }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Error al iniciar la cámara", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun showNameDialog() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Introduce un nombre")
            .setView(input)
            .setPositiveButton("Aceptar") { dialog, which ->
                val nombre = input.text.toString()
                takePhoto(nombre)
            }
            .setNegativeButton("Cancelar") { dialog, which -> }

        val dialog = builder.create()
        dialog.show()
    }

    private fun takePhoto(nombre: String) {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Foto guardada: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

                    // Guardar la imagen como bitmap
                    saveImage(photoFile, nombre)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Error al guardar la foto: ${exception.message}", exception)
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun saveImage(photoFile: File, nombre: String) {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

        val directory = getExternalFilesDir(null)
        val filename = "$nombre.jpg"
        val file = File(directory, filename)

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "TakePhoto"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}

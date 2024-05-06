package com.example.multimediapabloramdeviugelpi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class Pictures : AppCompatActivity() {

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private lateinit var photoImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pictures)

        photoImageView = findViewById(R.id.photoImageView)
        val takePhotoButton: Button = findViewById(R.id.takePhotoButton)
        val save = findViewById<Button>(R.id.savePhoto)
        val back = findViewById<Button>(R.id.backPhotos)
        takePhotoButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            } else {
                openCamera()
            }
        }
        back.setOnClickListener{
            val intent = Intent(this, MenuMain::class.java)
            startActivity(intent)
        }
        save.setOnClickListener{
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

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                photoImageView.setImageBitmap(imageBitmap)
                // Aquí puedes guardar la imagen y preguntar al usuario por el nombre
            } else {
                Toast.makeText(this, "Error al obtener la foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
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
        /*val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                TakePhoto.FILENAME_FORMAT, Locale.US
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
                    Log.e(TakePhoto.TAG, "Error al guardar la foto: ${exception.message}", exception)
                }
      */}//)
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
        private const val TAKE_PHOTO_REQUEST_CODE = 100
    }
}

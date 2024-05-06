import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.multimediapabloramdeviugelpi.R

class Photos : AppCompatActivity() {

    private lateinit var photoImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)
        enableEdgeToEdge()

        photoImageView = findViewById(R.id.photoImageView)
        val takePhotoButton: Button = findViewById(R.id.takePhotoButton)
        takePhotoButton.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                photoImageView.setImageBitmap(imageBitmap)
                // Aquí puedes guardar la imagen y preguntar al usuario por el nombre
            } else {
                Toast.makeText(this, "Error al obtener la foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAKE_PHOTO_REQUEST_CODE = 100
    }
}

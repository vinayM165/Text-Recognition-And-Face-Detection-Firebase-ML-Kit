package project.android.ml_kotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import kotlinx.android.synthetic.main.activity_text_recognisation.view.*

class TextRecognisation : AppCompatActivity() {
    val REQ_IMAGE = 1
    lateinit var imageV: ImageView
    lateinit var con_text: TextView
    lateinit var bitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_recognisation)
        imageV = findViewById(R.id.image)
        con_text = findViewById(R.id.converttxt)

    }
    fun click(view : View) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQ_IMAGE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQ_IMAGE && resultCode == Activity.RESULT_OK){
            bitmap = data!!.extras!!.get("data") as Bitmap
            imageV.setImageBitmap(bitmap)
        }
    }


    fun detect(v:View){
        if(imageV.drawable == null){
            Toast.makeText(this, "Select an Image First", Toast.LENGTH_LONG).show()
            return
        }
        v.isEnabled = false
        var image = FirebaseVisionImage.fromBitmap(bitmap)
        var detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image)
            .addOnSuccessListener {firebaseVisionText ->
                v.isEnabled = true
                    RecText(firebaseVisionText)
            }.addOnFailureListener{e ->
                v.isEnabled = true
             Toast.makeText(baseContext,"Failed to detect  " + e.message,Toast.LENGTH_SHORT).show()
            }
    }


    fun RecText(resultText : FirebaseVisionText){
        if (resultText.textBlocks.size == 0) {
            con_text.setText("No Text Found")
            return
        }
        for (block in resultText.textBlocks) {
            val blockText = block.text
            con_text.setText(blockText + "\n")
        }
    }


}







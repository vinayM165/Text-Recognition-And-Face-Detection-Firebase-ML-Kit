package project.android.ml_kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
   lateinit var text_Rec : CardView
   lateinit var face : CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text_Rec = findViewById(R.id.txt_rec)
        face = findViewById(R.id.face_det)
        text_Rec.setOnClickListener(){
            startActivity(Intent(baseContext,TextRec::class.java))
        }
        face.setOnClickListener(){
            startActivity(Intent(baseContext,FaceDetection::class.java))
        }


    }
}
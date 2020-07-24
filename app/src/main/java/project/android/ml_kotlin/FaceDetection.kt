package project.android.ml_kotlin

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.theartofdev.edmodo.cropper.CropImage
import java.io.IOException


class FaceDetection : AppCompatActivity() {
    var image  : FirebaseVisionImage? = null
    var textView  : TextView? = null
    var button : Button? = null
    var imageView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detection)
        textView = findViewById(R.id.text);
        button = findViewById(R.id.selectImage);
        imageView = findViewById(R.id.image);
        button!!.setOnClickListener{view ->
            CropImage.activity().start(this);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                if (result != null) {
                    val uri: Uri = result.uri //path of image in phone
                    imageView!!.setImageURI(uri) //set image in imageview
                    textView!!.text = "" //so that previous text don't get append with new one
                    detectFaceFromImage(uri)
                }
            }
        }
    }
    private fun detectFaceFromImage(uri: Uri) {
        try {
            image = FirebaseVisionImage.fromFilePath(baseContext, uri)
            val highAccuracyOpts =
                FirebaseVisionFaceDetectorOptions.Builder()
                    .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                    .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                    .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                    .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                    .build()
            val detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(highAccuracyOpts)
            detector.detectInImage(image!!)
                .addOnSuccessListener { faces ->
                    for (face in faces) {
                        val bounds: Rect = face.boundingBox
                        textView!!.append(
                            """
                                Bounding Polygon (${bounds.centerX()},${bounds.centerY()})
                                
                                
                                """.trimIndent()
                        )
                        val rotY =
                            face.headEulerAngleY // Head is rotated to the right rotY degrees
                        val rotZ =
                            face.headEulerAngleZ // Head is tilted sideways rotZ degrees
                        textView!!.append("Angles of rotation Y:$rotY,Z: $rotZ\n\n")
                        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                        // nose available):
                        // If face tracking was enabled:
                        if (face.trackingId != FirebaseVisionFace.INVALID_ID) {
                            val id = face.trackingId
                            textView!!.append("id: $id\n\n")
                        }
                        val leftEar =
                            face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
                        if (leftEar != null) {
                            val leftEarPos = leftEar.position
                            textView!!.append(
                                """
                                    LeftEarPos: (${leftEarPos.x},${leftEarPos.y})
                                    
                                    
                                    """.trimIndent()
                            )
                        }
                        val rightEar =
                            face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR)
                        if (rightEar != null) {
                            val rightEarPos = rightEar.position
                            textView!!.append(
                                """
                                    RightEarPos: (${rightEarPos.x},${rightEarPos.y})
                                    
                                    
                                    """.trimIndent()
                            )
                        }

                        // If contour detection was enabled:
                        val leftEyeContour =
                            face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points
                        val upperLipBottomContour =
                            face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM)
                                .points

                        // If classification was enabled:
                        if (face.smilingProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                            val smileProb = face.smilingProbability
                            textView!!.append(
                                "SmileProbability: " + ("" + smileProb * 100).subSequence(
                                    0,
                                    4
                                ) + "%" + "\n\n"
                            )
                        }
                        if (face.rightEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                            val rightEyeOpenProb =
                                face.rightEyeOpenProbability
                            textView!!.append(
                                "RightEyeOpenProbability: " + ("" + rightEyeOpenProb * 100).subSequence(
                                    0,
                                    4
                                ) + "%" + "\n\n"
                            )
                        }
                        if (face.leftEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                            val leftEyeOpenProbability =
                                face.leftEyeOpenProbability
                            textView!!.append(
                                "LeftEyeOpenProbability: " + ("" + leftEyeOpenProbability * 100).subSequence(
                                    0,
                                    4
                                ) + "%" + "\n\n"
                            )
                        }
                    }
                }
                .addOnFailureListener {
                   Toast.makeText(baseContext,"Failed to detect",Toast.LENGTH_SHORT).show()
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
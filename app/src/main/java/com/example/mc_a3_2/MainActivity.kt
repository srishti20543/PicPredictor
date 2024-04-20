package com.example.mc_a3_2

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mc_a3_2.ui.theme.MC_A3_2Theme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.graphics.asImageBitmap
import com.example.mc_a3_2.ml.PredictionModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.*

class MainActivity : ComponentActivity() {

    private var selectedImage: ImageBitmap? by mutableStateOf(null)
    private var selectedImageBitmap: Bitmap? by mutableStateOf(null)

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MC_A3_2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val context = LocalContext.current
                    val getContent =
                        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                            uri?.let {
                                selectedImage = loadBitmap(context.contentResolver, it)
                                selectedImageBitmap =
                                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                            }
                        }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        var classPredicted by remember { mutableStateOf("") }

                        Text(
                            text = "IMAGE CLASSIFIER",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(16.dp)
                        )

                        Button(
                            onClick = { clickPicture() },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Click Picture")
                        }

                        Button(
                            onClick = { getContent.launch("image/*") },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Select Image from Gallery")
                        }

                        Button(
                            onClick = { classPredicted = predict() },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Predict")
                        }

                        if (classPredicted != "") {
                            Text(
                                text = "Predicted Class: $classPredicted",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        selectedImage?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun clickPicture() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePicture.resolveActivity(this.packageManager) != null) {
            startActivityForResult(takePicture, 42)
        } else {
            Toast.makeText(this, "Unable to Open Camera", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            val takenImage = data?.extras?.get("data") as Bitmap
            selectedImageBitmap = takenImage
            selectedImage = takenImage.asImageBitmap()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadBitmap(contentResolver: ContentResolver, uri: Uri): ImageBitmap {
        val inputStream = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)?.asImageBitmap()!!
    }

    private val imageProcessor: ImageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(32, 32, ResizeOp.ResizeMethod.BILINEAR)).build()

    private fun predict(): String {

        if (selectedImageBitmap == null) {
            return "No Image"
        }

        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(selectedImageBitmap)
        tensorImage = imageProcessor.process(tensorImage)

        val model = PredictionModel.newInstance(this)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
        var max = 0
        outputFeature0.forEachIndexed { index, fl ->
            if (outputFeature0[max] < fl) {
                max = index
            }
        }

        model.close()

        return when (max) {
            0 -> "Apple"
            1 -> "Banana"
            2 -> "Orange"
            else -> "Can not Predict"
        }
    }
}
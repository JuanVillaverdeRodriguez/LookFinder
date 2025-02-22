package inditex.tech.lookfinder

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import inditex.tech.lookfinder.ui.theme.LookFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LookFinderTheme {
                CameraScreen()
            }
        }
    }
}

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageList by remember { mutableStateOf<List<String>>(emptyList()) } // Usar una lista de rutas de imágenes

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo = result.data?.extras?.get("data") as? Bitmap
            if (photo != null) {
                // Generar un nombre único para la imagen
                val fileName = "photo_${System.currentTimeMillis()}.png"

                // Guardar la imagen en el almacenamiento interno
                val imagePath = saveImageToInternalStorage(context, photo, fileName)

                // Agregar la ruta de la imagen a la lista
                imageList = imageList + imagePath

                Log.d("Camera", "Foto guardada en $imagePath")
            } else {
                Log.d("Camera", "⚠️ No se recibió ninguna imagen de la cámara.")
            }
        } else {
            Log.d("camera", "❌ La cámara fue cancelada o falló.")
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Botón para abrir la cámara
        Button(
            onClick = {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(context.packageManager) != null) {
                    println("✅ Cámara encontrada, abriendo...")
                    cameraLauncher.launch(intent)
                } else {
                    println("❌ No se encontró una aplicación de cámara.")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Abrir Cámara")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar las fotos tomadas en una cuadrícula
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // Número de columnas
            modifier = Modifier.fillMaxSize()
        ) {
            items(imageList) { imagePath ->
                // Decodificar la imagen desde el almacenamiento interno y mostrarla
                val bitmap = BitmapFactory.decodeFile(imagePath)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Foto tomada",
                    modifier = Modifier
                        .padding(4.dp)
                        .size(100.dp) // Tamaño de la imagen
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    LookFinderTheme {
        CameraScreen()
    }
}

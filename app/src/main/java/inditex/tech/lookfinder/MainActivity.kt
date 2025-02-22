package inditex.tech.lookfinder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
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
import androidx.core.content.FileProvider
import inditex.tech.lookfinder.ui.theme.LookFinderTheme
import java.io.File

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

        // Botón para compartir
        Button(
            onClick = {
                val textToShare = "Mirad mi nueva prenda que he comprado. Todo gracias a la increíble aplicación de LookFinder :)"

                // Obtener la primera imagen de la lista y convertirla a Bitmap
                val firstImageBitmap = imageList.firstOrNull()?.let { BitmapFactory.decodeFile(it) }

                // Comparte el texto y la imagen
                shareTextAndImage(context, textToShare, firstImageBitmap)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Compartir Texto e Imagen")
        }


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

private fun shareTextAndImage(context: Context, text: String, imageBitmap: Bitmap?) {
    // Crear el Intent de compartir
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "image/png" // Cambiar el tipo a imagen
    }

    // Si hay una imagen, convertirla a un URI y agregarla al Intent
    imageBitmap?.let {
        // Guardar la imagen en el almacenamiento interno
        val imagePath = saveImageToInternalStorage(context, it, "shared_image_${System.currentTimeMillis()}.png")
        val imageUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(imagePath))

        // Agregar la URI de la imagen al Intent
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Permitir el acceso a la URI
    }

    // Crear un Intent de chooser
    val chooserIntent = Intent.createChooser(shareIntent, "Compartir en Redes Sociales")

    // Filtrar solo las aplicaciones sociales
    val socialAppsPackages = listOf(
        "com.whatsapp",          // WhatsApp
        "com.instagram.android",  // Instagram
        "com.facebook.katana"     // Facebook
    )

    val filteredIntents = mutableListOf<Intent>()

    for (packageName in socialAppsPackages) {
        try {
            // Crear un nuevo Intent para cada paquete
            val intent = Intent(shareIntent).apply {
                setPackage(packageName) // Establecer el paquete de la aplicación
            }
            // Añadir el Intent a la lista solo si la aplicación está instalada
            context.packageManager.getPackageInfo(packageName, 0) // Verificar si la aplicación está instalada
            filteredIntents.add(intent)
        } catch (e: PackageManager.NameNotFoundException) {
            // En caso de que la aplicación no esté instalada, se ignora
        }
    }

    // Agregar los intents filtrados al chooser solo si hay alguno
    if (filteredIntents.isNotEmpty()) {
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, filteredIntents.toTypedArray())
    }

    // Iniciar el chooser
    context.startActivity(chooserIntent)
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    LookFinderTheme {
        CameraScreen()
    }
}

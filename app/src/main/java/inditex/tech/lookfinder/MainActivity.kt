package inditex.tech.lookfinder

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.net.Uri

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import java.io.File

import inditex.tech.lookfinder.ui.theme.LookFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LookFinderTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "camera_screen") {
        composable("camera_screen") { CameraScreen(navController) }
        composable("image_detail_screen/{imagePath}") { backStackEntry ->
            val imagePath = backStackEntry.arguments?.getString("imagePath") ?: return@composable
            ImageDetailScreen(navController, imagePath)
        }
    }
}

@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    var imageList by rememberSaveable { mutableStateOf(emptyList<String>()) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo = result.data?.extras?.get("data") as? Bitmap
            if (photo != null) {
                val fileName = "photo_${System.currentTimeMillis()}.png"
                val imagePath = saveImageToInternalStorage(context, photo, fileName)

                if (File(imagePath).exists()) {
                    imageList = imageList + imagePath
                } else {
                    Log.e("Camera", "Error al guardar la foto")
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(context.packageManager) != null) {
                cameraLauncher.launch(intent)
            } else {
                Toast.makeText(context, "No se encontró la cámara", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Abrir Cámara")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize()) {
            items(imageList) { imagePath ->
                val file = File(imagePath)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imagePath)

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Foto tomada",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(100.dp)
                            .clickable {
                                navController.navigate("image_detail_screen/${Uri.encode(imagePath)}")
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun ImageDetailScreen(navController: NavController, imagePath: String) {
    val decodedPath = Uri.decode(imagePath)
    val file = File(decodedPath)

    if (!file.exists()) {
        Log.e("ImageDetail", "Error: La imagen no existe en la ruta proporcionada.")
        Text("Error: No se pudo cargar la imagen")
        return
    }

    val bitmap = BitmapFactory.decodeFile(decodedPath)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Imagen ampliada",
            modifier = Modifier.fillMaxWidth().height(400.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Implementar la búsqueda de información */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar información de esta imagen")
        }
    }
}

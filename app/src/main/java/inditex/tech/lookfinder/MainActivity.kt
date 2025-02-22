package inditex.tech.lookfinder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import java.io.File
import inditex.tech.lookfinder.Model.DatabaseHelper
import inditex.tech.lookfinder.ui.theme.LookFinderTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import inditex.tech.lookfinder.screens.RecomendationsScreen
import inditex.tech.lookfinder.viewmodels.PostViewModel


class MainActivity : ComponentActivity() {
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = PostViewModel()

        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getpermission = Intent()
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(getpermission)
            }
        }

        // Inicializa el DatabaseHelper
        databaseHelper = DatabaseHelper(this)

        // Esto crea la base de datos si no existe
        databaseHelper.writableDatabase // Asegúrate de que la base de datos se crea
        setContent {
            LookFinderTheme {
                AppNavigation(viewModel)
                //val photoUrl by viewModel.photoUrl.collectAsState()

                LaunchedEffect(Unit) {
                    //viewModel.uploadPhoto("image.jpg")
                    //viewModel.fetchPosts("https://lookfinderserver-production.up.railway.app/uploads/image.jpg")
                }

                //Text(photoUrl)
                //Log.d("API", "photoUrl: " + photoUrl)
            }
        }
    }


}

@Composable
fun AppNavigation(viewModel: PostViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "splash_screen") {
        composable("splash_screen") { SplashScreen(navController) }
        composable("camera_screen") { CameraScreen(navController) }
        composable("image_detail_screen/{imagePath}") { backStackEntry ->
            val imagePath = backStackEntry.arguments?.getString("imagePath") ?: return@composable
            ImageDetailScreen(navController, imagePath)
        }
        composable("recomendations_screen/{imagePath}") { backStackEntry ->
            val imagePath = backStackEntry.arguments?.getString("imagePath") ?: return@composable
            RecomendationsScreen(navController, imagePath, viewModel)
        }
    }
}

private fun shareTextAndImage(context: Context, text: String, imageBitmap: Bitmap?) {
    // Crear el Intent de compartir
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type =
            if (imageBitmap != null) "image/png" else "text/plain" // Ajustar el tipo de contenido
    }

    // Si hay una imagen, guardarla y agregarla al Intent
    imageBitmap?.let {
        val imagePath = saveImageToInternalStorage(
            context,
            it,
            "shared_image_${System.currentTimeMillis()}.png"
        )
        val imageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            File(imagePath)
        )

        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    // Intent de chooser
    val chooserIntent = Intent.createChooser(shareIntent, "Compartir en Redes Sociales")

    // Filtrar solo las apps sociales
    val socialAppsPackages =
        listOf("com.whatsapp", "com.instagram.android", "com.facebook.katana")
    val packageManager = context.packageManager

    val filteredIntents = socialAppsPackages.mapNotNull { packageName ->
        try {
            packageManager.getPackageInfo(
                packageName,
                0
            ) // Verificar si la app está instalada
            Intent(shareIntent).apply { setPackage(packageName) } // Crear un intent para esa app
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    // Si hay apps sociales disponibles, añadirlas al chooser
    if (filteredIntents.isNotEmpty()) {
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            filteredIntents.toTypedArray()
        )
    }

    // Iniciar el compartir
    context.startActivity(chooserIntent)
}
/////////////////////////////////////////////////////////////////////
@Composable
fun ImageDetailScreen(navController: NavController, imagePath: String) {
    val context = LocalContext.current
    val decodedPath = Uri.decode(imagePath)
    val file = File(decodedPath)

    if (!file.exists()) {
        Log.e("ImageDetail", "Error: La imagen no existe en la ruta proporcionada.")
        Text("Error: No se pudo cargar la imagen")
        return
    }

    val bitmap = BitmapFactory.decodeFile(decodedPath)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Mostrar la imagen ampliada
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Imagen ampliada",
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para buscar información
        Button(
            onClick = { navController.navigate("recomendations_screen/${Uri.encode(imagePath)}") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar información de esta imagen")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para compartir la imagen específica
        Button(
            onClick = {
                val textToShare = "Mirad mi nueva prenda que he comprado. Todo gracias a la increíble aplicación de LookFinder :) 📸"
                shareTextAndImage(
                    context,
                    textToShare,
                    bitmap
                ) // Compartir la imagen específica
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Compartir esta imagen")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    var imageList by rememberSaveable { mutableStateOf(emptyList<String>()) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.extras?.getParcelable("data", Bitmap::class.java)
            } else {
                result.data?.extras?.get("data") as? Bitmap
            }
            if (photo != null) {
                val fileName = "photo_${System.currentTimeMillis()}.jpg"
                val imagePath = saveImageToInternalStorage(context, photo, fileName)

                if (File(imagePath).exists()) {
                    imageList = imageList + imagePath
                } else {
                    Log.e("Camera", "Error al guardar la foto")
                }
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = "gallery_${System.currentTimeMillis()}.png"
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            val imagePath = saveImageToInternalStorage(context, bitmap, fileName)

            if (File(imagePath).exists()) {
                imageList = imageList + imagePath
            } else {
                Log.e("Gallery", "Error al guardar la imagen de la galería")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Look Finder", style = MaterialTheme.typography.titleLarge)
                },
                colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )

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

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (intent.resolveActivity(context.packageManager) != null) {
                        cameraLauncher.launch(intent)
                    } else {
                        Toast.makeText(context, "No se encontró la cámara", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abrir Cámara")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abrir Galería")
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        // Simula una carga de datos con un retraso
        kotlinx.coroutines.delay(2000)
        navController.navigate("camera_screen") {
            popUpTo("splash_screen") { inclusive = true }
        }
    }

    // Contenedor con fondo gris
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray), // Fondo gris
        contentAlignment = Alignment.Center
    ) {
        // Texto centrado
        Text(
            text = "LOOK FINDER",
            fontSize = 36.sp, // Tamaño de fuente ajustable
            color = Color.White, // Color del texto
            fontFamily = FontFamily(Font(R.font.montserratthin))
        )
    }
}





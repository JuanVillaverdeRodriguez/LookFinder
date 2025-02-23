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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import inditex.tech.lookfinder.Model.DatabaseHelper
import inditex.tech.lookfinder.screens.FavoriteScreen
import inditex.tech.lookfinder.screens.RecomendationsScreen
import inditex.tech.lookfinder.ui.theme.LookFinderTheme
import inditex.tech.lookfinder.viewmodels.PostViewModel
import java.io.File


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
    override fun onDestroy() {
        super.onDestroy()
        // Cierra la base de datos cuando la actividad se destruye
        databaseHelper.close()
        Log.d("Cerrada bd","Se ha cerrado el asistente")
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
        composable("favorites_screen") { FavoriteScreen(navController) }
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailScreen(navController: NavController, imagePath: String) {
    val context = LocalContext.current
    val decodedPath = Uri.decode(imagePath)
    val file = File(decodedPath)

    val bitmap = BitmapFactory.decodeFile(decodedPath)

    Column(modifier = Modifier.fillMaxSize()) {
        // TopBar
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Look Finder",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFF333333),
                titleContentColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón para buscar información
                Button(
                    onClick = { navController.navigate("recomendations_screen/${Uri.encode(imagePath)}") }
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buscar información de esta imagen")
                }

                // Botón circular para compartir imagen
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF333333))
                        .clickable {
                            val textToShare = "Mirad mi nueva prenda que he comprado. Todo gracias a la increíble aplicación de LookFinder :) 📸"
                            shareTextAndImage(
                                context,
                                textToShare,
                                bitmap
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir",
                        tint = Color.White
                    )
                }
            }
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
                    Text(
                        text = "Look Finder",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF333333),
                    titleContentColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize()) {
                items(imageList.asReversed()) { imagePath ->
                    Log.d("GRID", "IMAGEN:$imagePath")
                    val file = File(imagePath)
                    if (file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(imagePath)
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Foto tomada",
                            modifier = Modifier
                                .padding(4.dp)
                                .size(150.dp)
                                .clickable {
                                    navController.navigate("image_detail_screen/${Uri.encode(imagePath)}")
                                }
                        )
                    }
                }
            }
        }

        // Aquí se agregan los tres botones en una fila
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center // Centrado de los botones
        ) {
            // Botón de la Galería (izquierda)
            Button(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier
                    .weight(1f), // Hace que el botón de la galería ocupe el 33% del espacio
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF333333),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoLibrary, // Ícono de galería
                    contentDescription = "Abrir Galería",
                    modifier = Modifier.size(24.dp) // Tamaño del ícono
                )
            }


            Spacer(modifier = Modifier.width(16.dp)) // Espacio entre botones

            // Botón de la Cámara (centro) con ícono de cámara
            Button(
                onClick = {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (intent.resolveActivity(context.packageManager) != null) {
                        cameraLauncher.launch(intent)
                    } else {
                        Toast.makeText(context, "No se encontró la cámara", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .weight(1f), // Hace que el botón de la cámara ocupe el 33% del espacio
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF333333),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Camera, // Ícono de la cámara
                    contentDescription = "Abrir cámara",
                    modifier = Modifier.size(24.dp) // Tamaño del ícono
                )
            }

            Spacer(modifier = Modifier.width(16.dp)) // Espacio entre botones

            // Botón "Fav" (derecha)
            Button(
                onClick = {
                    navController.navigate("favorites_screen")
                },
                modifier = Modifier
                    .weight(1f), // Hace que el botón de "Fav" ocupe el 33% del espacio
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF333333),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder, // Ícono de estrella de favoritos
                    contentDescription = "Añadir a Favoritos",
                    modifier = Modifier.size(24.dp) // Tamaño del ícono
                )
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





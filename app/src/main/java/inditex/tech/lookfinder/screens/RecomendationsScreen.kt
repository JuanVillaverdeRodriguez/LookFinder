package inditex.tech.lookfinder.screens

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import inditex.tech.lookfinder.viewmodels.PostViewModel

@Composable
fun RecomendationsScreen(navController: NavController, imagePath: String, viewModel: PostViewModel) {
    val photoUrl by viewModel.photoUrl.collectAsState()

    LaunchedEffect(imagePath) {
        Log.d("API", "Subiendo imagen...")
        viewModel.uploadPhoto(imagePath)
    }

    if (photoUrl.isNotEmpty()) {
        Log.d("API", "Imagen subida. URL: $photoUrl")
        viewModel.fetchPosts(photoUrl)
    } else {
        Log.e("API", "Error: No se pudo subir la imagen")
    }
    Text(photoUrl)
}

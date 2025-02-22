package inditex.tech.lookfinder.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import inditex.tech.lookfinder.viewmodels.PostViewModel

@Composable
fun RecomendationsScreen(navController: NavController, imagePath: String, viewModel: PostViewModel) {
    val photoUrl by viewModel.photoUrl.collectAsState()
    val recomendedProducts by viewModel.recomendedProducts.collectAsState()

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

    LazyColumn(
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        items(recomendedProducts) { product ->
            //Imagen
            Text(product.link)

            //Nombre
            Text(product.name)

            //Precio, EUR/USD
            Row {
                Text(product.price.value.toString())
                Text(product.price.currency)
            }

            HorizontalDivider()

        }
    }

}

package inditex.tech.lookfinder.screens

import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import inditex.tech.lookfinder.R
import inditex.tech.lookfinder.viewmodels.PostViewModel
import kotlinx.coroutines.*
import org.jsoup.Jsoup

@Composable
fun WebViewScreen(viewModel: PostViewModel, name: String, link: String) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }

    // Llamada al ViewModel para hacer scraping de la imagen
    LaunchedEffect(Unit) {
        viewModel.scrapURLImg(webView, link, name)
    }

    // Mostrar el WebView en tu interfaz de Compose
    AndroidView(
        factory = { webView },
        modifier = Modifier.fillMaxSize()
    )
}

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
            Log.d("Antes de bloque: ", "si funciona")

            WebViewScreen(viewModel, product.name, product.link)

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

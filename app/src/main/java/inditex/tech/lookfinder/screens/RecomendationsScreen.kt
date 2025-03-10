package inditex.tech.lookfinder.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import android.webkit.WebView
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import inditex.tech.lookfinder.Model.DatabaseHelper
import inditex.tech.lookfinder.dtos.Product
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
    // Inicializa el DatabaseHelper
    val context = LocalContext.current
    val databaseHelper = DatabaseHelper(context)

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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(recomendedProducts) { product ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProductBox(product)

                Spacer(modifier = Modifier.width(8.dp))

                // Botón de favorito para cada producto
                IconButton(
                    onClick = {
                        product.isFavorite = !product.isFavorite // Cambiar el estado de favorito
                        if (product.isFavorite) {
                            // Lógica para guardar el producto en la base de datos como favorito
                            databaseHelper.insertFavorite(
                                id = product.id, // Asegúrate de que `id` esté disponible en el objeto `product`
                                name = product.name,
                                priceCurrency = product.price.currency,
                                priceValueCurrent = product.price.value.current,
                                priceValueOriginal = product.price.value.original, // O un valor si lo tienes
                                link = product.link, // Asegúrate de que el objeto `product` tenga el campo `link`
                                brand = product.brand // Asegúrate de que el objeto `product` tenga el campo `brand`
                            )
                            Log.d("filled","Se inserto " + product.name + " favorito = " + product.isFavorite)
                        } else {
                            Log.d("filled","Producto num is not favorite " + product.name + " favorito = " + product.isFavorite)
                            // Lógica para eliminar el producto de la base de datos si es necesario
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Agregar a Favoritos",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun ProductBox(product: Product) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
            .shadow(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Imagen temporal (puedes cambiarlo por una imagen real en el futuro)
            Text(text = "imagen", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre del producto
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Precio del producto
                Text(
                    text = "${product.price.value} ${product.price.currency}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}


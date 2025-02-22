package inditex.tech.lookfinder.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import inditex.tech.lookfinder.R
import inditex.tech.lookfinder.dtos.Product
import inditex.tech.lookfinder.viewmodels.PostViewModel

@Composable
fun RecomendationsScreen(navController: NavController, imagePath: String, viewModel: PostViewModel) {
    val photoUrl by viewModel.photoUrl.collectAsState()
    val recomendedProducts by viewModel.recomendedProducts.collectAsState()
    var isFavorite by remember { mutableStateOf(false) }

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
                    .padding(vertical = 6.dp), // Espaciado vertical entre elementos
                verticalAlignment = Alignment.CenterVertically // Alinear verticalmente
            ) {
                ProductBox(product)

                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre la Box y el botón

                // Botón a la derecha de la Box
                IconButton(
                    onClick = {
                        isFavorite = !isFavorite // Cambiar el estado de favorito al hacer clic
                        if (isFavorite) {
                            // Lógica para guardar el producto en la base de datos como favorito
                            //AssistantDB.saveFavoriteProduct(product)
                        } else {
                            // Lógica para eliminar el producto de la base de datos si es necesario
                            //AssistantDB.removeFavoriteProduct(product)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically) // Alinear el botón al lado derecho
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Agregar a Favoritos",
                        tint = Color.Red // Puedes cambiar el color del ícono si lo deseas
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
            .fillMaxWidth(0.8f) // Ajustar el ancho para dar espacio al botón
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
            .shadow(4.dp) // Añadir sombra para imitar la elevación
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



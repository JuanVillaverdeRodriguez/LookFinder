package inditex.tech.lookfinder.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import inditex.tech.lookfinder.Model.DatabaseHelper
import inditex.tech.lookfinder.dtos.Product

@Composable
fun FavoriteScreen(AppNavigation: NavController) {
    // Inicializa el DatabaseHelper
    val context = LocalContext.current
    val databaseHelper = DatabaseHelper(context)

    // Obtén los productos favoritos desde la base de datos
    val favoriteProducts = databaseHelper.getAllFavorites()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(favoriteProducts) { product ->
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
                                id = product.id,
                                name = product.name,
                                priceCurrency = product.price.currency,
                                priceValueCurrent = product.price.value.current,
                                priceValueOriginal = product.price.value.original,
                                link = product.link,
                                brand = product.brand
                            )
                            Log.d(
                                "filled",
                                "Se inserto " + product.name + " favorito = " + product.isFavorite
                            )
                        } else {
                            Log.d(
                                "filled",
                                "Producto num is not favorite " + product.name + " favorito = " + product.isFavorite
                            )
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
}
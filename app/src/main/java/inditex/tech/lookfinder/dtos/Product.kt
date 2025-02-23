package inditex.tech.lookfinder.dtos

data class Product(
    val id: String,
    val name: String,
    val price: Price,
    val link: String,
    val brand: String,
    var isFavorite: Boolean = false
)
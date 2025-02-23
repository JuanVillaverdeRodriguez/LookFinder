package inditex.tech.lookfinder.Model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import inditex.tech.lookfinder.dtos.Price
import inditex.tech.lookfinder.dtos.PriceValue
import inditex.tech.lookfinder.dtos.Product

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "my_database.db"
        private const val DATABASE_VERSION = 1

        // Tabla de favoritos
        const val TABLE_FAVORITES = "favorites"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PRICE_CURRENCY = "price_currency"
        const val COLUMN_PRICE_VALUE_CURRENT = "price_value_current"
        const val COLUMN_PRICE_VALUE_ORIGINAL = "price_value_original"
        const val COLUMN_LINK = "link"
        const val COLUMN_BRAND = "brand"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear la tabla de favoritos
        val createFavoritesTableStatement = """
            CREATE TABLE $TABLE_FAVORITES (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PRICE_CURRENCY TEXT NOT NULL,
                $COLUMN_PRICE_VALUE_CURRENT REAL NOT NULL,
                $COLUMN_PRICE_VALUE_ORIGINAL REAL,
                $COLUMN_LINK TEXT NOT NULL,
                $COLUMN_BRAND TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createFavoritesTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Manejar la lógica de actualización de la base de datos
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
        onCreate(db)
    }
    // Método para insertar un producto en la tabla de favoritos
    fun insertFavorite(
        id: String,
        name: String,
        priceCurrency: String,
        priceValueCurrent: Double,
        priceValueOriginal: Double?,
        link: String,
        brand: String
    ): Long {
        val db = this.writableDatabase

        // Crear un ContentValues para almacenar los datos que se van a insertar
        val values = ContentValues().apply {
            put(COLUMN_ID, id)
            put(COLUMN_NAME, name)
            put(COLUMN_PRICE_CURRENCY, priceCurrency)
            put(COLUMN_PRICE_VALUE_CURRENT, priceValueCurrent)
            put(COLUMN_PRICE_VALUE_ORIGINAL, priceValueOriginal)
            put(COLUMN_LINK, link)
            put(COLUMN_BRAND, brand)
        }

        // Insertar el nuevo registro y manejar duplicados
        val newRowId = db.insertWithOnConflict(TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_IGNORE)

        // Cerrar la base de datos
        db.close()

        // Devolver el ID de la fila insertada o -1 si falló (por duplicado)
        return newRowId
    }
    fun getAllFavorites(): List<Product> {
        val favoritesList = mutableListOf<Product>()
        val db = this.readableDatabase

        // Realizar la consulta para obtener todos los registros de la tabla de favoritos
        val cursor = db.query(
            TABLE_FAVORITES, // Tabla
            null, // Todas las columnas
            null, // No hay condiciones
            null, // No hay argumentos para condiciones
            null, // No hay agrupamiento
            null, // No hay condiciones de agrupamiento
            null  // Sin orden específico
        )

        // Procesar el cursor y agregar los productos a la lista
        if (cursor.moveToFirst()) {
            do {
                // Obtener índices de columnas
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
                val priceCurrencyIndex = cursor.getColumnIndex(COLUMN_PRICE_CURRENCY)
                val priceValueCurrentIndex = cursor.getColumnIndex(COLUMN_PRICE_VALUE_CURRENT)
                val priceValueOriginalIndex = cursor.getColumnIndex(COLUMN_PRICE_VALUE_ORIGINAL)
                val linkIndex = cursor.getColumnIndex(COLUMN_LINK)
                val brandIndex = cursor.getColumnIndex(COLUMN_BRAND)

                // Verificar que todos los índices son válidos
                if (idIndex != -1 && nameIndex != -1 && priceCurrencyIndex != -1 &&
                    priceValueCurrentIndex != -1 && priceValueOriginalIndex != -1 &&
                    linkIndex != -1 && brandIndex != -1) {

                    // Extraer datos del cursor
                    val id = cursor.getString(idIndex)
                    val name = cursor.getString(nameIndex)
                    val priceCurrency = cursor.getString(priceCurrencyIndex)
                    val priceValueCurrent = cursor.getDouble(priceValueCurrentIndex)
                    val priceValueOriginal = cursor.getDouble(priceValueOriginalIndex)
                    val link = cursor.getString(linkIndex)
                    val brand = cursor.getString(brandIndex)

                    // Crear un objeto Product y agregarlo a la lista
                    val product = Product(
                        id,
                        name,
                        Price(priceCurrency, PriceValue(priceValueCurrent, priceValueOriginal)),
                        link,
                        brand,
                        true
                    )
                    favoritesList.add(product)
                } else {
                    Log.e("Database", "One or more column indices are invalid.")
                }
            } while (cursor.moveToNext())
        }

        // Cerrar el cursor y la base de datos
        cursor.close()
        db.close()

        // Devolver la lista de productos favoritos
        return favoritesList
    }
}
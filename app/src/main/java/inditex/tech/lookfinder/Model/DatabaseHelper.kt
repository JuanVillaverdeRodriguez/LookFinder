package inditex.tech.lookfinder.Model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
}

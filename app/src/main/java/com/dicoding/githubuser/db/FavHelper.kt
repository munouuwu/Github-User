package com.dicoding.githubuser.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.dicoding.githubuser.db.DatabaseContract.FavColumns.Companion.TABLE_NAME

class FavHelper(context: Context) {
    private var dataBaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private var INSTANCE: FavHelper? = null

        fun getInstance(context: Context): FavHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavHelper(context)
            }
    }

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }

    fun close() {
        dataBaseHelper.close()

        if (database.isOpen)
            database.close()
    }

    fun queryAll(): Cursor = database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseContract.FavColumns.USERNAME} ASC"
    )

    fun queryById(uName: String): Cursor = database.query(
                DATABASE_TABLE,
                null,
                "${DatabaseContract.FavColumns.USERNAME} = ?",
                arrayOf(uName),
                null,
                null,
                null,
                null
    )

    fun insert(values: ContentValues?): Long = database.insert(DATABASE_TABLE, null, values)

    fun update(uName: String, values: ContentValues?): Int = database.update(DATABASE_TABLE, values, "${DatabaseContract.FavColumns.USERNAME} = ?", arrayOf(uName))

    fun deleteById(uName: String): Int = database.delete(DATABASE_TABLE, "${DatabaseContract.FavColumns.USERNAME} = '$uName'", null)
}
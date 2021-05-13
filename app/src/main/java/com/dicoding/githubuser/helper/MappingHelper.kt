package com.dicoding.mynotesapp.helper

import android.database.Cursor
import com.dicoding.githubuser.data.Fav
import com.dicoding.githubuser.db.DatabaseContract
import java.util.*

object MappingHelper {
    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Fav> {
        val favList = ArrayList<Fav>()
        notesCursor?.apply {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.NAME))
                val username = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.USERNAME))
                val location = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.LOCATION))
                val repository = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.REPOSITORY))
                val company = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.COMPANY))
                val followers = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.FOLLOWERS))
                val following = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.FOLLOWING))
                val avatar = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.AVATAR))
                val favorite = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.FAVORITE))
                favList.add(
                    Fav(
                            name,
                            username,
                            location,
                            repository,
                            company,
                            followers,
                            following,
                            avatar,
                            favorite
                    )
                )
            }
        }
        return favList
    }
}

package com.dicoding.consumerapp.db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {
    const val AUTHORITY = "com.dicoding.githubuser"
    const val SCHEME = "content"

    internal class FavColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite"
            const val NAME = "name"
            const val USERNAME = "username"
            const val LOCATION = "location"
            const val REPOSITORY = "repository"
            const val COMPANY = "company"
            const val FOLLOWERS = "followers"
            const val FOLLOWING = "following"
            const val AVATAR = "avatar"
            const val FAVORITE = "fav"

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                    .authority(AUTHORITY)
                    .appendPath(TABLE_NAME)
                    .build()
        }
    }
}

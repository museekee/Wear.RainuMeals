package kr.museekee.rainu.wear.rainumeals.presentation.libs

import android.content.Context
import android.content.SharedPreferences

class SchoolListManager {
    companion object {
        fun getFavorites(context: Context): List<String> {
            val sharedPreference = context.getSharedPreferences("favorite_schools",
                Context.MODE_PRIVATE
            )
            val lst = mutableListOf<String>()
            for (item in sharedPreference.all)
                if (item.value.toString().toBoolean())
                    lst.add(item.key)
            return lst
        }
        fun toggleFavorite(context: Context, schoolCode: String): Boolean {
            val sharedPreference = context.getSharedPreferences("favorite_schools",
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPreference.edit()

            if (existFavorite(context, schoolCode))
                editor.putBoolean(schoolCode, false)
            else
                editor.putBoolean(schoolCode, true)

            editor.apply()

            return !existFavorite(context, schoolCode)
        }
        fun existFavorite(context: Context, schoolCode: String): Boolean {
            val sharedPreference = context.getSharedPreferences("favorite_schools",
                Context.MODE_PRIVATE
            )
            return sharedPreference.getBoolean(schoolCode, false)
        }
    }
}
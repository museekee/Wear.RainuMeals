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


        fun addSchoolNameDB(context: Context, schoolCode: String, schoolName: String) {
            val sharedPreference = context.getSharedPreferences("school_names",
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPreference.edit()
            if (!sharedPreference.contains(schoolCode))
                editor.putString(schoolCode, schoolName)
            editor.apply()
        }
        fun getSchoolNameByCode(context: Context, schoolCode: String): String {
            val sharedPreference = context.getSharedPreferences("school_names",
                Context.MODE_PRIVATE
            )
            return sharedPreference.getString(schoolCode, "알 수 없음") ?: "알 수 없음"
        }


        fun setNowSchool(context: Context, schoolCode: String) {
            val sharedPreference = context.getSharedPreferences("now_school",
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPreference.edit()
            editor.putString("now_school", schoolCode)
            editor.apply()
        }
        fun getNowSchool(context: Context): String {
            val sharedPreference = context.getSharedPreferences("now_school",
                Context.MODE_PRIVATE
            )
            return sharedPreference.getString("now_school", "None") ?: "None"
        }
    }
}
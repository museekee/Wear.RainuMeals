package kr.museekee.rainu.wear.rainumeals.presentation.libs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MealDataManager {
    companion object {
        fun resetMeals(context: Context, schoolCode: Int, date: String) {
            val sharedPreference = context.getSharedPreferences("${schoolCode}_meals", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreference.edit()
            editor.putString(date, "")
            editor.apply()
        }
        fun storeMeals(context: Context, schoolCode: Int, date: String, data: List<TMeal>) {
            val sharedPreference = context.getSharedPreferences("${schoolCode}_meals", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreference.edit()
            editor.putString(date, Json.encodeToString(data))
            editor.apply()
        }
        fun getMeals(context: Context, schoolCode: Int, date: String): List<TMeal> {
            val sharedPreference = context.getSharedPreferences("${schoolCode}_meals", MODE_PRIVATE)
            return Json.decodeFromString(sharedPreference.getString(date, "[]") ?: "[]") ?: listOf()
        }
        fun existMeals(context: Context, schoolCode: Int, date: String): Boolean {
            val sharedPreference = context.getSharedPreferences("${schoolCode}_meals", MODE_PRIVATE)
            val sd = sharedPreference.getString(date, "").toString()
            return sd != ""
        }

        fun toggleFavorite(context: Context, cook: String): Boolean {
            val sharedPreference = context.getSharedPreferences("favorite_meals", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreference.edit()

            if (existFavorite(context, cook))
                editor.putBoolean(cook, false)
            else
                editor.putBoolean(cook, true)

            editor.apply()

            return !existFavorite(context, cook)
        }
        fun existFavorite(context: Context, cook: String): Boolean {
            val sharedPreference = context.getSharedPreferences("favorite_meals", MODE_PRIVATE)
            return sharedPreference.getBoolean(cook, false)
        }
    }
}
package kr.museekee.rainu.wear.rainumeals.presentation.libs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MealDataManager {
    companion object {
        fun storeMeals(context: Context, date: String, data: List<TMeal>) {
            val sharedPreference = context.getSharedPreferences("meals", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreference.edit()
            editor.putString(date, Json.encodeToString(data))
            editor.apply()
        }
        fun getMeals(context: Context, date: String): List<TMeal> {
            val sharedPreference = context.getSharedPreferences("meals", MODE_PRIVATE)
            return Json.decodeFromString(sharedPreference.getString(date, "[]") ?: "[]") ?: listOf()
        }
        fun existMeals(context: Context, date: String): Boolean {
            val sharedPreference = context.getSharedPreferences("meals", MODE_PRIVATE)
            val sd = sharedPreference.getString(date, "").toString()
            return sd != ""
        }
    }
}
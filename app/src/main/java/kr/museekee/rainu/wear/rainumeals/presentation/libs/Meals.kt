package kr.museekee.rainu.wear.rainumeals.presentation.libs

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.fastMap
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.observeOn
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.serialization.json.Json.Default.encodeToString as encodeToString1

class Meals {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "meal_${LocalDate.now().year}${LocalDate.now().monthValue}_prefs")

    companion object {
        private val allergies = listOf("난류", "우유", "메밀", "땅콩", "대두", "밀"," 고등어", "게", "새우", "돼지고기", "복숭아", "토마토", "아황산류", "호두", "닭고기", "소고기", "오징어", "조개류", "잣")

        fun allergyToKorean(al: Int): String {
            return allergies[al-1]
        }
    }

    suspend fun get(context: Context): Flow<String?> {
        val mdm = MealDataManager(context.dataStore)
        mdm.mealDataFlow.collectLatest {
            if (it == null)
                mdm.storeMeal(Json.encodeToString(
                    getNeisMeals(
                        key = "8461581b65424dca9fe5613afa5870b6",
                        schoolCode = 7631122
                    )
                ))
//            Log.d("aaa", Json.encodeToString(neis))
//            Log.d("aaa", Json.decodeFromString<List<TMeal>>(Json.encodeToString(neis)).toString())
        }
        return mdm.mealDataFlow
    }

    private suspend fun getNeisMeals(
        key: String,
        schoolCode: Int
    ): List<TMeal> {
        val params = mutableMapOf<String, String>()
        params += "key" to key
        params += "type" to "json"
        params += "ATPT_OFCDC_SC_CODE" to "J10"
        params += "SD_SCHUL_CODE" to schoolCode.toString()
        params += "MLSV_YMD" to "${LocalDate.now().year}${LocalDate.now().monthValue}"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://open.neis.go.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val apiService = retrofit.create(Request::class.java)

        val body = apiService.get("/hub/mealServiceDietInfo", params)

        if (JSONObject(body).optJSONArray("mealServiceDietInfo") == null) return listOf()
        val data = JSONObject(body).getJSONArray("mealServiceDietInfo").getJSONObject(1).optJSONArray("row")

//        val result = JSONArray()
        val result = mutableListOf<TMeal>()

        if (data != null) {
            for (idx in 0 until data.length()) {
                val nowData = data.getJSONObject(idx)
                val meals = nowData.getString("DDISH_NM").split("<br/>")
                val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                val allergies = meals.map { meal ->
                    ("\\((?:\\d+\\.?)+\\)".toRegex().find(meal)?.value
                        ?.replace("(", "")?.replace(")", "")
                        ?.split(".") ?: listOf()).fastMap {
                            it.toInt()
                    }
                }
                val mealInfo = TMeal(
                    cooks = meals.map { "\\((?:\\d+\\.?)+\\)".toRegex().replace(it, "") },
                    date = LocalDate.parse(nowData.getString("MLSV_YMD"), formatter),
                    kiloCalories = nowData.getString("CAL_INFO").substring(0, nowData.getString("CAL_INFO").length - 6).toDouble(),
                    allergies = allergies
                )
                result += mealInfo
//                val jso = JSONObject()
//                jso.put("cooks", meals.map { "\\((?:\\d+\\.?)+\\)".toRegex().replace(it, "") })
//                jso.put("date", LocalDate.parse(nowData.getString("MLSV_YMD"), formatter))
//                jso.put("kiloCalories", nowData.getString("CAL_INFO").substring(0, nowData.getString("CAL_INFO").length - 6).toDouble())
//                jso.put("allergies", allergies)
//                result.put(jso)
            }
        }

        return result
    }
}

interface Request {
    @GET
    suspend fun get(@Url url: String, @QueryMap queryMap: Map<String, String>): String
}
package kr.museekee.rainu.wear.rainumeals.presentation.libs

import android.content.Context
import androidx.compose.ui.util.fastMap
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Meals {
    companion object {
        private val allergies = listOf("난류", "우유", "메밀", "땅콩", "대두", "밀"," 고등어", "게", "새우", "돼지고기", "복숭아", "토마토", "아황산류", "호두", "닭고기", "소고기", "오징어", "조개류", "잣")

        fun allergyToKorean(al: Int): String {
            return allergies[al-1]
        }
    }

    fun get(context: Context, schoolCode: Int): List<TMeal> {
        val date = "${LocalDate.now().year}${LocalDate.now().monthValue.toString().padStart(2, '0')}"
        return MealDataManager.getMeals(context, schoolCode, date)
    }

    suspend fun getNeisMeals(
        key: String,
        schoolCode: Int
    ): List<TMeal> {
        val params = mutableMapOf<String, String>()
        params += "key" to key
        params += "type" to "json"
        params += "ATPT_OFCDC_SC_CODE" to "J10"
        params += "SD_SCHUL_CODE" to schoolCode.toString()
        params += "MLSV_YMD" to "${LocalDate.now().year}${LocalDate.now().monthValue.toString().padStart(2, '0')}"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://open.neis.go.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val apiService = retrofit.create(Request::class.java)

        val body = apiService.get("/hub/mealServiceDietInfo", params)

        if (JSONObject(body).optJSONArray("mealServiceDietInfo") == null) throw Exception("No Meal Data in school")
        val data = JSONObject(body).getJSONArray("mealServiceDietInfo").getJSONObject(1).optJSONArray("row")

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
            }
        }

        return result
    }
}

interface Request {
    @GET
    suspend fun get(@Url url: String, @QueryMap queryMap: Map<String, String>): String
}
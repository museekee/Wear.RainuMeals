package kr.museekee.rainu.wear.rainumeals.presentation.libs

import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class Schools {
    suspend fun getNeisSchools(
        key: String,
        schoolName: String
    ): List<TSchool> {
        val params = mutableMapOf<String, String>()
        params += "key" to key
        params += "type" to "json"
        params += "SCHUL_NM" to schoolName

        val retrofit = Retrofit.Builder()
            .baseUrl("https://open.neis.go.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val apiService = retrofit.create(Request::class.java)

        val body = apiService.get("/hub/schoolInfo", params)

        if (JSONObject(body).optJSONArray("schoolInfo") == null) return listOf()
        val data = JSONObject(body).getJSONArray("schoolInfo").getJSONObject(1).optJSONArray("row")

        val result = mutableListOf<TSchool>()

        if (data != null) {
            for (idx in 0..data.length()) {
                val nowData = data.getJSONObject(idx)
                result += TSchool(
                    name = nowData.getString("SCHUL_NM"),
                    address = nowData.getString("ORG_RDNMA"),
                    schoolCode = nowData.getInt("SD_SCHUL_CODE")
                )
            }
        }
        return result
    }
}
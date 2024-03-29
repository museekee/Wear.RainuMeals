package kr.museekee.rainu.wear.rainumeals.presentation.pages

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.museekee.rainu.wear.rainumeals.presentation.libs.MealDataManager
import kr.museekee.rainu.wear.rainumeals.presentation.libs.Meals
import kr.museekee.rainu.wear.rainumeals.presentation.libs.SchoolListManager
import java.time.LocalDate

@Composable
fun DownloadAlert(context: Context, date: String, schoolCode: Int, onSuccess: () -> Unit, onFail: () -> Unit) {
    val today = LocalDate.now()
    var isDownloading by remember { mutableStateOf(false) }
    var isFailed by remember { mutableStateOf(false) }
    var downloadLabel by remember { mutableStateOf("") }

    Alert(
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 24.dp, bottom = 52.dp),
        title = {
            Text(
                textAlign = TextAlign.Center,
                text = "${SchoolListManager.getSchoolNameByCode(context, schoolCode.toString())}\n${today.monthValue}월 급식 다운로드"
            )
        },
        message = {
            Text(if (downloadLabel == "") "나이스에서 ${today.monthValue}월의 급식을 받아옵니다." else downloadLabel)
        },
        backgroundColor = Color.Black,
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Chip(
                label = {Text(
                    if (isFailed)
                        "확인"
                    else if (!isDownloading)
                        "다운로드"
                    else
                        "다운로드 중"
                )},
                enabled = !isDownloading,
                onClick = {
                    if (isFailed) {
                        onFail()
                        return@Chip
                    }
                    val coroutineScope = CoroutineScope(Dispatchers.Main)
                    coroutineScope.launch {
                        try {
                            val data = Meals().getNeisMeals(
                                key = "8461581b65424dca9fe5613afa5870b6",
                                schoolCode = schoolCode
                            )
                            Log.d("mdata", data.toString())
                            isDownloading = true
                            MealDataManager.storeMeals(
                                context = context,
                                schoolCode = schoolCode,
                                date = date,
                                data = data
                            )
                            onSuccess()
                        }
                        catch (e: Exception) {
                            downloadLabel = "급식 데이터가 없습니다.\n즐겨찾기 해제합니다."
                            isFailed = true
                        }
                    }
                },
                colors = ChipDefaults.primaryChipColors(),
            )
        }
        item {
            Chip(
                label = { Text("나가기") },
                onClick = {
                    val activity = (context as? Activity)
                    activity?.finish()
                },
                colors = ChipDefaults.secondaryChipColors(),
            )
        }
    }
}
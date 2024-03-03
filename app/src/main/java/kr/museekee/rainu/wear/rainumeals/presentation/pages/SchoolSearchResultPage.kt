package kr.museekee.rainu.wear.rainumeals.presentation.pages

import android.content.Context
import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import kotlinx.coroutines.launch
import kr.museekee.rainu.wear.rainumeals.presentation.libs.SchoolListManager
import kr.museekee.rainu.wear.rainumeals.presentation.libs.Schools
import kr.museekee.rainu.wear.rainumeals.presentation.libs.TSchool

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun SchoolSearchResultPage(context: Context, key: String, schoolName: String) {
    var schools by remember { mutableStateOf<List<TSchool>>(listOf()) }

    LaunchedEffect(Unit) {
        schools = Schools().getNeisSchools(
            key = key,
            schoolName = schoolName
        )
    }

    val listState = rememberScalingLazyListState()
    val focusRequester = rememberActiveFocusRequester()
    val coroutineScope = rememberCoroutineScope()
    ScalingLazyColumn(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    listState.scrollBy(it.verticalScrollPixels)

                    listState.animateScrollBy(0f)
                }
                true
            }
            .focusRequester(focusRequester)
            .focusable(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(schools) {
            SchoolListManager.addSchoolNameDB(context, it.schoolCode.toString(), it.name)
            SchoolSearchResultItem(
                context = context,
                schoolName = it.name,
                schoolCode = it.schoolCode,
                address = it.address,
            )
        }
    }
}

@Composable
fun SchoolSearchResultItem(context: Context, schoolName: String, schoolCode: Int, address: String) {
    Button(
        modifier = Modifier
            .fillMaxSize(),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        onClick = {},
    ) {
        var isFavorite by remember {
            mutableStateOf(
                SchoolListManager.existFavorite(
                    context,
                    schoolCode.toString()
                )
            )
        }
        Column(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            isFavorite =
                                !SchoolListManager.toggleFavorite(
                                    context,
                                    schoolCode.toString()
                                )
                            if (SchoolListManager.getNowSchool(context) == "None") // 기본 학교가 없을 때
                                SchoolListManager.setNowSchool(context, schoolCode.toString()) // (처음으로 추가했을) 이걸 기본학교로 지정
                        },
                        onTap = {
                            Log.d("schoolCode", schoolCode.toString()) // 그냥 급식 한 번 보는거 (근데 다음에도 볼 수 있으니 저장은 함)
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = schoolName,
                textAlign = TextAlign.Center,
                color = if (isFavorite) Color(0xffffff00) else Color(0xffffffff)
            )
            Text(
                text = address,
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                color = Color(0xffbbbbbb)
            )
        }
    }
}
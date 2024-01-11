package kr.museekee.rainu.wear.rainumeals.presentation.pages

import android.content.Context
import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun SchoolListPage(context: Context) {
    val favoriteSchools = SchoolListManager.getFavorites(context)
    val listState = rememberScalingLazyListState()
    val focusRequester = rememberActiveFocusRequester()
    val coroutineScope = rememberCoroutineScope()

    var nowSchool by remember {
        mutableStateOf(
            SchoolListManager.getNowSchool(context)
        )
    }
        ScalingLazyColumn(
            modifier = Modifier
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
            item {
                Text(
                    text = "더블 클릭: 기본 학교"
                )
            }
            items(favoriteSchools) { schoolCode ->
                Button(
                    modifier = Modifier
                        .fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    onClick = {},
                ) {
                    Column(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        nowSchool = schoolCode
                                        SchoolListManager.setNowSchool(
                                            context,
                                            schoolCode
                                        )
                                    },
                                    onTap = {
                                        Log.d("schoolCode", schoolCode)
                                    }
                                )
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = SchoolListManager.getSchoolNameByCode(context, schoolCode),
                            textAlign = TextAlign.Center,
                            color = if (nowSchool == schoolCode) Color(0xffff3333) else Color(0xffffffff)
                        )
                    }
                }
            }
        }


}
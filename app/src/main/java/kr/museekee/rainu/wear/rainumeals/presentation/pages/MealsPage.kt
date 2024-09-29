package kr.museekee.rainu.wear.rainumeals.presentation.pages

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import kotlinx.coroutines.launch
import kr.museekee.rainu.wear.rainumeals.presentation.libs.MealDataManager
import kr.museekee.rainu.wear.rainumeals.presentation.libs.Meals
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalWearFoundationApi::class, ExperimentalFoundationApi::class)
@Composable
fun MealsPage(context: Context, schoolCode: Int, navController: NavController) {
    val meals = Meals().get(context, schoolCode)

    // region 첫 화면 날짜 관련
    val today = OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.of("+9")).toLocalDate()
    val aroundDate = meals.indexOfFirst { it.date.isAfter(today) || it.date.isEqual(today) }
    // endregion

    // region 페이지 관련
    val pagerState = rememberPagerState (
        pageCount = { meals.size + 1 }, // 메뉴 페이지 때문에 1 더하기
        initialPage = (if (aroundDate == -1) 0 else aroundDate) + 1 // 첫 페이지가 메뉴 페이지라서 1 더함
    )
    // endregion
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = rememberActiveFocusRequester()

    HorizontalPager(
        modifier = Modifier
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    if (it.verticalScrollPixels < 1) {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    } else
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
                true
            }
            .focusRequester(focusRequester)
            .focusable(),
        state = pagerState
    ) { page ->
        if (page == 0) {
            val btnCoroutineScope = rememberCoroutineScope()
            Button(
                modifier = Modifier
                    .fillMaxSize(),
                onClick = {
                    navController.navigate("menuPage")
                    btnCoroutineScope.launch {
                        pagerState.scrollToPage((if (aroundDate == -1) 0 else aroundDate) + 1)
                    }
                }
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    text = "메뉴"
                )
            }
        }
        else {
            val meal = meals[page-1]
            val date = meal.date
            val cooks = meal.cooks
            val allergies = meal.allergies

            Column(
                modifier = Modifier
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                /*
                Button(onClick = {
                    val sharedPreference = context.getSharedPreferences("${schoolCode}_meals", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreference.edit()
                    editor.clear().apply()
                }) {
                    Text("제거")
                }
                */ // 급식 정보 제거
                Text(
                    text = "${date.monthValue}월 ${date.dayOfMonth}일",
                    color = if (date.dayOfMonth == today.dayOfMonth) Color(0xFFFF6D60) else Color.White,
                    fontWeight = if (date.dayOfMonth == today.dayOfMonth) FontWeight.Bold else FontWeight.Normal
                )

                ScalingLazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(cooks.size) { cookIdx ->
                        var isFavorite by remember {
                            mutableStateOf(
                                MealDataManager.existFavorite(
                                    context,
                                    cooks[cookIdx]
                                )
                            )
                        }

                        Column(
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onDoubleTap = {
                                            isFavorite =
                                                !MealDataManager.toggleFavorite(
                                                    context,
                                                    cooks[cookIdx]
                                                )
                                        }
                                    )
                                }
                        ) {
                            Text(
                                text = cooks[cookIdx],
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = if (isFavorite) Color(0xFFFF6D60) else Color(
                                    0xffffffff
                                )
                            )
                            Text(
                                text = allergies[cookIdx].joinToString(", ") {
                                    Meals.allergyToKorean(it)
                                },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp,
                                color = Color(0xffbbbbbb)
                            )
                        }
                    }
                    item {
                        Text(
                            text = "${meal.kiloCalories} kcal",
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFFFF)
                        )
                    }
                }
            }
        }
    }
}
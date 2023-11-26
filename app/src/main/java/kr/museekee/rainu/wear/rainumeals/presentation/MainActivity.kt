/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package kr.museekee.rainu.wear.rainumeals.presentation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.PageIndicatorStyle
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.museekee.rainu.wear.rainumeals.presentation.libs.MealDataManager
import kr.museekee.rainu.wear.rainumeals.presentation.libs.Meals
import kr.museekee.rainu.wear.rainumeals.presentation.theme.RainuMealsTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun DownloadAlert(context: Context, date: String, onClick: () -> Unit) {
    val today = LocalDate.now()
    var isDownloading by remember { mutableStateOf(false) }

    Alert(
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 24.dp, bottom = 52.dp),
        title = {
            Text("${today.monthValue}월 급식 다운로드")
        },
        message = {
            Text("나이스에서 ${today.monthValue}월의 급식을 받아옵니다.")
        },
        backgroundColor = Color.Black,
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Chip(
                label = { Text(if (!isDownloading) "다운로드" else "다운로드 중") },
                enabled = !isDownloading,
                onClick = {
                    val coroutineScope = CoroutineScope(Dispatchers.Main)
                    coroutineScope.launch {
                        isDownloading = true
                        MealDataManager.storeMeals(
                            context = context,
                            date = date,
                            data = Meals().getNeisMeals(
                                key = "8461581b65424dca9fe5613afa5870b6",
                                schoolCode = 7631122
                            )
                        )
                        onClick()
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalWearFoundationApi::class)
@Composable
fun MealsMain(context: Context) {
    val meals = Meals().get(context)

    val today = OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.of("+9")).toLocalDate()

    var aroundDate by remember { mutableStateOf(0) }

    aroundDate = meals.indexOfFirst { it.date.isAfter(today) || it.date.isEqual(today) }

    val pagerState = rememberPagerState (
        pageCount = { meals.size },
        initialPage = if (aroundDate == -1) 0 else aroundDate
    )
    var finalValue by remember { mutableStateOf(0) }
    val animatedSelectedPage by animateFloatAsState(
        targetValue = pagerState.currentPage.toFloat(),
        label = "",
    ) {
        finalValue = it.toInt()
    }

    val pageIndicatorState: PageIndicatorState = remember {
        object : PageIndicatorState {
            override val pageOffset: Float
                get() = animatedSelectedPage - finalValue
            override val selectedPage: Int
                get() = finalValue
            override val pageCount: Int
                get() = pagerState.pageCount
        }
    }

    RainuMealsTheme {
        Scaffold(
            modifier = Modifier
                .padding(5.dp)
        ) {
            val coroutineScope = rememberCoroutineScope()
            val focusRequester = rememberActiveFocusRequester()


            HorizontalPager(
                modifier = Modifier
                    .onRotaryScrollEvent {
                        coroutineScope.launch {
                            if (it.verticalScrollPixels < 1)
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            else
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                        true
                    }
                    .focusRequester(focusRequester)
                    .focusable(),
                state = pagerState
            ) { page ->
                val meal = meals[page]
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
                        val sharedPreference = context.getSharedPreferences("meals", Context.MODE_PRIVATE)
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
                            var isFavorite by remember { mutableStateOf(MealDataManager.existFavorite(context, cooks[cookIdx])) }

                            Column(
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onDoubleTap = {
                                                isFavorite = !MealDataManager.toggleFavorite(context, cooks[cookIdx])
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
                                    color = if (isFavorite) Color(0xFFFF6D60) else Color(0xffffffff)
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
                    }
                }
            }
            HorizontalPageIndicator(
                pageIndicatorState = pageIndicatorState,
                indicatorStyle = PageIndicatorStyle.Curved
            )
        }
    }
}

@Composable
fun WearApp() {
    var isDownloaded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dateKey = "${LocalDate.now().year}${LocalDate.now().monthValue}"

    if (MealDataManager.existMeals(context, dateKey) || isDownloaded)
        MealsMain(context)
    else
        DownloadAlert(context, dateKey) {
            isDownloaded = true
        }
}

@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}
/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package kr.museekee.rainu.wear.rainumeals.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.PageIndicatorStyle
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.museekee.rainu.wear.rainumeals.R
import kr.museekee.rainu.wear.rainumeals.presentation.libs.Meals
import kr.museekee.rainu.wear.rainumeals.presentation.libs.TMeal
import kr.museekee.rainu.wear.rainumeals.presentation.theme.RainuMealsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalWearFoundationApi::class)
@Composable
fun WearApp() {
    val meals = remember {
        mutableStateListOf<TMeal>()
    }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        Meals().get(context).collectLatest {
            if (it != null) {
                meals += Json.decodeFromString<List<TMeal>>(it)
            }
        }
    }
    val pagerState = rememberPagerState { meals.size }
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
//                beyondBoundsPageCount = 35,
                state = pagerState
            ) { page ->
                val meal = meals[page]
                val date = meal.date
                val cooks = meal.cooks
                val allergies = meal.allergies
                Log.d("aaa", meal.cooks.joinToString(", "))

                Column(
                    modifier = Modifier
                        .padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${date.monthValue}월 ${date.dayOfMonth}일")

                    ScalingLazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(cooks.size) {
                            Column {
                                Text(
                                    text = cooks[it],
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = allergies[it].joinToString(", ") {
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
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}
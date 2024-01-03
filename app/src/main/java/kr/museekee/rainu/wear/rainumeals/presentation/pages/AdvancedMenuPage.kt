package kr.museekee.rainu.wear.rainumeals.presentation.pages

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Text

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdvancedMenuPage(context: Context) {
//    MealDataManager.resetMeals(context, 7631122, "202401")

    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 3 }
    )
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> Text(
                modifier = Modifier
                    .fillMaxSize(),
                text = "학교 리스트"
            )
            1 -> Text(
                modifier = Modifier
                    .fillMaxSize(),
                text = "급식 메뉴 검색"
            )
            2 -> SchoolSearchPage(
                context = context
            )
        }
    }
}
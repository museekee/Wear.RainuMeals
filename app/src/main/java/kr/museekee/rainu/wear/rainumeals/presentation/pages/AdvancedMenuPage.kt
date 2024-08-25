package kr.museekee.rainu.wear.rainumeals.presentation.pages

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.wear.compose.material.Text

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdvancedMenuPage(context: Context, navController: NavController) {
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 3 }
    )
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> SchoolListPage(
                context = context
            )
            1 -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center) {
                Text (
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "양 옆으로 슬라이드 해보세요"
//                            급식 메뉴 검색 (미완성)
                )
            }
            2 -> SchoolSearchPage(
                context = context,
                navController = navController
            )
        }
    }
}
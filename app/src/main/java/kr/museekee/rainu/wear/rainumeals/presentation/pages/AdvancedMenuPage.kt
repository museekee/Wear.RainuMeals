package kr.museekee.rainu.wear.rainumeals.presentation.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Text

@Composable
fun AdvancedMenuPage() {
    Text(
        text = "부가 메뉴 (급식 메뉴 검색, 학교 리스트, 학교 추가)",
        modifier = Modifier
            .fillMaxSize()
    )
}
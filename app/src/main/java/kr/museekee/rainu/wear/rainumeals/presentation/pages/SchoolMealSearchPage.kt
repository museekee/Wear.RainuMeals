package kr.museekee.rainu.wear.rainumeals.presentation.pages

import android.content.Context
import kr.museekee.rainu.wear.rainumeals.presentation.libs.Meals
import kr.museekee.rainu.wear.rainumeals.presentation.libs.SchoolListManager

fun SchoolMealSearchPage(context: Context) {
    val nowMeals = Meals().get(context, SchoolListManager.getNowSchool(context).toInt())
    // 검색창 만들어서 검색어 입력하면 저거 돌려서 include된거만 출력
}
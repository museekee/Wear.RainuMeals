package kr.museekee.rainu.wear.rainumeals.presentation.libs

import java.time.LocalDate

data class TMeal(
    val date: LocalDate,
    val cooks: List<String>,
    val kiloCalories: Double,
    val allergies: List<List<Int>>
)

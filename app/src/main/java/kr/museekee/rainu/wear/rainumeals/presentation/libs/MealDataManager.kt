package kr.museekee.rainu.wear.rainumeals.presentation.libs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.properties.ReadOnlyProperty

class MealDataManager(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val MEAL_DATA = stringPreferencesKey("MEAL_DATA")
    }

    suspend fun storeMeal(
        data: String
    ) {
        dataStore.edit {
            it[MEAL_DATA] = data
        }
    }

    val mealDataFlow: Flow<String?> = dataStore.data.map {
        it[MEAL_DATA]
    }
}
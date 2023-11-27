/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package kr.museekee.rainu.wear.rainumeals.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Scaffold
import kr.museekee.rainu.wear.rainumeals.presentation.libs.MealDataManager
import kr.museekee.rainu.wear.rainumeals.presentation.pages.DownloadAlert
import kr.museekee.rainu.wear.rainumeals.presentation.pages.MealsPage
import kr.museekee.rainu.wear.rainumeals.presentation.pages.AdvancedMenuPage
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp(7631122)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MealsMain(context: Context, schoolCode: Int) {
    Scaffold(
        modifier = Modifier
            .padding(5.dp)
    ) {
        VerticalPager(
            state = rememberPagerState(
                initialPage = 1,
                pageCount = { 2 }
            )
        ) { vPage ->
            if (vPage == 0)
                AdvancedMenuPage()
            else if (vPage == 1)
                MealsPage(context, schoolCode)
        }
    }
}

@Composable
fun WearApp(schoolCode: Int) {
    var isDownloaded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dateKey = "${LocalDate.now().year}${LocalDate.now().monthValue}"

    if (MealDataManager.existMeals(context, schoolCode, dateKey) || isDownloaded)
        MealsMain(context, schoolCode)
    else
        DownloadAlert(context, dateKey, schoolCode) {
            isDownloaded = true
        }
}
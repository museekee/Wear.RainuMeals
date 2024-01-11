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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.wear.compose.material.Scaffold
import kr.museekee.rainu.wear.rainumeals.presentation.libs.MealDataManager
import kr.museekee.rainu.wear.rainumeals.presentation.libs.SchoolListManager
import kr.museekee.rainu.wear.rainumeals.presentation.pages.AdvancedMenuPage
import kr.museekee.rainu.wear.rainumeals.presentation.pages.DownloadAlert
import kr.museekee.rainu.wear.rainumeals.presentation.pages.MealsPage
import kr.museekee.rainu.wear.rainumeals.presentation.pages.SchoolSearchPage
import kr.museekee.rainu.wear.rainumeals.presentation.pages.SchoolSearchResultPage
import java.time.LocalDate

// adb uninstall kr.museekee.rainu.wear.rainumeals
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyNavHost()
        }
    }
}

@Composable
fun MyNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "wearApp"
    ) {
        composable(
            "wearApp"
        ) {
            WearApp(navController)
        }
        composable("menuPage") {
            AdvancedMenuPage(LocalContext.current, navController)
        }
        composable(
            "schoolSearchResult/{schoolName}",
            arguments = listOf(
                navArgument("schoolName") { defaultValue = "" }
            )
        ) {
            SchoolSearchResultPage(
                context = LocalContext.current,
                key = "8461581b65424dca9fe5613afa5870b6",
                schoolName = it.arguments?.getString("schoolName") ?: ""
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MealsMain(context: Context, schoolCode: Int, navController: NavController) {
    Scaffold(
        modifier = Modifier
            .padding(5.dp)
    ) {
        MealsPage(context, schoolCode, navController)
    }
}

@Composable
fun WearApp(navController: NavController) {
    var isDownloaded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dateKey = "${LocalDate.now().year}${LocalDate.now().monthValue.toString().padStart(2, '0')}"

    val nowSchoolCode = SchoolListManager.getNowSchool(context)

    if (nowSchoolCode == "None") // 기본 학교 설정이 안 되어 있을 때
        SchoolSearchPage(context, navController) // 학교 찾아라

    else if (MealDataManager.existMeals(context, nowSchoolCode.toInt(), dateKey) || isDownloaded)
        MealsMain(context, nowSchoolCode.toInt(), navController)
    else
        DownloadAlert(context, dateKey, nowSchoolCode.toInt()) {
            isDownloaded = true
        }
}
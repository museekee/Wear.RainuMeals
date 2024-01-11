package kr.museekee.rainu.wear.rainumeals.presentation.pages

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
// import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.Text

@Composable
fun SchoolSearchPage(context: Context, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
            .padding(horizontal = 5.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "학교 검색",
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        val interactionSource = remember { MutableInteractionSource() }
        var value by rememberSaveable { mutableStateOf("") }
//        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = FocusRequester()
        val focusManager = LocalFocusManager.current
        BasicTextField(
            value = value,
            onValueChange = { value = it },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            cursorBrush = SolidColor(Color.White),
            interactionSource = interactionSource,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    navController.navigate("schoolSearchResult/$value")
                }
            ),
            decorationBox = { innerTextField ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xff333333), CircleShape)
                        .padding(16.dp)
                        .focusRequester(focusRequester)
                ) {
                    innerTextField()
                }
            }
        )
    }
}
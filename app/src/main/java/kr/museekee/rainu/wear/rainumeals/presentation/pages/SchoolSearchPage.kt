package kr.museekee.rainu.wear.rainumeals.presentation.pages

import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Text
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.WearableButtonsProvider

@Composable
fun SchoolSearchPage(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
            .padding(horizontal = 5.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "학교 검색",
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
//        TextField(
//            value = "",
//            modifier = Modifier
//                .background(
//                    color = Color(0x88000000)
//                )
//                .fillMaxWidth(),
//            onValueChange = {
//                Log.d("SEARCH", it)
//            },
//        )
    }
}

/*
@Composable
fun TextInput(
    placeholder: String,
    value: String?,
    onChange: (value: String) -> Unit,
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { data ->
                val results: Bundle = RemoteInput.getResultsFromIntent(data)
                val newValue: CharSequence? = results.getCharSequence(placeholder)
                onChange(newValue as String)
            }
        }
    Column() {
        Chip(
            label = { Text(if (value.isNullOrEmpty()) placeholder else value) },
            onClick = {
                val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent();
                val remoteInputs: List<RemoteInput> = listOf(
                    RemoteInput.Builder(placeholder)
                        .setLabel(placeholder)
                        // https://stackoverflow.com/questions/70099277/problem-with-basictextfield-in-jetpack-compose-on-wear-os
                        .wearableExtender {
                            setEmojisAllowed(false)
                            setInputActionType(EditorInfo.IME_ACTION_DONE)
                        }.build()
                )

                RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)

                launcher.launch(intent)
            }
        )
    }
}
*/
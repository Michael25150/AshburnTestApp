package com.example.ashburntestapp.presentation.views

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ashburntestapp.R
import com.example.ashburntestapp.presentation.ui.theme.AshburnTestAppTheme
import com.example.ashburntestapp.presentation.viewmodels.SendDataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagingScreen(
    manageDataViewModel: SendDataViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var inputTextState by remember { mutableStateOf("") }
    val dataState by manageDataViewModel.state

    LaunchedEffect(key1 = dataState.isError) {
        if (dataState.isError) {
            Toast.makeText(context, R.string.error_message, Toast.LENGTH_SHORT).show()
        }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = inputTextState,
                onValueChange = { inputTextState = it },
                placeholder = { Text(stringResource(id = R.string.text_field_placeholder)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = {
                    manageDataViewModel.saveData(inputTextState)
                }) {
                    Text(stringResource(id = R.string.save))
                }
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = Modifier.padding(end = 10.dp),
                        onClick = { manageDataViewModel.sendData(false) }
                    ) {
                        Text(stringResource(id = R.string.encrypt))
                    }
                    Button(onClick = { manageDataViewModel.sendData(true) }
                    ) {
                        Text(stringResource(id = R.string.decrypt))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = dataState.data,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AshburnTestAppTheme {
        DataManagingScreen()
    }
}
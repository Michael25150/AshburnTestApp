package com.example.ashburntestapp.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ashburntestapp.common.DATA_FILE_NAME
import com.example.ashburntestapp.common.FileManager
import com.example.ashburntestapp.core.utils.fromMarkedBytes
import com.example.ashburntestapp.core.utils.isDataEncrypted
import com.example.ashburntestapp.core.utils.toMarkedBytes
import com.example.ashburntestapp.domain.usecases.ProcessDataUseCase
import com.example.ashburntestapp.presentation.models.DataUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SendDataViewModel @Inject constructor(
    private val sendDataUseCase: ProcessDataUseCase,
    private val fileManager: FileManager,
) : ViewModel() {

    private val _state = mutableStateOf(DataUiState())
    val state: State<DataUiState> = _state

    init {
        readData()
    }

    fun saveData(data: String) {
        saveDataToFile(data.toMarkedBytes())
    }

    fun sendData(isDecryption: Boolean) {
        val currentData = readData()
        if (currentData.isEmpty() || (isDecryption != currentData.isDataEncrypted())) return

        _state.value = _state.value.copy(isLoading = true, isError = false)

        viewModelScope.launch(Dispatchers.IO) {
            sendDataUseCase.processData(currentData, isDecryption)
                .onSuccess { data ->
                    saveDataToFile(data)
                }.onFailure { error ->
                    Log.d("fsf", error.toString())
                    _state.value =
                        _state.value.copy(isLoading = false, isError = true)
                }
        }
    }

    private fun saveDataToFile(data: ByteArray) {

        fileManager.writeToFile(data, DATA_FILE_NAME)
        _state.value =
            _state.value.copy(
                isLoading = false,
                data = data.fromMarkedBytes().second.decodeToString()
            )
    }

    private fun readData(): ByteArray {
        _state.value = _state.value.copy(isLoading = true, isError = false)
        val data = fileManager.readFromFile(DATA_FILE_NAME)
        _state.value =
            _state.value.copy(
                isLoading = false,
                data = data.fromMarkedBytes().second.decodeToString()
            )
        return data
    }
}
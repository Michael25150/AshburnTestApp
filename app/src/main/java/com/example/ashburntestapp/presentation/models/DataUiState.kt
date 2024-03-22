package com.example.ashburntestapp.presentation.models

data class DataUiState (
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val data: String = "",
)

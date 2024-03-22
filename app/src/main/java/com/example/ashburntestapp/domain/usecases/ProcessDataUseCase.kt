package com.example.ashburntestapp.domain.usecases

import com.example.ashburntestapp.domain.repositories.DataManagingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProcessDataUseCase @Inject constructor(
    private val dataManagingRepository: DataManagingRepository
) {
    suspend fun processData(data: ByteArray, isDecryption: Boolean = false): Result<ByteArray> =
        dataManagingRepository.processData(data, isDecryption)
}
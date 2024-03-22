package com.example.ashburntestapp.domain.repositories

interface DataManagingRepository {

    suspend fun processData(markedData: ByteArray, isDecryption: Boolean): Result<ByteArray>
}
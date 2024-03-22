package com.example.ashburntestapp.data.repositories

import com.example.ashburntestapp.core.cryptography.AESCryptographer
import com.example.ashburntestapp.core.cryptography.AESCryptographer.Companion.TRANSFORMATION
import com.example.ashburntestapp.core.utils.EncodeData
import com.example.ashburntestapp.core.utils.fromMarkedBytes
import com.example.ashburntestapp.core.utils.getIv
import com.example.ashburntestapp.core.utils.pack
import com.example.ashburntestapp.core.utils.toMarkedBytes
import com.example.ashburntestapp.core.utils.unpack
import com.example.ashburntestapp.data.remote.TCPClient
import com.example.ashburntestapp.domain.repositories.DataManagingRepository
import javax.inject.Inject

class DataManagingRepositoryImpl @Inject constructor(
    private val tcpClient: TCPClient,
    private val aesCryptographer: AESCryptographer,
) : DataManagingRepository {

    override suspend fun processData(
        markedData: ByteArray,
        isDecryption: Boolean,
    ): Result<ByteArray> = kotlin.runCatching {
        val key = aesCryptographer.getKey()
        val data = markedData.fromMarkedBytes().second

        if (isDecryption) {
            val iv = data.getIv()
            val ivSize = data.first().toInt()
            val dataToDecrypt = data.copyOfRange(ivSize + 1, data.size)

            val request = EncodeData(
                dataToDecrypt.toMutableList(),
                key.toMutableList(),
                iv.toMutableList(),
                TRANSFORMATION,
                false
            )
            val response = tcpClient.sendMessage(pack(request).toByteArray())
            markData(unpack(response.toMutableList()).data.toByteArray(), true)
        } else {
            val iv = aesCryptographer.generateIv()

            val request = EncodeData(
                data.toMutableList(),
                key.toMutableList(),
                iv.toMutableList(),
                TRANSFORMATION,
                true
            )
            val response = tcpClient.sendMessage(pack(request).toByteArray())
            markData(unpack(response.toMutableList()).data.toByteArray(), false, iv)
        }
    }

    private fun markData(
        data: ByteArray,
        isDecryption: Boolean,
        iv: ByteArray = byteArrayOf(),
    ): ByteArray = if (isDecryption) {
        data.toMarkedBytes(false)
    } else {
        (byteArrayOf(iv.size.toByte()) + iv + data).toMarkedBytes(true)
    }
}
package com.example.ashburntestapp.data.remote

import com.example.ashburntestapp.core.utils.bytesToInt
import com.example.ashburntestapp.core.utils.intToBytes
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TCPClient @Inject constructor() {

    fun sendMessage(data: ByteArray): ByteArray {
        val socket = Socket(SERVER_ADDRESS, SERVER_PORT)
        val outputStream: OutputStream = socket.getOutputStream()
        val inputStream: InputStream = socket.getInputStream()

        val request = intToBytes(data.size).plus(data)
        outputStream.write(request)
        outputStream.flush()


        val sizeBuffer = ByteArray(4)
        inputStream.read(sizeBuffer)
        val messageSize = bytesToInt(sizeBuffer)

        val messageBuffer = ByteArray(messageSize)
        inputStream.read(messageBuffer)

        socket.close()
        return messageBuffer
    }

    private companion object {
        const val SERVER_ADDRESS = "13.50.100.228"
        const val SERVER_PORT = 9002

        //      Use to access your actual machine
//        const val SERVER_ADDRESS = "10.0.2.2"
//        const val SERVER_PORT = 9002
    }
}
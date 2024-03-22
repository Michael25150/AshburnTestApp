package com.example.ashburntestapp.common

import android.content.Context
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class FileManager @Inject constructor(private val context: Context) {

    fun writeToFile(data: ByteArray, fileName: String) {
        var fos: FileOutputStream? = null
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fos.write(data)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            closeStream(fos)
        }
    }

    fun readFromFile(fileName: String): ByteArray {
        var fin: FileInputStream? = null
        return try {
            fin = context.openFileInput(fileName)
            val bytes = ByteArray(fin.available())
            fin.read(bytes)
            bytes
        } catch (e: IOException) {
            e.printStackTrace()
            byteArrayOf()
        } finally {
            closeStream(fin)
        }
    }

    fun isFileExists(fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return file.exists()
    }

    private fun closeStream(stream: Closeable?) {
        try {
            stream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
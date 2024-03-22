package com.example.ashburntestapp.core.utils

fun ByteArray.toMarkedBytes(isEncryption: Boolean = false): ByteArray =
    byteArrayOf(isEncryption.toByte()) + this

fun ByteArray.fromMarkedBytes(): Pair<Boolean, ByteArray> =
    if (this.isEmpty()) Pair(false, byteArrayOf())
    else Pair(this.first().toBoolean(), this.copyOfRange(1, this.size))

fun ByteArray.getIv(): ByteArray = this.copyOfRange(1, this.first().toInt() + 1)

fun ByteArray.isDataEncrypted(): Boolean =
    if (this.isNotEmpty()) this.fromMarkedBytes().first else false

fun String.toMarkedBytes(isEncryption: Boolean = false): ByteArray =
    if (this.isEmpty()) byteArrayOf()
    else byteArrayOf(isEncryption.toByte()) + this.toByteArray()

fun Boolean.toByte(): Byte = if (this) 1 else 0

fun Byte.toBoolean(): Boolean = this.toInt() != 0

package com.example.ashburntestapp.core.cryptography

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.example.ashburntestapp.common.FileManager
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AESCryptographer @Inject constructor(private val fileManager: FileManager) {

    fun getKey(): ByteArray {
        return if (fileManager.isFileExists(SECRET_KEY_FILE_NAME)) {
            val keyData = fileManager.readFromFile(SECRET_KEY_FILE_NAME)
            val ivSize = keyData.first().toInt()
            val iv = keyData.copyOfRange(1, ivSize + 1)
            val encryptedBytes = keyData.copyOfRange(ivSize + 1, keyData.size)
            return getDecryptCipherForIv(iv).doFinal(encryptedBytes)
        } else {
            generateKey().encoded
        }
    }

    fun generateIv(): ByteArray {
        val iv = ByteArray(16)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(iv)
        return iv
    }

    private val keyStore = KeyStore.getInstance(KEY_STORE_NAME).apply {
        load(null)
    }

    private lateinit var encryptCipher: Cipher

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(PARENT_TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getParentKey(), IvParameterSpec(iv))
        }
    }

    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        keyGenerator.init(SECRET_KEY_SIZE)
        val key = keyGenerator.generateKey()
        saveKey(key.encoded)
        return key
    }

    private fun saveKey(bytes: ByteArray) {
        encryptCipher = Cipher.getInstance(PARENT_TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getParentKey())
        }
        val dataToSave =
            byteArrayOf(encryptCipher.iv.size.toByte())
                .plus(encryptCipher.iv)
                .plus(encryptCipher.doFinal(bytes))
        fileManager.writeToFile(dataToSave, SECRET_KEY_FILE_NAME)
    }

    private fun getParentKey(): SecretKey {
        val parentSecretKey =
            keyStore.getEntry(PARENT_SECRET_KEY_NAME, null) as? KeyStore.SecretKeyEntry
        return parentSecretKey?.secretKey ?: generateParentKey()
    }

    private fun generateParentKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    PARENT_SECRET_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    companion object {
        private const val KEY_STORE_NAME = "AndroidKeyStore"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val PARENT_TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/PKCS5Padding"
        private const val PARENT_SECRET_KEY_NAME = "parentSecretKey"
        private const val SECRET_KEY_SIZE = 256
        private const val SECRET_KEY_FILE_NAME = "secretKey.txt"
    }
}


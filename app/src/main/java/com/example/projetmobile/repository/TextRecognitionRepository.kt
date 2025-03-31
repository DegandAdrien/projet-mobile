package com.example.projetmobile.repository

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface TextRecognitionRepository {
    suspend fun recognizeText(bitmap: Bitmap): TextRecognitionResult
    fun isValidLicensePlate(text: String): Boolean
}

data class TextRecognitionResult(
    val text: String,
    val isLicensePlate: Boolean
)

class TextRecognitionRepositoryImpl : TextRecognitionRepository {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // Regex pour les plaques françaises (format AA-123-AA)
    private val frenchLicensePlatePattern = "[A-Z]{2}-\\d{3}-[A-Z]{2}".toRegex()

    override suspend fun recognizeText(bitmap: Bitmap): TextRecognitionResult = suspendCancellableCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val detectedText = visionText.text
                val licensePlate = extractLicensePlate(detectedText)

                if (licensePlate != null) {
                    continuation.resume(TextRecognitionResult(licensePlate, true))
                } else {
                    continuation.resume(TextRecognitionResult(detectedText, false))
                }
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }

    override fun isValidLicensePlate(text: String): Boolean {
        return frenchLicensePlatePattern.matches(text)
    }

    private fun extractLicensePlate(text: String): String? {
        val matches = frenchLicensePlatePattern.findAll(text)
        return matches.firstOrNull()?.value
    }
}


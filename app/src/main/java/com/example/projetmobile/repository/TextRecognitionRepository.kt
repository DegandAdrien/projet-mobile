package com.example.projetmobile.repository

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface TextRecognitionRepository {
    suspend fun recognizeText(bitmap: Bitmap): String
}
class TextRecognitionRepositoryImpl : TextRecognitionRepository {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun recognizeText(bitmap: Bitmap): String = suspendCancellableCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val detectedText = visionText.text
                val licensePlate = extractLicensePlate(detectedText)
                continuation.resume(licensePlate ?: detectedText)
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }

    private fun extractLicensePlate(text: String): String? {
        // Adaptation aux plaques francaises: AA-123-AA
        val frenchPattern = "[A-Z]{2}-\\d{3}-[A-Z]{2}".toRegex()

        val matches = frenchPattern.findAll(text)

        return matches.firstOrNull()?.value
    }
}
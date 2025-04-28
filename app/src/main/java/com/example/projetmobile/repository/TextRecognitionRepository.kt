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
    fun formatMotoPlate(text: String): String
}

data class TextRecognitionResult(
    val text: String,
    val isLicensePlate: Boolean,
    val isMotoPlate: Boolean = false
)

class TextRecognitionRepositoryImpl : TextRecognitionRepository {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // Regex pour les différents formats de plaques françaises
    private val licensePlatePatterns = listOf(
        "[A-Z]{2}-\\d{3}-[A-Z]{2}".toRegex(),                // Format standard: AA-123-AA
        "\\d{3}\\s*[A-Z]{3}\\s*\\d{2}".toRegex(),            // Format: 777 AAA 77
        "\\d{1}\\s*[A-Z]{3}\\s*\\d{2}".toRegex(),            // Format: 7 AAA 77
        "\\d{4}\\s*[A-Z]{2}\\s*\\d{2}".toRegex(),            // Format: 7777 AA 77
        "[A-Z]{2}-".toRegex()                                // Format moto ligne 1: AA-
    )

    override suspend fun recognizeText(bitmap: Bitmap): TextRecognitionResult = suspendCancellableCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val detectedText = visionText.text
                val motoPlate = extractMotoPlate(detectedText)

                if (motoPlate != null) {
                    val formattedPlate = formatMotoPlate(motoPlate)
                    continuation.resume(TextRecognitionResult(formattedPlate, true, true))
                } else {
                    for (pattern in licensePlatePatterns) {
                        val match = pattern.find(detectedText)
                        if (match != null) {
                            continuation.resume(TextRecognitionResult(match.value, true, false))
                            return@addOnSuccessListener
                        }
                    }

                    continuation.resume(TextRecognitionResult(detectedText, false, false))
                }
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }

    override fun isValidLicensePlate(text: String): Boolean {

        if (licensePlatePatterns.any { it.matches(text) }) {
            return true
        }

        return isMotoPlate(text)
    }

    override fun formatMotoPlate(text: String): String {
        if (!text.contains("\n")) {
            return text
        }

        val lines = text.split("\n")
        if (lines.size != 2) return text

        val line1 = lines[0].trim()
        val line2 = lines[1].trim()

        return "${line1}${line2}"
    }

    private fun extractMotoPlate(text: String): String? {
        val lines = text.split("\n")
        if (lines.size < 2) return null

        for (i in 0 until lines.size - 1) {
            val line1 = lines[i].trim()
            val line2 = lines[i + 1].trim()

            val line1Match = "[A-Z]{2}-".toRegex().matchEntire(line1)
            val line2Match = "\\d{3}-[A-Z]{2}".toRegex().matchEntire(line2)

            if (line1Match != null && line2Match != null) {
                return "${line1Match.value}\n${line2Match.value}"
            }
        }

        return null
    }

    private fun isMotoPlate(text: String): Boolean {
        if (text.matches("[A-Z]{2}-\\d{3}-[A-Z]{2}".toRegex())) {
            return true
        }

        val lines = text.split("\n")
        if (lines.size != 2) return false

        val line1 = lines[0].trim()
        val line2 = lines[1].trim()

        val line1Match = "[A-Z]{2}-".toRegex().matches(line1)
        val line2Match = "\\d{3}-[A-Z]{2}".toRegex().matches(line2)

        return line1Match && line2Match
    }
}

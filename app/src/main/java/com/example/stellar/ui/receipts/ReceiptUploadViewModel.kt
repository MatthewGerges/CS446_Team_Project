package com.example.stellar.ui.receipts

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stellar.data.model.Receipt
import com.example.stellar.data.model.ReceiptStatus
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ReceiptUiState(
    val receipts: List<Receipt> = listOf(
        Receipt("1", "Walmart", 67.43, "Feb 14", ReceiptStatus.CONFIRMED),
        Receipt("2", "Costco", 124.99, "Feb 12", ReceiptStatus.PENDING),
        Receipt("3", "Target", 34.21, "Feb 10", ReceiptStatus.CONFIRMED),
        Receipt("4", "Whole Foods", 89.50, "Feb 8", ReceiptStatus.PENDING)
    ),
    val isUploading: Boolean = false,
    val isProcessingOCR: Boolean = false
)

class ReceiptUploadViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReceiptUiState())
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun processReceiptImage(uri: Uri, context: Context) {
        _uiState.update { it.copy(isProcessingOCR = true) }
        
        viewModelScope.launch {
            try {
                val image = InputImage.fromFilePath(context, uri)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val receipt = parseReceipt(visionText.text)
                        _uiState.update { 
                            it.copy(
                                receipts = listOf(receipt) + it.receipts,
                                isProcessingOCR = false
                            )
                        }
                    }
                    .addOnFailureListener {
                        _uiState.update { it.copy(isProcessingOCR = false) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessingOCR = false) }
            }
        }
    }

    private fun parseReceipt(text: String): Receipt {
        val lines = text.split("\n").filter { it.isNotBlank() }
        
        val rawVendor = lines.firstOrNull()?.trim() ?: "Unknown Vendor"
        val vendorName = rawVendor.replace("[^a-zA-Z\\s]".toRegex(), "").trim()
        
        val priceRegex = """(\d+\.\d{2})""".toRegex()
        val prices = priceRegex.findAll(text)
            .map { it.value.toDoubleOrNull() ?: 0.0 }
            .toList()
        
        val totalAmount = prices.maxOrNull() ?: 0.0
        
        val currentDate = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date())
        
        return Receipt(
            id = UUID.randomUUID().toString(),
            storeName = vendorName,
            amount = totalAmount,
            date = currentDate,
            status = ReceiptStatus.PENDING
        )
    }
}

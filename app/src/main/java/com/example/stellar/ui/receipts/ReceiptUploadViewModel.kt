package com.example.stellar.ui.receipts

import androidx.lifecycle.ViewModel
import com.example.stellar.data.model.Receipt
import com.example.stellar.data.model.ReceiptStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ReceiptUiState(
    val receipts: List<Receipt> = listOf(
        Receipt("1", "Walmart", 67.43, "Feb 14", ReceiptStatus.CONFIRMED),
        Receipt("2", "Costco", 124.99, "Feb 12", ReceiptStatus.PENDING),
        Receipt("3", "Target", 34.21, "Feb 10", ReceiptStatus.CONFIRMED),
        Receipt("4", "Whole Foods", 89.50, "Feb 8", ReceiptStatus.PENDING)
    ),
    val isUploading: Boolean = false
)

class ReceiptUploadViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReceiptUiState())
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()
}

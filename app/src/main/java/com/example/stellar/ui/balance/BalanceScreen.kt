package com.example.stellar.ui.balance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stellar.data.model.BalanceItem
import kotlin.math.abs
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    onAddExpenseClick: () -> Unit = {},
    viewModel: BalanceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val (settleTarget, setSettleTarget) = remember { mutableStateOf<BalanceItem?>(null) }
    val (selectedMethod, setSelectedMethod) = remember { mutableStateOf("Cash") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Balances") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpenseClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add Expense")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                NetBalanceHeader(netBalance = uiState.netBalance)
            }

            if (uiState.youOwe.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("You Owe", style = MaterialTheme.typography.titleMedium)
                }
                items(uiState.youOwe) { item ->
                    BalanceRow(
                        item = item,
                        onSettleUpClick = { setSettleTarget(it) }
                    )
                }
            }

            if (uiState.owedToYou.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Owed To You", style = MaterialTheme.typography.titleMedium)
                }
                items(uiState.owedToYou) { item ->
                    BalanceRow(
                        item = item,
                        onSettleUpClick = { setSettleTarget(it) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        settleTarget?.let { target ->
            LaunchedEffect(target.id) {
                setSelectedMethod("Cash")
            }

            AlertDialog(
                onDismissRequest = { setSettleTarget(null) },
                title = { Text("Settle up with ${target.personName}?") },
                text = {
                    Column {
                        Text(
                            text = "You're settling $${String.format("%.2f", abs(target.amount))} with ${target.personName}. How did you pay?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        val methods = listOf("Cash", "E-Transfer", "Other")
                        methods.forEach { method ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedMethod == method,
                                    onClick = { setSelectedMethod(method) }
                                )
                                Spacer(modifier = Modifier.padding(start = 8.dp))
                                Text(method)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            BalanceRepository.settleUp(
                                personId = target.id,
                                method = selectedMethod
                            )
                            setSettleTarget(null)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Marked as settled with ${target.personName}"
                                )
                            }
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { setSettleTarget(null) }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun NetBalanceHeader(netBalance: Double) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Net Balance", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (netBalance >= 0) "+$${String.format("%.2f", netBalance)}"
                else "-$${String.format("%.2f", abs(netBalance))}",
                style = MaterialTheme.typography.headlineMedium,
                color = if (netBalance >= 0) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun BalanceRow(
    item: BalanceItem,
    onSettleUpClick: (BalanceItem) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.padding(start = 12.dp))
                Column {
                    Text(item.personName, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "$${String.format("%.2f", abs(item.amount))}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (item.amount > 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }
            }
            OutlinedButton(onClick = { onSettleUpClick(item) }) {
                Text("Settle Up")
            }
        }
    }
}

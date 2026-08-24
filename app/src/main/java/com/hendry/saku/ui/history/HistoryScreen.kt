package com.hendry.saku.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.hendry.saku.data.model.Transaction
import com.hendry.saku.navigation.Screen
import com.hendry.saku.utils.format.toReadableDate
import com.hendry.saku.utils.format.toRupiah

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val activeFilter by viewModel.activeFilter.collectAsState()
    val pagingItems: LazyPagingItems<Transaction> =
        viewModel.transactionsPagingFlow.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 24.dp, end = 24.dp)
    ) {

        Text(
            text = "Riwayat Transaksi",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Semua aktivitas transaksi akun kamu",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TransactionFilterRow(
            activeFilter = activeFilter,
            onFilterSelected = { filter ->
                viewModel.setFilter(filter)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            pagingItems.loadState.refresh is LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            pagingItems.loadState.refresh is LoadState.Error -> {
                val error = pagingItems.loadState.refresh as LoadState.Error
                Text(
                    text = error.error.message ?: "Gagal memuat transaksi",
                    color = MaterialTheme.colorScheme.error
                )
            }

            pagingItems.itemCount == 0 && pagingItems.loadState.refresh is LoadState.NotLoading -> {
                EmptyHistoryCard(activeFilter = activeFilter)
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        count = pagingItems.itemCount,
                        key = { index ->
                            pagingItems.peek(index)?.id ?: index
                        }
                    ) { index ->
                        val transaction = pagingItems[index]
                        if (transaction != null) {
                            TransactionHistoryItem(
                                transaction = transaction,
                                onClick = {
                                    navController.navigate(
                                        Screen.TransactionDetail.createRoute(
                                            transaction.id
                                        )
                                    )
                                }
                            )
                        }
                    }

                    when (val appendState = pagingItems.loadState.append) {
                        is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                                }
                            }
                        }
                        is LoadState.Error -> {
                            item {
                                Text(
                                    text = appendState.error.message ?: "Gagal memuat lebih banyak",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionFilterRow(
    activeFilter: TransactionFilter,
    onFilterSelected: (TransactionFilter) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        items(TransactionFilter.entries.size) { index ->
            val filter = TransactionFilter.entries[index]
            val isSelected = filter == activeFilter
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(50)
            )
        }
    }
}

@Composable
private fun TransactionHistoryItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val isIncome = transaction.isIncomeTransaction()

    val amountColor = if (isIncome) {
        Color(0xFF16A34A)
    } else {
        Color(0xFFDC2626)
    }

    val amountPrefix = if (isIncome) {
        "+ "
    } else {
        "- "
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = transaction.getDisplayTitle(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = transaction.getDisplayDescription(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }

                Text(
                    text = amountPrefix + transaction.amount.toRupiah(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = transaction.createdAt.toReadableDate(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun EmptyHistoryCard(activeFilter: TransactionFilter) {
    val message = if (activeFilter == TransactionFilter.ALL) {
        "Riwayat transaksi kamu akan muncul di sini setelah melakukan aktivitas."
    } else {
        "Tidak ada transaksi untuk kategori \"${activeFilter.label}\"."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Belum ada transaksi",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun Transaction.isIncomeTransaction(): Boolean {
    return type == "TRANSFER_IN" || type == "TOP_UP"
}

private fun Transaction.getDisplayTitle(): String {
    return when (type) {
        "TOP_UP"       -> "Top Up"
        "TRANSFER_IN"  -> "Transfer Masuk"
        "TRANSFER_OUT" -> "Transfer Keluar"
        else           -> title
    }
}

private fun Transaction.getDisplayDescription(): String {
    return when (type) {
        "TOP_UP" -> "Isi saldo Saku"
        else     -> description
    }
}
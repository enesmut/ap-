package com.seninadiniz.apiogren.ui.theme

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seninadiniz.apiogren.data.NewsItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(vm: NewsViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val ctx = LocalContext.current

    var query by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Google Haberler (RSS)") }) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {

            // Arama kutusu
            Row(Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Ara (örn: android)") }
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    if (query.isBlank()) vm.fetchTop() else vm.search(query)
                }) { Text("Getir") }
            }

            when (state) {
                is UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = "Hata: ${(state as UiState.Error).message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is UiState.Success -> {
                    val news = (state as UiState.Success).news
                    NewsList(news) { url ->
                        // Tıklandığında tarayıcıda aç
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        ctx.startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsList(items: List<NewsItem>, onClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { n ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
                    .clickable { onClick(n.link) }
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(n.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(n.source ?: "Kaynak bilinmiyor", style = MaterialTheme.typography.labelMedium)
                    n.pubDate?.let {
                        Spacer(Modifier.height(2.dp))
                        Text(it, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
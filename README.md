#  Apiogren

Google Haberler RSS ve JSONPlaceholder API'den veri çeken, **Jetpack Compose** ile yazılmış örnek Android uygulaması.  
Proje; **Retrofit**, **OkHttp**, **Coroutines**, **Flow**, **ViewModel** ve **XmlPullParser** kullanımını gösterir.

---

##  Özellikler
- Google News RSS üzerinden **manşetler** ve **arama**
- JSONPlaceholder API’den **post listesi** ve **tekil post**
- **Jetpack Compose + Material 3** UI
- **Repository Pattern** ve **StateFlow** ile reaktif durum yönetimi
- RSS **XML parsing** (`XmlPullParser`)

---

##  Bağımlılıklar (Gradle)

```gradle
// Kotlin
implementation "org.jetbrains.kotlin:kotlin-stdlib:1.9.0"

// AndroidX & Lifecycle
implementation "androidx.core:core-ktx:1.10.1"
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1"
implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.6.1"

// Jetpack Compose
implementation "androidx.activity:activity-compose:1.7.2"
implementation platform("androidx.compose:compose-bom:2023.06.01")
implementation "androidx.compose.ui:ui"
implementation "androidx.compose.ui:ui-tooling-preview"
implementation "androidx.compose.material3:material3"
debugImplementation "androidx.compose.ui:ui-tooling"
debugImplementation "androidx.compose.ui:ui-test-manifest"

// Retrofit & OkHttp
implementation "com.squareup.retrofit2:retrofit:2.9.0"
implementation "com.squareup.okhttp3:okhttp:4.11.0"
implementation "com.squareup.okhttp3:logging-interceptor:4.11.0"

// Coroutines
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
```
API’ler
Google News RSS – https://news.google.com/rss

JSONPlaceholder – https://jsonplaceholder.typicode.com
```
* Proje Yapısı
com.seninadiniz.apiogren
│
├── data/
│   ├── NewsItem.kt
│   ├── NewsRepository.kt
│   ├── post.kt
│   ├── PostsRepository.kt
│
├── network/
│   ├── GoogleNewsApi.kt
│   ├── JsonPlaceholderApi.kt
│   ├── NetworkModule.kt
│
├── ui/theme/
│   ├── ApiogrenTheme.kt
│   ├── NewsScreen.kt
│   ├── NewsViewModel.kt
│   ├── UiState.kt
│
├── util/
│   ├── RssParser.kt
│
└── MainActivity.kt
```
### Örnek Kodlar


# Retrofit – Google News RSS
```
interface GoogleNewsApi {
    @GET("rss")
    suspend fun topHeadlines(
        @Query("hl") hl: String = "tr",
        @Query("gl") gl: String = "TR",
        @Query("ceid") ceid: String = "TR:tr"
    ): ResponseBody

    @GET("rss/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("hl") hl: String = "tr",
        @Query("gl") gl: String = "TR",
        @Query("ceid") ceid: String = "TR:tr"
    ): ResponseBody
}
```
# RSS Parser – XML → Model
```
object RssParser {
    fun parseItems(xml: String): List<NewsItem> {
        val items = mutableListOf<NewsItem>()
        val factory = XmlPullParserFactory.newInstance().apply { isNamespaceAware = false }
        val parser = factory.newPullParser().apply { setInput(StringReader(xml)) }
        var event = parser.eventType
        var inside = false
        var title: String? = null; var link: String? = null; var source: String? = null; var pubDate: String? = null

        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> when (parser.name.lowercase()) {
                    "item" -> { inside = true; title = null; link = null; source = null; pubDate = null }
                    "title" -> if (inside) title = parser.nextText().trim()
                    "link" -> if (inside) link = parser.nextText().trim()
                    "source" -> if (inside) source = parser.nextText().trim()
                    "pubdate" -> if (inside) pubDate = parser.nextText().trim()
                }
                XmlPullParser.END_TAG -> if (inside && parser.name.equals("item", true)) {
                    items += NewsItem(title.orEmpty(), unwrap(link.orEmpty()), source, pubDate)
                    inside = false
                }
            }
            event = parser.next()
        }
        return items
    }

    private fun unwrap(link: String): String =
        runCatching { Uri.parse(link).getQueryParameter("url") ?: link }.getOrDefault(link)
}
```
# Repository – Ağ çağrıları ve parse
```
class NewsRepository(private val api: GoogleNewsApi) {
    suspend fun getTopHeadlines(): Result<List<NewsItem>> = try {
        val xml = api.topHeadlines().string()
        Result.success(RssParser.parseItems(xml))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun search(q: String): Result<List<NewsItem>> = try {
        val xml = api.search(q).string()
        Result.success(RssParser.parseItems(xml))
    } catch (e: Exception) { Result.failure(e) }
}
```
# ViewModel – UI State akışı
```
class NewsViewModel : ViewModel() {
    private val repo = NewsRepository(NetworkModule.newsApi)
    private val _ui = MutableStateFlow<UiState>(UiState.Loading)
    val ui: StateFlow<UiState> = _ui

    init { fetchTop() }

    fun fetchTop() {
        _ui.value = UiState.Loading
        viewModelScope.launch {
            _ui.value = repo.getTopHeadlines().fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Bilinmeyen hata") }
            )
        }
    }

    fun search(query: String) {
        _ui.value = UiState.Loading
        viewModelScope.launch {
            _ui.value = repo.search(query).fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Bilinmeyen hata") }
            )
        }
    }
}
```
# Compose – Haber Listesi
```
@Composable
fun NewsList(items: List<NewsItem>, onClick: (String) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(items, key = { it.link }) { n ->
            ElevatedCard(Modifier.fillMaxWidth().clickable { onClick(n.link) }) {
                Column(Modifier.padding(12.dp)) {
                    Text(n.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(n.source ?: "Kaynak bilinmiyor", style = MaterialTheme.typography.labelMedium)
                    n.pubDate?.let { Text(it, style = MaterialTheme.typography.labelSmall) }
                }
            }
        }
    }
}
```

package com.seninadiniz.apiogren.util
import android.net.Uri
import com.seninadiniz.apiogren.data.NewsItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

object RssParser {fun parseItems(xml: String): List<NewsItem> {
    val items = mutableListOf<NewsItem>()
    val factory = XmlPullParserFactory.newInstance()
    factory.isNamespaceAware = false
    val parser = factory.newPullParser()

    parser.setInput(StringReader(xml))

    var event = parser.eventType
    var insideItem = false

    var title: String? = null
    var link: String? = null
    var source: String? = null
    var pubDate: String? = null

    while (event != XmlPullParser.END_DOCUMENT) {
        when (event) {
            XmlPullParser.START_TAG -> {
                when (parser.name.lowercase()) {
                    "item" -> {
                        insideItem = true
                        title = null; link = null; source = null; pubDate = null
                    }
                    "title" -> if (insideItem) title = parser.nextText().trim()
                    "link" -> if (insideItem) link = parser.nextText().trim()
                    "pubdate" -> if (insideItem) pubDate = parser.nextText().trim()
                    "source" -> if (insideItem) source = parser.nextText().trim()
                }
            }
            XmlPullParser.END_TAG -> {
                if (insideItem && parser.name.equals("item", ignoreCase = true)) {
                    val safeLink = unwrapGoogleNewsLink(link.orEmpty())
                    items += NewsItem(
                        title = title.orEmpty(),
                        link = safeLink,
                        source = source,
                        pubDate = pubDate
                    )
                    insideItem = false
                }
            }
        }
        event = parser.next()
    }
    return items
}

    // Google News RSS <link> genelde "news.google.com/rss/articles/..." olur.
    // İçinde çoğunlukla gerçek kaynağı "url" query paramı olarak taşır.
    private fun unwrapGoogleNewsLink(link: String): String {
        return try {
            val uri = Uri.parse(link)
            uri.getQueryParameter("url") ?: link
        } catch (_: Exception) {
            link
        }
    }
}
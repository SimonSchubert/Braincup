package com.inspiredandroid.braincup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.sponsors_become
import braincup.composeapp.generated.resources.sponsors_subtitle
import braincup.composeapp.generated.resources.sponsors_title
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.hoverHand
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource

data class Sponsor(val username: String, val avatar: String)

data class Sponsors(
    val current: List<Sponsor> = emptyList(),
    val past: List<Sponsor> = emptyList(),
)

@Serializable
private data class SponsorsResponse(val sponsors: SponsorList)

@Serializable
private data class SponsorList(
    val current: List<SponsorDto> = emptyList(),
    val past: List<SponsorDto> = emptyList(),
)

@Serializable
private data class SponsorDto(val username: String, val avatar: String)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SponsorsSection() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }

    var sponsors by remember { mutableStateOf(Sponsors()) }

    LaunchedEffect(Unit) {
        sponsors = fetchSponsors()
    }

    val uriHandler = LocalUriHandler.current

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.sponsors_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.sponsors_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        val allSponsors = sponsors.current + sponsors.past
        if (allSponsors.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                allSponsors.forEach { sponsor ->
                    AsyncImage(
                        model = sponsor.avatar,
                        contentDescription = sponsor.username,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .hoverHand()
                            .clickable { uriHandler.openUri("https://github.com/${sponsor.username}") },
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        DefaultButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            value = stringResource(Res.string.sponsors_become),
            onClick = { uriHandler.openUri("https://github.com/sponsors/SimonSchubert") },
        )
        Spacer(Modifier.height(16.dp))
    }
}

private val sponsorsClient by lazy {
    HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
}

private suspend fun fetchSponsors(): Sponsors = try {
    val response: SponsorsResponse = sponsorsClient
        .get("https://ghs.vercel.app/v3/sponsors/SimonSchubert")
        .body()
    Sponsors(
        current = response.sponsors.current.map { Sponsor(it.username, it.avatar) },
        past = response.sponsors.past.map { Sponsor(it.username, it.avatar) },
    )
} catch (e: Exception) {
    Sponsors()
}

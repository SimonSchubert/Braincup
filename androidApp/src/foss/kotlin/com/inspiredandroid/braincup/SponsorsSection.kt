package com.inspiredandroid.braincup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.inspiredandroid.braincup.app.R
import com.inspiredandroid.braincup.ui.components.DefaultButton
import com.inspiredandroid.braincup.ui.components.TextPrismButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URI

data class Sponsor(val username: String, val avatar: String)

@Composable
fun MainMenuSponsors() {
    SponsorsSection()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SponsorsSection() {
    var currentSponsors by remember { mutableStateOf<List<Sponsor>>(emptyList()) }
    var pastSponsors by remember { mutableStateOf<List<Sponsor>>(emptyList()) }

    LaunchedEffect(Unit) {
        val (current, past) = withContext(Dispatchers.IO) { fetchSponsors() }
        currentSponsors = current
        pastSponsors = past
    }

    val uriHandler = LocalUriHandler.current

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.sponsors_title),
            style = MaterialTheme.typography.titleMedium,
        )

        if (currentSponsors.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.sponsors_monthly),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                currentSponsors.forEach { sponsor ->
                    AsyncImage(
                        model = sponsor.avatar,
                        contentDescription = sponsor.username,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .clickable { uriHandler.openUri("https://github.com/${sponsor.username}") },
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        DefaultButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            value = stringResource(R.string.sponsors_become),
            onClick = { uriHandler.openUri("https://github.com/sponsors/SimonSchubert") },
        )
        Spacer(Modifier.height(16.dp))
    }
}

private fun fetchSponsors(): Pair<List<Sponsor>, List<Sponsor>> = try {
    val connection = URI("https://ghs.vercel.app/v3/sponsors/SimonSchubert").toURL()
        .openConnection() as HttpURLConnection
    connection.connectTimeout = 5000
    connection.readTimeout = 5000

    val response = JSONObject(connection.inputStream.bufferedReader().readText())
    val sponsors = response.getJSONObject("sponsors")

    fun parseSponsors(array: org.json.JSONArray) = (0 until array.length()).map { i ->
        val obj = array.getJSONObject(i)
        Sponsor(obj.getString("username"), obj.getString("avatar"))
    }

    Pair(
        parseSponsors(sponsors.getJSONArray("current")),
        parseSponsors(sponsors.getJSONArray("past")).take(10),
    )
} catch (e: Exception) {
    Pair(emptyList(), emptyList())
}

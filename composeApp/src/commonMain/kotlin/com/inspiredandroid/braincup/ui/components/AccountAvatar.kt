package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.animal_blowfish
import braincup.composeapp.generated.resources.animal_clam
import braincup.composeapp.generated.resources.animal_crab
import braincup.composeapp.generated.resources.animal_dolphin
import braincup.composeapp.generated.resources.animal_fish
import braincup.composeapp.generated.resources.animal_jellyfish
import braincup.composeapp.generated.resources.animal_lobster
import braincup.composeapp.generated.resources.animal_manta_ray
import braincup.composeapp.generated.resources.animal_octopus
import braincup.composeapp.generated.resources.animal_seagull
import braincup.composeapp.generated.resources.animal_seahorse
import braincup.composeapp.generated.resources.animal_seal
import braincup.composeapp.generated.resources.animal_seashell
import braincup.composeapp.generated.resources.animal_squid
import braincup.composeapp.generated.resources.animal_starfish
import braincup.composeapp.generated.resources.animal_swordfish
import braincup.composeapp.generated.resources.animal_tuna
import braincup.composeapp.generated.resources.animal_turtle
import braincup.composeapp.generated.resources.animal_whale
import braincup.composeapp.generated.resources.animal_winkle
import com.inspiredandroid.braincup.api.AccountIcon
import com.inspiredandroid.braincup.api.PlayerAccount
import com.inspiredandroid.braincup.ui.theme.Primary
import com.inspiredandroid.braincup.ui.theme.PrismSlot
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.painterResource

fun AccountIcon.drawable(): DrawableResource = when (this) {
    AccountIcon.BLOWFISH -> Res.drawable.animal_blowfish
    AccountIcon.CLAM -> Res.drawable.animal_clam
    AccountIcon.CRAB -> Res.drawable.animal_crab
    AccountIcon.DOLPHIN -> Res.drawable.animal_dolphin
    AccountIcon.FISH -> Res.drawable.animal_fish
    AccountIcon.JELLYFISH -> Res.drawable.animal_jellyfish
    AccountIcon.LOBSTER -> Res.drawable.animal_lobster
    AccountIcon.MANTA_RAY -> Res.drawable.animal_manta_ray
    AccountIcon.OCTOPUS -> Res.drawable.animal_octopus
    AccountIcon.SEAGULL -> Res.drawable.animal_seagull
    AccountIcon.SEAHORSE -> Res.drawable.animal_seahorse
    AccountIcon.SEAL -> Res.drawable.animal_seal
    AccountIcon.SEASHELL -> Res.drawable.animal_seashell
    AccountIcon.SQUID -> Res.drawable.animal_squid
    AccountIcon.STARFISH -> Res.drawable.animal_starfish
    AccountIcon.SWORDFISH -> Res.drawable.animal_swordfish
    AccountIcon.TUNA -> Res.drawable.animal_tuna
    AccountIcon.TURTLE -> Res.drawable.animal_turtle
    AccountIcon.WHALE -> Res.drawable.animal_whale
    AccountIcon.WINKLE -> Res.drawable.animal_winkle
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AccountAvatar(
    account: PlayerAccount,
    playAvatarBytes: ByteArray?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    val identityKey = "${account.id}:${account.name}"
    var heldPlayBitmap by remember { mutableStateOf<Pair<String, ImageBitmap>?>(null) }
    val decodedPlayBitmap = remember(playAvatarBytes) {
        playAvatarBytes?.let { runCatching { it.decodeToImageBitmap() }.getOrNull() }
    }
    if (decodedPlayBitmap != null) {
        heldPlayBitmap = identityKey to decodedPlayBitmap
    }
    val playBitmap = decodedPlayBitmap ?: heldPlayBitmap?.takeIf { it.first == identityKey }?.second
    Box(
        modifier = modifier
            .size(size)
            .clip(PrismSlot)
            .background(Primary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        if (account.isStoreAccount && playBitmap != null) {
            Image(
                bitmap = playBitmap,
                contentDescription = account.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Image(
                painter = painterResource(account.icon.drawable()),
                contentDescription = account.name,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
            )
        }
    }
}

package com.inspiredandroid.braincup.games.tools

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
import org.jetbrains.compose.resources.DrawableResource

/** A sea-creature figure used by the Spot the New game, backed by a vector drawable. */
enum class Animal(val displayName: String, val resource: DrawableResource) {
    BLOWFISH("blowfish", Res.drawable.animal_blowfish),
    CLAM("clam", Res.drawable.animal_clam),
    CRAB("crab", Res.drawable.animal_crab),
    DOLPHIN("dolphin", Res.drawable.animal_dolphin),
    FISH("fish", Res.drawable.animal_fish),
    JELLYFISH("jellyfish", Res.drawable.animal_jellyfish),
    LOBSTER("lobster", Res.drawable.animal_lobster),
    MANTA_RAY("manta ray", Res.drawable.animal_manta_ray),
    OCTOPUS("octopus", Res.drawable.animal_octopus),
    SEAGULL("seagull", Res.drawable.animal_seagull),
    SEAHORSE("seahorse", Res.drawable.animal_seahorse),
    SEAL("seal", Res.drawable.animal_seal),
    SEASHELL("seashell", Res.drawable.animal_seashell),
    SQUID("squid", Res.drawable.animal_squid),
    STARFISH("starfish", Res.drawable.animal_starfish),
    SWORDFISH("swordfish", Res.drawable.animal_swordfish),
    TUNA("tuna", Res.drawable.animal_tuna),
    TURTLE("turtle", Res.drawable.animal_turtle),
    WHALE("whale", Res.drawable.animal_whale),
    WINKLE("winkle", Res.drawable.animal_winkle),
}

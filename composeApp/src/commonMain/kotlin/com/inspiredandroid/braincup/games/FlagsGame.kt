package com.inspiredandroid.braincup.games

import com.inspiredandroid.braincup.app.AnswerButton
import com.inspiredandroid.braincup.app.FlagsUiState
import com.inspiredandroid.braincup.app.GameUiState
import kotlinx.collections.immutable.toImmutableList

class FlagsGame : Game() {
    override val adaptiveDifficulty: Boolean = false

    var correctCountry: String = ""
        private set
    var possibleAnswers: List<String> = emptyList()
        private set
    private val usedCountries: MutableSet<String> = mutableSetOf()

    /** True once every flag has been the correct answer at least once. */
    fun isComplete(): Boolean = usedCountries.size >= allCountries.size

    override fun generateRound() {
        val popularRemaining = popularCountries.filterNot { it in usedCountries }
        val pool = if (round < POPULAR_POOL_ROUNDS && popularRemaining.isNotEmpty()) {
            popularRemaining
        } else {
            allCountries.filterNot { it in usedCountries }
        }
        correctCountry = pool.random()
        usedCountries += correctCountry
        // Distractors can include previously-seen flags — they only show as names, not images.
        val distractorPool = pool.filter { it != correctCountry }
            .ifEmpty { allCountries.filter { it != correctCountry } }
        val distractors = distractorPool.shuffled().take(buttonCount() - 1)
        possibleAnswers = (distractors + correctCountry).shuffled()
    }

    private fun buttonCount(): Int = (round / 3 + 2).coerceAtMost(MAX_BUTTONS)

    override fun isCorrect(input: String): Boolean = input == correctCountry

    override fun solution(): String = correctCountry

    override fun hint(): String? = null

    override fun toUiState(): GameUiState = FlagsUiState(
        countrySlug = correctCountry,
        possibleAnswers = possibleAnswers.map { AnswerButton(it) }.toImmutableList(),
        currentScore = 0,
        bestScore = 0,
    )

    companion object {
        const val ROUND_TIME_MILLIS: Long = 8_000L
        const val MAX_BUTTONS: Int = 6
        const val POPULAR_POOL_ROUNDS: Int = 50

        val allCountries: List<String> = listOf(
            "afghanistan", "aland_islands", "albania", "algeria", "andorra", "angola",
            "anguilla", "antigua_and_barbuda", "argentina", "armenia", "aruba",
            "australia", "austria", "azerbaijan", "bahamas", "bahrain", "bangladesh",
            "barbados", "belarus", "belgium", "belize", "benin", "bermuda", "bhutan",
            "bolivia", "bosnia_and_herzegovina", "botswana", "brazil",
            "british_indian_ocean_territory", "british_virgin_islands",
            "brunei_darussalam", "bulgaria", "burkina_faso", "burundi", "cambodia",
            "cameroon", "canada", "cape_verde", "cayman_islands",
            "central_african_republic", "chad", "chile", "china", "christmas_island",
            "cocos_keeling_islands", "colombia", "comoros", "congo", "cook_islands",
            "costa_rica", "croatia", "cuba", "curacao", "cyprus", "czech_republic",
            "democratic_republic_of_the_congo", "denmark", "djibouti", "dominica",
            "dominican_republic", "ecuador", "egypt", "el_salvador",
            "equatorial_guinea", "eritrea", "estonia", "eswatini", "ethiopia",
            "falkland_islands", "faroe_islands", "fiji", "finland", "france",
            "french_polynesia", "french_southern_territories", "gabon", "gambia",
            "georgia", "germany", "ghana", "gibraltar", "great_britain", "greece",
            "greenland", "grenada", "guam", "guatemala", "guernsey", "guinea",
            "guinea_bissau", "guyana", "haiti", "honduras", "hong_kong", "hungary",
            "iceland", "india", "indonesia", "iran", "iraq", "ireland", "isle_of_man",
            "israel", "italy", "ivory_coast", "jamaica", "japan", "jersey", "jordan",
            "kazakhstan", "kenya", "kiribati", "kuwait", "kyrgyzstan", "laos",
            "latvia", "lebanon", "lesotho", "liberia", "libya", "liechtenstein",
            "lithuania", "luxembourg", "macao", "macedonia", "madagascar", "malawi",
            "malaysia", "maldives", "mali", "malta", "marshall_islands", "mauritania",
            "mauritius", "mexico", "micronesia", "moldova", "monaco", "mongolia",
            "montenegro", "montserrat", "morocco", "mozambique", "myanmar", "namibia",
            "nauru", "nepal", "netherlands", "new_zealand", "nicaragua", "niger",
            "nigeria", "niue", "norfolk_island", "north_korea",
            "northern_mariana_islands", "norway", "oman", "pakistan", "panama",
            "papua_new_guinea", "paraguay", "peru", "philippines", "pitcairn_islands",
            "poland", "portugal", "puerto_rico", "qatar", "romania",
            "russian_federation", "rwanda", "saint_kitts_and_nevis", "saint_lucia",
            "saint_vincent_and_the_grenadines", "samoa", "san_marino",
            "sao_tome_and_principe", "saudi_arabia", "senegal", "serbia", "seychelles",
            "sierra_leone", "singapore", "slovakia", "slovenia", "solomon_islands",
            "somalia", "south_africa", "south_georgia_and_the_south_sandwich_islands",
            "south_korea", "south_sudan", "spain", "sri_lanka", "sudan", "suriname",
            "sweden", "switzerland", "syria", "tajikistan", "tanzania", "thailand",
            "timor_leste", "togo", "tokelau", "tonga", "trinidad_and_tobago",
            "tunisia", "turkey", "turkmenistan", "turks_and_caicos_islands", "tuvalu",
            "uganda", "ukraine", "united_arab_emirates",
            "united_states_virgin_islands", "uruguay", "usa", "uzbekistan", "vanuatu",
            "vatican_city", "venezuela", "vietnam", "western_sahara", "yemen",
            "zambia", "zimbabwe",
        )

        val popularCountries: List<String> = listOf(
            "usa", "great_britain", "france", "germany", "italy", "spain", "portugal",
            "netherlands", "belgium", "switzerland", "austria", "sweden", "norway",
            "denmark", "finland", "iceland", "ireland", "poland", "czech_republic",
            "hungary", "greece", "romania", "bulgaria", "croatia", "serbia",
            "slovakia", "slovenia", "ukraine", "belarus", "lithuania", "latvia",
            "estonia", "russian_federation", "turkey", "cyprus", "malta",
            "luxembourg", "monaco", "china", "japan", "south_korea", "north_korea",
            "india", "pakistan", "bangladesh", "sri_lanka", "nepal", "afghanistan",
            "iran", "iraq", "israel", "saudi_arabia", "united_arab_emirates", "qatar",
            "kuwait", "jordan", "lebanon", "syria", "thailand", "vietnam",
            "philippines", "indonesia", "malaysia", "singapore", "myanmar",
            "cambodia", "mongolia", "kazakhstan", "egypt", "morocco", "algeria",
            "tunisia", "libya", "sudan", "ethiopia", "kenya", "nigeria", "ghana",
            "south_africa", "senegal", "somalia", "australia", "new_zealand", "fiji",
            "papua_new_guinea", "canada", "mexico", "cuba", "jamaica", "haiti",
            "dominican_republic", "guatemala", "honduras", "nicaragua", "costa_rica",
            "panama", "colombia", "venezuela", "ecuador", "peru", "brazil", "bolivia",
            "paraguay", "uruguay", "argentina", "chile", "vatican_city",
        )
    }
}

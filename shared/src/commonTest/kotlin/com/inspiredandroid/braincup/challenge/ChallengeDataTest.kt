package com.inspiredandroid.braincup.challenge

import io.ktor.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(InternalAPI::class)
class ChallengeDataTest {

    @Test
    fun parseCorrectSherlockCalculationChallengeData() {
        val url = "https://braincup.app/";
        listOf(
            ChallengeTestCase(
                json = """{"game":2,"goal":4,"numbers":"1,3"}""",
                expected = SherlockCalculationChallengeData(
                    url = url,
                    t = "",
                    secret = "",
                    goal = 4,
                    numbers = listOf(1, 3)
                )
            ),
            ChallengeTestCase(
                json = """{"game":2,"title":"title1","secret":"secret1","goal":15,"numbers":"1,2,3,4,5"}""",
                expected = SherlockCalculationChallengeData(
                    url = url,
                    t = "title1",
                    secret = "secret1",
                    goal = 15,
                    numbers = (1..5).toList()
                )
            ),
        ).forEach { testCase ->
            val source = testCase.json.encodeBase64()
            val result = ChallengeData.parse(url, source)
            assertEquals(testCase.expected, result)
        }
    }

    @Test
    fun parseCorrectRiddleChallengeData() {
        val url = "https://braincup.app/";
        listOf(
            ChallengeTestCase(
                json = """{"game":7,"description":"Riddle","answers":"Answer"}""",
                expected = RiddleChallengeData(
                    url = url,
                    t = "",
                    secret = "",
                    description = "Riddle",
                    answers = listOf("Answer")
                )
            ),
            ChallengeTestCase(
                json = """{"game":7,"title":"Title1","secret":"Secret1","description":"Description1",
                |"answers":"answer1,answer2,answer3"}""".trimMargin(),
                expected = RiddleChallengeData(
                    url = url,
                    t = "Title1",
                    secret = "Secret1",
                    description = "Description1",
                    answers = listOf("answer1", "answer2", "answer3")
                )
            )
        ).forEach { testCase ->
            val source = testCase.json.encodeBase64()
            val result = ChallengeData.parse(url, source)
            assertEquals(testCase.expected, result)
        }
    }

    private data class ChallengeTestCase(val json: String, val expected: ChallengeData)
}
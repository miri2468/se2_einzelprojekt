package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res = controller.getLeaderboard(null).body!!

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectIdSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res = controller.getLeaderboard(null).body!!

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(first, res[2])
    }

    @Test
    fun test_getLeaderboard_noRank_returnsEverything() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second,first,third))

        val res = controller.getLeaderboard(null)
        assertEquals(200,res.statusCode.value())
        assertEquals(3,res.body?.size)
    }


    @Test
    fun test_getLeaderboard_invalidRankZero_returns400() {
        whenever(mockedService.getGameResults()).thenReturn(listOf(GameResult(1, "first", 20, 10.0)))
        val res = controller.getLeaderboard(0)
        assertEquals(400,res.statusCode.value())
    }

    @Test
    fun test_getLeaderboard_invalidRankLarge_returns400() {
        whenever(mockedService.getGameResults()).thenReturn(listOf(GameResult(1, "first", 20, 10.0)))

        val res = controller.getLeaderboard(200)
        assertEquals(400,res.statusCode.value())
    }

    @Test
    fun test_getLeaderboard_startRank_success() {
        val result = (1..10).map { GameResult(it.toLong(), "P$it", it *40, it * 4.0)}
        whenever(mockedService.getGameResults()).thenReturn(result)

        val res = controller.getLeaderboard(1)
        assertEquals(200,res.statusCode.value())
        assertEquals(4, res.body?.size)
    }

    @Test
    fun test_getLeaderboard_middleRank_success() {
        val result = (1..10).map { GameResult(it.toLong(), "P$it", (11-it)*10, it.toDouble())}
        whenever(mockedService.getGameResults()).thenReturn(result)

        val res = controller.getLeaderboard(5)
        assertEquals(200,res.statusCode.value())
        assertEquals(7, res.body?.size)
    }

    @Test
    fun test_test_getLeaderboard_endRank_success() {
        val result = (1..10).map { GameResult(it.toLong(), "P$it", (11-it)*10, it.toDouble())}
        whenever(mockedService.getGameResults()).thenReturn(result)

        val res = controller.getLeaderboard(10)
        assertEquals(200,res.statusCode.value())
        assertEquals(4, res.body?.size)
    }

}
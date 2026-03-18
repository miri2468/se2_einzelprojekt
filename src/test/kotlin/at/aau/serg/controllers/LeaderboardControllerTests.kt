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
        mockedService = mock<GameResultService>() // creating mock service
        controller = LeaderboardController(mockedService) // injecting mock into controller
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        //arrange: players are in the wrong order
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)
        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        //act
        val res = controller.getLeaderboard(null).body!!

        //assert: sort score starting from higher
        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectIdSorting() {
        //arrange: players have same score so sort by time differences
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)
        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        //act
        val res = controller.getLeaderboard(null).body!!

        //asser: tiebreak by faster time
        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(second, res[0]) // fastest time wins
        assertEquals(third, res[1])
        assertEquals(first, res[2]) // slowest time loses
    }

    @Test
    fun test_getLeaderboard_noRank_returnsEverything() {
        //arrange
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)
        whenever(mockedService.getGameResults()).thenReturn(listOf(second,first,third))


        //act
        val res = controller.getLeaderboard(null)

        //assert
        assertEquals(200,res.statusCode.value())
        assertEquals(3,res.body?.size)
    }


    @Test
    fun test_getLeaderboard_invalidRankNegative_returns400() {
        //arrange: list with 10 players
        val result = (1..10).map { GameResult(it.toLong(), "player$it", (11-it)*10, it.toDouble()) }
        whenever(mockedService.getGameResults()).thenReturn(result)

        //act: invalid negative rank
        val res = controller.getLeaderboard(0)

        //assert: returns 400 bad request
        assertEquals(400,res.statusCode.value())
    }

    @Test
    fun test_getLeaderboard_invalidRankLarge_returns400() {
        //arrange
        val result = (1..10).map { GameResult(it.toLong(), "player$it", (11-it)*10, it.toDouble()) }
        whenever(mockedService.getGameResults()).thenReturn(result)

        //act: rank exceeds list size
        val res = controller.getLeaderboard(200)

        //assert: returns 400 bad request
        assertEquals(400,res.statusCode.value())
    }

    @Test
    fun test_getLeaderboard_startRank_success() {
        //arrange: 10 players in the list sorted by score (lowest score first)
        val result = (1..10).map { GameResult(it.toLong(), "player$it", (11-it)*10, it.toDouble()) }
        whenever(mockedService.getGameResults()).thenReturn(result)

        //act: rank 1 shows only players below it
        val res = controller.getLeaderboard(1)
        val body = res.body!!

        //asser
        assertEquals(4, body.size)
        assertEquals("player1", body[0].playerName)
        assertEquals("player4", body[3].playerName)
    }

    @Test
    fun test_getLeaderboard_middleRank_success() {
        //arrange
        val result = (1..10).map { GameResult(it.toLong(), "player$it", (11-it)*10, it.toDouble())}
        whenever(mockedService.getGameResults()).thenReturn(result)

        //act: rank 5 shows both below and above players
        val res = controller.getLeaderboard(5)
        val body = res.body!!

        //assert
        assertEquals(7, body.size)
        assertEquals("player2", body[0].playerName)
        assertEquals("player8", body[6].playerName)
    }

    @Test
    fun test_getLeaderboard_endRank_success() {
        //arrange
        val result = (1..10).map { GameResult(it.toLong(), "player$it", (11-it)*10, it.toDouble())}
        whenever(mockedService.getGameResults()).thenReturn(result)

        //act: rank 10 shows only above players
        val res = controller.getLeaderboard(10)
        val body = res.body!!

        //assert
        assertEquals(4, body.size)
        assertEquals("player7", body[0].playerName)
        assertEquals("player10", body[3].playerName)
    }

}
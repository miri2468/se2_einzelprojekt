package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.mockito.Mockito.verify


class GameResultControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: GameResultController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = GameResultController(mockedService)
    }

    @Test
    fun test_getGameResult_returnsValidResult() {
        val first = GameResult(1, "first", 20, 20.0)
        whenever(mockedService.getGameResult(1)).thenReturn(first)

        val res = controller.getGameResult(1)
        verify(mockedService).getGameResult(1)
        assertEquals(first, res)
    }

    @Test
    fun test_getGameResult_returnsNull() {
        whenever(mockedService.getGameResult(200)).thenReturn(null)

        val res = controller.getGameResult(200)
        verify(mockedService).getGameResult(200)
        assertNull(res)
    }

    @Test
    fun test_getGameResults_returnsAll() {
        val gameResult = listOf(GameResult(1, "first", 20, 20.0))
        whenever(mockedService.getGameResults()).thenReturn(gameResult)

        val res = controller.getAllGameResults()
        verify(mockedService).getGameResults()

        assertEquals(gameResult, res)
    }

    @Test
    fun test_addGameResult() {
        val first = GameResult(1, "first", 20, 20.0)
        controller.addGameResult(first)

        verify(mockedService).addGameResult(first)
    }

    @Test
    fun test_deleteGameResult() {
        val first = GameResult(1, "first", 20, 20.0)
        controller.deleteGameResult(first.id)
        verify(mockedService).deleteGameResult(first.id)
    }




}
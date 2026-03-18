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
        mockedService = mock<GameResultService>() // mock service
        controller = GameResultController(mockedService) // injecting mock into the controller
    }

    @Test
    fun test_getGameResult_returnsValidResult() {
        //arrange: mock returns a valid game result for id 1
        val first = GameResult(1, "first", 20, 20.0)
        whenever(mockedService.getGameResult(1)).thenReturn(first)

        //act
        val res = controller.getGameResult(1)

        //assert: result matches
        verify(mockedService).getGameResult(1)
        assertEquals(first, res)
    }

    @Test
    fun test_getGameResult_returnsNull() {
        //arrange: for non-existing id the mock returns null
        whenever(mockedService.getGameResult(200)).thenReturn(null)

        //act
        val res = controller.getGameResult(200)

        //assert: null is returned after service call
        verify(mockedService).getGameResult(200)
        assertNull(res)
    }

    @Test
    fun test_getGameResults_returnsAll() {
        //arrange
        val gameResult = listOf(GameResult(1, "first", 20, 20.0))
        whenever(mockedService.getGameResults()).thenReturn(gameResult)

        //act
        val res = controller.getAllGameResults()

        //assert: full list is returned after service call
        verify(mockedService).getGameResults()
        assertEquals(gameResult, res)
    }

    @Test
    fun test_addGameResult() {
        //arrange
        val first = GameResult(1, "first", 20, 20.0)

        //act
        controller.addGameResult(first)

        //assert: service called with correct object
        verify(mockedService).addGameResult(first)
    }

    @Test
    fun test_deleteGameResult() {
        //arrange
        val first = GameResult(1, "first", 20, 20.0)

        //act
        controller.deleteGameResult(first.id)

        //assert: service called with correct id
        verify(mockedService).deleteGameResult(first.id)
    }




}
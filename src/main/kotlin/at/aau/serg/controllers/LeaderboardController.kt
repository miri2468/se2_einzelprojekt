package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(
        @RequestParam(required=false) rank: Int?
    ): ResponseEntity<List<GameResult>> {

        // sort 1: higher score (in descending order)
        // sort 2: if tiebreak, faster time wins (in ascending order)
        val sort = gameResultService.getGameResults().sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))

        // if no rank parameter-> return everything
        if (rank == null) {
            return ResponseEntity.ok(sort)
        }

        // if invalid rank value -> send HTTP 400 back to caller
        if (rank < 1 || rank > sort.size) {
            return ResponseEntity.badRequest().build()
        }

        // index of the list starts with 0 -> rank 1 has index 0, rank 2 has index 1, etc.
        val targetIndex = rank - 1

        // shows 3 players above the target
        val from = maxOf(0, targetIndex - 3)

        // shows 3 players below the target
        val to = minOf(sort.size - 1, targetIndex + 3)


        return ResponseEntity.ok(sort.subList(from, to + 1))
    }
}

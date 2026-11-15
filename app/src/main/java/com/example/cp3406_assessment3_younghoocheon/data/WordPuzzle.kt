package com.example.cp3406_assessment3_younghoocheon.data

import java.util.Random

/** One draggable word. The id keeps duplicate words apart. */
data class Tile(val id: Int, val text: String)

/** Logic for the Word Builder activity, kept free of Android so it can be unit tested. */
object WordPuzzle {
    const val MIN_WORDS = 3
    const val MAX_WORDS = 14

    fun tokenize(answer: String): List<String> =
        answer.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }

    fun isPlayable(answer: String): Boolean = tokenize(answer).size in MIN_WORDS..MAX_WORDS

    /** Shuffles deterministically for a seed and makes sure the order actually changes. */
    fun shuffledTiles(words: List<String>, seed: Long): List<Tile> {
        val tiles = words.mapIndexed { index, word -> Tile(index, word) }
        if (tiles.size < 2) return tiles
        val random = Random(seed)
        var shuffled = tiles.shuffled(random)
        var attempts = 0
        while (shuffled.map { it.id } == tiles.map { it.id } && attempts < 10) {
            shuffled = tiles.shuffled(random)
            attempts += 1
        }
        return shuffled
    }

    fun isCorrect(chosen: List<Tile>, target: List<String>): Boolean =
        chosen.map { it.text } == target
}
package com.example.cp3406_assessment3_younghoocheon

import com.example.cp3406_assessment3_younghoocheon.data.Tile
import com.example.cp3406_assessment3_younghoocheon.data.WordPuzzle

class WordPuzzleTest {

    @Test
    fun tokenize_splitsOnAnyWhitespaceAndIgnoresEdges() {
        assertEquals(listOf("a", "b", "c"), WordPuzzle.tokenize("  a   b\tc \n"))
        assertTrue(WordPuzzle.tokenize("   ").isEmpty())
    }

    @Test
    fun isPlayable_requiresBetweenThreeAndFourteenWords() {
        assertFalse(WordPuzzle.isPlayable("one two"))
        assertTrue(WordPuzzle.isPlayable("one two three"))
        assertTrue(WordPuzzle.isPlayable((1..14).joinToString(" ")))
        assertFalse(WordPuzzle.isPlayable((1..15).joinToString(" ")))
    }

    @Test
    fun shuffledTiles_keepsEveryWordAndGivesUniqueIds() {
        val words = listOf("keep", "only", "the", "data", "you", "need")

        val tiles = WordPuzzle.shuffledTiles(words, seed = 7)

        assertEquals(words.sorted(), tiles.map { it.text }.sorted())
        assertEquals(words.size, tiles.map { it.id }.toSet().size)
    }

    @Test
    fun shuffledTiles_isDeterministicForTheSameSeed() {
        val words = listOf("a", "b", "c", "d", "e", "f")

        assertEquals(WordPuzzle.shuffledTiles(words, 42), WordPuzzle.shuffledTiles(words, 42))
    }

    @Test
    fun shuffledTiles_neverReturnsTheOriginalOrderForManySeeds() {
        val words = listOf("one", "two", "three")

        for (seed in 0L..200L) {
            val order = WordPuzzle.shuffledTiles(words, seed).map { it.id }
            assertNotEquals("seed $seed kept the original order", listOf(0, 1, 2), order)
        }
    }

    @Test
    fun shuffledTiles_handlesShortLists() {
        assertTrue(WordPuzzle.shuffledTiles(emptyList(), 1).isEmpty())
        assertEquals(listOf(Tile(0, "solo")), WordPuzzle.shuffledTiles(listOf("solo"), 1))
    }

    @Test
    fun isCorrect_comparesTextInOrder() {
        val target = listOf("limit", "access", "to", "data")

        assertTrue(WordPuzzle.isCorrect(target.mapIndexed { i, w -> Tile(i, w) }, target))
        assertFalse(WordPuzzle.isCorrect(listOf(Tile(1, "access"), Tile(0, "limit")), target))
        assertFalse(WordPuzzle.isCorrect(emptyList(), target))
    }

    @Test
    fun isCorrect_acceptsDuplicateWordsInEitherPosition() {
        val target = listOf("the", "cat", "and", "the", "dog")
        val swappedDuplicates = listOf(
            Tile(3, "the"),
            Tile(1, "cat"),
            Tile(2, "and"),
            Tile(0, "the"),
            Tile(4, "dog")
        )

        assertTrue(WordPuzzle.isCorrect(swappedDuplicates, target))
    }
}
package com.example.cp3406_assessment3_younghoocheon.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExploreSearchTest {

    private fun staff(id: String, name: String, expertise: List<String>, categories: List<String>) = StaffMember(
        id = id,
        name = name,
        title = "Lecturer",
        photo = 0,
        qualifications = emptyList(),
        expertise = expertise,
        summary = "Works on $name things",
        highlights = emptyList(),
        publications = emptyList(),
        categories = categories,
    )

    private val people = listOf(
        staff("a", "Ada Lovelace", listOf("Algorithms"), listOf("AI & Data")),
        staff("b", "Grace Hopper", listOf("Compilers", "Security"), listOf("Security")),
        staff("c", "Alan Turing", listOf("Cryptography"), listOf("Security", "AI & Data")),
    )

    private val programs = listOf(
        ProgramInfo(
            id = "p1",
            name = "Bachelor of IT",
            level = "Undergraduate",
            image = 0,
            summary = "Learn to build software",
            sections = listOf(ProgramSection("Core", listOf("Databases", "Mobile computing"))),
            accreditation = null,
        ),
        ProgramInfo(
            id = "p2",
            name = "Master of Data Science",
            level = "Postgraduate",
            image = 0,
            summary = "Analyse large data sets",
            sections = emptyList(),
            accreditation = "ACS",
        ),
    )

    @Test
    fun staff_blankQueryReturnsEveryone() {
        assertEquals(people, ExploreSearch.staff("   ", people))
    }

    @Test
    fun staff_matchesNameExpertiseAndCategoryIgnoringCase() {
        assertEquals(listOf("a"), ExploreSearch.staff("LOVELACE", people).map { it.id })
        assertEquals(listOf("b"), ExploreSearch.staff("compilers", people).map { it.id })
        assertEquals(listOf("b", "c"), ExploreSearch.staff("security", people).map { it.id })
    }

    @Test
    fun staff_noMatchGivesEmptyList() {
        assertTrue(ExploreSearch.staff("zebra", people).isEmpty())
    }

    @Test
    fun staffByCategory_allReturnsEveryoneAndOtherwiseFilters() {
        assertEquals(people, ExploreSearch.staffByCategory("All", people))
        assertEquals(listOf("a", "c"), ExploreSearch.staffByCategory("AI & Data", people).map { it.id })
    }

    @Test
    fun programs_searchesNameLevelSummaryAndSectionPoints() {
        assertEquals(listOf("p1"), ExploreSearch.programs("mobile", programs).map { it.id })
        assertEquals(listOf("p2"), ExploreSearch.programs("postgraduate", programs).map { it.id })
        assertEquals(listOf("p1", "p2"), ExploreSearch.programs("", programs).map { it.id })
    }
}

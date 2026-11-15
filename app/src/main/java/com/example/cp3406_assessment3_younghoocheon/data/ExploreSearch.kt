package com.example.cp3406_assessment3_younghoocheon.data

/** Search and filter helpers for the Explore screen. Pure functions, easy to unit test. */
object ExploreSearch {

    fun staff(query: String, all: List<StaffMember> = UniversityData.staff): List<StaffMember> {
        val needle = query.trim().lowercase()
        if (needle.isEmpty()) return all
        return all.filter { member ->
            val haystack = listOf(member.name, member.title, member.summary) +
                    member.expertise + member.categories
            haystack.any { it.lowercase().contains(needle) }
        }
    }

    fun programs(query: String, all: List<ProgramInfo> = UniversityData.programs): List<ProgramInfo> {
        val needle = query.trim().lowercase()
        if (needle.isEmpty()) return all
        return all.filter { program ->
            val haystack = listOf(program.name, program.level, program.summary) +
                    program.sections.flatMap { it.points }
            haystack.any { it.lowercase().contains(needle) }
        }
    }

    fun staffByCategory(category: String, all: List<StaffMember> = UniversityData.staff): List<StaffMember> =
        if (category == "All") all else all.filter { category in it.categories }
}
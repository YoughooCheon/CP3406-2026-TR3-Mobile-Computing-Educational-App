package com.example.cp3406_assessment3_younghoocheon.data

data class LinkItem(
    val kind: String,
    val title: String,
    val shortTitle: String,
    val subtitle: String,
    val url: String,
    val thumbnailUrl: String? = null,
)

data class Flashcard(val front: String, val back: String, val topic: String)

object SampleData {
    val topics = listOf("All", "Privacy", "Access", "Memory")

    val links = listOf(
        LinkItem(
            kind = "Lecture",
            title = "Stanford CS153 Frontier Systems | Ben Horowitz from a16z on Venture Capital Systems, Network Effects",
            shortTitle = "Stanford CS153 Frontier Systems",
            subtitle = "Stanford Online",
            url = "https://www.youtube.com/watch?v=B8NvdfssGac",
            thumbnailUrl = "https://img.youtube.com/vi/B8NvdfssGac/hqdefault.jpg",
        ),
        LinkItem(
            kind = "Channel",
            title = "Stanford Online",
            shortTitle = "Stanford Online",
            subtitle = "YouTube channel with Stanford lectures",
            url = "https://www.youtube.com/@StanfordOnline",
        ),
    )

    val cards = listOf(
        Flashcard(
            front = "What is data minimisation?",
            back = "Collecting and keeping only the data a feature really needs.",
            topic = "Privacy",
        ),
        Flashcard(
            front = "Why should an app ask for a permission only when it is needed?",
            back = "It limits unnecessary access and lets users understand why it is requested.",
            topic = "Privacy",
        ),
        Flashcard(
            front = "Name one way to make an app more accessible.",
            back = "Add content descriptions, support larger text and keep good colour contrast.",
            topic = "Access",
        ),
        Flashcard(
            front = "Why offer a high contrast option?",
            back = "It helps users with low vision read text and spot controls more easily.",
            topic = "Access",
        ),
        Flashcard(
            front = "What is spaced repetition?",
            back = "Reviewing material at increasing intervals so it is remembered for longer.",
            topic = "Memory",
        ),
        Flashcard(
            front = "What is active recall?",
            back = "Trying to retrieve an answer from memory before looking at it.",
            topic = "Memory",
        ),
    )

    fun cardsFor(topic: String): List<Flashcard> =
        if (topic == "All") cards else cards.filter { it.topic == topic }
}
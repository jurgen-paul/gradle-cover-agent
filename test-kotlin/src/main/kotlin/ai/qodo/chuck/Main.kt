package ai.qodo.chuck

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    if (args.isEmpty()) {
        println("Please provide keywords as command line arguments")
        return@runBlocking
    }

    // Create dependencies
    val httpClient = HttpClient(CIO)


    httpClient.use { client ->
        val jokeService = ChuckNorrisJokeService(client)
        val keywordFinder = KeywordFinder()
        val keywords = args.toSet()
        val joke = jokeService.getRandomJoke()

        println("Chuck Norris Joke: $joke")

        val foundKeywords = keywordFinder.findKeywords(joke, keywords)
        println("Found keywords in joke: $foundKeywords")
    }
}
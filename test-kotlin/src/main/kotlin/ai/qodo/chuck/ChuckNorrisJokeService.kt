package ai.qodo.chuck

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

class ChuckNorrisJokeService(private val client: HttpClient) : JokeService {
    override suspend fun getRandomJoke(): String {
        return try {
            val response = client.get("https://api.chucknorris.io/jokes/random")
            val jsonString = response.bodyAsText()
            val json = Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
            json.decodeFromString<ChuckNorrisJoke>(jsonString).value
        } catch (e: Exception) {
            println("Error fetching joke: ${e.message}")
            ""
        }
    }
}
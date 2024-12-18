package ai.qodo.chuck

interface JokeService {
    suspend fun getRandomJoke(): String
}
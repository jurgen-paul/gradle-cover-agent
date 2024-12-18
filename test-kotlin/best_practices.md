
Best Practices for Qodo Kotlin Projects

1. Package Structure
- Follow the established `ai.qodo` package prefix
- Group related functionality in subpackages (e.g., `ai.qodo.chuck`)
```kotlin
package ai.qodo.chuck
```

2. Interface Design
- Use interfaces for service abstractions
- Keep interfaces focused and minimal
- Name interfaces without the 'I' prefix
```kotlin
interface JokeService {
    suspend fun getRandomJoke(): String
}
```

3. Error Handling
- Use try-catch blocks for external service calls
- Log errors with meaningful messages
- Return empty strings or default values instead of null
```kotlin
try {
    // API call
} catch (e: Exception) {
    println("Error fetching joke: ${e.message}")
    ""
}
```

4. HTTP Client Management
- Use resource management with `use` blocks for HTTP clients
- Initialize clients at the component level
```kotlin
httpClient.use { client ->
    val jokeService = ChuckNorrisJokeService(client)
    // Use the client
}
```

5. Data Class Serialization
- Use `@Serializable` annotation for data classes
- Use `@SerialName` for JSON field mapping
- Provide default values for optional fields
```kotlin
@Serializable
data class ChuckNorrisJoke(
    val categories: List<String> = emptyList(),
    @SerialName("created_at")
    val createdAt: String
)
```

6. JSON Configuration
- Configure JSON parsing to be lenient
- Enable unknown key ignoring
- Enable coercion for flexible parsing
```kotlin
val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}
```

7. Dependency Injection
- Pass dependencies through constructors
- Use interfaces for loose coupling
```kotlin
class ChuckNorrisJokeService(private val client: HttpClient) : JokeService
```

8. Command Line Arguments
- Validate command line arguments early
- Provide clear usage messages
```kotlin
if (args.isEmpty()) {
    println("Please provide keywords as command line arguments")
    return@runBlocking
}
```

9. String Processing
- Normalize strings before processing (e.g., lowercase)
- Use regex patterns for word splitting
```kotlin
val normalizedText = text.lowercase()
normalizedText.split(Regex("\\s+"))
```

10. Coroutine Usage
- Use `runBlocking` for main function
- Use `suspend` functions for asynchronous operations
```kotlin
fun main(args: Array<String>) = runBlocking {
    // Async operations
}
```

11. Resource Cleanup
- Use Kotlin's `use` function for closeable resources
- Initialize resources as late as possible
```kotlin
httpClient.use { client ->
    // Use the client
}
```

12. Service Implementation
- Keep service implementations focused on a single responsibility
- Handle errors at the service level
- Return meaningful default values on failure
```kotlin
class ChuckNorrisJokeService(private val client: HttpClient) : JokeService {
    override suspend fun getRandomJoke(): String {
        return try {
            // Implementation
        } catch (e: Exception) {
            ""
        }
    }
}
```

13. Collection Handling
- Use immutable collections when possible
- Provide default empty collections
```kotlin
val categories: List<String> = emptyList()
val keywords = args.toSet()
```

14. Output Formatting
- Use string templates for output
- Format complex output across multiple lines
```kotlin
println("Chuck Norris Joke: $joke")
println("Found keywords in joke: $foundKeywords")
```

15. API Response Handling
- Parse API responses using kotlinx.serialization
- Handle response text before deserialization
```kotlin
val response = client.get("https://api.chucknorris.io/jokes/random")
val jsonString = response.bodyAsText()
json.decodeFromString<ChuckNorrisJoke>(jsonString)
```
16. Testing 
- Use Junit 5 for testing
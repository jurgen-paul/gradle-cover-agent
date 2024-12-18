package ai.qodo.chuck

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChuckNorrisJoke(
    val categories: List<String> = emptyList(),
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("icon_url")
    val iconUrl: String,
    val id: String,
    @SerialName("updated_at")
    val updatedAt: String,
    val url: String,
    val value: String
)
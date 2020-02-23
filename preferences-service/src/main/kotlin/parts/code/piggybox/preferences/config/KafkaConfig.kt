package parts.code.piggybox.preferences.config

data class KafkaConfig(
    val bootstrapServersConfig: String,
    val schemaRegistryUrlConfig: String,
    val topics: Topics
) {

    data class Topics(
        val preferencesAuthorization: String,
        val preferences: String
    )
}

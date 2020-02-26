package parts.code.piggybox.preferences.config

data class KafkaConfig(
    val bootstrapServersConfig: String,
    val schemaRegistryUrlConfig: String,
    val topics: Topics,
    val stateStores: StateStores
) {

    data class Topics(
        val preferencesAuthorization: String,
        val preferences: String
    )

    data class StateStores(
        val preferences: String
    )
}

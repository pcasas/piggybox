package parts.code.piggybox.query.config

data class KafkaConfig(
    val bootstrapServersConfig: String,
    val schemaRegistryUrlConfig: String,
    val topics: Topics,
    val stateStores: StateStores
) {

    data class Topics(
        val preferences: String,
        val balance: String
    )

    data class StateStores(
        val preferencesReadModel: String,
        val balanceReadModel: String,
        val historyReadModel: String
    )
}

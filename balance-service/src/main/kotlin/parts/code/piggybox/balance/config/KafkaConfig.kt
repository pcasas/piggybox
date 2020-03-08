package parts.code.piggybox.balance.config

data class KafkaConfig(
    val bootstrapServersConfig: String,
    val schemaRegistryUrlConfig: String,
    val topics: Topics,
    val stateStores: StateStores
) {

    data class Topics(
        val balanceAuthorization: String,
        val balance: String
    )

    data class StateStores(
        val balance: String
    )
}

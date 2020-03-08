package parts.code.piggybox.kafka.init.config

data class KafkaConfig(
    val bootstrapServersConfig: String,
    val topics: Topics
) {

    data class Topics(
        val preferencesAuthorization: String,
        val preferences: String,
        val balanceAuthorization: String,
        val balance: String
    )
}

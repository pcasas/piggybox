package parts.code.piggybox.command.application.config

data class KafkaConfig(
    val bootstrapServersConfig: String,
    val schemaRegistryUrlConfig: String,
    val topics: Topics
) {

    data class Topics(
        val fundsAuthorization: String
    )
}

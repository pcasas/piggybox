package parts.code.piggybox.kafka.init.services

import java.util.Properties
import javax.inject.Inject
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.admin.KafkaAdminClient
import parts.code.piggybox.kafka.init.config.KafkaConfig
import ratpack.service.Service

class CreateTopicsService @Inject constructor(
    config: KafkaConfig,
    kafkaAdminClientService: KafkaAdminClientService
) : Service {

    init {
        val properties = Properties().apply {
            put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServersConfig)
        }

        kafkaAdminClientService.createTopics(
            listOf(
                config.topics.preferencesAuthorization,
                config.topics.preferences,
                config.topics.balanceAuthorization
            ), KafkaAdminClient.create(properties)
        )
    }
}

package parts.code.piggybox.kafka.init.services

import java.util.Properties
import javax.inject.Inject
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.clients.admin.NewTopic
import parts.code.piggybox.kafka.init.config.KafkaConfig
import ratpack.service.Service

class CreateTopicsService @Inject constructor(
    config: KafkaConfig
) : Service {

    init {
        val properties = Properties().apply {
            put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServersConfig)
        }

        KafkaAdminClient.create(properties).createTopics(
            listOf(
                NewTopic(config.topics.preferencesAuthorization, 1, 1),
                NewTopic(config.topics.preferences, 1, 1),
                NewTopic(config.topics.balanceAuthorization, 1, 1),
                NewTopic(config.topics.balance, 1, 1)
            )
        ).all().get()
    }
}

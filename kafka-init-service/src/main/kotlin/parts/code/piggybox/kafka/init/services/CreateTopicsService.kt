package parts.code.piggybox.kafka.init.services

import java.util.Properties
import javax.inject.Inject
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.slf4j.LoggerFactory
import parts.code.piggybox.kafka.init.config.KafkaConfig
import ratpack.service.Service

class CreateTopicsService @Inject constructor(
    config: KafkaConfig
) : Service {

    private val logger = LoggerFactory.getLogger(CreateTopicsService::class.java)

    init {
        val properties = Properties().apply {
            put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServersConfig)
        }

        val client = KafkaAdminClient.create(properties)

        val topics = listOf(
            config.topics.preferencesAuthorization,
            config.topics.preferences,
            config.topics.balanceAuthorization,
            config.topics.balance
        ).filter { client.listTopics().names().get().contains(it).not() }
            .map { NewTopic(it, 1, 1) }

        if (topics.isNotEmpty()) {
            client.createTopics(topics).all().get()
            logger.info("Created topic(s): ${topics.joinToString(separator = ", ") { it.name() }}")
        }
    }
}

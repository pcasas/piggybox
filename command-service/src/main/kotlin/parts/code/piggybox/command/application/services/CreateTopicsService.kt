package parts.code.piggybox.command.application.services

import java.util.Properties
import javax.inject.Inject
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.slf4j.LoggerFactory
import parts.code.piggybox.command.application.config.KafkaConfig
import ratpack.service.Service

class CreateTopicsService @Inject constructor(
    private val config: KafkaConfig
) : Service {

    private val logger = LoggerFactory.getLogger(CreateTopicsService::class.java)

    init {
        logger.info("Creating topics...")

        val properties = Properties().apply {
            put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServersConfig)
        }

        val kafkaAdminClient = KafkaAdminClient.create(properties)

        val topics = listOf(
            config.topics.preferencesAuthorization
        ).filter { kafkaAdminClient.listTopics().names().get().contains(it).not() }
            .map { NewTopic(it, 1, 1) }

        if (topics.isNotEmpty()) {
            kafkaAdminClient.createTopics(topics).all().get()
            logger.info("Topics created: ${topics.joinToString(separator = ", ") { it.name() }}")
        }
    }
}

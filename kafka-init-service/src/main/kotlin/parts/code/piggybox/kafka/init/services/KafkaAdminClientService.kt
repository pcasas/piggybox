package parts.code.piggybox.kafka.init.services

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.slf4j.LoggerFactory

class KafkaAdminClientService {

    private val logger = LoggerFactory.getLogger(KafkaAdminClientService::class.java)

    fun createTopics(newTopics: Collection<String>, client: AdminClient) {
        logger.info("Creating topics...")

        val topics = newTopics
            .filter { client.listTopics().names().get().contains(it).not() }
            .map { NewTopic(it, 1, 1) }

        if (topics.isNotEmpty()) {
            client.createTopics(topics).all().get()
            logger.info("Topics created: ${topics.joinToString(separator = ", ") { it.name() }}")
        }
    }
}

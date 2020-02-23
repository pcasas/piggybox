package parts.code.piggybox.kafka.init.services

import org.apache.kafka.clients.admin.AdminClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class KafkaAdminClientServiceTest {

    @Test
    fun `should create topics if topics list is not empty`() {
        val adminClient = mock(AdminClient::class.java, RETURNS_DEEP_STUBS)
        Mockito.lenient().`when`(adminClient.listTopics().names().get()).thenReturn(emptySet())

        KafkaAdminClientService().createTopics(listOf("topic-name"), adminClient)

        verify(adminClient, times(1)).createTopics(any())
    }

    @Test
    fun `should not create topics if topics list is empty`() {
        val adminClient = mock(AdminClient::class.java)

        KafkaAdminClientService().createTopics(emptyList(), adminClient)

        verify(adminClient, times(0)).createTopics(any())
    }
}

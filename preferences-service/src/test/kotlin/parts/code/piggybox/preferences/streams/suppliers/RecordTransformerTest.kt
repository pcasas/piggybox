package parts.code.piggybox.preferences.streams.suppliers

import io.kotlintest.shouldBe
import java.util.UUID
import org.apache.avro.specific.SpecificRecord
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import parts.code.piggybox.preferences.config.KafkaConfig
import parts.code.piggybox.preferences.services.PreferencesService

internal class RecordTransformerTest {

    @Test
    fun `should return null for an unknown record type`() {
        val record = Mockito.mock(SpecificRecord::class.java)

        val kafkaConfig = KafkaConfig("", "", KafkaConfig.Topics("", ""), KafkaConfig.StateStores(""))
        RecordTransformer(kafkaConfig, PreferencesService()).transform(UUID.randomUUID().toString(), record) shouldBe null
    }
}

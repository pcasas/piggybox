package parts.code.piggybox.preferences.services

import java.time.Instant
import java.util.UUID
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import parts.code.piggybox.schemas.commands.CreatePreferencesCommand
import parts.code.piggybox.schemas.events.PreferencesCreated
import parts.code.piggybox.schemas.events.PreferencesDenied

class PreferencesService {

    fun createPreferences(command: CreatePreferencesCommand): KeyValue<String, SpecificRecord>? {
        val event = PreferencesCreated(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            command.currency
        )

        return KeyValue(command.customerId, event)
    }

    fun denyPreferences(command: CreatePreferencesCommand): KeyValue<String, SpecificRecord>? {
        val event = PreferencesDenied(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            command.currency
        )

        return KeyValue(command.customerId, event)
    }
}

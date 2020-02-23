package parts.code.piggybox.preferences.services

import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import parts.code.piggybox.schemas.commands.CreatePreferencesCommand
import parts.code.piggybox.schemas.events.PreferencesCreated

class PreferencesService {

    fun createPreferences(command: CreatePreferencesCommand): KeyValue<String, SpecificRecord>? {
        val event = PreferencesCreated(
            command.id,
            command.occurredOn,
            command.customerId,
            command.currency
        )

        return KeyValue(command.customerId, event)
    }
}

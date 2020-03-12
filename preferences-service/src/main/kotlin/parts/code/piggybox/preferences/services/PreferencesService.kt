package parts.code.piggybox.preferences.services

import java.time.Instant
import java.util.UUID
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import parts.code.piggybox.schemas.commands.ChangeCountryCommand
import parts.code.piggybox.schemas.commands.CreatePreferencesCommand
import parts.code.piggybox.schemas.events.ChangeCountryDenied
import parts.code.piggybox.schemas.events.CountryChanged
import parts.code.piggybox.schemas.events.CreatePreferencesDenied
import parts.code.piggybox.schemas.events.PreferencesCreated

class PreferencesService {

    fun createPreferences(command: CreatePreferencesCommand): KeyValue<String, SpecificRecord>? {
        val event = PreferencesCreated(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            command.currency,
            command.country
        )

        return KeyValue(command.customerId, event)
    }

    fun denyCreatePreferences(command: CreatePreferencesCommand): KeyValue<String, SpecificRecord>? {
        val event = CreatePreferencesDenied(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            command.currency,
            command.country
        )

        return KeyValue(command.customerId, event)
    }

    fun changeCountry(command: ChangeCountryCommand): KeyValue<String, SpecificRecord>? {
        val event = CountryChanged(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            command.country
        )

        return KeyValue(command.customerId, event)
    }

    fun denyChangeCountry(command: ChangeCountryCommand): KeyValue<String, SpecificRecord>? {
        val event = ChangeCountryDenied(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            command.country
        )

        return KeyValue(command.customerId, event)
    }
}

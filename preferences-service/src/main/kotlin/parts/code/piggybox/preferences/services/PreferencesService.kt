package parts.code.piggybox.preferences.services

import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import parts.code.piggybox.schemas.ChangeCountryCommand
import parts.code.piggybox.schemas.ChangeCountryDenied
import parts.code.piggybox.schemas.CountryChanged
import parts.code.piggybox.schemas.CreatePreferencesCommand
import parts.code.piggybox.schemas.CreatePreferencesDenied
import parts.code.piggybox.schemas.PreferencesCreated

class PreferencesService @Inject constructor(
    private val clock: Clock
) {

    fun createPreferences(command: CreatePreferencesCommand): KeyValue<String, SpecificRecord> {
        val event = PreferencesCreated(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.currency,
            command.country
        )

        return KeyValue(command.customerId, event)
    }

    fun denyCreatePreferences(command: CreatePreferencesCommand): KeyValue<String, SpecificRecord> {
        val event = CreatePreferencesDenied(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.currency,
            command.country
        )

        return KeyValue(command.customerId, event)
    }

    fun changeCountry(command: ChangeCountryCommand): KeyValue<String, SpecificRecord> {
        val event = CountryChanged(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.country
        )

        return KeyValue(command.customerId, event)
    }

    fun denyChangeCountry(command: ChangeCountryCommand): KeyValue<String, SpecificRecord> {
        val event = ChangeCountryDenied(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.country
        )

        return KeyValue(command.customerId, event)
    }
}

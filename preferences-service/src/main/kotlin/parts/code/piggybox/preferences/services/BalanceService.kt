package parts.code.piggybox.preferences.services

import java.time.Instant
import java.util.UUID
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import parts.code.piggybox.schemas.commands.AddFundsCommand
import parts.code.piggybox.schemas.events.AddFundsDenied

class BalanceService {

    fun denyAddFunds(command: AddFundsCommand): KeyValue<String, SpecificRecord>? {
        val event = AddFundsDenied(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            command.amount,
            command.currency
        )

        return KeyValue(command.customerId, event)
    }
}

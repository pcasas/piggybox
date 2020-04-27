package parts.code.piggybox.preferences.services

import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import parts.code.piggybox.schemas.AddFundsCommand
import parts.code.piggybox.schemas.AddFundsDenied
import parts.code.piggybox.schemas.BuyGameCommand
import parts.code.piggybox.schemas.BuyGameDenied

class BalanceService @Inject constructor(
    private val clock: Clock
) {

    fun denyAddFunds(command: AddFundsCommand): KeyValue<String, SpecificRecord> {
        val event = AddFundsDenied(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.moneyIDL
        )

        return KeyValue(command.customerId, event)
    }

    fun denyBuyGame(command: BuyGameCommand): KeyValue<String, SpecificRecord> {
        val event = BuyGameDenied(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.gameId,
            command.moneyIDL
        )

        return KeyValue(command.customerId, event)
    }
}

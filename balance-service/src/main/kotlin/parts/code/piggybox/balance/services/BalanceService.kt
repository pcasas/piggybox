package parts.code.piggybox.balance.services

import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import parts.code.piggybox.schemas.AddFundsCommand
import parts.code.piggybox.schemas.AddFundsDenied
import parts.code.piggybox.schemas.BuyGameCommand
import parts.code.piggybox.schemas.BuyGameDenied
import parts.code.piggybox.schemas.FundsAdded
import parts.code.piggybox.schemas.GameBought

class BalanceService @Inject constructor(
    private val clock: Clock
) {

    fun addFunds(command: AddFundsCommand): KeyValue<String, SpecificRecord> {
        val event = FundsAdded(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.moneyIDL
        )

        return KeyValue(command.customerId, event)
    }

    fun denyAddFunds(command: AddFundsCommand): KeyValue<String, SpecificRecord> {
        val event = AddFundsDenied(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.moneyIDL
        )

        return KeyValue(command.customerId, event)
    }

    fun buyGame(command: BuyGameCommand): KeyValue<String, SpecificRecord> {
        val event = GameBought(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.gameId,
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

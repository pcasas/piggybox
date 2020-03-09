package parts.code.piggybox.balance.services

import java.time.Instant
import java.util.UUID
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import parts.code.piggybox.schemas.commands.AddFundsCommand
import parts.code.piggybox.schemas.commands.BuyGameCommand
import parts.code.piggybox.schemas.commands.BuyGameDenied
import parts.code.piggybox.schemas.commands.GameBought
import parts.code.piggybox.schemas.events.AddFundsDenied
import parts.code.piggybox.schemas.events.FundsAdded

class BalanceService {

    fun addFunds(command: AddFundsCommand): KeyValue<String, SpecificRecord>? {
        val event = FundsAdded(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            command.amount,
            command.currency
        )

        return KeyValue(command.customerId, event)
    }

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

    fun buyGame(command: BuyGameCommand): KeyValue<String, SpecificRecord>? {
        val event = GameBought(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            command.gameId,
            command.amount,
            command.currency
        )

        return KeyValue(command.customerId, event)
    }

    fun denyBuyGame(command: BuyGameCommand): KeyValue<String, SpecificRecord>? {
        val event = BuyGameDenied(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            command.gameId,
            command.amount,
            command.currency
        )

        return KeyValue(command.customerId, event)
    }
}

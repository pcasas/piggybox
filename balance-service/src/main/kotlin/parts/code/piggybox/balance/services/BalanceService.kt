package parts.code.piggybox.balance.services

import java.time.Instant
import java.util.UUID
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import parts.code.piggybox.schemas.AddFundsCommand
import parts.code.piggybox.schemas.AddFundsDenied
import parts.code.piggybox.schemas.BuyGameCommand
import parts.code.piggybox.schemas.BuyGameDenied
import parts.code.piggybox.schemas.FundsAdded
import parts.code.piggybox.schemas.GameBought
import parts.code.piggybox.schemas.MoneyIDL

class BalanceService {

    fun addFunds(command: AddFundsCommand): KeyValue<String, SpecificRecord> {
        val event = FundsAdded(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            MoneyIDL(command.amount, command.currency)
        )

        return KeyValue(command.customerId, event)
    }

    fun denyAddFunds(command: AddFundsCommand): KeyValue<String, SpecificRecord> {
        val event = AddFundsDenied(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            command.amount,
            command.currency
        )

        return KeyValue(command.customerId, event)
    }

    fun buyGame(command: BuyGameCommand): KeyValue<String, SpecificRecord> {
        val event = GameBought(
            UUID.randomUUID().toString(),
            Instant.now(),
            command.customerId,
            command.gameId,
            MoneyIDL(command.amount, command.currency)
        )

        return KeyValue(command.customerId, event)
    }

    fun denyBuyGame(command: BuyGameCommand): KeyValue<String, SpecificRecord> {
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

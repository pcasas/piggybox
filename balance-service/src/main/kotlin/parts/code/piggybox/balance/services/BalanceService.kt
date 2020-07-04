package parts.code.piggybox.balance.services

import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import parts.code.piggybox.schemas.AddFundsCommand
import parts.code.piggybox.schemas.AddFundsDenied
import parts.code.piggybox.schemas.FundsAdded
import parts.code.piggybox.schemas.FundsWithdrawn
import parts.code.piggybox.schemas.WithdrawFundsCommand
import parts.code.piggybox.schemas.WithdrawFundsDenied

class BalanceService @Inject constructor(
    private val clock: Clock
) {

    fun addFunds(command: AddFundsCommand): KeyValue<String, SpecificRecord> {
        val event = FundsAdded(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.amount
        )

        return KeyValue(command.customerId, event)
    }

    fun denyAddFunds(command: AddFundsCommand): KeyValue<String, SpecificRecord> {
        val event = AddFundsDenied(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.amount
        )

        return KeyValue(command.customerId, event)
    }

    fun withdrawFunds(command: WithdrawFundsCommand): KeyValue<String, SpecificRecord> {
        val event = FundsWithdrawn(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.amount
        )

        return KeyValue(command.customerId, event)
    }

    fun denyWithdrawFunds(command: WithdrawFundsCommand): KeyValue<String, SpecificRecord> {
        val event = WithdrawFundsDenied(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.amount
        )

        return KeyValue(command.customerId, event)
    }
}

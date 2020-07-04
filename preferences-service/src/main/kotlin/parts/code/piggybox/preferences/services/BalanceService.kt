package parts.code.piggybox.preferences.services

import java.time.Clock
import java.util.UUID
import javax.inject.Inject
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import parts.code.piggybox.schemas.AddFundsCommand
import parts.code.piggybox.schemas.AddFundsDenied
import parts.code.piggybox.schemas.WithdrawFundsCommand
import parts.code.piggybox.schemas.WithdrawFundsDenied

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

    fun denyWithdrawFunds(command: WithdrawFundsCommand): KeyValue<String, SpecificRecord> {
        val event = WithdrawFundsDenied(
            UUID.randomUUID().toString(),
            clock.instant(),
            command.customerId,
            command.moneyIDL
        )

        return KeyValue(command.customerId, event)
    }
}

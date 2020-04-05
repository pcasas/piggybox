package parts.code.piggybox.integration.tests

import parts.code.piggybox.balance.BalanceServiceApplication
import parts.code.piggybox.command.CommandServiceApplication
import parts.code.piggybox.kafka.init.KafkaInitServiceApplication
import parts.code.piggybox.preferences.PreferencesServiceApplication
import parts.code.piggybox.query.QueryServiceApplication
import ratpack.test.MainClassApplicationUnderTest

class ApplicationsUnderTest {
    val kafkaInitService = MainClassApplicationUnderTest(KafkaInitServiceApplication::class.java)
    val commandService = MainClassApplicationUnderTest(CommandServiceApplication::class.java)
    val preferencesService = MainClassApplicationUnderTest(PreferencesServiceApplication::class.java)
    val balanceService = MainClassApplicationUnderTest(BalanceServiceApplication::class.java)
    val queryService = MainClassApplicationUnderTest(QueryServiceApplication::class.java)

    fun started() = kafkaInitService.address != null &&
            commandService.address != null &&
            preferencesService.address != null &&
            balanceService.address != null &&
            queryService.address != null

    fun close() {
        kafkaInitService.close()
        commandService.close()
        preferencesService.close()
        balanceService.close()
        queryService.close()
    }
}

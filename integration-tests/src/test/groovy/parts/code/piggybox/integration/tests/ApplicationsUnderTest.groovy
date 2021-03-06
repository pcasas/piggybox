package parts.code.piggybox.integration.tests

import parts.code.piggybox.balance.BalanceServiceApplication
import parts.code.piggybox.command.CommandServiceApplication
import parts.code.piggybox.preferences.PreferencesServiceApplication
import parts.code.piggybox.query.QueryServiceApplication
import ratpack.test.MainClassApplicationUnderTest

class ApplicationsUnderTest {
    final def commandService = new MainClassApplicationUnderTest(CommandServiceApplication)
    final def preferencesService = new MainClassApplicationUnderTest(PreferencesServiceApplication)
    final def balanceService = new MainClassApplicationUnderTest(BalanceServiceApplication)
    final def queryService = new MainClassApplicationUnderTest(QueryServiceApplication)

    boolean started() {
        return (commandService.address != null &&
                preferencesService.address != null &&
                balanceService.address != null &&
                queryService.address != null)
    }

    void close() {
        commandService.close()
        preferencesService.close()
        balanceService.close()
        queryService.close()
    }
}

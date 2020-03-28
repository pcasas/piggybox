package parts.code.piggybox.integration.tests

import ratpack.test.MainClassApplicationUnderTest

class ApplicationsUnderTest constructor(
    val kafkaInitService: MainClassApplicationUnderTest,
    val commandService: MainClassApplicationUnderTest,
    val preferencesService: MainClassApplicationUnderTest,
    val balanceService: MainClassApplicationUnderTest,
    val queryService: MainClassApplicationUnderTest
) {

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

package parts.code.piggybox.schemas

import parts.code.money.Currency
import parts.code.money.Money

fun MoneyIDL.toMoney() = Money(amount, Currency.valueOf(currency))
fun Money.toMoneyIDL() = MoneyIDL(amount, currency.name)

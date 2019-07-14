package pl.dybuk.currencylxtest.core.value

data class CurrencyType(
    val value : String) {

    companion object {
        val PLN = CurrencyType("PLN")
        val USD = CurrencyType("USD")
        val EUR = CurrencyType("EUR")
        val AUD = CurrencyType("AUD")

        private val known = mutableListOf(PLN, USD, EUR, AUD)

        fun create(currency : String) : CurrencyType {
            var currencyType = known.firstOrNull { it.value == currency.toUpperCase() }

            if (currencyType == null) {
                currencyType = CurrencyType(currency)
                known.add(currencyType)
            }

            return currencyType
        }

    }

}
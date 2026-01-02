interface YearProvider {
    fun currentYear(): String
}

object SystemYearProvider : YearProvider {
    override fun currentYear() =
        java.time.Year
            .now()
            .toString()
}

val yearProvider: YearProvider = SystemYearProvider

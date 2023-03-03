package com.goat.app.presentation.utils

import javafx.util.StringConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateStringConverter: StringConverter<LocalDate>() {

    private val pattern = "MM-dd-yyyy"
    private var formatter = DateTimeFormatter.ofPattern(pattern)

    override fun toString(date: LocalDate?): String? {
        var text: String? = null

        if (date != null)
        {
            text = formatter.format(date)
        }

        return text
    }

    override fun fromString(string: String?): LocalDate {
        var date: LocalDate? = null

        if (string != null && string.trim().isNotEmpty()) {
            date = LocalDate.parse(string, formatter)
        }

        return date!!
    }
}
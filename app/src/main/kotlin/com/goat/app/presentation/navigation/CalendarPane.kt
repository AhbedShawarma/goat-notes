package com.goat.app.presentation.navigation

import com.calendarfx.model.Calendar
import com.calendarfx.model.CalendarSource
import com.calendarfx.model.Entry
import com.goat.app.business.Controller
import com.goat.app.persistence.Model
import com.goat.app.presentation.IView
import com.calendarfx.view.YearMonthView
import com.goat.app.persistence.Context
import com.goat.app.persistence.NavigationViewSelection
import javafx.collections.SetChangeListener
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class CalendarPane(val model: Model, val controller: Controller) : VBox(), IView {
    private val calendarView = YearMonthView()
    private val calendarModel = Calendar()
    private val dateText = Text()
    private val notesList = FilteredNotesList(model, controller)
    private var entries = mutableListOf<Entry<*>>()

    init {
        model.addView(this)

        calendarView.isShowTodayButton = false
        calendarView.isShowWeekNumbers = false

        calendarView.selectedDates.addListener(SetChangeListener { change ->
            if (change.wasAdded()) {
                controller.setFilteredDate(change.elementAdded)
            }
        })

        calendarView.yearMonthProperty().addListener { _, _, _ ->
            calendarView.isShowUsageColors = false
            controller.setFilteredDate(model.filteredDate!!)
        }

        val calendarSource = CalendarSource("Tests")
        calendarSource.calendars.add(calendarModel)
        calendarView.calendarSources.add(calendarSource)

        calendarView.onMouseMoved = EventHandler { event ->
            val right = boundsInParent.width
            if (event.x < right - 10) {
                cursor = Cursor.DEFAULT
            }
        }

        dateText.fill = Color.WHITE
        dateText.style = "-fx-font-size: 24px;"

        children.addAll(calendarView, dateText, notesList)
    }

    override fun update(ctx: Context) {
        if (model.navigationViewSelection == NavigationViewSelection.CALENDAR) {
            calendarView.isShowUsageColors = true
            calendarModel.removeEntries(entries)
            entries = model.datesWithNotes.map {
                val entry = Entry<String>("Test")
                entry.changeStartDate(it)
                entry.changeEndDate(entry.startDate)
                entry.isFullDay = true
                entry.calendar = calendarModel
                entry
            }.toMutableList()
            if (model.filteredDate != null) {
                val formattedDate = model.filteredDate?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
                dateText.text = "$formattedDate:"
            } else {
                dateText.text = ""
            }
        }
    }
}
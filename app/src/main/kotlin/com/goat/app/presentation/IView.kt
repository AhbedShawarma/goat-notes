package com.goat.app.presentation

import com.goat.app.persistence.Context

interface IView {
    fun update(ctx: Context = Context()) {}
}
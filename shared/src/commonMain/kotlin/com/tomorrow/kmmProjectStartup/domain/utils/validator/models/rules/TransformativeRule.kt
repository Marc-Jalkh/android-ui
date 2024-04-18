package com.tomorrow.kmmProjectStartup.domain.utils.validator.models.rules

abstract class TransformativeRule<T> : Rule() {
    abstract fun getInstanceIfValid(value: Any?): T?
}
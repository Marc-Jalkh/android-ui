package com.tomorrow.kmmProjectStartup

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
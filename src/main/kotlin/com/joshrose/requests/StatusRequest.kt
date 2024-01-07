package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class StatusRequest(val status: String?)

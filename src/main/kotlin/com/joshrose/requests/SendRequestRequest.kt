package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class SendRequestRequest(val requesterId: Int, val toId: Int)

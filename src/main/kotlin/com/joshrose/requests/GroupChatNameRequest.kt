package com.joshrose.requests

import kotlinx.serialization.Serializable

@Serializable
data class GroupChatNameRequest(val name: String)

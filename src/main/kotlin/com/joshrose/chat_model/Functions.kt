package com.joshrose.chat_model

enum class Functions {
    GROUP,
    INDIVIDUAL,
    JOIN,
    LEAVE,
    ERROR
}

// TODO: Return this to enum class and add just ERROR
//@Serializable
//sealed interface Functions {
//    @Serializable(with = GroupAsStringSerializer::class) data class Group(val message: String? = null) : Functions
//    @Serializable(with = IndividualAsStringSerializer::class) data class Individual(val message: String? = null) : Functions
//    @Serializable(with = LeaveAsStringSerializer::class) data class Leave(val message: String? = null) : Functions
//    @Serializable(with = JoinAsStringSerializer::class) data class Join(val message: String? = null) : Functions
//    @Serializable(with = ErrorAsStringSerializer::class) data class Error(val message: String) : Functions
//}
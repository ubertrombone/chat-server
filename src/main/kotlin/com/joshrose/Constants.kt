package com.joshrose

object Constants {
    const val INCORRECT_CREDS = "The username or password is incorrect"
    const val INCORRECT_PASSWORD = "Incorrect password"
    const val PASSWORD_REQUIREMENT_MIN = 12
    const val REQUIREMENT_MAX = 24
    const val PASSWORD_SHORT = "Password must be at least $PASSWORD_REQUIREMENT_MIN characters!"
    const val PASSWORD_LONG = "Password must be no more than $REQUIREMENT_MAX characters!"
    const val PASSWORDS_DONT_MATCH = "Passwords don't match"
    const val PASSWORD_MUST_BE_NEW = "New password cannot be the same as old password"
    const val PASSWORD_REQUIRED_CHARS = "Password must contain at least 1 letter and 1 number."
    const val INVALID_CHARS_PASSWORD = "Invalid characters!"
    const val USERNAME_EXISTS = "Username already exists!"
    const val USERNAME_DOESNT_EXIST = "Username doesn't exist!"
    const val USERNAME_TOO_LONG = "Username must be no more than $REQUIREMENT_MAX characters!"
    const val USERNAME_TOO_SHORT = "Username must contain at least 1 character!"
    const val INVALID_CHARS_USERNAME = "Username can only contain alpha-numeric characters!"
    const val FRIEND_ALREADY_ADDED = "Friend already added!"
    const val FRIEND_DOESNT_EXIST = "User not in friend list!"
    const val USER_ALREADY_BLOCKED = "User already blocked!"
    const val USER_NOT_BLOCKED = "User not in blocked list!"
    const val FRIEND_REQUEST_EXISTS = "A friend request has already been sent!"
    const val FRIEND_REQUEST_DOESNT_EXIST = "No such friend request!"
    const val REQUEST_ALREADY_RECEIVED = "Request exists; check your requests!"
    const val GROUP_NAME_EXISTS = "Name currently in use!"
    const val GROUP_NAME_MAXIMUM = 100
    const val GROUP_NAME_TOO_LONG = "Name can only contain $GROUP_NAME_MAXIMUM characters!"
    const val GROUP_NAME_TOO_SHORT = "Name must contain at least 1 character!"
    const val GROUP_NAME_INVALID_CHARS = "Name may only include letters or numbers!"
    const val UNAUTHORIZED = "Token is not valid or has expired"
    const val UNKNOWN_ERROR = "An unknown error occurred"
}
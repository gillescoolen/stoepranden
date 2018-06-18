package com.novasports.stoepranden.model


data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}
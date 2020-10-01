package com.adt.testApp.ui.main.rest.models

class ApiResponse(
    val info: ResponseInfo,
    val results: List<CharacterActor>
) {

    class ResponseInfo(
        val count: Int,
        val pages: Int,
        val next: String?,
        val prev: String?
    ) {}

}
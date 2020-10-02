package com.adt.testApp.ui.main.rest.models

class CharacterActor(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val location: Location,
    val image:String?
) {

    val statusAndSpecies: String
    get(){
        return "Status: $status,  Species: $species"
    }

    class Location(
        val name: String,
        val url: String
    ){}

}
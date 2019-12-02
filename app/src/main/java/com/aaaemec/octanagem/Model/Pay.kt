package com.aaaemec.octanagem.Model

class Pay {

    data class pay(
    val payer: Payer)

    data class Payer(
        val address: Address,
        val date_created: String,
        val email: String,
        val name: String,
        val phone: Phone,
        val surname: String
    )
    data class Address(
        val street_name: String,
        val street_number: Int,
        val zip_code: String
    )
    data class Phone(
        val area_code: String,
        val number: String
    )
}
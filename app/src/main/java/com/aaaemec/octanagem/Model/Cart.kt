package com.aaaemec.octanagem.Model

class Cart(val title: String, val price: String, val thumbnail: String, val id: Long?) {
    constructor(): this("","","",0)

}
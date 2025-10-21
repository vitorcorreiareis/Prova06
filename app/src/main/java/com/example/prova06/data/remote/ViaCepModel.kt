package com.example.prova06.data.remote

import com.google.gson.annotations.SerializedName

data class ViaCepResponse(
    @SerializedName("cep") val cep: String = "",
    @SerializedName("logradouro") val street: String = "",
    @SerializedName("complemento") val complement: String = "",
    @SerializedName("bairro") val neighborhood: String = "",
    @SerializedName("localidade") val city: String = "",
    @SerializedName("uf") val state: String = "",
    @SerializedName("erro") val error: Boolean = false
)

package com.msd.domain.explorer.model

sealed class SMBException : Exception() {

    object ConnectionError : SMBException()
    object AccessDenied : SMBException()
    object UnknownError : SMBException()
}
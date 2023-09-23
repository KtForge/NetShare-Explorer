package com.msd.domain.explorer.model

sealed class SMBException : Exception() {

    object ConnectionError : SMBException()
    object AccessDenied : SMBException()
    object CancelException : SMBException()
    object UnknownError : SMBException()
}

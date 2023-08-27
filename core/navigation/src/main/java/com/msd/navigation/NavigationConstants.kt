package com.msd.navigation

object NavigationConstants {

    const val NetworkSettingsList = "main"

    const val SmbConfigurationRouteIdArg = "id"
    const val SmbConfigurationRouteIdArgToReplace = "{$SmbConfigurationRouteIdArg}"
    const val SmbConfigurationRouteNoIdArg = "-1"
    const val EditNetworkConfiguration = "settings/$SmbConfigurationRouteIdArgToReplace"

    const val SmbConfigurationRouteNameArg = "name"
    const val SmbConfigurationRouteNameArgToReplace = "{$SmbConfigurationRouteNameArg}"
    const val Explorer =
        "explorer/$SmbConfigurationRouteIdArgToReplace/$SmbConfigurationRouteNameArgToReplace"
}
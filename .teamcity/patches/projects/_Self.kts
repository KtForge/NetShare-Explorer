package patches.projects

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the root project
accordingly, and delete the patch script.
*/
changeProject(DslContext.projectId) {
    params {
        add {
            password("env.GH_TOKEN", "credentialsJSON:dffd27e6-f6e0-41d2-bcc3-51ef9adb3aa4", label = "REDACTED", display = ParameterDisplay.HIDDEN)
        }
    }
}

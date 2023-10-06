import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.XmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.xmlReport
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.05"

project {

    buildType(Build)
    buildType(TestCoverage)

    params {
        param("env.GOOGLE_JSON", "ewogICJwcm9qZWN0X2luZm8iOiB7CiAgICAicHJvamVjdF9udW1iZXIiOiAiNzM5OTg1NzY1MTI3IiwKICAgICJwcm9qZWN0X2lkIjogIm5ldHNoYXJlLWV4cGxvcmVyLTIzNGZjIiwKICAgICJzdG9yYWdlX2J1Y2tldCI6ICJuZXRzaGFyZS1leHBsb3Jlci0yMzRmYy5hcHBzcG90LmNvbSIKICB9LAogICJjbGllbnQiOiBbCiAgICB7CiAgICAgICJjbGllbnRfaW5mbyI6IHsKICAgICAgICAibW9iaWxlc2RrX2FwcF9pZCI6ICIxOjczOTk4NTc2NTEyNzphbmRyb2lkOmU1MDdmODA1YmEwYmU3MDU1NWNiYTIiLAogICAgICAgICJhbmRyb2lkX2NsaWVudF9pbmZvIjogewogICAgICAgICAgInBhY2thZ2VfbmFtZSI6ICJjb20ubXNkLm5ldHdvcmsuZXhwbG9yZXIiCiAgICAgICAgfQogICAgICB9LAogICAgICAib2F1dGhfY2xpZW50IjogW10sCiAgICAgICJhcGlfa2V5IjogWwogICAgICAgIHsKICAgICAgICAgICJjdXJyZW50X2tleSI6ICJBSXphU3lBZkNuZUxuOEQ5V3FiVFBMWmVTMDc0TXRKZzlKbHBOVXMiCiAgICAgICAgfQogICAgICBdLAogICAgICAic2VydmljZXMiOiB7CiAgICAgICAgImFwcGludml0ZV9zZXJ2aWNlIjogewogICAgICAgICAgIm90aGVyX3BsYXRmb3JtX29hdXRoX2NsaWVudCI6IFtdCiAgICAgICAgfQogICAgICB9CiAgICB9CiAgXSwKICAiY29uZmlndXJhdGlvbl92ZXJzaW9uIjogIjEiCn0=")
    }
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "Download google json file"
            scriptContent = "bash tooling/scripts/files/decode_google_json"
        }
        gradle {
            tasks = "clean build"
            gradleWrapperPath = ""
        }
    }

    triggers {
        vcs {
            enabled = false
        }
    }

    features {
        perfmon {
        }
    }
})

object TestCoverage : BuildType({
    name = "Test Coverage"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "Create emulator"
            scriptContent = "bash tooling/scripts/avd/create_emulator"
        }
        script {
            name = "Start and wait for emulator"
            scriptContent = "bash tooling/scripts/avd/start_and_wait_for_emulator"
        }
        gradle {
            name = "Run unit test cases"
            tasks = "debugUnitTestCoverage"
        }
        script {
            name = "Stop emulators"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = "bash tooling/scripts/avd/stop_emulators"
        }
        script {
            name = "Aggregate unit test reports"
            scriptContent = "bash tooling/scripts/reports/aggregate_test_results"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
        xmlReport {
            reportType = XmlReport.XmlReportType.JUNIT
            rules = "test-results-deploy/all_reports.xml"
        }
        pullRequests {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            provider = github {
                authType = token {
                    token = "credentialsJSON:dffd27e6-f6e0-41d2-bcc3-51ef9adb3aa4"
                }
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
    }
})

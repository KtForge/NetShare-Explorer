import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.XmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
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

    buildType(PublishPatchReleaseCandidate)
    buildType(TestCoverage)

    params {
        param(
            "env.GOOGLE_JSON",
            "ewogICJwcm9qZWN0X2luZm8iOiB7CiAgICAicHJvamVjdF9udW1iZXIiOiAiNzM5OTg1NzY1MTI3IiwKICAgICJwcm9qZWN0X2lkIjogIm5ldHNoYXJlLWV4cGxvcmVyLTIzNGZjIiwKICAgICJzdG9yYWdlX2J1Y2tldCI6ICJuZXRzaGFyZS1leHBsb3Jlci0yMzRmYy5hcHBzcG90LmNvbSIKICB9LAogICJjbGllbnQiOiBbCiAgICB7CiAgICAgICJjbGllbnRfaW5mbyI6IHsKICAgICAgICAibW9iaWxlc2RrX2FwcF9pZCI6ICIxOjczOTk4NTc2NTEyNzphbmRyb2lkOmU1MDdmODA1YmEwYmU3MDU1NWNiYTIiLAogICAgICAgICJhbmRyb2lkX2NsaWVudF9pbmZvIjogewogICAgICAgICAgInBhY2thZ2VfbmFtZSI6ICJjb20ubXNkLm5ldHdvcmsuZXhwbG9yZXIiCiAgICAgICAgfQogICAgICB9LAogICAgICAib2F1dGhfY2xpZW50IjogW10sCiAgICAgICJhcGlfa2V5IjogWwogICAgICAgIHsKICAgICAgICAgICJjdXJyZW50X2tleSI6ICJBSXphU3lBZkNuZUxuOEQ5V3FiVFBMWmVTMDc0TXRKZzlKbHBOVXMiCiAgICAgICAgfQogICAgICBdLAogICAgICAic2VydmljZXMiOiB7CiAgICAgICAgImFwcGludml0ZV9zZXJ2aWNlIjogewogICAgICAgICAgIm90aGVyX3BsYXRmb3JtX29hdXRoX2NsaWVudCI6IFtdCiAgICAgICAgfQogICAgICB9CiAgICB9CiAgXSwKICAiY29uZmlndXJhdGlvbl92ZXJzaW9uIjogIjEiCn0="
        )
        password("env.ANDROID_KEYSTORE_ALIAS", "credentialsJSON:48197dd5-840c-4c87-af19-0f75d4d88e75", label = "REDACTED", display = ParameterDisplay.HIDDEN)
        password("env.ANDROID_KEYSTORE_PASSWORD", "credentialsJSON:5fc20552-16ee-4c52-9493-cd2b14f2d98c", label = "REDACTED", display = ParameterDisplay.HIDDEN)
        password("env.ANDROID_KEYSTORE_PRIVATE_KEY_PASSWORD", "credentialsJSON:7655ce54-ac21-4cb6-b869-7411cf1f52e9", label = "REDACTED", display = ParameterDisplay.HIDDEN)
        password("env.CODECOV_TOKEN", "credentialsJSON:ce200648-5ba0-4804-92c1-3623eb3d6bcc", label = "REDACTED", display = ParameterDisplay.HIDDEN)
    }
}

object PublishPatchReleaseCandidate : BuildType({
    name = "Publish patch release candidate"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "Download google json file"
            scriptContent = "bash tooling/scripts/files/decode_google_json"
        }
        script {
            name = "Increase patch version"
            scriptContent = "bash tooling/scripts/versioning/increase_patch_version"
        }
        script {
            name = "Update version and tag"
            scriptContent = "bash tooling/scripts/versioning/update_version_and_tag"
        }
        gradle {
            name = "Build release app"
            tasks = "assembleRelease bundleRelease"
            gradleWrapperPath = ""
        }
        script {
            name = "Push changes to main"
            scriptContent = "bash tooling/scripts/versioning/push_changes"
        }
        script {
            name = "Publish new Github release"
            scriptContent = "bash tooling/scripts/versioning/publish_github_release"
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
        sshAgent {
            teamcitySshKey = "NetShare Github"
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
            name = "Run unit test cases and create coverage report"
            tasks = "createTestCoverageReport"
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
        script {
            name = "Upload Coverage report to Codecov"
            scriptContent = "bash tooling/scripts/reports/upload_coverage_report"
        }
    }

    triggers {
        vcs {
            branchFilter = """
                +:pull/*
            """.trimIndent()
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
                filterSourceBranch = ""
                filterTargetBranch = "+:refs/heads/main"
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
        commitStatusPublisher {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:dffd27e6-f6e0-41d2-bcc3-51ef9adb3aa4"
                }
            }
        }
    }
})

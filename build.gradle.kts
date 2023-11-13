plugins {
    java
    application
    alias(libs.plugins.style)
    alias(libs.plugins.jagr.gradle)
}

version = file("version").readLines().first()

jagr {
    assignmentId.set("h02")
    submissions {
        val main by creating {
            studentId.set("ab12cdef")
            firstName.set("sol_first")
            lastName.set("sol_last")
        }
    }
    graders {
        val graderPublic by creating {
            graderName.set("H02-Public")
            rubricProviderName.set("h02.H02_RubricProvider")
            configureDependencies {
                implementation(libs.algoutils.tutor)
            }
        }
        val graderPrivate by creating {
            graderName.set("H02-Private")
            rubricProviderName.set("h02.H02_RubricProvider")
            config.set(
                org.sourcegrade.jagr.launcher.env.Config(
                    executor = org.sourcegrade.jagr.launcher.env.Executor(
                        jvmArgs = listOf(
                            "-Djava.awt.headless=true",
                            "-Dtestfx.robot=glass",
                            "-Dtestfx.headless=true",
                            "-Dprism.order=sw",
                            "-Dprism.lcdtext=false",
                            "-Dprism.subpixeltext=false",
                            "-Dglass.win.uiScale=100%",
                            "-Dprism.text=t2k",
                        ),
                    ),
                    transformers = org.sourcegrade.jagr.launcher.env.Transformers(
                        timeout = org.sourcegrade.jagr.launcher.env.Transformers.TimeoutTransformer(enabled = true),
                    ),
                ),
            )
            configureDependencies {
                implementation(libs.algoutils.tutor)
            }
        }
    }
}

dependencies {
    implementation(libs.annotations)
    implementation(libs.algoutils.student)
    implementation(libs.fopbot)
    implementation(libs.mockito.core)
    testImplementation(libs.junit.core)
}

application {
    mainClass.set("h02.Main")
}

tasks {
    val runDir = File("build/run")
    withType<JavaExec> {
        doFirst {
            runDir.mkdirs()
        }
        workingDir = runDir
    }
    test {
        doFirst {
            runDir.mkdirs()
        }
        workingDir = runDir
        useJUnitPlatform()
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}

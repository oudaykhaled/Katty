import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    jacoco
}

val instrumentationCoverage: Boolean =
    (project.findProperty("instrumentationCoverage") as? String)?.toBoolean() == true

android {
    namespace = "com.catfact.app"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.catfact.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.catfact.app.HiltTestRunner"
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") { dimension = "environment" }
        create("prod") { dimension = "environment" }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = instrumentationCoverage
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("benchmark") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
        managedDevices {
            localDevices {
                create("pixel6Api34") {
                    device = "Pixel 6"
                    apiLevel = 34
                    systemImageSource = "aosp-atd"
                    testedAbi = "x86_64"
                }
            }
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

composeCompiler {
    stabilityConfigurationFiles.add(project.layout.projectDirectory.file("compose_compiler_config.conf"))
    metricsDestination = layout.buildDirectory.dir("compose_metrics")
    reportsDestination = layout.buildDirectory.dir("compose_reports")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:sync"))
    implementation(project(":core:logging"))
    implementation(project(":core:telemetry"))

    implementation(project(":feature:facts"))
    implementation(project(":feature:favorites"))
    implementation(project(":feature:factdetail"))

    implementation(libs.google.android.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.androidx.profileinstaller)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    kspAndroidTest(libs.hilt.android.compiler)

    implementation(libs.androidx.hilt.navigation.compose)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.robolectric)
    testImplementation(project(":core:testing"))

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(project(":core:testing"))

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

jacoco {
    toolVersion = "0.8.12"
}

val withInstrumentation: Boolean =
    (project.findProperty("withInstrumentation") as? String)?.toBoolean() == true

tasks.register("testDashboard") {
    group = "verification"
    description =
        "Run all tests and open a coverage dashboard. " +
        "Add -PwithInstrumentation=true to also run instrumentation tests on a connected device/emulator."

    dependsOn(rootProject.tasks.named("jacocoCombinedReport"))

    doLast {
        val jacocoXml = rootProject.file("build/reports/jacoco/combined/jacoco.xml")
        val dashboardDir = file("${layout.buildDirectory.get()}/reports/dashboard")
        dashboardDir.mkdirs()

        val coverage = mutableMapOf<String, Triple<Int, Int, Double>>()
        if (jacocoXml.exists()) {
            val xmlContent = jacocoXml.readText()
            listOf("INSTRUCTION", "BRANCH", "LINE", "COMPLEXITY", "METHOD", "CLASS").forEach { type ->
                val pattern = """<counter type="$type" missed="(\d+)" covered="(\d+)"/>""".toRegex()
                val matches = pattern.findAll(xmlContent).toList()
                if (matches.isNotEmpty()) {
                    val lastMatch = matches.last()
                    val missed = lastMatch.groupValues[1].toInt()
                    val covered = lastMatch.groupValues[2].toInt()
                    val total = missed + covered
                    val pct = if (total > 0) (covered.toDouble() / total * 100) else 0.0
                    coverage[type] = Triple(covered, total, pct)
                }
            }
        }

        fun parseTestXml(file: File): Triple<Int, Int, Int> {
            val content = file.readText()
            val hasTestSuites = content.contains("<testsuites")
            if (hasTestSuites) {
                val topMatch = """<testsuites\s[^>]*tests="(\d+)"[^>]*failures="(\d+)"[^>]*errors="(\d+)"[^>]*skipped="(\d+)"""".toRegex().find(content)
                if (topMatch != null) {
                    val tests = topMatch.groupValues[1].toIntOrNull() ?: 0
                    val failures = topMatch.groupValues[2].toIntOrNull() ?: 0
                    val errors = topMatch.groupValues[3].toIntOrNull() ?: 0
                    val skip = topMatch.groupValues[4].toIntOrNull() ?: 0
                    return Triple(tests - failures - errors - skip, failures + errors, skip)
                }
            }
            val tests = """tests="(\d+)"""".toRegex().find(content)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            val failures = """failures="(\d+)"""".toRegex().find(content)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            val errors = """errors="(\d+)"""".toRegex().find(content)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            val skip = """skipped="(\d+)"""".toRegex().find(content)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            return Triple(tests - failures - errors - skip, failures + errors, skip)
        }

        fun aggregateTestResults(rootDirs: List<File>): Triple<Int, Int, Int> {
            var passed = 0; var failed = 0; var skipped = 0
            rootDirs.filter { it.isDirectory }.forEach { root ->
                root.walkTopDown()
                    .filter { it.isFile && it.extension == "xml" && it.name.startsWith("TEST-") }
                    .forEach { file ->
                        val (p, f, s) = parseTestXml(file)
                        passed += p; failed += f; skipped += s
                    }
            }
            return Triple(passed, failed, skipped)
        }

        val unitTestDirs = rootProject.subprojects
            .filter { it.path !in setOf(":benchmark", ":core:testing") }
            .map { File(it.layout.buildDirectory.get().asFile, "test-results/testDevDebugUnitTest") }
        val (unitTestsPassed, unitTestsFailed, unitTestsSkipped) = aggregateTestResults(unitTestDirs)

        val instrumentationDirs = rootProject.subprojects
            .filter { it.path !in setOf(":benchmark", ":core:testing") }
            .flatMap { sub ->
                listOf(
                    File(sub.layout.buildDirectory.get().asFile, "outputs/androidTest-results/managedDevice"),
                    File(sub.layout.buildDirectory.get().asFile, "outputs/androidTest-results/connected")
                )
            }
        val (instrumentationPassed, instrumentationFailed, instrumentationSkipped) = aggregateTestResults(instrumentationDirs)

        val totalTests = unitTestsPassed + unitTestsFailed + instrumentationPassed + instrumentationFailed
        val totalPassed = unitTestsPassed + instrumentationPassed
        val totalFailed = unitTestsFailed + instrumentationFailed

        val lineCoverage = coverage["LINE"]?.third ?: 0.0
        val branchCoverage = coverage["BRANCH"]?.third ?: 0.0
        val instructionCoverage = coverage["INSTRUCTION"]?.third ?: 0.0

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

        val testStatusClass = if (totalFailed == 0) "success" else "danger"
        val testStatusText = if (totalFailed == 0) "All tests passing" else "$totalFailed failed"
        val lineStroke = if (lineCoverage >= 80) "#64ffda" else if (lineCoverage >= 60) "#ffd93d" else "#ff6b6b"
        val lineClass = if (lineCoverage >= 80) "success" else if (lineCoverage >= 60) "warning" else "danger"
        val lineOffset = 327 - (327 * lineCoverage / 100)
        val branchStroke = if (branchCoverage >= 80) "#64ffda" else if (branchCoverage >= 60) "#ffd93d" else "#ff6b6b"
        val branchClass = if (branchCoverage >= 80) "success" else if (branchCoverage >= 60) "warning" else "danger"
        val branchOffset = 327 - (327 * branchCoverage / 100)
        val instrStroke = if (instructionCoverage >= 80) "#64ffda" else if (instructionCoverage >= 60) "#ffd93d" else "#ff6b6b"
        val instrClass = if (instructionCoverage >= 80) "success" else if (instructionCoverage >= 60) "warning" else "danger"
        val instrOffset = 327 - (327 * instructionCoverage / 100)
        val lineCovered = coverage["LINE"]?.first ?: 0
        val lineTotal = coverage["LINE"]?.second ?: 0
        val branchCovered = coverage["BRANCH"]?.first ?: 0
        val branchTotal = coverage["BRANCH"]?.second ?: 0
        val instrCovered = coverage["INSTRUCTION"]?.first ?: 0
        val instrTotal = coverage["INSTRUCTION"]?.second ?: 0

        val tableRows = coverage.entries.sortedByDescending { it.value.third }.joinToString("\n") { (type, data) ->
            val (covered, total, pct) = data
            val color = if (pct >= 80) "#64ffda" else if (pct >= 60) "#ffd93d" else "#ff6b6b"
            """<tr><td>$type</td><td><div class="coverage-bar"><div class="coverage-bar-fill" style="width: $pct%; background: $color;"></div></div><span style="color: $color">${"%.1f".format(pct)}%</span></td><td>$covered</td><td>$total</td></tr>"""
        }

        val chartLabels = coverage.keys.joinToString(",") { "'$it'" }
        val chartData = coverage.values.joinToString(",") { "%.1f".format(it.third) }
        val chartColors = coverage.values.joinToString(",") {
            val pct = it.third
            if (pct >= 80) "'rgba(100, 255, 218, 0.8)'"
            else if (pct >= 60) "'rgba(255, 217, 61, 0.8)'"
            else "'rgba(255, 107, 107, 0.8)'"
        }

        val dashboardHtml = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test Coverage Dashboard</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); min-height: 100vh; color: #fff; padding: 20px; }
        .container { max-width: 1400px; margin: 0 auto; }
        header { text-align: center; padding: 30px 0; margin-bottom: 30px; }
        header h1 { font-size: 2.5rem; background: linear-gradient(90deg, #00d4ff, #7b2ff7); -webkit-background-clip: text; -webkit-text-fill-color: transparent; margin-bottom: 10px; }
        header p { color: #8892b0; font-size: 1.1rem; }
        .timestamp { color: #64ffda; font-size: 0.9rem; margin-top: 10px; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .card { background: rgba(255,255,255,0.05); border-radius: 16px; padding: 24px; backdrop-filter: blur(10px); border: 1px solid rgba(255,255,255,0.1); transition: transform 0.3s, box-shadow 0.3s; }
        .card:hover { transform: translateY(-5px); box-shadow: 0 20px 40px rgba(0,0,0,0.3); }
        .card h3 { font-size: 0.9rem; text-transform: uppercase; letter-spacing: 1px; color: #8892b0; margin-bottom: 15px; }
        .card .value { font-size: 2.5rem; font-weight: 700; margin-bottom: 10px; }
        .card .subtitle { color: #8892b0; font-size: 0.9rem; }
        .success { color: #64ffda; } .warning { color: #ffd93d; } .danger { color: #ff6b6b; }
        .progress-ring { position: relative; width: 120px; height: 120px; margin: 0 auto 15px; }
        .progress-ring svg { transform: rotate(-90deg); }
        .progress-ring circle { fill: none; stroke-width: 8; stroke-linecap: round; }
        .progress-ring .bg { stroke: rgba(255,255,255,0.1); }
        .progress-ring .progress { transition: stroke-dashoffset 1s ease; }
        .progress-ring .percentage { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); font-size: 1.5rem; font-weight: 700; }
        .chart-container { background: rgba(255,255,255,0.05); border-radius: 16px; padding: 24px; border: 1px solid rgba(255,255,255,0.1); margin-bottom: 20px; }
        .chart-container h3 { font-size: 1.2rem; margin-bottom: 20px; color: #fff; }
        .coverage-table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        .coverage-table th, .coverage-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid rgba(255,255,255,0.1); }
        .coverage-table th { color: #8892b0; font-weight: 500; text-transform: uppercase; font-size: 0.8rem; letter-spacing: 1px; }
        .coverage-bar { height: 8px; background: rgba(255,255,255,0.1); border-radius: 4px; overflow: hidden; width: 100px; display: inline-block; vertical-align: middle; margin-right: 10px; }
        .coverage-bar-fill { height: 100%; border-radius: 4px; transition: width 1s ease; }
        .links { display: flex; gap: 15px; flex-wrap: wrap; justify-content: center; margin-top: 30px; }
        .links a { display: inline-flex; align-items: center; gap: 8px; padding: 12px 24px; background: rgba(100, 255, 218, 0.1); color: #64ffda; text-decoration: none; border-radius: 8px; border: 1px solid #64ffda; transition: all 0.3s; font-size: 0.9rem; }
        .links a:hover { background: #64ffda; color: #1a1a2e; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
        .card, .chart-container { animation: fadeIn 0.5s ease forwards; }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>Test Coverage Dashboard</h1>
            <p>Cat Fact App - Android Test Results</p>
            <div class="timestamp">Generated: $timestamp</div>
        </header>
        <div class="grid">
            <div class="card"><h3>Total Tests</h3><div class="value $testStatusClass">$totalPassed / $totalTests</div><div class="subtitle">$testStatusText</div></div>
            <div class="card"><h3>Line Coverage</h3><div class="progress-ring"><svg width="120" height="120"><circle class="bg" cx="60" cy="60" r="52"/><circle class="progress" cx="60" cy="60" r="52" stroke="$lineStroke" stroke-dasharray="327" stroke-dashoffset="$lineOffset"/></svg><div class="percentage $lineClass">${"%.1f".format(lineCoverage)}%</div></div><div class="subtitle">$lineCovered / $lineTotal lines</div></div>
            <div class="card"><h3>Branch Coverage</h3><div class="progress-ring"><svg width="120" height="120"><circle class="bg" cx="60" cy="60" r="52"/><circle class="progress" cx="60" cy="60" r="52" stroke="$branchStroke" stroke-dasharray="327" stroke-dashoffset="$branchOffset"/></svg><div class="percentage $branchClass">${"%.1f".format(branchCoverage)}%</div></div><div class="subtitle">$branchCovered / $branchTotal branches</div></div>
            <div class="card"><h3>Instruction Coverage</h3><div class="progress-ring"><svg width="120" height="120"><circle class="bg" cx="60" cy="60" r="52"/><circle class="progress" cx="60" cy="60" r="52" stroke="$instrStroke" stroke-dasharray="327" stroke-dashoffset="$instrOffset"/></svg><div class="percentage $instrClass">${"%.1f".format(instructionCoverage)}%</div></div><div class="subtitle">$instrCovered / $instrTotal instructions</div></div>
        </div>
        <div class="grid" style="grid-template-columns: 1fr 1fr;">
            <div class="chart-container"><h3>Test Results</h3><canvas id="testChart"></canvas></div>
            <div class="chart-container"><h3>Coverage Breakdown</h3><canvas id="coverageChart"></canvas></div>
        </div>
        <div class="chart-container">
            <h3>Detailed Coverage Metrics</h3>
            <table class="coverage-table"><thead><tr><th>Metric</th><th>Coverage</th><th>Covered</th><th>Total</th></tr></thead><tbody>$tableRows</tbody></table>
        </div>
        <div class="links">
            <a href="../../../build/reports/jacoco/combined/html/index.html">Detailed JaCoCo Report</a>
            <a href="../tests/testDevDebugUnitTest/index.html">Unit Test Report</a>
            <a href="../androidTests/managedDevice/devDebug/allDevices/index.html">Instrumentation Test Report</a>
        </div>
    </div>
    <script>
        new Chart(document.getElementById('testChart'), { type: 'doughnut', data: { labels: ['Unit Tests Passed', 'Unit Tests Failed', 'Instrumentation Passed', 'Instrumentation Failed'], datasets: [{ data: [$unitTestsPassed, $unitTestsFailed, $instrumentationPassed, $instrumentationFailed], backgroundColor: ['#64ffda', '#ff6b6b', '#7b2ff7', '#ff6b6b'], borderWidth: 0 }] }, options: { responsive: true, plugins: { legend: { position: 'bottom', labels: { color: '#8892b0', padding: 20 } } }, cutout: '60%' } });
        new Chart(document.getElementById('coverageChart'), { type: 'bar', data: { labels: [$chartLabels], datasets: [{ label: 'Coverage %', data: [$chartData], backgroundColor: [$chartColors], borderRadius: 8 }] }, options: { responsive: true, scales: { y: { beginAtZero: true, max: 100, grid: { color: 'rgba(255,255,255,0.1)' }, ticks: { color: '#8892b0' } }, x: { grid: { display: false }, ticks: { color: '#8892b0' } } }, plugins: { legend: { display: false } } } });
    </script>
</body>
</html>
        """.trimIndent()

        val dashboardFile = File(dashboardDir, "index.html")
        dashboardFile.writeText(dashboardHtml)

        println("\n" + "=".repeat(60))
        println("TEST DASHBOARD GENERATED")
        println("=".repeat(60))
        println("\nTest Results:")
        println("  Unit Tests:          $unitTestsPassed passed, $unitTestsFailed failed")
        println("  Instrumentation:     $instrumentationPassed passed, $instrumentationFailed failed")
        println("\nCoverage Summary:")
        coverage.forEach { (type, data) ->
            val (covered, total, pct) = data
            println("  %-12s %6d / %6d (%5.1f%%)".format(type + ":", covered, total, pct))
        }
        println("\nDashboard: file://" + dashboardFile.absolutePath)
        println("=".repeat(60))

        val os = System.getProperty("os.name").lowercase()
        val command = when {
            os.contains("mac") -> "open"
            os.contains("win") -> "start"
            else -> "xdg-open"
        }
        ProcessBuilder(command, dashboardFile.absolutePath).start()
    }
}

if (withInstrumentation) {
    gradle.projectsEvaluated {
        tasks.named("testDashboard").configure {
            rootProject.subprojects.forEach { sub ->
                val androidTestRoot = sub.layout.projectDirectory.asFile.resolve("src/androidTest")
                val hasAndroidTestSources = androidTestRoot.isDirectory &&
                    androidTestRoot.walkTopDown().any { file ->
                        file.isFile && (file.extension == "kt" || file.extension == "java")
                    }
                if (hasAndroidTestSources) {
                    sub.tasks.findByName("connectedDevDebugAndroidTest")?.let { dependsOn(it) }
                }
            }
        }
    }
}

package io.jenkins.plugins.codeInsights.domain

@kotlinx.serialization.Serializable
data class Annotation(
    private val line: Int,
    private val message: String,
    val path: String,
    private val severity: Severity = Severity.MEDIUM,
)

@Suppress("unused")
@kotlinx.serialization.Serializable
enum class Severity {
    LOW, MEDIUM, HIGH
}

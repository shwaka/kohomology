package com.github.shwaka.kohomology.linalg.log

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private enum class TestOperation(
    override val displayName: String
) : OperationKind {
    FAST("Fast"),
    SLOW("Slow"),
}

private sealed interface TestInput : OperationInput<TestOperation> {
    public data class Fast(
        public val size: Int,
    ) : TestInput {
        override val operation: TestOperation = TestOperation.FAST
    }

    public data class Slow(
        public val size: Int,
    ) : TestInput {
        override val operation: TestOperation = TestOperation.SLOW
    }
}

private data class TestSummary(
    override val operation: TestOperation,
    override val invocationCount: Int,
    override val maxDuration: Duration,
    override val totalDuration: Duration,
    override val metricsText: String,
) : OperationSummary<TestOperation>

private object TestSummaryFactory :
    OperationSummaryFactory<TestOperation, TestInput, TestSummary> {
    override fun create(
        operation: TestOperation,
        measurements: List<OperationMeasurement<TestOperation, TestInput>>,
    ): TestSummary {
        return TestSummary(
            operation = operation,
            invocationCount = measurements.size,
            maxDuration = measurements.maxOf { it.duration },
            totalDuration = measurements.fold(Duration.ZERO) { acc, measurement ->
                acc + measurement.duration
            },
            metricsText = "maxSize=${measurements.maxOf { it.input.size }}",
        )
    }

    private val TestInput.size: Int
        get() = when (this) {
            is TestInput.Fast -> this.size
            is TestInput.Slow -> this.size
        }
}

class OperationLoggerTest : FreeSpec({
    "formatSummaries should sort summaries by total duration" {
        val summaries: Map<TestOperation, TestSummary> = listOf(
            TestSummary(
                operation = TestOperation.FAST,
                invocationCount = 1,
                maxDuration = 5.milliseconds,
                totalDuration = 5.milliseconds,
                metricsText = "maxSize=2",
            ),
            TestSummary(
                operation = TestOperation.SLOW,
                invocationCount = 2,
                maxDuration = 15.milliseconds,
                totalDuration = 20.milliseconds,
                metricsText = "maxSize=30",
            ),
        ).associateBy { it.operation }
        val expected = """
            |name total  max count metrics   
            |Slow  20ms 15ms     2 maxSize=30
            |Fast   5ms  5ms     1 maxSize=2 
        """.trimMargin()

        formatSummaries(summaries) shouldBe expected
    }

    "summaries should group measurements by operation" {
        val logger = OperationLogger(TestSummaryFactory)
        logger.add(
            OperationMeasurement(
                duration = 1.milliseconds,
                input = TestInput.Fast(size = 2),
            ),
        )
        logger.add(
            OperationMeasurement(
                duration = 3.milliseconds,
                input = TestInput.Fast(size = 5),
            ),
        )
        logger.add(
            OperationMeasurement(
                duration = 10.milliseconds,
                input = TestInput.Slow(size = 7),
            ),
        )

        val summaries = logger.summaries()
        summaries.size shouldBe 2
        summaries.shouldContainKey(TestOperation.FAST)
        summaries[TestOperation.FAST] shouldBe TestSummary(
            operation = TestOperation.FAST,
            invocationCount = 2,
            maxDuration = 3.milliseconds,
            totalDuration = 4.milliseconds,
            metricsText = "maxSize=5",
        )
        summaries[TestOperation.SLOW] shouldBe TestSummary(
            operation = TestOperation.SLOW,
            invocationCount = 1,
            maxDuration = 10.milliseconds,
            totalDuration = 10.milliseconds,
            metricsText = "maxSize=7",
        )
    }

    "clear should remove measurements" {
        val logger = OperationLogger(TestSummaryFactory)
        logger.add(
            OperationMeasurement(
                duration = 1.milliseconds,
                input = TestInput.Fast(size = 2),
            ),
        )

        logger.measurement.shouldHaveSize(1)
        logger.clear()
        logger.measurement.shouldHaveSize(0)
        logger.summaries().size shouldBe 0
    }

    "measureOperation should return block result and record a measurement" {
        val logger = OperationLogger(TestSummaryFactory)
        val input = TestInput.Fast(size = 2)

        val result = logger.measureOperation(input) {
            "result"
        }

        result shouldBe "result"
        logger.measurement.shouldHaveSize(1)
        logger.measurement[0].input shouldBe input
    }

    "castedInputs should cast inputs to the expected type" {
        val measurements = listOf(
            OperationMeasurement(
                duration = 1.milliseconds,
                input = TestInput.Fast(size = 2),
            ),
            OperationMeasurement(
                duration = 3.milliseconds,
                input = TestInput.Fast(size = 5),
            ),
        )

        measurements.castedInputs<TestOperation, TestInput, TestInput.Fast>() shouldBe listOf(
            TestInput.Fast(size = 2),
            TestInput.Fast(size = 5),
        )
    }

    "castedInputs should throw IllegalStateException if input type is unexpected" {
        val measurements = listOf(
            OperationMeasurement(
                duration = 1.milliseconds,
                input = TestInput.Slow(size = 2),
            ),
        )

        shouldThrow<IllegalStateException> {
            measurements.castedInputs<TestOperation, TestInput, TestInput.Fast>()
        }
    }
})

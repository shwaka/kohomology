package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class FiniteMonoidEndTest : FreeSpec({
    tags(finiteMonoidTag, finiteMonoidMapTag)

    val monoidList = listOf(
        CyclicGroup(2),
        CyclicGroup(3),
        SymmetricGroup(3),
        TruncatedAdditionMonoid(2),
        TruncatedAdditionMonoid(3),
    )

    "check axioms of finite monoid" - {
        for (monoid in monoidList) {
            "FiniteMonoidEnd($monoid) should satisfy monoid axioms" {
                val end = FiniteMonoidEnd(monoid)
                shouldNotThrowAny {
                    end.checkMonoidAxioms()
                }
            }
        }
    }

    "check indices of elements" - {
        for (monoid in monoidList) {
            "check FiniteMonoidEnd($monoid).elements.map { it.index }" {
                val end = FiniteMonoidEnd(monoid)
                end.elements.map { it.index } shouldBe (0 until end.size).toList()
            }
        }
    }

    "test toString" - {
        val monoid = CyclicGroup(3)
        val end = FiniteMonoidEnd(monoid)
        val (id, f1, f2) = end.elements

        "test toString with PrintType.PLAIN and EndElementFormat.Raw" {
            val p = Printer(PrintType.PLAIN) {
                register(EndElementPrintConfig(EndElementFormat.Raw))
            }
            p(id) shouldBe "FiniteMonoidMap(t^0->t^0, t^1->t^1, t^2->t^2)"
            p(f1) shouldContain "FiniteMonoidMap("
            p(f2) shouldContain "FiniteMonoidMap("
        }

        "test toString with PrintType.TEX and EndElementFormat.Raw" {
            val p = Printer(PrintType.TEX) {
                register(EndElementPrintConfig(EndElementFormat.Raw))
            }
            p(id) shouldBe "\\mathrm{FiniteMonoidMap}(t^{0}\\to t^{0}, t^{1}\\to t^{1}, t^{2}\\to t^{2})"
            p(f1) shouldContain "\\mathrm{FiniteMonoidMap}("
            p(f2) shouldContain "\\mathrm{FiniteMonoidMap}("
        }

        "test toString with PrintType.PLAIN and EndElementFormat.Indexed" {
            val symbol = "g"
            val p = Printer(PrintType.PLAIN) {
                register(EndElementPrintConfig(EndElementFormat.Indexed(symbol)))
            }
            p(id) shouldBe "id"
            p(f1) shouldBe "${symbol}_1"
            p(f2) shouldBe "${symbol}_2"
        }

        "test toString with PrintType.TEX and EndElementFormat.Indexed" {
            val symbol = "g"
            val p = Printer(PrintType.TEX) {
                register(EndElementPrintConfig(EndElementFormat.Indexed(symbol)))
            }
            p(id) shouldBe "\\mathrm{id}"
            p(f1) shouldBe "${symbol}_{1}"
            p(f2) shouldBe "${symbol}_{2}"
        }
    }
})

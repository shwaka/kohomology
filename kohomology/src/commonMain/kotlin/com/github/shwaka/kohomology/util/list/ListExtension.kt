package com.github.shwaka.kohomology.util.list

// for n in $(seq 6 15); do echo "operator fun <T> List<T>.component$n() = this[$(expr $n - 1)]"; done
// for n in $(seq 6 15); do echo "import com.github.shwaka.kohomology.util.list.component$n"; done

operator fun <T> List<T>.component6() = this[5]
operator fun <T> List<T>.component7() = this[6]
operator fun <T> List<T>.component8() = this[7]
operator fun <T> List<T>.component9() = this[8]
operator fun <T> List<T>.component10() = this[9]
operator fun <T> List<T>.component11() = this[10]
operator fun <T> List<T>.component12() = this[11]
operator fun <T> List<T>.component13() = this[12]
operator fun <T> List<T>.component14() = this[13]
operator fun <T> List<T>.component15() = this[14]

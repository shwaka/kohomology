package com.github.shwaka.kohomology.util

private class EmptyPermutation<T> : Iterator<Pair<List<T>, Sign>> {
    var hasNextElm = true
    override fun hasNext(): Boolean {
        return hasNextElm
    }

    override fun next(): Pair<List<T>, Sign> {
        hasNextElm = false
        return Pair(listOf(), Sign.PLUS)
    }
}

private class NonEmptyPermutation<T>(val list: List<T>) : Iterator<Pair<List<T>, Sign>> {
    init {
        if (list.isEmpty()) throw IllegalArgumentException("list should be nonempty")
    }
    var zerothTo: Int = 0
    var subIterator: Iterator<Pair<List<T>, Sign>> = getPermutation(list.subList(1, list.size))

    override fun hasNext(): Boolean {
        return this.subIterator.hasNext() || (this.zerothTo + 1 < this.list.size)
    }

    override fun next(): Pair<List<T>, Sign> {
        if (!this.subIterator.hasNext()) {
            this.zerothTo += 1
            this.subIterator = getPermutation(list.subList(1, list.size))
        }
        val (subPermutation: List<T>, sign: Sign) = this.subIterator.next()
        val tempSign = Sign.fromInt(this.zerothTo)
        return Pair(this.insert(subPermutation, this.zerothTo, this.list[0]), sign * tempSign)
    }

    private fun insert(list: List<T>, index: Int, elm: T): List<T> {
        return list.subList(0, index) + listOf(elm) + list.subList(index, list.size)
    }
}

public fun <T> getPermutation(list: List<T>): Iterator<Pair<List<T>, Sign>> {
    return if (list.isEmpty()) {
        EmptyPermutation()
    } else {
        NonEmptyPermutation(list)
    }
}

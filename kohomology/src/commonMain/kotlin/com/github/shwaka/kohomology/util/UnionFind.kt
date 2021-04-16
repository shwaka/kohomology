package com.github.shwaka.kohomology.util

class UnionFind(private val size: Int) {
    private val parent: MutableList<Int> = MutableList(this.size) { index -> index }

    fun rootOf(index: Int): Int {
        return if (this.parent[index] == index) {
            index
        } else {
            this.parent[index] = this.rootOf(this.parent[index])
            this.parent[index]
        }
    }

    fun same(index1: Int, index2: Int): Boolean {
        return this.rootOf(index1) == this.rootOf(index2)
    }

    fun unite(index1: Int, index2: Int) {
        val root1 = this.rootOf(index1)
        val root2 = this.rootOf(index2)
        if (root1 != root2)
            this.parent[root1] = root2
    }

    fun groups(): List<List<Int>> {
        return (0 until this.size).groupBy { this.rootOf(it) }.values.toList()
    }
}

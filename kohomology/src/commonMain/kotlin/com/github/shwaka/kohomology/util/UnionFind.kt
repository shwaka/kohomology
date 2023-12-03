package com.github.shwaka.kohomology.util

internal interface FrozenUnionFind {
    fun rootOf(index: Int): Int
    fun same(index1: Int, index2: Int): Boolean
    fun groups(): List<List<Int>>
    fun representatives(): List<Int>
}

internal class UnionFind(private val size: Int) : FrozenUnionFind {
    private val parent: MutableList<Int> = MutableList(this.size) { index -> index }

    override fun rootOf(index: Int): Int {
        return if (this.parent[index] == index) {
            index
        } else {
            this.rootOf(this.parent[index]).also {
                this.parent[index] = it
            }
        }
    }

    override fun same(index1: Int, index2: Int): Boolean {
        return this.rootOf(index1) == this.rootOf(index2)
    }

    fun unite(index1: Int, index2: Int) {
        val root1 = this.rootOf(index1)
        val root2 = this.rootOf(index2)
        if (root1 != root2)
            this.parent[root1] = root2
    }

    private fun groupsAsMap(): Map<Int, List<Int>> {
        return (0 until this.size).groupBy { this.rootOf(it) }
    }

    override fun groups(): List<List<Int>> {
        return this.groupsAsMap().values.toList()
    }

    override fun representatives(): List<Int> {
        return this.groupsAsMap().keys.toList()
    }
}

internal interface FrozenGenericUnionFind<T> {
    fun rootOf(element: T): T
    fun same(element1: T, element2: T): Boolean
    fun groups(): List<List<T>>
    fun representatives(): List<T>
}

internal class GenericUnionFind<T>(private val elements: List<T>) : FrozenGenericUnionFind<T> {
    init {
        require(this.elements.distinct().size == elements.size) {
            "elements must be a list without duplicates"
        }
    }

    private val unionFind = UnionFind(elements.size)
    private val indices: Map<T, Int> = elements.mapIndexed { index, element ->
        Pair(element, index)
    }.toMap()

    private fun indexOf(element: T): Int {
        return this.indices.getValue(element)
    }

    override fun rootOf(element: T): T {
        val index = this.indexOf(element)
        return this.elements[this.unionFind.rootOf(index)]
    }

    override fun same(element1: T, element2: T): Boolean {
        val index1 = this.indexOf(element1)
        val index2 = this.indexOf(element2)
        return this.unionFind.same(index1, index2)
        // return this.rootOf(element1) == this.rootOf(element2)
    }

    fun unite(element1: T, element2: T) {
        val index1 = this.indexOf(element1)
        val index2 = this.indexOf(element2)
        this.unionFind.unite(index1, index2)
    }

    override fun groups(): List<List<T>> {
        return this.unionFind.groups().map { group ->
            group.map { index -> this.elements[index] }
        }
    }

    override fun representatives(): List<T> {
        return this.unionFind.representatives().map { index ->
            this.elements[index]
        }
    }
}

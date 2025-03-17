package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree

internal class PartitionCalculator<D : Degree>(
    val degreeGroup: AugmentedDegreeGroup<D>,
    val summandList: List<D>,
    val isAllowedDegree: (D) -> Boolean,
    val allowMultipleOfOdd: Boolean = false,
) {
    private val unit: IntArray = IntArray(summandList.size) { 0 }

    // (degree: D, index: Int) -> List<IntArray>
    private val cache: MutableMap<Pair<D, Int>, List<IntArray>> = mutableMapOf()

    fun getList(degree: D): List<IntArray> {
        if (!this.isAllowedDegree(degree)) {
            return emptyList()
        }
        return this.getListInternal(degree, 0)
    }

    private fun getListInternal(degree: D, index: Int): List<IntArray> {
        if (index < 0 || index > this.summandList.size)
            throw Exception("This can't happen! (illegal index: $index)")
        if (index == this.summandList.size) {
            return if (degree.isZero())
                listOf(this.unit)
            else
                emptyList()
        }
        val cacheKey = Pair(degree, index)
        return this.cache.getOrPut(cacheKey) {
            // Since 0 <= index < this.indeterminateList.size,
            // we have 0 < this.indeterminateList.size
            val newDegree = this.degreeGroup.context.run { degree - this@PartitionCalculator.summandList[index] }
            val listWithNonZeroAtIndex = if (this.isAllowedDegree(newDegree)) {
                this.getListInternal(newDegree, index)
                    .mapNotNull { array -> array.increaseAtIndex(index) }
            } else emptyList()
            val listWithZeroAtIndex = this.getListInternal(degree, index + 1)
            listWithNonZeroAtIndex + listWithZeroAtIndex
        }
    }

    private fun IntArray.increaseAtIndex(index: Int): IntArray? {
        // 奇数次の場合
        if (
            !this@PartitionCalculator.allowMultipleOfOdd &&
            (this@PartitionCalculator.summandList[index].isOdd()) &&
            (this[index] == 1)
        ) {
            return null
        }
        val newArray = IntArray(this@PartitionCalculator.summandList.size) {
            if (it == index) this[it] + 1 else this[it]
        }
        return newArray
    }
}

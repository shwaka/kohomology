import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational

fun main() {
    val scriptList: List<ProfiledScript> = listOf(CohomologyOfFreeLoopSpace)
    println("Select script to profile: (default = 0)")
    scriptList.mapIndexed { index, script ->
        println("$index: ${script.description}")
    }
    val index = readLine()?.toIntOrNull() ?: 0
    val script = scriptList[index]
    script.setup()
    print("Press ENTER to continue!!!")
    readLine()
    script.main()
}

interface ProfiledScript {
    val description: String
    fun setup() {}
    fun main(): String
}

object CohomologyOfFreeLoopSpace : ProfiledScript {
    override val description = "cohomology of free loop space of 2-sphere"
    override fun main(): String {
        val sphereDim = 2
        val matrixSpace = SparseMatrixSpaceOverBigRational
        val sphere = sphere(matrixSpace, sphereDim)
        val freeLoopSpace = FreeLoopSpace(sphere)

        var result = ""
        for (degree in 0 until 150) {
            result += freeLoopSpace.cohomology[degree].toString() + "\n"
        }
        return result
    }
}

package com.github.shwaka.kohomology.depgraph

data class Package(val names: List<String>) {
    // fun commonPrefix(other: Package): Package {
    //     val commonNames = this.names.zip(other.names).filter { it.first == it.second }.map { it.first }
    //     return Package(commonNames)
    // }

    fun removePrefix(prefix: Package?): Package {
        return when {
            (prefix == null) -> this
            this.startsWith(prefix) -> Package(this.names.drop(prefix.names.size))
            else -> this
        }
    }

    fun startsWith(prefix: Package): Boolean {
        if (this.names.size < prefix.names.size)
            return false
        for (i in prefix.names.indices) {
            if (this.names[i] != prefix.names[i])
                return false
        }
        return true
    }

    override fun toString(): String {
        return this.names.joinToString(".")
    }

    companion object {
        fun fromString(string: String): Package {
            return Package(string.split("."))
        }

        fun fromPackageDeclaration(packageDeclaration: String): Package {
            val matchResult: MatchResult = Regex("^package (.*)$").find(packageDeclaration)
                ?: throw Exception("Invalid package declaration: $packageDeclaration")
            return Package(matchResult.groupValues[1].split("."))
        }

        fun fromImportStatement(importStatement: String): Package {
            val matchResult: MatchResult = Regex("^import (.*)$").find(importStatement)
                ?: throw Exception("Invalid import statement: $importStatement")
            return Package(matchResult.groupValues[1].split(".").dropLast(1))
        }
    }
}

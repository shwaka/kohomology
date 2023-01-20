package com.github.shwaka.kohomology.depgraph

import java.io.File
import java.util.SortedMap
import java.util.SortedSet

class DependencyViewer {
    private val dependency: MutableMap<Package, MutableSet<Package>> = mutableMapOf()

    fun parse(file: File) {
        val text = file.readText()
        val filename = file.path
        val currentPackage = this.parseCurrentPackage(text, filename)
        if (!this.dependency.containsKey(currentPackage)) {
            this.dependency[currentPackage] = mutableSetOf()
        }
        for (importedPackage in this.parseImportedPackageList(text)) {
            this.add(currentPackage, importedPackage)
        }
    }

    private fun add(currentPackage: Package, importedPackage: Package) {
        val dependencyForCurrentPackage: MutableSet<Package> =
            this.dependency[currentPackage] ?: throw Exception("This can't happen!")
        dependencyForCurrentPackage.add(importedPackage)
    }

    private fun parseCurrentPackage(text: String, fileName: String): Package {
        val matchResult: MatchResult = Regex("^package .*$", RegexOption.MULTILINE).find(text)
            ?: throw Exception("Package declaration not found in $fileName")
        val line = matchResult.groupValues[0]
        val lineWithoutComment = this.removeComment(line)
        return Package.fromPackageDeclaration(lineWithoutComment)
    }

    private fun parseImportedPackageList(text: String): List<Package> {
        val matchResults: Sequence<MatchResult> = Regex("^import .*$", RegexOption.MULTILINE).findAll(text)
        return matchResults.map { matchResult ->
            val line = matchResult.value
            val lineWithoutComment = this.removeComment(line)
            Package.fromImportStatement(lineWithoutComment)
        }.toList()
    }

    private fun removeComment(line: String): String {
        val matchResult: MatchResult = Regex("^([^/]*) *(//.*)?$").find(line) ?: throw Exception("This can't happen!")
        return matchResult.groupValues[1]
    }

    // private fun getCommonPrefix(): Package {
    //     val packageList: List<Package> = this.dependency.map { (currentPackage, importedPackageSet) ->
    //         listOf(currentPackage) + importedPackageSet.toList()
    //     }.flatten()
    //     check(packageList.isNotEmpty())
    //     return packageList.reduce { acc, pkg -> acc.commonPrefix(pkg) }
    // }

    fun getDependency(prefix: Package? = null): SortedMap<Package, SortedSet<Package>> {
        if (prefix == null) {
            return this.dependency.mapValues { (_, packages) -> packages.toSortedSet() }.toSortedMap()
        }
        return this.dependency
            .mapKeys { (currentPackage, _) -> currentPackage.removePrefix(prefix) }
            .mapValues { (_, importedPackageSet) ->
                importedPackageSet.map { importedPackage -> importedPackage.removePrefix(prefix) }.toSet()
            }
            .mapValues { (_, packages) -> packages.toSortedSet() }
            .toSortedMap()
    }

    private fun getInternalPackages(): Set<Package> {
        return this.getDependency().keys
    }

    private fun getExternalPackages(): Set<Package> {
        val importedPackages: Set<Package> = this.getDependency().values
            .fold(emptySet()) { acc, set -> acc + set }
        return importedPackages - this.getInternalPackages()
    }

    private fun getAllPackages(): Set<Package> {
        return this.getInternalPackages() + this.getExternalPackages()
    }

    fun toUml(
        prefix: Package? = null,
        ignore: Set<Package> = emptySet(),
        groups: Set<Package> = emptySet()
    ): String {
        // mermaid.js には "hide members" がないっぽいので、plant uml を使うことにした
        // https://github.com/mermaid-js/mermaid/issues/1368
        val lines = mutableListOf(
            "@startuml",
            "hide members",
            "hide circle",
            "set namespaceSeparator ::",
        )
        val groupPackages: Map<Package, MutableSet<Package>> =
            groups.map { Pair(it, mutableSetOf<Package>()) }.toMap()
        for (pkg in this.getAllPackages()) {
            if (ignore.contains(pkg)) {
                continue
            }
            val group: Package? = groups.find { group -> pkg.startsWith(group) }
            if (group != null) {
                groupPackages.getValue(group).add(pkg)
            } else {
                lines.add("class ${pkg.removePrefix(prefix)}")
            }
        }
        for ((group, packages) in groupPackages) {
            if (packages.isEmpty()) {
                continue
            }
            lines.add("package ${group.removePrefix(prefix)} {")
            for (pkg in packages) {
                lines.add("  class ${pkg.removePrefix(prefix)}")
            }
            lines.add("}")
        }
        for ((currentPackage, importedPackages) in this.getDependency()) {
            if (ignore.contains(currentPackage)) {
                continue
            }
            for (importedPackage in importedPackages) {
                if (ignore.contains(importedPackage)) {
                    continue
                }
                val current = currentPackage.removePrefix(prefix)
                val imported = importedPackage.removePrefix(prefix)
                lines.add("$current --> $imported")
            }
        }
        lines.add("@enduml")
        return lines.joinToString("\n")
    }
}

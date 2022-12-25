package com.github.shwaka.kohomology.profile.executable

abstract class Executable {
    abstract val description: String
    protected open fun setupFun() {}
    private var setupFinished = false
    fun setup() {
        if (this.setupFinished)
            return
        this.setupFun()
        this.setupFinished = true
    }
    protected abstract fun mainFun(): String
    fun main(): String {
        // 測定したい処理に返り値を依存させることで、コンパイラの最適化で消されるのを回避する
        // 必要ないかもしれないけど念の為
        if (!this.setupFinished)
            throw Exception("setup not finished")
        return this.mainFun()
    }
}

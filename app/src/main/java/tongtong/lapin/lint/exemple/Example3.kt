package tongtong.lapin.lint.exemple

class Example3 {
    private val worker = ExampleWorker()
    fun run() {
        worker.run()
    }

    private fun run1() {
        worker.run()
    }

    private fun run2() {
        worker.run()
    }
}

class ExampleWorker {
    fun run() {
        // do something... maybe...
    }
}
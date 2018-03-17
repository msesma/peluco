package eu.sesma.peluco.ui

interface MainActivityUserInterface {

    fun initialize(delegate: Delegate)

    fun showError(error: Exception)

    interface Delegate {

        fun onRefresh()

    }
}

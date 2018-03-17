package eu.sesma.peluco.ui

import javax.inject.Inject

class MainActivityPresenter
@Inject
constructor() {

    val TAG = MainActivityPresenter::class.simpleName

    private var decorator: MainActivityUserInterface? = null

    private val delegate = object : MainActivityUserInterface.Delegate {

        override fun onRefresh() {}
    }

    fun initialize(decorator: MainActivityUserInterface) {
        this.decorator = decorator
        this.decorator?.initialize(delegate)
    }

    fun onResume() {}

    fun dispose() {
        this.decorator = null
    }
}

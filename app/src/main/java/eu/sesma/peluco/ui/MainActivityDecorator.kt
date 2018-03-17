package eu.sesma.peluco.ui

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import butterknife.ButterKnife
import eu.sesma.peluco.R
import javax.inject.Inject

class MainActivityDecorator
@Inject
constructor(
        val activity: AppCompatActivity
) : MainActivityUserInterface {

    private val TAG = MainActivityDecorator::class.simpleName

//    @BindView(R.id.toolbar)
//    lateinit var toolbar: Toolbar
//    @BindView(R.id.graph)
//    lateinit var graphView: ImageView
//    @BindView(R.id.condition)
//    lateinit var tvcondition: TextView

    private var delegate: MainActivityUserInterface.Delegate? = null

    internal var refreshListener: SwipeRefreshLayout.OnRefreshListener = SwipeRefreshLayout.OnRefreshListener { delegate?.onRefresh() }

    fun bind(view: View) {
        ButterKnife.bind(this, view)
        initToolbar()
    }

    fun dispose() {
        delegate = null
    }

    override fun initialize(delegate: MainActivityUserInterface.Delegate) {
        this.delegate = delegate
//        toolbar.title = ""
    }

    override fun showError(error: Exception) {
    }

    private fun initToolbar() {
//        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setIcon(R.mipmap.ic_launcher)
    }
}

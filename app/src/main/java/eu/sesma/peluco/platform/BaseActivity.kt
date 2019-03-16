package eu.sesma.peluco.platform

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import eu.sesma.peluco.injection.ActivityComponent
import eu.sesma.peluco.injection.ApplicationComponent
import eu.sesma.peluco.injection.DaggerActivityComponent


open class BaseActivity : AppCompatActivity() {

    fun Activity.getRootView(): ViewGroup = (this.findViewById<ViewGroup>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup

    private val applicationComponent: ApplicationComponent
        get() = (application as AndroidApplication).applicationComponent

    lateinit var activityComponent: ActivityComponent

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        activityComponent = DaggerActivityComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(ActivityModule(this))
                .build()
    }
}

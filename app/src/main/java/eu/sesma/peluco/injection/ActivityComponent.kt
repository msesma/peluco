package eu.sesma.peluco.injection

import android.support.v7.app.AppCompatActivity
import dagger.Component
import eu.sesma.peluco.MainActivity
import eu.sesma.peluco.platform.ActivityModule

@PerActivity
@Component(dependencies = arrayOf(ApplicationComponent::class), modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(mainActivity: MainActivity)

    //Exposed to sub-graphs.
    fun activity(): AppCompatActivity
}

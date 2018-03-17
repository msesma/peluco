package eu.sesma.peluco.injection

import android.content.Context
import android.content.SharedPreferences
import dagger.Component
import eu.sesma.peluco.platform.ApplicationModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        ApplicationModule::class))
interface ApplicationComponent {

    //Exposed to sub-graphs
    fun provideContext(): Context

    fun provideSharedPreferences(): SharedPreferences

}

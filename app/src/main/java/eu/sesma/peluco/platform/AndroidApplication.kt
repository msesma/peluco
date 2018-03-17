package eu.sesma.peluco.platform

import android.app.Application
import eu.sesma.peluco.injection.ApplicationComponent
import eu.sesma.peluco.injection.DaggerApplicationComponent

class AndroidApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        this.applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }
}

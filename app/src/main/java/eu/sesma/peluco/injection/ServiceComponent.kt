package eu.sesma.peluco.injection

import android.app.Service
import eu.sesma.peluco.platform.ServiceModule
import eu.sesma.peluco.scheduler.UpdateJobService
import dagger.Component

@PerService
@Component(dependencies = arrayOf(ApplicationComponent::class), modules = arrayOf(ServiceModule::class))
interface ServiceComponent {

    fun inject(updateJobService: UpdateJobService)

    //Exposed to sub-graphs.
    fun service(): Service
}

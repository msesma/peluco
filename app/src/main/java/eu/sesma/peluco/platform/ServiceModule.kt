package eu.sesma.peluco.platform

import android.app.Service
import eu.sesma.peluco.injection.PerService
import dagger.Module
import dagger.Provides

@Module
class ServiceModule(private val service: Service) {

    @Provides
    @PerService
    internal fun service(): Service = this.service
}


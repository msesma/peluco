package eu.sesma.peluco.scheduler


import android.util.Log
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import eu.sesma.peluco.injection.ApplicationComponent
import eu.sesma.peluco.injection.DaggerServiceComponent
import eu.sesma.peluco.platform.AndroidApplication
import eu.sesma.peluco.platform.ServiceModule
import javax.inject.Inject


class UpdateJobService : JobService() {

    val TAG = UpdateJobService::class.simpleName!!

    private val applicationComponent: ApplicationComponent
        get() = (application as AndroidApplication).applicationComponent

    @Inject
    lateinit var updater: Updater

    override fun onCreate() {
        super.onCreate()
        DaggerServiceComponent.builder()
                .applicationComponent(applicationComponent)
                .serviceModule(ServiceModule(this))
                .build()
                .inject(this)

    }

    override fun onStartJob(job: JobParameters): Boolean {
        Log.d(TAG, "onStartJob")
        updater.start(this, job)
        return true
    }

    override fun onStopJob(job: JobParameters): Boolean {
        Log.d(TAG, "onStopJob")
        return false
    }

}
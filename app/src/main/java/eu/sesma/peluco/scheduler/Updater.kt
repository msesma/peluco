package eu.sesma.peluco.scheduler

import android.content.Context
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import javax.inject.Inject

class Updater @Inject constructor(
        private val context: Context
) {

    private val TAG = Updater::class.simpleName!!
    private var jobService: JobService? = null
    private var jobParameters: JobParameters? = null

    fun start(jobService: UpdateJobService?, jobParameters: JobParameters?) {
        this.jobService = jobService
        this.jobParameters = jobParameters
    }
}


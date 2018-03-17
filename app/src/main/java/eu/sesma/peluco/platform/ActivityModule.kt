package eu.sesma.peluco.platform

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import eu.sesma.peluco.injection.PerActivity
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: AppCompatActivity) {

    @Provides
    @PerActivity
    internal fun activity(): AppCompatActivity = this.activity

//    @Provides
//    @PerActivity
//    internal fun provideLinearLayoutManager(activity: AppCompatActivity) = LinearLayoutManager(activity as Context)
}

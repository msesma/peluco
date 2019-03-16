package eu.sesma.peluco.platform

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import eu.sesma.peluco.injection.PerActivity

@Module
class ActivityModule(private val activity: AppCompatActivity) {

    @Provides
    @PerActivity
    internal fun activity(): AppCompatActivity = this.activity

//    @Provides
//    @PerActivity
//    internal fun provideLinearLayoutManager(activity: AppCompatActivity) = LinearLayoutManager(activity as Context)
}

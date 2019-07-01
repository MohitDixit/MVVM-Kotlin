package com.example.mycode_lm_ng

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import com.example.mycode_lm_ng.di.DaggerAppComponent
import javax.inject.Inject
import kotlin.text.Typography.dagger

class AndroidApp: Application(), HasActivityInjector {

    @Inject lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity>? = activityDispatchingAndroidInjector
}
package com.sushantkhadka.musicplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point for Hilt. This class must be registered in
 * AndroidManifest.xml via android:name=".MusicApplication" — without
 * that manifest registration, @AndroidEntryPoint on MainActivity (or
 * any other Android class) will fail at runtime with an error like
 * "Hilt Activity must be attached to an @HiltAndroidApp Application."
 */
@HiltAndroidApp
class MusicApplication : Application()
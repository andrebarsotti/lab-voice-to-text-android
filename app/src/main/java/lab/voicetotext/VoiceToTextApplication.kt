package lab.voicetotext

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import lab.voicetotext.data.AppContainer
import lab.voicetotext.data.AppContainerImpl

class VoiceToTextApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}

//Extensão utilizada para a injeção de dependência nas ViewModels
fun CreationExtras.application(): VoiceToTextApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VoiceToTextApplication)
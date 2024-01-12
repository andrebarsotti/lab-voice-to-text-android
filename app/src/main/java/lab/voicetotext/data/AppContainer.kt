package lab.voicetotext.data

import android.content.Context

interface AppContainer {
    val voiceToTextParser: VoiceToTextParser
}

class AppContainerImpl(private val context: Context) : AppContainer {
    override val voiceToTextParser: VoiceToTextParser by lazy {
        VoiceToTextParser(context)
    }
}
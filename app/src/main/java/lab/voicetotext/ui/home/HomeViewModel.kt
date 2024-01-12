package lab.voicetotext.ui.home

import androidx.lifecycle.ViewModel
import lab.voicetotext.data.VoiceToTextParser

class HomeViewModel(private val voiceToText: VoiceToTextParser): ViewModel() {
    val voiceState = voiceToText.state

    fun startListening(languageCode: String = "pt-BR") = voiceToText.startListening(languageCode)

    fun stopListening() = voiceToText.stopListening()
}
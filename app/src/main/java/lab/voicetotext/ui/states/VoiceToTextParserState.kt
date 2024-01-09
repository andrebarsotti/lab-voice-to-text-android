package lab.voicetotext.ui.states

data class VoiceToTextParserState(
    val isSpeaking: Boolean = false,
    val spokenText: String = "",
    val error: String? = null
)
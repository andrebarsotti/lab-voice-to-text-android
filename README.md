# Lab Voice To Text no Android

No Android existe a API nativa do pacote android.speech é uma biblioteca nativa que fornece os elementos básicos para executar a tarefa de reconhecimento de fala, também conhecida como ASR (*Automatic Speech Recognition*) ou STT (*speech-to-text*). Esse é um método convencional de tratamento de fala e portanto, em geral, apenas o conjunto das palavras é obtido como resultado, isto é, *não* são identificadas pontuações de forma automática.

Esse laboratório explora a utilização dessa biblioteca nativa para apresentar os textos que são falados pelo usuário ao clicar em um botão.

## Requisitos

Esse aplicativo foi desenvolvido no seguinte ambiente:

- Android Studio Hedgehog | 2023.1.1 Patch 1 (Build #AI-231.9392.1.2311.11255304, built on December 26, 2023)
- OpenJDK 17
- Android SDK 34
- Gradle 8.2.0

## Sobre a aplicação e sua arquitetura

O aplicativo foi construído seguindo a arquitetura sugerida pelo The Android Open Source Project (2023) e utiliza *Jetpack Compose* para construção das interfaces. O core foi construído seguindo basicamente os passos descritos por Atitienei (2023) em seu artigo do Medium, com ajustes para a arquitetura e alguns pontos relevantes descritos no artigo de Khare (2021).

A classe [VoicetoTextParser.kt](app/src/main/java/lab/voicetotext/data/VoiceToTextParser.kt) contém o core da aplicação, com a implementação do processo de reconhecimento de voz e seu envio para a UI utilizando *StateFlow*.

## Bibliotecas e permissões

Para incluir o STT (*speech-to-text*) em uma aplicação Android nenhuma biblioteca externa é necessária, porém é preciso que a aplicação tenha permissões para "gravar" audios. Dessa forma é preciso incluir a linha abaixo no manifesto da aplicação:

``` XML
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

De forma complementar também será necessário solicitar a permissão de audio em algum momento o que pode ser feito da seguinte forma:

```Kotlin
ActivityCompat.requestPermissions(
    context as Activity,
    arrayOf(Manifest.permission.RECORD_AUDIO),
    123
)
```

Um exemplo completo da implementação do código necessário pode ser encontrado no arquivo [setupRecordPermissions.kt](app\src\main\java\lab\voicetotext\utils\setupRecordPermissions.kt)

## Reconhecendo a fala do usuário e convertendo em texto

Existem dois elementos básicos necessários para utilizar a função de texto para fala da biblioteca nativa do android:

- **SpeechRecognizer**: Essa é a classe responsável por efetivamente executar a função de reconhecimento de voz.
- **RecognitionListener**: Essa é uma interface onde os retornos das chamadas do reconhecimento de voz podem ser manipulados através de sua implementação em um objeto concreto.

O início do processo acontece quando uma instância do *SpeechRecognizer* é criada a partir do método estático *factory* da própria classe como no exemplo abaixo:

```Kotlin
import android.speech.SpeechRecognizer

val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
```

Nesse caso o contexto pode ser tanto a aplicação quanto a *Activity*, no nosso app de exemplo utilizamos como contexto a própria aplicação.

Com o *recognizer* criado vamos agora setar o *listener*, que será responsável por intermediar o que será reconhecido e tratar o texto resultante. Para setar utilize o método *setRecognitionListener* como no exemplo abaixo:

```Kotlin
recognizer.setRecognitionListener(listener)
```

Agora será preciso definir uma *Intent* que irá iniciar nossa aplicação, esse intent passa para o *SpeechRecognizer* vários parâmetros que desejamos. Abaixo um exemplo:

```Kotlin
import android.speech.RecognizerIntent

val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
    putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    )
    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
}
```

Nessa *Intent* informamos que nossa ação será de reconhecimento de voz (ACTION_RECOGNIZE_SPEECH), o extra EXTRA_LANGUAGE_MODEL é obrigatório pois precisamos definir o modelo de reconhecimento de falar que será utilizado, no nosso caso utilizamos o LANGUAGE_MODEL_FREE_FORM que indica um modelo de fala livre. Já o extra EXTRA_PARTIAL_RESULTS setado como *true* nos permite receber parciais do texto durante o reconhecimento da fala e o EXTRA_LANGUAGE com o valor *pt-BR* garante que haverá reconhecimento de voz para o português brasileiro. Mais detalhes sobre os parâmetros pode ser obtido na [documentação do Intent](https://developer.android.com/reference/android/speech/RecognizerIntent#ACTION_RECOGNIZE_SPEECH).

Com esses passos feitos podemos iniciar o reconhecimento de voz chamando o método *startListening* passando nossa *Intent* como parâmetro:

```Kotlin
recognizer.startListening(intent)
```

Para interromper basta chamar o método *stopListening*. Abaixo o exemplo:

```Kotlin
recognizer.stopListening()
```

Como mencionado anteriormente os texto gerado pelo *SpeechRecognizer* é tratado em uma instância concreta da interface *RecognitionListener*, pois é nela que estão os eventos. Essa interface obriga a implementação dos seguintes métodos:

- **onReadyForSpeech**: É chamado quando o *SpeechRecognizer* foi corretamente inicializado e esta pronto para processar a voz do usuário. Pode ser utilizado para indicar na interface com o usuário que ele pode começar a falar.
- **onBeginningOfSpeech**: É chamado após o *SpeechRecognizer* identificar que o usuário começou a falar. Essa informação pode ser utilizada para indicar graficamente na interface que o usuário começou a falar.
- **onRmsChanged**: Esse método recebe como um *float* (rmsdB) que indica uma métrica da variação do volume da fala durante o reconhecimento da voz. Pode ser utilizado para indicar graficamente na interface que a voz esta sendo reconhecida.
- **onEndOfSpeech**: É chamado após o *SpeechRecognizer* identificar que o usuário terminou de falar. Essa informação pode ser utilizada para indicar graficamente na interface que o usuário terminou a falar.
- **onResults** e **onPartialResults**: recebe um *Bundle* como parâmetro com o resultado do processamento total (*onResults*) ou parcial (*onPartialResults*), antes do usuário terminar de falar. É através desses métodos que o texto do que foi falados é gerado.
- **onError**: Quando acontece algum erro no processamento da fala (fala irreconhecida, por exemplo) esse método será chamado e ele recebe um inteiro com o código do erro correspondente.
- **onBufferReceived**: Recebe um *buffer* como um array de *bytes* que pode ser utilizado para salvar o audio gravado se for desejado.
- **onEvent**: Esse método esta reservado para uso futuro e no momento *não* é chamado.

Mais detalhes sobre cada um dos *callbacks* pode ser encontrada na [documentação](https://developer.android.com/reference/android/speech/RecognitionListener). Podemos verificar que os métodos mais relevantes são o *onResults* e o *onError*, pois contemplam o texto transcrito do que foi falado ou o erro, caso aconteça algum problema. Abaixo um exemplo de implementação simples.

```Kotlin
import android.speech.RecognitionListener
import android.widget.Toast

recognizer.setRecognitionListener(object:RecognitionListener {
    override fun onReadyForSpeech(params: Bundle?) = Unit

    override fun onBeginningOfSpeech() = Unit

    override fun onRmsChanged(rmsdB: Float) = Unit

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    override fun onEndOfSpeech() = Unit

    override fun onError(error: Int) {
        val msgError = when (error) {
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech input"
            SpeechRecognizer.ERROR_NO_MATCH -> "No recognition result matched."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            else -> "Didn't understand, please try again."
        }
        
        Toast.makeText(context,"Error: $msgError", Toast.LENGTH_SHORT).show()
    }

    override fun onResults(results: Bundle?) {
        results
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.getOrNull(0)
            ?.let { text ->
                Toast.makeText(context,"Text: $text", Toast.LENGTH_LONG).show()
            }
    }

    override fun onPartialResults(partialResults: Bundle?) = Unit

    override fun onEvent(eventType: Int, params: Bundle?) = Unit
})
```

## Referências

**ANDROID API REFERENCE**: android.speech. [S. l.], [s. d.]. Disponível em: https://developer.android.com/reference/android/speech/package-summary. Acesso em: 12 jan. 2024.

ATITIENEI, D. **Voice to Text in Jetpack Compose — Android**. *In*: MEDIUM. 10 mar. 2023. Disponível em: <https://medium.com/@daniel.atitienei/voice-to-text-in-jetpack-compose-android-c1e077627abe>. Acesso em: 9 jan. 2024.

KHARE, A. **Add Voice Commands to Android Apps**. *In*: MEDIUM. GEEK CULTURE. 6 jul. 2021. Disponível em: <https://medium.com/geekculture/add-voice-commands-to-android-apps-80157c0d5bcc>. Acesso em: 9 jan. 2024.

THE ANDROID OPEN SOURCE PROJECT. **GUIDE TO APP ARCHITECTURE**. *Android for Developers, 2023*. Disponível em: <https://developer.android.com/topic/architecture#modern-app-architecture> Acessado em: 29/11/2023.

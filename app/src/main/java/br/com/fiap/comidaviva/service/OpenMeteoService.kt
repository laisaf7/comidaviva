package br.com.fiap.comidaviva.service

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Integração do ComidaViva com o serviço público Open-Meteo.
 *
 * ENDEREÇO DO SERVIÇO CONSUMIDO:
 *   https://api.open-meteo.com/v1/forecast
 *
 * Por que este serviço: alimentos excedentes têm uma janela de segurança que
 * encurta conforme a temperatura ambiente sobe. Consultando a previsão real do
 * local da coleta, o app deixa de exibir um prazo fixo e passa a recomendar uma
 * janela de coleta calculada a partir de dado externo — que é o "insight" que o
 * pilar Ambiental do ESG pede.
 *
 * IMPLEMENTAÇÃO SEM BIBLIOTECAS EXTERNAS:
 *   - HttpURLConnection  -> nativo do Android SDK (java.net)
 *   - JSONObject         -> nativo do Android SDK (org.json)
 *   - SharedPreferences  -> conteúdo da fase, usado aqui como cache offline
 * Nenhuma dependência nova foi adicionada ao Gradle por causa desta integração.
 */
object OpenMeteoService {

    /** Endereço base do serviço, exibido também na documentação de entrega. */
    const val ENDERECO_SERVICO = "https://api.open-meteo.com/v1/forecast"

    /** Coordenadas do ponto de coleta: Av. Paulista, 1374 — São Paulo/SP. */
    const val LATITUDE_COLETA = -23.5613
    const val LONGITUDE_COLETA = -46.6565

    private const val ARQUIVO_PREFERENCIAS = "comidaviva_previsao"
    private const val CHAVE_RESPOSTA = "ultima_resposta_json"
    private const val TEMPO_LIMITE_MS = 10_000
    private const val QUANTIDADE_HORAS_EXIBIDAS = 6

    /**
     * Consulta a previsão do tempo do ponto de coleta.
     *
     * Executa em [Dispatchers.IO] porque o Android proíbe acesso de rede na thread
     * principal. Se a requisição falhar (sem internet, por exemplo), tenta devolver
     * a última resposta salva em SharedPreferences para que a tela continue útil.
     */
    suspend fun carregarPrevisao(
        context: Context,
        latitude: Double = LATITUDE_COLETA,
        longitude: Double = LONGITUDE_COLETA
    ): ResultadoPrevisao = withContext(Dispatchers.IO) {
        try {
            val respostaJson = requisitar(montarUrl(latitude, longitude))
            salvarNoCache(context, respostaJson)
            ResultadoPrevisao.Sucesso(converter(respostaJson), doCache = false)
        } catch (erro: Exception) {
            val respostaSalva = lerDoCache(context)
            if (respostaSalva != null) {
                try {
                    ResultadoPrevisao.Sucesso(converter(respostaSalva), doCache = true)
                } catch (_: Exception) {
                    ResultadoPrevisao.Erro(descreverErro(erro))
                }
            } else {
                ResultadoPrevisao.Erro(descreverErro(erro))
            }
        }
    }

    // ------------------------------------------------------------------
    // Montagem da requisição
    // ------------------------------------------------------------------

    /**
     * Monta a URL com os parâmetros que o Open-Meteo espera:
     *   current  -> condição do momento (temperatura e umidade)
     *   hourly   -> série horária, usada na faixa de previsão da tela
     *   daily    -> máxima do dia, usada no cálculo do risco térmico
     *   timezone -> garante horários já convertidos para o fuso de São Paulo
     */
    private fun montarUrl(latitude: Double, longitude: Double): String =
        "$ENDERECO_SERVICO" +
            "?latitude=$latitude" +
            "&longitude=$longitude" +
            "&current=temperature_2m,relative_humidity_2m" +
            "&hourly=temperature_2m" +
            "&daily=temperature_2m_max" +
            "&timezone=America%2FSao_Paulo" +
            "&forecast_days=1"

    /** Abre a conexão HTTPS, lê o corpo da resposta e devolve o JSON como texto. */
    private fun requisitar(endereco: String): String {
        val conexao = (URL(endereco).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = TEMPO_LIMITE_MS
            readTimeout = TEMPO_LIMITE_MS
            setRequestProperty("Accept", "application/json")
        }

        try {
            if (conexao.responseCode != HttpURLConnection.HTTP_OK) {
                throw IllegalStateException("HTTP ${conexao.responseCode}")
            }
            return conexao.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conexao.disconnect()
        }
    }

    // ------------------------------------------------------------------
    // Conversão do JSON para o modelo do app
    // ------------------------------------------------------------------

    /** Traduz o JSON bruto do Open-Meteo para o modelo [PrevisaoTempo]. */
    private fun converter(respostaJson: String): PrevisaoTempo {
        val raiz = JSONObject(respostaJson)

        val atual = raiz.getJSONObject("current")
        val instanteAtual = atual.getString("time")

        val horario = raiz.getJSONObject("hourly")
        val horas = horario.getJSONArray("time")
        val temperaturas = horario.getJSONArray("temperature_2m")

        // Localiza a primeira hora igual ou posterior ao instante atual.
        // Datas em ISO-8601 podem ser comparadas como texto.
        var inicio = (0 until horas.length()).firstOrNull { horas.getString(it) >= instanteAtual } ?: 0

        val proximasHoras = mutableListOf<HoraPrevisao>()
        while (inicio < horas.length() && proximasHoras.size < QUANTIDADE_HORAS_EXIBIDAS) {
            proximasHoras += HoraPrevisao(
                hora = extrairHora(horas.getString(inicio)),
                temperatura = temperaturas.getDouble(inicio)
            )
            inicio++
        }

        val maximaDoDia = raiz.getJSONObject("daily")
            .getJSONArray("temperature_2m_max")
            .getDouble(0)

        return PrevisaoTempo(
            temperaturaAtual = atual.getDouble("temperature_2m"),
            umidadeAtual = atual.getInt("relative_humidity_2m"),
            temperaturaMaxima = maximaDoDia,
            horaLeitura = extrairHora(instanteAtual),
            proximasHoras = proximasHoras
        )
    }

    /** Converte "2026-08-30T16:00" em "16:00". */
    private fun extrairHora(instanteIso: String): String =
        instanteIso.substringAfter("T").take(5)

    // ------------------------------------------------------------------
    // Cache local em SharedPreferences
    // ------------------------------------------------------------------

    private fun preferencias(context: Context) =
        context.getSharedPreferences(ARQUIVO_PREFERENCIAS, Context.MODE_PRIVATE)

    /** Guarda a última resposta bem-sucedida para uso offline. */
    private fun salvarNoCache(context: Context, respostaJson: String) {
        preferencias(context)
            .edit()
            .putString(CHAVE_RESPOSTA, respostaJson)
            .apply()
    }

    /** Recupera a última resposta salva, ou null se nunca houve uma. */
    private fun lerDoCache(context: Context): String? =
        preferencias(context).getString(CHAVE_RESPOSTA, null)

    // ------------------------------------------------------------------

    /** Traduz a exceção técnica em uma mensagem legível para o usuário. */
    private fun descreverErro(erro: Exception): String = when (erro) {
        is java.net.UnknownHostException ->
            "Sem conexão com a internet."
        is java.net.SocketTimeoutException ->
            "O serviço de previsão demorou para responder."
        else ->
            "Não foi possível consultar a previsão do tempo."
    }
}

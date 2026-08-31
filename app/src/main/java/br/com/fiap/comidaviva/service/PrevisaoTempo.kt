package br.com.fiap.comidaviva.service

/**
 * Modelos de dados da integração com o serviço Open-Meteo.
 *
 * Serviço consumido: https://api.open-meteo.com/v1/forecast
 * API pública e gratuita, sem necessidade de chave de acesso ou back-end próprio.
 */

/** Uma leitura horária de temperatura devolvida pelo serviço. */
data class HoraPrevisao(
    val hora: String,          // "16:00"
    val temperatura: Double    // 27.4
)

/** Previsão do tempo consolidada para o ponto de coleta. */
data class PrevisaoTempo(
    val temperaturaAtual: Double,
    val umidadeAtual: Int,
    val temperaturaMaxima: Double,
    val horaLeitura: String,
    val proximasHoras: List<HoraPrevisao>
)

/**
 * Faixa térmica da leitura, usada apenas para a cor do indicador.
 *
 * É diferente de [RiscoTermico]: esta descreve a sensação do momento (frio,
 * ameno, quente) a partir da temperatura atual, enquanto o risco é calculado
 * sobre a máxima do dia para definir a janela de coleta.
 */
enum class FaixaTemperatura(val rotulo: String) {
    FRIO("Frio"),
    AMENO("Ameno"),
    QUENTE("Quente");

    companion object {
        fun avaliar(temperatura: Double): FaixaTemperatura = when {
            temperatura < 18.0 -> FRIO
            temperatura < 28.0 -> AMENO
            else -> QUENTE
        }
    }
}

/**
 * Classificação do risco térmico da coleta.
 *
 * Regra de negócio do ComidaViva: quanto maior a temperatura ambiente, menor é a
 * janela em que o alimento excedente permanece seguro para consumo fora de
 * refrigeração. A previsão real do Open-Meteo alimenta essa recomendação.
 */
enum class RiscoTermico(
    val rotulo: String,
    val janelaSegura: String,
    val recomendacao: String
) {
    BAIXO(
        rotulo = "Risco baixo",
        janelaSegura = "até 4h",
        recomendacao = "Condição favorável. A janela padrão de coleta pode ser mantida."
    ),
    MODERADO(
        rotulo = "Risco moderado",
        janelaSegura = "até 2h",
        recomendacao = "Calor moderado. Antecipe a coleta e priorize transporte em caixa térmica."
    ),
    ALTO(
        rotulo = "Risco alto",
        janelaSegura = "até 1h",
        recomendacao = "Calor elevado. Colete com urgência e mantenha refrigeração ativa na rota."
    );

    companion object {
        /** Converte a temperatura prevista em uma faixa de risco. */
        fun avaliar(temperatura: Double): RiscoTermico = when {
            temperatura < 25.0 -> BAIXO
            temperatura < 30.0 -> MODERADO
            else -> ALTO
        }
    }
}

/**
 * Estados possíveis da tela enquanto o serviço é consultado.
 * Modelado como sealed class para que a tela trate os três casos de forma explícita.
 */
sealed class ResultadoPrevisao {
    /** Requisição em andamento. */
    object Carregando : ResultadoPrevisao()

    /**
     * Previsão obtida com sucesso.
     * @param doCache true quando o dado veio do SharedPreferences (sem internet no momento).
     */
    data class Sucesso(
        val previsao: PrevisaoTempo,
        val doCache: Boolean = false
    ) : ResultadoPrevisao()

    /** Falha de rede e sem cache disponível. */
    data class Erro(val mensagem: String) : ResultadoPrevisao()
}

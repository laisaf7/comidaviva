package br.com.fiap.comidaviva.service

import kotlin.math.roundToInt

/**
 * Indicadores ESG consolidados de um mês.
 *
 * Em uma versão com back-end estes números viriam do banco. No MVP eles são a
 * base histórica que alimenta o Dashboard, o comparativo entre meses e o
 * relatório em PDF.
 */
data class RelatorioMensal(
    val mes: String,
    val mesCurto: String,
    val quilosSalvos: Int,
    val refeicoesDoadas: Int,
    val co2Evitado: Int,
    val ongsParceiras: Int,
    val lotesPublicados: Int,
    val taxaColeta: Int,
    val doacoesPorSemana: List<Int>
)

/**
 * Variação percentual de um indicador entre dois meses.
 * Devolve `null` quando não há mês anterior para comparar.
 */
fun variacaoPercentual(atual: Int, anterior: Int?): Int? {
    if (anterior == null || anterior == 0) return null
    return (((atual - anterior).toDouble() / anterior) * 100).roundToInt()
}

/**
 * Histórico de relatórios, do mês mais recente para o mais antigo.
 * O primeiro item é o mês exibido por padrão no Dashboard.
 */
val HISTORICO_IMPACTO = listOf(
    RelatorioMensal(
        mes = "Agosto 2026",
        mesCurto = "Ago",
        quilosSalvos = 847,
        refeicoesDoadas = 1694,
        co2Evitado = 423,
        ongsParceiras = 7,
        lotesPublicados = 38,
        taxaColeta = 94,
        doacoesPorSemana = listOf(180, 236, 214, 217)
    ),
    RelatorioMensal(
        mes = "Julho 2026",
        mesCurto = "Jul",
        quilosSalvos = 712,
        refeicoesDoadas = 1424,
        co2Evitado = 356,
        ongsParceiras = 6,
        lotesPublicados = 31,
        taxaColeta = 89,
        doacoesPorSemana = listOf(158, 191, 176, 187)
    ),
    RelatorioMensal(
        mes = "Junho 2026",
        mesCurto = "Jun",
        quilosSalvos = 634,
        refeicoesDoadas = 1268,
        co2Evitado = 317,
        ongsParceiras = 5,
        lotesPublicados = 27,
        taxaColeta = 86,
        doacoesPorSemana = listOf(142, 168, 155, 169)
    ),
    RelatorioMensal(
        mes = "Maio 2026",
        mesCurto = "Mai",
        quilosSalvos = 519,
        refeicoesDoadas = 1038,
        co2Evitado = 259,
        ongsParceiras = 4,
        lotesPublicados = 22,
        taxaColeta = 81,
        doacoesPorSemana = listOf(118, 134, 129, 138)
    )
)

/** Devolve o mês imediatamente anterior ao informado, ou null se for o mais antigo. */
fun mesAnteriorA(relatorio: RelatorioMensal): RelatorioMensal? {
    val posicao = HISTORICO_IMPACTO.indexOf(relatorio)
    return HISTORICO_IMPACTO.getOrNull(posicao + 1)
}

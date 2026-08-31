package br.com.fiap.comidaviva.service

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Geração do relatório ESG em PDF.
 *
 * Usa `android.graphics.pdf.PdfDocument`, que faz parte do Android SDK — nenhuma
 * biblioteca externa (iText, PdfBox) foi adicionada ao projeto. O desenho é feito
 * com `Canvas` e `Paint`, as mesmas classes por trás do Compose Canvas usado nos
 * gráficos do app.
 */
object RelatorioPdfService {

    // Dimensões de uma página A4 em pontos (72 dpi)
    private const val LARGURA_A4 = 595
    private const val ALTURA_A4 = 842
    private const val MARGEM = 48f

    // Paleta da marca, em ARGB
    private const val AMEIXA = 0xFF4C1D3D.toInt()
    private const val AMEIXA_ESCURA = 0xFF331127.toInt()
    private const val AMBAR = 0xFFF2A33C.toInt()
    private const val AMBAR_TEXTO = 0xFF9A600C.toInt()
    private const val GRAFITE = 0xFF1C1A22.toInt()
    private const val CINZA = 0xFF7A7A7A.toInt()
    private const val CREME = 0xFFFAF6EF.toInt()
    private const val BORDA = 0xFFEADBCE.toInt()
    private const val VERDE_ALTA = 0xFF2E6F9E.toInt()
    private const val VERMELHO_BAIXA = 0xFFB03D26.toInt()

    /**
     * Monta o PDF do mês informado e devolve o arquivo gravado.
     *
     * @param comparativo quando presente, adiciona a seção de variação mês a mês.
     */
    fun gerar(
        context: Context,
        relatorio: RelatorioMensal,
        comparativo: RelatorioMensal?
    ): File {
        val documento = PdfDocument()
        val pagina = documento.startPage(
            PdfDocument.PageInfo.Builder(LARGURA_A4, ALTURA_A4, 1).create()
        )

        desenharPagina(pagina.canvas, relatorio, comparativo)

        documento.finishPage(pagina)

        val pasta = File(context.cacheDir, "relatorios").apply { mkdirs() }
        val arquivo = File(pasta, "ComidaViva-${relatorio.mesCurto}-ESG.pdf")

        FileOutputStream(arquivo).use { saida -> documento.writeTo(saida) }
        documento.close()

        return arquivo
    }

    /**
     * Intent pronta para abrir ou compartilhar o PDF em outro app.
     * O arquivo é exposto via FileProvider — o Android proíbe compartilhar
     * `file://` diretamente desde a API 24.
     */
    fun intentDeCompartilhamento(context: Context, arquivo: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            arquivo
        )

        return Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Relatório ESG ComidaViva")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            },
            "Compartilhar relatório ESG"
        )
    }

    // ------------------------------------------------------------------
    // Desenho da página
    // ------------------------------------------------------------------

    private fun desenharPagina(
        canvas: Canvas,
        relatorio: RelatorioMensal,
        comparativo: RelatorioMensal?
    ) {
        val texto = Paint(Paint.ANTI_ALIAS_FLAG)
        val forma = Paint(Paint.ANTI_ALIAS_FLAG)

        canvas.drawColor(Color.WHITE)

        var y = desenharCabecalho(canvas, texto, forma, relatorio)
        y = desenharIndicadores(canvas, texto, forma, relatorio, comparativo, y)
        y = desenharDetalhamento(canvas, texto, forma, relatorio, y)

        if (comparativo != null) {
            y = desenharComparativo(canvas, texto, forma, relatorio, comparativo, y)
        }

        y = desenharOds(canvas, texto, forma, y)
        desenharRodape(canvas, texto, relatorio)
    }

    /** Faixa Ameixa com a marca e o mês de referência. */
    private fun desenharCabecalho(
        canvas: Canvas,
        texto: Paint,
        forma: Paint,
        relatorio: RelatorioMensal
    ): Float {
        forma.color = AMEIXA_ESCURA
        canvas.drawRect(0f, 0f, LARGURA_A4.toFloat(), 108f, forma)

        // Tigela e traço da marca, simplificados para o formato impresso
        forma.color = CREME
        canvas.drawRect(MARGEM, 46f, MARGEM + 26f, 49f, forma)
        canvas.drawArc(RectF(MARGEM, 50f, MARGEM + 26f, 74f), 0f, 180f, true, forma)

        forma.color = AMBAR
        forma.strokeWidth = 3f
        forma.style = Paint.Style.STROKE
        canvas.drawLine(MARGEM + 2f, 40f, MARGEM + 10f, 32f, forma)
        canvas.drawLine(MARGEM + 10f, 32f, MARGEM + 24f, 18f, forma)
        forma.style = Paint.Style.FILL

        texto.color = Color.WHITE
        texto.textSize = 22f
        texto.isFakeBoldText = true
        canvas.drawText("ComidaViva", MARGEM + 38f, 48f, texto)

        texto.color = AMBAR
        texto.textSize = 10f
        canvas.drawText("RELATÓRIO DE IMPACTO ESG", MARGEM + 38f, 64f, texto)

        texto.isFakeBoldText = false
        texto.color = 0xB3FFFFFF.toInt()
        texto.textSize = 11f
        canvas.drawText("Empresa Batel S/A · ${relatorio.mes}", MARGEM + 38f, 82f, texto)

        return 132f
    }

    /** Quatro indicadores principais em grade 2×2, com variação opcional. */
    private fun desenharIndicadores(
        canvas: Canvas,
        texto: Paint,
        forma: Paint,
        relatorio: RelatorioMensal,
        comparativo: RelatorioMensal?,
        yInicial: Float
    ): Float {
        var y = tituloSecao(canvas, texto, "INDICADORES DO MÊS", yInicial)

        val indicadores = listOf(
            Triple("kg de alimento salvo", relatorio.quilosSalvos, comparativo?.quilosSalvos),
            Triple("Refeições doadas", relatorio.refeicoesDoadas, comparativo?.refeicoesDoadas),
            Triple("kg de CO₂ evitado", relatorio.co2Evitado, comparativo?.co2Evitado),
            Triple("ONGs parceiras", relatorio.ongsParceiras, comparativo?.ongsParceiras)
        )

        val larguraCard = (LARGURA_A4 - MARGEM * 2 - 12f) / 2
        val alturaCard = 56f

        indicadores.forEachIndexed { indice, (rotulo, valor, anterior) ->
            val coluna = indice % 2
            val linha = indice / 2
            val x = MARGEM + coluna * (larguraCard + 12f)
            val topo = y + linha * (alturaCard + 8f)

            forma.color = CREME
            canvas.drawRoundRect(
                RectF(x, topo, x + larguraCard, topo + alturaCard), 8f, 8f, forma
            )

            texto.color = GRAFITE
            texto.textSize = 24f
            texto.isFakeBoldText = true
            canvas.drawText(formatarNumero(valor), x + 14f, topo + 32f, texto)

            texto.isFakeBoldText = false
            texto.color = CINZA
            texto.textSize = 10f
            canvas.drawText(rotulo, x + 14f, topo + 48f, texto)

            // Variação em relação ao mês anterior
            val variacao = variacaoPercentual(valor, anterior)
            if (variacao != null) {
                texto.color = if (variacao >= 0) VERDE_ALTA else VERMELHO_BAIXA
                texto.textSize = 11f
                texto.isFakeBoldText = true
                val sinal = if (variacao >= 0) "+" else ""
                val marcador = if (variacao >= 0) "▲" else "▼"
                canvas.drawText(
                    "$marcador $sinal$variacao%",
                    x + larguraCard - 58f,
                    topo + 32f,
                    texto
                )
                texto.isFakeBoldText = false
            }
        }

        return y + 2 * (alturaCard + 8f) + 14f
    }

    /** Operação do mês e evolução semanal. */
    private fun desenharDetalhamento(
        canvas: Canvas,
        texto: Paint,
        forma: Paint,
        relatorio: RelatorioMensal,
        yInicial: Float
    ): Float {
        var y = tituloSecao(canvas, texto, "OPERAÇÃO", yInicial)

        val linhas = listOf(
            "Lotes publicados" to "${relatorio.lotesPublicados}",
            "Taxa de coleta concluída" to "${relatorio.taxaColeta}%",
            "Média por lote" to "${relatorio.quilosSalvos / relatorio.lotesPublicados} kg",
            "Refeições por quilo" to "2,0"
        )

        linhas.forEach { (rotulo, valor) ->
            texto.color = CINZA
            texto.textSize = 11f
            canvas.drawText(rotulo, MARGEM, y, texto)

            texto.color = GRAFITE
            texto.isFakeBoldText = true
            canvas.drawText(valor, LARGURA_A4 - MARGEM - texto.measureText(valor), y, texto)
            texto.isFakeBoldText = false

            forma.color = BORDA
            canvas.drawRect(MARGEM, y + 6f, LARGURA_A4 - MARGEM, y + 6.6f, forma)

            y += 20f
        }

        y += 8f

        // Barras da evolução semanal
        texto.color = CINZA
        texto.textSize = 10f
        canvas.drawText("Distribuição semanal (kg)", MARGEM, y, texto)
        y += 12f

        val maximo = relatorio.doacoesPorSemana.maxOrNull() ?: 1
        val larguraBarra = (LARGURA_A4 - MARGEM * 2 - 3 * 12f) / 4
        val alturaMaxima = 44f

        relatorio.doacoesPorSemana.forEachIndexed { indice, valor ->
            val x = MARGEM + indice * (larguraBarra + 12f)
            val altura = (valor.toFloat() / maximo) * alturaMaxima

            forma.color = AMEIXA
            canvas.drawRoundRect(
                RectF(x, y + alturaMaxima - altura, x + larguraBarra, y + alturaMaxima),
                4f, 4f, forma
            )

            texto.color = CINZA
            texto.textSize = 9f
            val rotulo = "S${indice + 1} · $valor"
            canvas.drawText(
                rotulo,
                x + (larguraBarra - texto.measureText(rotulo)) / 2,
                y + alturaMaxima + 13f,
                texto
            )
        }

        return y + alturaMaxima + 30f
    }

    /** Tabela comparando o mês selecionado com o anterior. */
    private fun desenharComparativo(
        canvas: Canvas,
        texto: Paint,
        forma: Paint,
        relatorio: RelatorioMensal,
        comparativo: RelatorioMensal,
        yInicial: Float
    ): Float {
        var y = tituloSecao(
            canvas, texto,
            "COMPARATIVO · ${relatorio.mesCurto.uppercase()} VS ${comparativo.mesCurto.uppercase()}",
            yInicial
        )

        val colunaRotulo = MARGEM
        val colunaAtual = MARGEM + 230f
        val colunaAnterior = MARGEM + 330f
        val colunaVariacao = MARGEM + 430f

        // Cabeçalho da tabela
        texto.color = CINZA
        texto.textSize = 9f
        texto.isFakeBoldText = true
        canvas.drawText("INDICADOR", colunaRotulo, y, texto)
        canvas.drawText(relatorio.mesCurto.uppercase(), colunaAtual, y, texto)
        canvas.drawText(comparativo.mesCurto.uppercase(), colunaAnterior, y, texto)
        canvas.drawText("VARIAÇÃO", colunaVariacao, y, texto)
        texto.isFakeBoldText = false

        forma.color = AMEIXA
        canvas.drawRect(MARGEM, y + 6f, LARGURA_A4 - MARGEM, y + 7f, forma)
        y += 24f

        val linhas = listOf(
            Triple("Alimento salvo (kg)", relatorio.quilosSalvos, comparativo.quilosSalvos),
            Triple("Refeições doadas", relatorio.refeicoesDoadas, comparativo.refeicoesDoadas),
            Triple("CO₂ evitado (kg)", relatorio.co2Evitado, comparativo.co2Evitado),
            Triple("Lotes publicados", relatorio.lotesPublicados, comparativo.lotesPublicados),
            Triple("Taxa de coleta (%)", relatorio.taxaColeta, comparativo.taxaColeta),
            Triple("ONGs parceiras", relatorio.ongsParceiras, comparativo.ongsParceiras)
        )

        linhas.forEach { (rotulo, atual, anterior) ->
            texto.color = GRAFITE
            texto.textSize = 11f
            canvas.drawText(rotulo, colunaRotulo, y, texto)

            texto.isFakeBoldText = true
            canvas.drawText(formatarNumero(atual), colunaAtual, y, texto)
            texto.isFakeBoldText = false

            texto.color = CINZA
            canvas.drawText(formatarNumero(anterior), colunaAnterior, y, texto)

            val variacao = variacaoPercentual(atual, anterior)
            if (variacao != null) {
                texto.color = if (variacao >= 0) VERDE_ALTA else VERMELHO_BAIXA
                texto.isFakeBoldText = true
                val sinal = if (variacao >= 0) "+" else ""
                canvas.drawText("$sinal$variacao%", colunaVariacao, y, texto)
                texto.isFakeBoldText = false
            }

            forma.color = BORDA
            canvas.drawRect(MARGEM, y + 6f, LARGURA_A4 - MARGEM, y + 6.6f, forma)
            y += 20f
        }

        return y + 12f
    }

    /** Selos dos Objetivos de Desenvolvimento Sustentável e fórmula ambiental. */
    private fun desenharOds(
        canvas: Canvas,
        texto: Paint,
        forma: Paint,
        yInicial: Float
    ): Float {
        var y = tituloSecao(canvas, texto, "ALINHAMENTO ODS — ONU", yInicial)

        val selos = listOf(
            "ODS 2" to "Fome Zero e Agricultura Sustentável",
            "ODS 12" to "Consumo e Produção Responsáveis"
        )

        val largura = (LARGURA_A4 - MARGEM * 2 - 12f) / 2

        selos.forEachIndexed { indice, (codigo, descricao) ->
            val x = MARGEM + indice * (largura + 12f)

            forma.color = CREME
            canvas.drawRoundRect(RectF(x, y - 14f, x + largura, y + 28f), 8f, 8f, forma)

            texto.color = AMBAR_TEXTO
            texto.textSize = 13f
            texto.isFakeBoldText = true
            canvas.drawText(codigo, x + 12f, y + 2f, texto)

            texto.isFakeBoldText = false
            texto.color = CINZA
            texto.textSize = 8.5f
            canvas.drawText(descricao, x + 12f, y + 18f, texto)
        }

        y += 46f

        texto.color = GRAFITE
        texto.textSize = 10f
        canvas.drawText(
            "Pegada de carbono evitada = kg de alimento salvo × fator de emissão de metano (0,5)",
            MARGEM, y, texto
        )

        return y + 20f
    }

    private fun desenharRodape(canvas: Canvas, texto: Paint, relatorio: RelatorioMensal) {
        texto.color = CINZA
        texto.textSize = 8.5f
        texto.isFakeBoldText = false
        canvas.drawText(
            "Documento gerado pelo aplicativo ComidaViva · Dados de ${relatorio.mes}",
            MARGEM, ALTURA_A4 - 42f, texto
        )
        canvas.drawText(
            "Plataforma em conformidade com a Lei nº 14.016/2020 · Previsão do tempo: api.open-meteo.com",
            MARGEM, ALTURA_A4 - 28f, texto
        )
    }

    // ------------------------------------------------------------------

    /** Título de seção em Ameixa com filete inferior. */
    private fun tituloSecao(canvas: Canvas, texto: Paint, rotulo: String, y: Float): Float {
        texto.color = AMEIXA
        texto.textSize = 11f
        texto.isFakeBoldText = true
        canvas.drawText(rotulo, MARGEM, y, texto)
        texto.isFakeBoldText = false
        return y + 24f
    }

    /** 1694 -> "1.694" */
    private fun formatarNumero(valor: Int): String =
        valor.toString().reversed().chunked(3).joinToString(".").reversed()
}

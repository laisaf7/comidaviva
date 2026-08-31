package br.com.fiap.comidaviva.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.comidaviva.components.ComidaVivaBottomBar
import br.com.fiap.comidaviva.navigation.Destination
import br.com.fiap.comidaviva.navigation.navegarPelaBarra
import br.com.fiap.comidaviva.service.HISTORICO_IMPACTO
import br.com.fiap.comidaviva.service.RelatorioMensal
import br.com.fiap.comidaviva.service.RelatorioPdfService
import br.com.fiap.comidaviva.service.mesAnteriorA
import br.com.fiap.comidaviva.service.variacaoPercentual
import br.com.fiap.comidaviva.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DashboardImpactoScreen(navController: NavController) {
    val context = LocalContext.current
    val escopo = rememberCoroutineScope()
    val estadoSnackbar = remember { SnackbarHostState() }

    // Mês em exibição — por padrão o mais recente do histórico
    var mesSelecionado by remember { mutableStateOf(HISTORICO_IMPACTO.first()) }
    var mostrarComparativo by remember { mutableStateOf(false) }
    var mostrarSeletorMes by remember { mutableStateOf(false) }
    var exportando by remember { mutableStateOf(false) }

    val mesAnterior = mesAnteriorA(mesSelecionado)
    // Só compara quando o usuário liga o comparativo e existe mês anterior
    val comparativo = if (mostrarComparativo) mesAnterior else null

    if (mostrarSeletorMes) {
        SeletorDeMesDialog(
            selecionado = mesSelecionado,
            onSelecionar = {
                mesSelecionado = it
                mostrarSeletorMes = false
            },
            onFechar = { mostrarSeletorMes = false }
        )
    }

    Scaffold(
        containerColor = Creme,
        snackbarHost = { SnackbarHost(estadoSnackbar) },
        bottomBar = {
            ComidaVivaBottomBar(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header: Relatório ESG
            item {
                DashboardHeaderSection(relatorio = mesSelecionado)
            }

            // Filtro de mês, comparativo e exportação em PDF
            item {
                Spacer(modifier = Modifier.height(16.dp))
                BarraDeAcoesRelatorio(
                    relatorio = mesSelecionado,
                    comparativoAtivo = mostrarComparativo,
                    temMesAnterior = mesAnterior != null,
                    exportando = exportando,
                    onAbrirFiltro = { mostrarSeletorMes = true },
                    onAlternarComparativo = { mostrarComparativo = !mostrarComparativo },
                    onExportar = {
                        escopo.launch {
                            exportando = true
                            // A escrita do arquivo sai da thread principal
                            val resultado = runCatching {
                                withContext(Dispatchers.IO) {
                                    RelatorioPdfService.gerar(
                                        context = context,
                                        relatorio = mesSelecionado,
                                        comparativo = mesAnterior
                                    )
                                }
                            }
                            exportando = false

                            resultado
                                .onSuccess { arquivo ->
                                    context.startActivity(
                                        RelatorioPdfService.intentDeCompartilhamento(context, arquivo)
                                    )
                                }
                                .onFailure {
                                    estadoSnackbar.showSnackbar(
                                        "Não foi possível gerar o relatório em PDF."
                                    )
                                }
                        }
                    }
                )
            }

            // Grid com 4 KPIs Principais
            item {
                Spacer(modifier = Modifier.height(12.dp))
                KpiGridSection(relatorio = mesSelecionado, comparativo = comparativo)
            }

            // Tabela comparativa, exibida só quando o comparativo está ligado
            if (comparativo != null) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    CardComparativo(atual = mesSelecionado, anterior = comparativo)
                }
            }

            // Gráfico 1: Doações Mensais (Barras)
            item {
                Spacer(modifier = Modifier.height(12.dp))
                GraficoDoacoesMensaisCard(selecionado = mesSelecionado)
            }

            // Gráfico 2: Refeições Doadas (Linha/Onda com Gradiente)
            item {
                Spacer(modifier = Modifier.height(12.dp))
                GraficoRefeicoesDoadasCard()
            }

            // Card 1: Alinhamento ODS — ONU
            item {
                Spacer(modifier = Modifier.height(12.dp))
                CardAlinhamentoOds()
            }

            // Card 2: Cálculo de Impacto Ambiental
            item {
                Spacer(modifier = Modifier.height(12.dp))
                CardCalculoImpactoAmbiental()
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// MARK: - Subcomponentes

@Composable
fun DashboardHeaderSection(relatorio: RelatorioMensal) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AmeixaEscura,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Text(
            text = "RELATÓRIO ESG",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Dashboard de Impacto",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "${relatorio.mes} · Empresa Batel S/A",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

/** Descrição de um cartão de indicador da grade. */
private data class IndicadorKpi(
    val icone: ImageVector,
    val fundoIcone: Color,
    val corIcone: Color,
    val valor: String,
    val unidade: String?,
    val legenda: String,
    val variacao: Int?
)

/** 1694 -> "1.694" */
private fun formatarMilhar(valor: Int): String =
    valor.toString().reversed().chunked(3).joinToString(".").reversed()

/**
 * Grade de indicadores do mês selecionado.
 *
 * Quando [comparativo] é informado, cada cartão ganha a variação percentual em
 * relação ao mês anterior.
 */
@Composable
fun KpiGridSection(relatorio: RelatorioMensal, comparativo: RelatorioMensal?) {
    val indicadores = listOf(
        IndicadorKpi(
            icone = Icons.Outlined.Eco,
            fundoIcone = Color(0xFFF3EAE7),
            corIcone = Ameixa,
            valor = formatarMilhar(relatorio.quilosSalvos),
            unidade = "kg",
            legenda = "kg salvos no mês",
            variacao = variacaoPercentual(relatorio.quilosSalvos, comparativo?.quilosSalvos)
        ),
        IndicadorKpi(
            icone = Icons.Outlined.People,
            fundoIcone = Color(0xFFFAF2E8),
            corIcone = AmbarTexto,
            valor = formatarMilhar(relatorio.refeicoesDoadas),
            unidade = null,
            legenda = "Refeições doadas",
            variacao = variacaoPercentual(relatorio.refeicoesDoadas, comparativo?.refeicoesDoadas)
        ),
        IndicadorKpi(
            icone = Icons.Outlined.Bolt,
            fundoIcone = Color(0xFFF3EAE7),
            corIcone = Ameixa,
            valor = formatarMilhar(relatorio.co2Evitado),
            unidade = "kg",
            legenda = "CO₂ evitado",
            variacao = variacaoPercentual(relatorio.co2Evitado, comparativo?.co2Evitado)
        ),
        IndicadorKpi(
            icone = Icons.Outlined.EmojiEvents,
            fundoIcone = Color(0xFFFAF2E8),
            corIcone = TijoloTexto,
            valor = "${relatorio.ongsParceiras}",
            unidade = null,
            legenda = "ONGs parceiras",
            variacao = variacaoPercentual(relatorio.ongsParceiras, comparativo?.ongsParceiras)
        ),
        IndicadorKpi(
            icone = Icons.Outlined.Inventory2,
            fundoIcone = Color(0xFFF3EAE7),
            corIcone = Ameixa,
            valor = "${relatorio.lotesPublicados}",
            unidade = null,
            legenda = "Lotes publicados",
            variacao = variacaoPercentual(relatorio.lotesPublicados, comparativo?.lotesPublicados)
        ),
        IndicadorKpi(
            icone = Icons.Outlined.TaskAlt,
            fundoIcone = Color(0xFFFAF2E8),
            corIcone = AmbarTexto,
            valor = "${relatorio.taxaColeta}",
            unidade = "%",
            legenda = "Coletas concluídas",
            variacao = variacaoPercentual(relatorio.taxaColeta, comparativo?.taxaColeta)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Duas colunas por linha
        indicadores.chunked(2).forEach { linha ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                linha.forEach { indicador ->
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        icon = indicador.icone,
                        iconBg = indicador.fundoIcone,
                        iconColor = indicador.corIcone,
                        valor = indicador.valor,
                        unidade = indicador.unidade,
                        legenda = indicador.legenda,
                        variacao = indicador.variacao
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    valor: String,
    unidade: String?,
    legenda: String,
    /** Variação percentual contra o mês anterior; null quando não há comparativo. */
    variacao: Int? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (variacao != null) {
                    val subiu = variacao >= 0
                    val cor = if (subiu) TempFrio else TempQuente

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (subiu) TempFrioFundo else TempQuenteFundo)
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (subiu) Icons.Outlined.TrendingUp
                            else Icons.Outlined.TrendingDown,
                            contentDescription = null,
                            tint = cor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${if (subiu) "+" else ""}$variacao%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = cor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = valor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Grafite
                )
                if (unidade != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unidade,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = legenda,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun GraficoDoacoesMensaisCard(selecionado: RelatorioMensal) {
    // Do mais antigo para o mais recente, como um gráfico temporal espera
    val serie = HISTORICO_IMPACTO.reversed()
    val maximo = serie.maxOf { it.quilosSalvos }.toFloat()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Doações mensais",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Grafite
            )
            Text(
                text = "Kg salvos · ${serie.first().mesCurto} — ${serie.last().mesCurto} 2026",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                serie.forEach { mes ->
                    val destacado = mes.mesCurto == selecionado.mesCurto
                    // Piso de 15% para que meses fracos continuem visíveis
                    val altura = 0.15f + 0.85f * (mes.quilosSalvos / maximo)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${mes.quilosSalvos}",
                            fontSize = 10.sp,
                            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal,
                            color = if (destacado) AmbarTexto else Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Área de altura fixa para as barras: sem ela, a barra
                        // esticava a coluna e empurrava o rótulo do mês para fora
                        Box(
                            modifier = Modifier.height(110.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .fillMaxHeight(altura)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (destacado) Ambar else Ameixa)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = mes.mesCurto,
                            fontSize = 11.sp,
                            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal,
                            color = if (destacado) Grafite else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GraficoRefeicoesDoadasCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Refeições doadas",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Grafite
            )
            Text(
                text = "Acumulado Mar — Ago 2026",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                val width = size.width
                val height = size.height

                val path = Path().apply {
                    moveTo(0f, height * 0.7f)
                    cubicTo(
                        width * 0.25f, height * 0.5f,
                        width * 0.5f, height * 0.65f,
                        width * 0.75f, height * 0.2f
                    )
                    cubicTo(
                        width * 0.85f, height * 0.35f,
                        width * 0.95f, height * 0.6f,
                        width, height * 0.7f
                    )
                }

                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Ambar.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )

                drawPath(
                    path = path,
                    color = AmbarTexto,
                    style = Stroke(width = 4f)
                )
            }
        }
    }
}

// Card 1: Alinhamento ODS — ONU
@Composable
fun CardAlinhamentoOds() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Alinhamento ODS — ONU",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Grafite
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Selo ODS 2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFAF2E8))
                        .border(1.dp, Ambar.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ODS 2",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmbarTexto
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Fome Zero e Agric.\nSustentável",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Selo ODS 12
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF0ECE9))
                        .border(1.dp, Color(0xFFE0D8D4), RoundedCornerShape(14.dp))
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ODS 12",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ameixa
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Consumo e Produção\nResponsáveis",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// Card 2: Cálculo de Impacto Ambiental
@Composable
fun CardCalculoImpactoAmbiental() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2EBE3)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "CÁLCULO DE IMPACTO AMBIENTAL",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pegada de Carbono Evitada = kg de Comida Salva × Fator de Emissão de Metano",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Grafite,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Eco,
                    contentDescription = null,
                    tint = Ameixa,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Este mês: 423 kg de CO₂ evitados",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ameixa
                )
            }
        }
    }
}

// MARK: - Filtro, comparativo e exportação

@Composable
fun BarraDeAcoesRelatorio(
    relatorio: RelatorioMensal,
    comparativoAtivo: Boolean,
    temMesAnterior: Boolean,
    exportando: Boolean,
    onAbrirFiltro: () -> Unit,
    onAlternarComparativo: () -> Unit,
    onExportar: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Seletor de mês
            BotaoAcao(
                modifier = Modifier.weight(1f),
                icone = Icons.Outlined.CalendarMonth,
                texto = relatorio.mesCurto,
                ativo = false,
                onClick = onAbrirFiltro
            )

            // Comparativo com o mês anterior
            BotaoAcao(
                modifier = Modifier.weight(1f),
                icone = Icons.Outlined.CompareArrows,
                texto = "Comparar",
                ativo = comparativoAtivo,
                habilitado = temMesAnterior,
                onClick = onAlternarComparativo
            )

            // Exportação em PDF
            BotaoAcao(
                modifier = Modifier.weight(1f),
                icone = Icons.Outlined.PictureAsPdf,
                texto = if (exportando) "Gerando…" else "PDF",
                ativo = false,
                habilitado = !exportando,
                carregando = exportando,
                onClick = onExportar
            )
        }

        if (!temMesAnterior) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Não há mês anterior no histórico para comparar.",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun BotaoAcao(
    modifier: Modifier = Modifier,
    icone: ImageVector,
    texto: String,
    ativo: Boolean,
    habilitado: Boolean = true,
    carregando: Boolean = false,
    onClick: () -> Unit
) {
    val corConteudo = when {
        !habilitado -> Color.Gray.copy(alpha = 0.6f)
        ativo -> Color.White
        else -> Ameixa
    }

    Row(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (ativo) Ameixa else Color.White)
            .border(
                width = 1.dp,
                color = if (ativo) Ameixa else Color(0xFFEADBCE),
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(enabled = habilitado) { onClick() }
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (carregando) {
            CircularProgressIndicator(
                modifier = Modifier.size(15.dp),
                strokeWidth = 2.dp,
                color = corConteudo
            )
        } else {
            Icon(
                imageVector = icone,
                contentDescription = null,
                tint = corConteudo,
                modifier = Modifier.size(17.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = texto,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = corConteudo,
            maxLines = 1
        )
    }
}

/** Tabela de variação entre o mês exibido e o anterior. */
@Composable
fun CardComparativo(atual: RelatorioMensal, anterior: RelatorioMensal) {
    val linhas = listOf(
        Triple("Alimento salvo (kg)", atual.quilosSalvos, anterior.quilosSalvos),
        Triple("Refeições doadas", atual.refeicoesDoadas, anterior.refeicoesDoadas),
        Triple("CO₂ evitado (kg)", atual.co2Evitado, anterior.co2Evitado),
        Triple("Lotes publicados", atual.lotesPublicados, anterior.lotesPublicados),
        Triple("Coletas concluídas (%)", atual.taxaColeta, anterior.taxaColeta),
        Triple("ONGs parceiras", atual.ongsParceiras, anterior.ongsParceiras)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Comparativo mês a mês",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Grafite
            )
            Text(
                text = "${atual.mes} contra ${anterior.mes}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Cabeçalho das colunas
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "INDICADOR",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.weight(2.4f)
                )
                Text(
                    text = atual.mesCurto.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = anterior.mesCurto.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "VAR.",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = Ameixa.copy(alpha = 0.4f), thickness = 1.dp)

            linhas.forEach { (rotulo, valorAtual, valorAnterior) ->
                val variacao = variacaoPercentual(valorAtual, valorAnterior)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rotulo,
                        fontSize = 12.sp,
                        color = Grafite,
                        modifier = Modifier.weight(2.4f)
                    )
                    Text(
                        text = formatarMilhar(valorAtual),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Grafite,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatarMilhar(valorAnterior),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (variacao == null) "—"
                        else "${if (variacao >= 0) "+" else ""}$variacao%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            variacao == null -> Color.Gray
                            variacao >= 0 -> TempFrio
                            else -> TempQuente
                        },
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider(color = Color(0xFFEADBCE))
            }
        }
    }
}

/** Lista de meses disponíveis no histórico. */
@Composable
fun SeletorDeMesDialog(
    selecionado: RelatorioMensal,
    onSelecionar: (RelatorioMensal) -> Unit,
    onFechar: () -> Unit
) {
    Dialog(onDismissRequest = onFechar) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Creme
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "PERÍODO DO RELATÓRIO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ameixa,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Escolha o mês a ser exibido",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                HISTORICO_IMPACTO.forEach { mes ->
                    val ativo = mes.mesCurto == selecionado.mesCurto

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (ativo) Ameixa else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (ativo) Ameixa else Color(0xFFEADBCE),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelecionar(mes) }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mes.mes,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (ativo) Color.White else Grafite
                            )
                            Text(
                                text = "${formatarMilhar(mes.quilosSalvos)} kg · " +
                                    "${formatarMilhar(mes.refeicoesDoadas)} refeições",
                                fontSize = 12.sp,
                                color = if (ativo) Color.White.copy(alpha = 0.75f) else Color.Gray
                            )
                        }

                        if (ativo) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = "Selecionado",
                                tint = Ambar,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onFechar) {
                        Text(
                            text = "Fechar",
                            color = Ameixa,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DashboardImpactoScreenPreview() {
    ComidaVivaTheme {
        DashboardImpactoScreen(rememberNavController())
    }
}
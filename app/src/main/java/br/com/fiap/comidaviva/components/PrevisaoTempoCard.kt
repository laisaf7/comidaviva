package br.com.fiap.comidaviva.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.comidaviva.service.FaixaTemperatura
import br.com.fiap.comidaviva.service.OpenMeteoService
import br.com.fiap.comidaviva.service.PrevisaoTempo
import br.com.fiap.comidaviva.service.ResultadoPrevisao
import br.com.fiap.comidaviva.service.RiscoTermico
import br.com.fiap.comidaviva.ui.theme.*
import java.util.Locale

/**
 * Componentes visuais da integração com o Open-Meteo.
 *
 * O estado vive em `remember { mutableStateOf(...) }` (conteúdo da fase) e a
 * chamada de rede é disparada por um `LaunchedEffect`, que garante que a
 * requisição rode fora da thread principal e seja cancelada se a tela sair de cena.
 */

/** Formata 24.34 como "24,3". */
private fun formatar(valor: Double): String =
    String.format(Locale.forLanguageTag("pt-BR"), "%.1f", valor)

/** Cor de texto, cor de fundo e ícone de cada faixa térmica. */
private data class EstiloFaixa(
    val cor: Color,
    val fundo: Color,
    val icone: ImageVector
)

/**
 * Traduz a faixa de temperatura em cor.
 * Frio puxa para o azul, ameno para o âmbar da marca e quente para o tijolo.
 */
private fun estiloDaFaixa(faixa: FaixaTemperatura): EstiloFaixa = when (faixa) {
    FaixaTemperatura.FRIO -> EstiloFaixa(TempFrio, TempFrioFundo, Icons.Outlined.AcUnit)
    FaixaTemperatura.AMENO -> EstiloFaixa(TempAmeno, TempAmenoFundo, Icons.Outlined.Thermostat)
    FaixaTemperatura.QUENTE -> EstiloFaixa(TempQuente, TempQuenteFundo, Icons.Outlined.LocalFireDepartment)
}

/** Cores de destaque de cada faixa de risco térmico da coleta. */
private fun corDoRisco(risco: RiscoTermico): Pair<Color, Color> = when (risco) {
    RiscoTermico.BAIXO -> Ameixa to Color(0xFFF3EAE7)
    RiscoTermico.MODERADO -> AmbarTexto to TempAmenoFundo
    RiscoTermico.ALTO -> TijoloTexto to TempQuenteFundo
}

// ======================================================================
// Card completo — usado na tela de Agendamento de Coleta
// ======================================================================

/**
 * Card que consulta o Open-Meteo e converte a previsão em uma recomendação
 * operacional de janela de coleta.
 */
@Composable
fun PrevisaoTempoCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // `tentativa` funciona como gatilho: ao incrementar, o LaunchedEffect
    // roda de novo e refaz a consulta.
    var resultado by remember { mutableStateOf<ResultadoPrevisao>(ResultadoPrevisao.Carregando) }
    var tentativa by remember { mutableIntStateOf(0) }

    LaunchedEffect(tentativa) {
        resultado = ResultadoPrevisao.Carregando
        resultado = OpenMeteoService.carregarPrevisao(context)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            CabecalhoPrevisao()

            Spacer(modifier = Modifier.height(14.dp))

            when (val estado = resultado) {
                is ResultadoPrevisao.Carregando -> ConteudoCarregando()

                is ResultadoPrevisao.Erro -> ConteudoErro(
                    mensagem = estado.mensagem,
                    onTentarNovamente = { tentativa++ }
                )

                is ResultadoPrevisao.Sucesso -> ConteudoPrevisao(
                    previsao = estado.previsao,
                    doCache = estado.doCache,
                    onAtualizar = { tentativa++ }
                )
            }
        }
    }
}

@Composable
private fun CabecalhoPrevisao() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "CONDIÇÃO NO PONTO DE COLETA",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Janela segura de coleta",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Grafite
            )
        }

        // Selo indicando a origem do dado — exigência de transparência ESG
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF3EAE7))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Open-Meteo",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Ameixa
            )
        }
    }
}

@Composable
private fun ConteudoCarregando() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
            color = Ameixa
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Consultando a previsão do tempo…",
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun ConteudoErro(mensagem: String, onTentarNovamente: () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                tint = TijoloTexto,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = mensagem,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Grafite
                )
                Text(
                    text = "A janela padrão de 4h será considerada.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onTentarNovamente,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Ameixa)
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Tentar novamente", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ConteudoPrevisao(
    previsao: PrevisaoTempo,
    doCache: Boolean,
    onAtualizar: () -> Unit
) {
    // O risco usa a máxima do dia, não a temperatura atual, porque a coleta
    // pode ocorrer no horário mais quente da tarde.
    val risco = RiscoTermico.avaliar(previsao.temperaturaMaxima)
    val (corRisco, fundoRisco) = corDoRisco(risco)

    // A faixa colore a leitura do momento: frio, ameno ou quente.
    val faixa = FaixaTemperatura.avaliar(previsao.temperaturaAtual)
    val estilo = estiloDaFaixa(faixa)

    Column {
        // Linha 1: temperatura atual (colorida pela faixa) + selo de risco
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(estilo.fundo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = estilo.icone,
                        contentDescription = faixa.rotulo,
                        tint = estilo.cor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${formatar(previsao.temperaturaAtual)}°",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = estilo.cor
                        )
                        Text(
                            text = "C",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = estilo.cor.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 5.dp, start = 2.dp)
                        )
                    }
                    Text(
                        text = faixa.rotulo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = estilo.cor
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(fundoRisco)
                    .border(1.dp, corRisco.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = risco.rotulo,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = corRisco
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Linha 2: umidade, máxima e hora da leitura
        Row(verticalAlignment = Alignment.CenterVertically) {
            IndicadorSimples(
                icone = { Icon(Icons.Outlined.WaterDrop, null, tint = Color.Gray, modifier = Modifier.size(14.dp)) },
                texto = "Umidade ${previsao.umidadeAtual}%"
            )
            Spacer(modifier = Modifier.width(14.dp))
            IndicadorSimples(
                icone = { Icon(Icons.Outlined.Thermostat, null, tint = Color.Gray, modifier = Modifier.size(14.dp)) },
                texto = "Máx ${formatar(previsao.temperaturaMaxima)}°C"
            )
            Spacer(modifier = Modifier.width(14.dp))
            IndicadorSimples(
                icone = { Icon(Icons.Outlined.Schedule, null, tint = Color.Gray, modifier = Modifier.size(14.dp)) },
                texto = previsao.horaLeitura
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Faixa com as próximas horas, cada uma tingida pela própria faixa térmica
        if (previsao.proximasHoras.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                previsao.proximasHoras.forEach { hora ->
                    val estiloHora = estiloDaFaixa(FaixaTemperatura.avaliar(hora.temperatura))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = hora.hora,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(estiloHora.fundo),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${hora.temperatura.toInt()}°",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = estiloHora.cor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Recomendação derivada do dado externo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(fundoRisco)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "Recolher em ${risco.janelaSegura} após o preparo",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = corRisco
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = risco.recomendacao,
                    fontSize = 12.sp,
                    color = Grafite,
                    lineHeight = 16.sp
                )
            }
        }

        // Aviso de dado offline
        if (doCache) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CloudOff,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Última leitura salva no aparelho.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Atualizar",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ameixa,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF3EAE7))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun IndicadorSimples(icone: @Composable () -> Unit, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icone()
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = texto, fontSize = 12.sp, color = Color.Gray)
    }
}

// ======================================================================
// Faixa compacta — usada no cabeçalho da tela Home
// ======================================================================

/**
 * Faixa de largura total exibida no cabeçalho escuro da Home.
 */
@Composable
fun PrevisaoTempoResumo(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var resultado by remember { mutableStateOf<ResultadoPrevisao>(ResultadoPrevisao.Carregando) }

    LaunchedEffect(Unit) {
        resultado = OpenMeteoService.carregarPrevisao(context)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (val estado = resultado) {
            is ResultadoPrevisao.Carregando -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Ambar
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Consultando a previsão do ponto de coleta…",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            is ResultadoPrevisao.Erro -> {
                Icon(
                    imageVector = Icons.Outlined.CloudOff,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Previsão indisponível no momento",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            is ResultadoPrevisao.Sucesso -> {
                val previsao = estado.previsao
                val faixa = FaixaTemperatura.avaliar(previsao.temperaturaAtual)
                val estilo = estiloDaFaixa(faixa)
                val risco = RiscoTermico.avaliar(previsao.temperaturaMaxima)

                // Bolha colorida pela faixa térmica
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(estilo.fundo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = estilo.icone,
                        contentDescription = faixa.rotulo,
                        tint = estilo.cor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${formatar(previsao.temperaturaAtual)}°C",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(estilo.fundo)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = faixa.rotulo,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = estilo.cor
                            )
                        }
                    }
                    Text(
                        text = "Máx ${formatar(previsao.temperaturaMaxima)}°C · coleta ${risco.janelaSegura}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }

                Text(
                    text = "Open-Meteo",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ambar.copy(alpha = 0.85f)
                )
            }
        }
    }
}

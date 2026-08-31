package br.com.fiap.comidaviva.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.comidaviva.components.ComidaVivaBottomBar
import br.com.fiap.comidaviva.ui.theme.*

enum class TipoNotificacao(
    val icone: ImageVector,
    val cor: Color,
    val corFundo: Color,
    val etiqueta: String
) {
    POSTAGEM(Icons.Outlined.Upload, Ameixa, Color(0xFFF3EAE7), "Postagem"),
    ENTREGA(Icons.Outlined.CheckCircle, TempFrio, TempFrioFundo, "Entrega"),
    COLETA(Icons.Outlined.LocalShipping, AmbarTexto, TempAmenoFundo, "Coleta"),
    PRAZO(Icons.Outlined.Schedule, TijoloTexto, TempQuenteFundo, "Prazo")
}

/** Um aviso exibido na central de notificações. */
data class NotificacaoItem(
    val tipo: TipoNotificacao,
    val titulo: String,
    val descricao: String,
    val horario: String,
    /** Avisos fixados permanecem no topo e não somem da lista. */
    val fixada: Boolean = false
)

/** Avisos fixos do ciclo da doação: postagem realizada e entrega recebida. */
val NOTIFICACOES_FIXADAS = listOf(
    NotificacaoItem(
        tipo = TipoNotificacao.POSTAGEM,
        titulo = "Postagem realizada",
        descricao = "Lote #2847 · 18 kg de hortifrúti publicado e visível para 7 ONGs parceiras.",
        horario = "Hoje · 14:05",
        fixada = true
    ),
    NotificacaoItem(
        tipo = TipoNotificacao.ENTREGA,
        titulo = "Entrega recebida",
        descricao = "ONG Prato Cheio confirmou o recebimento do lote #2841 · 28 kg · 56 refeições.",
        horario = "Hoje · 11:32",
        fixada = true
    )
)

/** Demais avisos, em ordem cronológica. */
val NOTIFICACOES_RECENTES = listOf(
    NotificacaoItem(
        tipo = TipoNotificacao.COLETA,
        titulo = "Coleta a caminho",
        descricao = "O veículo da ONG Prato Cheio saiu para retirar o lote #2847.",
        horario = "Hoje · 15:10"
    ),
    NotificacaoItem(
        tipo = TipoNotificacao.PRAZO,
        titulo = "Prazo encurtado pelo calor",
        descricao = "Máxima de 29,4 °C prevista. Janela de coleta reduzida para 2 h.",
        horario = "Hoje · 09:48"
    ),
    NotificacaoItem(
        tipo = TipoNotificacao.ENTREGA,
        titulo = "Entrega recebida",
        descricao = "Cozinha Solidária Vila Maria confirmou o lote #2836 · 12 kg.",
        horario = "Ontem · 17:20"
    ),
    NotificacaoItem(
        tipo = TipoNotificacao.POSTAGEM,
        titulo = "Postagem realizada",
        descricao = "Lote #2836 · 12 kg de pães e lanches publicado.",
        horario = "Ontem · 15:02"
    )
)

@Composable
fun NotificacoesScreen(navController: NavController) {
    Scaffold(
        containerColor = Creme,
        bottomBar = { ComidaVivaBottomBar(navController = navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { NotificacoesHeader(navController = navController) }

            // Bloco fixo: os dois marcos do ciclo da doação
            item {
                Spacer(modifier = Modifier.height(16.dp))
                TituloSecaoNotificacao(
                    texto = "FIXADAS",
                    icone = Icons.Outlined.PushPin
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(NOTIFICACOES_FIXADAS) { aviso ->
                CardNotificacao(aviso = aviso)
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                TituloSecaoNotificacao(texto = "RECENTES")
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(NOTIFICACOES_RECENTES) { aviso ->
                CardNotificacao(aviso = aviso)
                Spacer(modifier = Modifier.height(10.dp))
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// MARK: - Subcomponentes

@Composable
private fun NotificacoesHeader(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AmeixaEscura,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "CENTRAL DE AVISOS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Notificações",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TituloSecaoNotificacao(texto: String, icone: ImageVector? = null) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icone != null) {
            Icon(
                imageVector = icone,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = texto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun CardNotificacao(aviso: NotificacaoItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .then(
                // Avisos fixados ganham contorno para se destacarem da lista
                if (aviso.fixada) {
                    Modifier.border(
                        width = 1.dp,
                        color = aviso.tipo.cor.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (aviso.fixada) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(aviso.tipo.corFundo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = aviso.tipo.icone,
                    contentDescription = null,
                    tint = aviso.tipo.cor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = aviso.titulo,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Grafite,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (aviso.fixada) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Outlined.PushPin,
                            contentDescription = "Fixada",
                            tint = aviso.tipo.cor,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = aviso.descricao,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(aviso.tipo.corFundo)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = aviso.tipo.etiqueta,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = aviso.tipo.cor
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = aviso.horario,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun NotificacoesScreenPreview() {
    ComidaVivaTheme {
        NotificacoesScreen(rememberNavController())
    }
}

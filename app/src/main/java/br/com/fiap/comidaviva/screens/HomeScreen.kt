package br.com.fiap.comidaviva.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import br.com.fiap.comidaviva.components.PrevisaoTempoResumo
import br.com.fiap.comidaviva.components.AssinaturaComidaViva
import br.com.fiap.comidaviva.components.ComidaVivaBottomBar
import br.com.fiap.comidaviva.navigation.Destination
import br.com.fiap.comidaviva.navigation.navegarPelaBarra
import br.com.fiap.comidaviva.ui.theme.*

// Data Model para as Doações
data class DoacaoItem(
    val id: String,
    val categoria: String,
    val emColeta: Boolean = false,
    val tempoRestante: String,
    val titulo: String,
    val quantidade: String,
    val horarioRetirada: String,
    val temperatura: String
)
@Composable
fun HomeScreen(navController: NavController) {
    // Dados de exemplo
    val doacoes = listOf(
        DoacaoItem(
            id = "1",
            categoria = "Refeição Pronta",
            tempoRestante = "47min",
            titulo = "Arroz, feijão, frango grelhado",
            quantidade = "28 kg · 56 porções",
            horarioRetirada = "Retirada até 16:30",
            temperatura = "Quente"
        ),
        DoacaoItem(
            id = "2",
            categoria = "Hortifrúti",
            tempoRestante = "97min",
            titulo = "Tomate, alface, cenoura",
            quantidade = "12 kg",
            horarioRetirada = "Retirada até 18:00",
            temperatura = "Resfriado"
        ),
        DoacaoItem(
            id = "3",
            categoria = "Pães & Lanches",
            emColeta = true,
            tempoRestante = "67min",
            titulo = "Pão francês, sanduíches naturais",
            quantidade = "6 kg · 40 unidades",
            horarioRetirada = "Retirada até 17:00",
            temperatura = "Ambiente"
        )
    )

    Scaffold(
        containerColor = Creme,
        bottomBar = {
            ComidaVivaBottomBar(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header roxo com logotipo, notificação e KPIs
            item {
                HeaderSection(navController = navController)
            }

            // Conteúdo principal em fundo creme
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Botão "Registrar Novo Lote"
                RegistrarNovoLoteBanner(
                    onClick = { navController.navigate(Destination.FormularioExcedente.route) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Título "Doações Ativas" e "Ver todas"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Doações Ativas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Grafite
                    )
                    // Leva ao histórico de lotes já retirados e confirmados
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                navController.navigate(Destination.DoacoesEntregues.route)
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Ver entregues",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ameixa
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Ameixa,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Cards de Doações Ativas
            items(doacoes) { doacao ->
                DoacaoCardItem(doacao = doacao)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// MARK: - Subcomponentes

@Composable
fun HeaderSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AmeixaEscura,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Data e Ícone de Notificação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "QUINTA · 07 AGO 2026",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                // Acesso às telas de conta (abertura, login e cadastro).
                // O app inicia direto na Home, então este é o caminho para elas.
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable { navController.navigate(Destination.Initial.route) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Conta",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Ícone do Sininho com Badge — abre a central de notificações
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable { navController.navigate(Destination.Notificacoes.route) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificações",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    // Badge de notificação com o número 3
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(TijoloTexto),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "3",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        AssinaturaComidaViva(
            claro = true,
            tamanhoLogo = 30.dp,
            tamanhoTexto = 24.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        PrevisaoTempoResumo()

        Spacer(modifier = Modifier.height(14.dp))

        // Três Cards KPI do Topo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Inventory2,
                valor = "3",
                rotulo = "Lotes hoje"
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Eco,
                valor = "847",
                rotulo = "kg salvos/mês"
            )
            KpiCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.People,
                valor = "1.694",
                rotulo = "Refeições/mês"
            )
        }
    }
}

@Composable
fun KpiCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    valor: String,
    rotulo: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Ambar,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = valor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = rotulo,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun RegistrarNovoLoteBanner(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Ambar),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Grafite
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Registrar Novo Lote",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Grafite
                    )
                    Text(
                        text = "Alimentos disponíveis para doação",
                        fontSize = 12.sp,
                        color = Grafite.copy(alpha = 0.8f)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Grafite
            )
        }
    }
}

@Composable
fun DoacaoCardItem(doacao: DoacaoItem) {
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
            // Tags e Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Tag Categoria
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF0EAE1))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = doacao.categoria,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ameixa
                        )
                    }

                    // Tag Opcional: "Em coleta"
                    if (doacao.emColeta) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Ambar.copy(alpha = 0.25f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Em coleta",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmbarTexto
                            )
                        }
                    }
                }

                // Chip de Tempo Restante
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(TijoloTexto.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null,
                            tint = TijoloTexto,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = doacao.tempoRestante,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TijoloTexto
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Título e Quantidade
            Text(
                text = doacao.titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Grafite
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = doacao.quantidade,
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(10.dp))

            // Rodapé com Horário e Temperatura
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = doacao.horarioRetirada,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Thermostat,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = doacao.temperatura,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    ComidaVivaTheme() {
        HomeScreen(rememberNavController())
    }
}
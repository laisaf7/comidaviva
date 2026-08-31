package br.com.fiap.comidaviva.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.comidaviva.components.PrevisaoTempoCard
import br.com.fiap.comidaviva.components.ComidaVivaBottomBar
import br.com.fiap.comidaviva.navigation.Destination
import br.com.fiap.comidaviva.navigation.navegarPelaBarra
import br.com.fiap.comidaviva.ui.theme.*

@Composable
fun AgendamentoColetaScreen(navController: NavController) {
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
            // Seção Superior: Header + Mapa da Rota
            item {
                MapaHeaderSection()
            }

            // Previsão do tempo consumida do serviço Open-Meteo.
            // Converte a temperatura prevista na janela segura de coleta.
            item {
                Spacer(modifier = Modifier.height(16.dp))
                PrevisaoTempoCard()
            }

            // Cards de Informações e Rota
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Card 1: Ponto de Origem (Empresa/Refeitório)
                PontoColetaCard(
                    icon = Icons.Outlined.Inventory2,
                    titulo = "Refeitório Corporativo Batel",
                    endereco = "Av. Paulista, 1374 · Portaria B",
                    info1 = "Retirada até 16:30",
                    info1Icon = Icons.Outlined.Schedule,
                    info2 = "2,4 km da ONG",
                    info2Icon = Icons.Outlined.NearMe
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card 2: Ponto de Destino (ONG Receptora)
                PontoColetaCard(
                    icon = Icons.Outlined.People,
                    titulo = "ONG Prato Cheio",
                    endereco = "R. Consolação, 987 · Vila Buarque",
                    info1 = "Capacidade: 200 refeições/dia",
                    info1Icon = Icons.Outlined.Groups,
                    info2 = null,
                    info2Icon = null
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card 3: QR Code de Entrega / Isenção
                QrCodeCardSection()

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// MARK: - Subcomponentes

@Composable
fun MapaHeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AmeixaEscura,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(top = 16.dp, bottom = 12.dp)
    ) {
        // Título "AGENDAMENTO" + "Rota de Coleta"
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "AGENDAMENTO",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rota de Coleta",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Área do Mapa Escuro Ilustrado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(AmeixaEscura)
        ) {
            // Desenho do Grid do Mapa + Linha de Rota Pontilhada
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Linhas do Grid
                val gridColor = Color.White.copy(alpha = 0.06f)
                val strokeGrid = 2f

                // Linhas Verticais
                drawLine(gridColor, Offset(width * 0.2f, 0f), Offset(width * 0.2f, height), strokeGrid)
                drawLine(gridColor, Offset(width * 0.5f, 0f), Offset(width * 0.5f, height), strokeGrid)
                drawLine(gridColor, Offset(width * 0.78f, 0f), Offset(width * 0.78f, height), strokeGrid)

                // Linhas Horizontais
                drawLine(gridColor, Offset(0f, height * 0.35f), Offset(width, height * 0.35f), strokeGrid)
                drawLine(gridColor, Offset(0f, height * 0.7f), Offset(width, height * 0.7f), strokeGrid)

                // Blocos de Quadras do Mapa
                drawRect(
                    color = Color.White.copy(alpha = 0.03f),
                    topLeft = Offset(width * 0.23f, height * 0.4f),
                    size = androidx.compose.ui.geometry.Size(width * 0.24f, height * 0.25f)
                )
                drawRect(
                    color = Color.White.copy(alpha = 0.03f),
                    topLeft = Offset(width * 0.53f, height * 0.4f),
                    size = androidx.compose.ui.geometry.Size(width * 0.22f, height * 0.25f)
                )

                // Rota Pontilhada Âmbar (Empresa -> ONG)
                val start = Offset(width * 0.25f, height * 0.55f)
                val corner = Offset(width * 0.5f, height * 0.55f)
                val turn = Offset(width * 0.5f, height * 0.28f)
                val end = Offset(width * 0.78f, height * 0.28f)

                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

                // Trecho Horizontal 1
                drawLine(Ambar, start, corner, strokeWidth = 5f, pathEffect = pathEffect)
                // Trecho Vertical
                drawLine(Ambar, corner, turn, strokeWidth = 5f, pathEffect = pathEffect)
                // Trecho Horizontal 2
                drawLine(Ambar, turn, end, strokeWidth = 5f, pathEffect = pathEffect)
            }

            // Pin Empresa (Esquerda)
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 70.dp, top = 82.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Ambar.copy(alpha = 0.25f))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Ambar)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Empresa",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ambar
                )
            }

            // Pin ONG (Direita)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 55.dp, top = 35.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ONG",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Localização no canto inferior direito
            Text(
                text = "São Paulo · SP",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 10.dp)
            )
        }
    }
}

@Composable
fun PontoColetaCard(
    icon: ImageVector,
    titulo: String,
    endereco: String,
    info1: String,
    info1Icon: ImageVector,
    info2: String?,
    info2Icon: ImageVector?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Ícone circular da categoria
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFAF2E8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AmbarTexto,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Grafite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = endereco,
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Detalhes extras no rodapé do Card
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = info1Icon,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = info1,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    if (info2 != null && info2Icon != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = info2Icon,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = info2,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QrCodeCardSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                width = 1.dp,
                color = Ameixa.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F5EF)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Ameixa.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.QrCode2,
                            contentDescription = null,
                            tint = Ameixa,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "QR Code de Entrega",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Grafite
                        )
                        Text(
                            text = "Apresentar na portaria",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Tag "Gerado ✓"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE8DFD8))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Gerado ✓",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ameixa
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFEADBCE))
            Spacer(modifier = Modifier.height(14.dp))

            // Campo de Token/Hash do QR Code
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFEDE5DC))
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CropFree,
                        contentDescription = null,
                        tint = Ameixa,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "#CV-2026-08-07-2847",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ameixa,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AgendamentoColetaScreenPreview() {
    ComidaVivaTheme() {
        AgendamentoColetaScreen(rememberNavController())
    }
}
package br.com.fiap.comidaviva.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.comidaviva.components.ComidaVivaBottomBar
import br.com.fiap.comidaviva.ui.theme.*

/** Um lote já retirado e confirmado pela ONG receptora. */
data class DoacaoEntregue(
    val lote: String,
    val categoria: String,
    val descricao: String,
    val quilos: Int,
    val refeicoes: Int,
    val ong: String,
    val data: String
)

/** Histórico de entregas confirmadas do mês corrente. */
val DOACOES_ENTREGUES = listOf(
    DoacaoEntregue("#2841", "Refeição Pronta", "Arroz, feijão, carne moída", 28, 56, "ONG Prato Cheio", "07/08 · 11:32"),
    DoacaoEntregue("#2836", "Pães & Lanches", "Pão francês, sanduíches naturais", 12, 24, "Cozinha Solidária Vila Maria", "06/08 · 17:20"),
    DoacaoEntregue("#2829", "Hortifrúti", "Tomate, alface, cenoura, batata", 21, 42, "ONG Prato Cheio", "05/08 · 16:05"),
    DoacaoEntregue("#2824", "Refeição Pronta", "Macarrão, frango desfiado", 34, 68, "Instituto Mesa Farta", "04/08 · 15:48"),
    DoacaoEntregue("#2818", "Hortifrúti", "Banana, maçã, laranja", 17, 34, "Casa de Apoio Esperança", "02/08 · 14:12"),
    DoacaoEntregue("#2811", "Pães & Lanches", "Bolos, biscoitos caseiros", 9, 18, "Cozinha Solidária Vila Maria", "01/08 · 16:40")
)

@Composable
fun DoacoesEntreguesScreen(navController: NavController) {
    // Totais calculados a partir da lista, não digitados à mão
    val totalQuilos = DOACOES_ENTREGUES.sumOf { it.quilos }
    val totalRefeicoes = DOACOES_ENTREGUES.sumOf { it.refeicoes }
    val ongsAtendidas = DOACOES_ENTREGUES.map { it.ong }.distinct().size

    Scaffold(
        containerColor = Creme,
        bottomBar = { ComidaVivaBottomBar(navController = navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                EntreguesHeader(
                    navController = navController,
                    totalQuilos = totalQuilos,
                    totalRefeicoes = totalRefeicoes,
                    ongsAtendidas = ongsAtendidas
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${DOACOES_ENTREGUES.size} entregas confirmadas em agosto",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(DOACOES_ENTREGUES) { entrega ->
                CardDoacaoEntregue(entrega = entrega)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// MARK: - Subcomponentes

@Composable
private fun EntreguesHeader(
    navController: NavController,
    totalQuilos: Int,
    totalRefeicoes: Int,
    ongsAtendidas: Int
) {
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
                    text = "HISTÓRICO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Doações Entregues",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResumoEntregaCard(
                modifier = Modifier.weight(1f),
                valor = "$totalQuilos",
                rotulo = "kg entregues"
            )
            ResumoEntregaCard(
                modifier = Modifier.weight(1f),
                valor = "$totalRefeicoes",
                rotulo = "Refeições"
            )
            ResumoEntregaCard(
                modifier = Modifier.weight(1f),
                valor = "$ongsAtendidas",
                rotulo = "ONGs atendidas"
            )
        }
    }
}

@Composable
private fun ResumoEntregaCard(
    modifier: Modifier = Modifier,
    valor: String,
    rotulo: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
private fun CardDoacaoEntregue(entrega: DoacaoEntregue) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Linha 1: categoria e selo de entrega confirmada
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF3EAE7))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = entrega.categoria,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ameixa
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = TempFrio,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Entregue",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TempFrio
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = entrega.descricao,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Grafite
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Eco,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${entrega.quilos} kg",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Outlined.People,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${entrega.refeicoes} refeições",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEADBCE))
            Spacer(modifier = Modifier.height(10.dp))

            // Rodapé: ONG receptora, lote e data
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = entrega.ong,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "${entrega.lote} · ${entrega.data}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview
@Composable
private fun DoacoesEntreguesScreenPreview() {
    ComidaVivaTheme {
        DoacoesEntreguesScreen(rememberNavController())
    }
}

package br.com.fiap.comidaviva.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.CorporateFare
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import br.com.fiap.comidaviva.components.AssinaturaComidaViva
import br.com.fiap.comidaviva.navigation.Destination
import br.com.fiap.comidaviva.ui.theme.*

@Composable
fun CadastroPasso1Screen(navController: NavController) {
    var tipoContaSelecionada by remember { mutableStateOf("Empresa") } // "Empresa" ou "ONG"

    Scaffold(
        containerColor = Creme
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Barra Superior: Voltar + Logo + Indicador de Progresso (Pill)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botão Voltar Circular
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFEADBCE), CircleShape)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Grafite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Logo "ComidaViva"
                AssinaturaComidaViva(tamanhoLogo = 22.dp, tamanhoTexto = 18.sp)

                Spacer(modifier = Modifier.weight(1f))

                // Indicador de Progresso (Barra Passo 1 de 2)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 20.dp, height = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Ameixa)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0D5CC))
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Título da Etapa
            Text(
                text = "PASSO 1 DE 2",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Ameixa,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Quem é você?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Grafite
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Selecione o tipo de conta para personalizar sua experiência",
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card Empresa / Refeitório
            TipoContaCard(
                titulo = "Empresa / Refeitório",
                subtitulo = "Doe excedentes do seu refeitório corporativo",
                icon = Icons.Outlined.CorporateFare,
                selecionado = tipoContaSelecionada == "Empresa",
                onClick = { tipoContaSelecionada = "Empresa" }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Card ONG / Cozinha Comunitária
            TipoContaCard(
                titulo = "ONG / Cozinha Comunitária",
                subtitulo = "Receba alimentos para distribuição",
                icon = Icons.Outlined.FavoriteBorder,
                selecionado = tipoContaSelecionada == "ONG",
                onClick = { tipoContaSelecionada = "ONG" }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card de Benefícios "GRATUITO PARA TODOS"
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2EBE3)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "GRATUITO PARA TODOS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )

                    BeneficioItem(texto = "Sem taxas ou mensalidade")
                    BeneficioItem(texto = "Relatórios ESG automáticos")
                    BeneficioItem(texto = "Conformidade com Lei 14.016/2020")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão "Continuar →"
            Button(
                onClick = {
                    navController.navigate(Destination.CadastroPasso2.route) {
                        popUpTo(Destination.Initial.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ameixa,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Continuar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// MARK: - Subcomponentes

@Composable
fun TipoContaCard(
    titulo: String,
    subtitulo: String,
    icon: ImageVector,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selecionado) Color(0xFFF0ECE9) else Color.White
    val borderColor = if (selecionado) Ameixa else Color(0xFFEADBCE)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE6DCD5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Ameixa,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Grafite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitulo,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Radio Button Customizado
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (selecionado) Ameixa else Color(0xFFC0B2A8), CircleShape)
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selecionado) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Ameixa)
                    )
                }
            }
        }
    }
}

@Composable
fun BeneficioItem(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.TaskAlt,
            contentDescription = null,
            tint = Grafite,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            fontSize = 12.sp,
            color = Grafite
        )
    }
}

@Preview
@Composable
private fun CadastroPasso1ScreenPreview() {
    ComidaVivaTheme {
        CadastroPasso1Screen(rememberNavController())
    }
}
package br.com.fiap.comidaviva.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.comidaviva.components.ComidaVivaBottomBar
import br.com.fiap.comidaviva.navigation.Destination
import br.com.fiap.comidaviva.navigation.navegarPelaBarra
import br.com.fiap.comidaviva.ui.theme.*

@Composable
fun DoacaoPublicadaScreen(navController: NavController) {
    Scaffold(
        containerColor = Creme,
        bottomBar = {
            ComidaVivaBottomBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Ícone de Confirmação Suave (Check)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFE8E1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Ameixa,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título e Descrição
            Text(
                text = "Doação Publicada!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Grafite
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ONGs parceiras foram notificadas. Aguarde a confirmação de coleta.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card Resumo dos Dados Cadastrados
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 18.dp, horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Tipo", fontSize = 14.sp, color = Color.Gray)
                        Text(
                            text = "Hortifrúti",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Grafite
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Quantidade", fontSize = 14.sp, color = Color.Gray)
                        Text(
                            text = "18 kg",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Grafite
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Horário limite", fontSize = 14.sp, color = Color.Gray)
                        Text(
                            text = "17:00",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Grafite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botão "Novo Registro"
            OutlinedButton(
                onClick = {
                    navController.navigate(Destination.FormularioExcedente.route) {
                        popUpTo(Destination.FormularioExcedente.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Grafite
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE5DDD5))
                )
            ) {
                Text(
                    text = "Novo Registro",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
private fun DoacaoPublicadaScreenPreview() {
    ComidaVivaTheme {
        DoacaoPublicadaScreen(rememberNavController())
    }
}
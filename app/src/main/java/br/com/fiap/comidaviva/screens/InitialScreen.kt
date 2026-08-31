package br.com.fiap.comidaviva.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.comidaviva.components.LogoComidaViva
import br.com.fiap.comidaviva.navigation.Destination
import br.com.fiap.comidaviva.ui.theme.*

@Composable
fun InitialScreen(navController: NavController) {
    Scaffold(
        containerColor = Creme
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Hero com Gradiente e Logo "Pulso"
            AberturaHeroHeader()

            Spacer(modifier = Modifier.height(20.dp))

            // Conteúdo principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Conectando quem tem comida\ncom quem precisa dela",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Grafite,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Combate ao desperdício e à fome pelo setor corporativo",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Três Pilares Informativos
                PilarCard(
                    icon = Icons.Outlined.Eco,
                    iconBg = Color(0xFFF3EAE7),
                    iconColor = Ameixa,
                    titulo = "Impacto ambiental",
                    subtitulo = "Redução de metano no aterro"
                )

                Spacer(modifier = Modifier.height(10.dp))

                PilarCard(
                    icon = Icons.Outlined.FavoriteBorder,
                    iconBg = Color(0xFFFAF2E8),
                    iconColor = AmbarTexto,
                    titulo = "Segurança alimentar",
                    subtitulo = "ONGs e cozinhas comunitárias"
                )

                Spacer(modifier = Modifier.height(10.dp))

                PilarCard(
                    icon = Icons.Outlined.CorporateFare,
                    iconBg = Color(0xFFF3EAE7),
                    iconColor = TijoloTexto,
                    titulo = "ESG corporativo",
                    subtitulo = "Relatórios prontos para B2B"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botão Primário: "Entrar na conta"
                Button(
                    onClick = {
                        navController.navigate(Destination.Login.route) {
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
                            text = "Entrar na conta",
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

                Spacer(modifier = Modifier.height(12.dp))

                // Botão Secundário: "Criar conta grátis"
                OutlinedButton(
                    onClick = {
                        navController.navigate(Destination.CadastroPasso1.route) {
                            popUpTo(Destination.Initial.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Ameixa
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Ameixa)
                    )
                ) {
                    Text(
                        text = "Criar conta grátis",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Rodapé Legal
                Text(
                    text = "Plataforma em conformidade com a Lei nº 14.016/2020",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// MARK: - Subcomponentes

@Composable
fun AberturaHeroHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AmeixaEscura, Ameixa)
                ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logotipo "Pulso" com Tigela e Órbita Vital
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width * 0.42f

                    // Órbita Externa Suave
                    drawCircle(
                        color = Color.White.copy(alpha = 0.15f),
                        radius = radius,
                        style = Stroke(width = 1.5f)
                    )

                    // Órbita Interna Ambar
                    drawCircle(
                        color = Ambar.copy(alpha = 0.6f),
                        radius = radius * 0.82f,
                        style = Stroke(width = 2f)
                    )

                    // Pontos de Conexão na Órbita
                    drawCircle(color = Ambar, radius = 4f, center = Offset(center.x, center.y - radius * 0.82f))
                    drawCircle(color = Color.White, radius = 3f, center = Offset(center.x + radius * 0.82f, center.y))
                    drawCircle(color = Ambar, radius = 3.5f, center = Offset(center.x - radius * 0.82f, center.y + 10f))
                }

                // Marca oficial no centro da órbita
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    LogoComidaViva(tamanho = 38.dp, claro = true)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Wordmark "ComidaViva"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Comida",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Viva",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ambar
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "IMPACTO QUE SE MEDE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun PilarCard(
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    titulo: String,
    subtitulo: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFEADBCE), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = titulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Grafite
                )
                Text(
                    text = subtitulo,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview
@Composable
private fun InitialScreenPreview() {
    ComidaVivaTheme {
        InitialScreen(rememberNavController())
    }
}
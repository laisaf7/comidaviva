package br.com.fiap.comidaviva.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CorporateFare
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.comidaviva.components.AssinaturaComidaViva
import br.com.fiap.comidaviva.navigation.Destination
import br.com.fiap.comidaviva.ui.theme.*

@Composable
fun CadastroPasso2Screen(navController: NavController) {
    var nomeEmpresa by remember { mutableStateOf("Batel Alimentos S/A") }
    var emailCorporativo by remember { mutableStateOf("esg@batel.com.br") }
    var senha by remember { mutableStateOf("123456789012") }
    var senhaVisivel by remember { mutableStateOf(false) }

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

            // Barra Superior: Voltar + Logo + Indicador de Progresso (Passo 2 ativo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                AssinaturaComidaViva(tamanhoLogo = 22.dp, tamanhoTexto = 18.sp)

                Spacer(modifier = Modifier.weight(1f))

                // Indicador de Progresso (Passo 2 de 2 Ativo)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0D5CC))
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 20.dp, height = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Ameixa)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "PASSO 2 DE 2",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Ameixa,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Seus dados",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Grafite
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tag da Categoria Selecionada no Passo 1
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFEBE0D8))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CorporateFare,
                        contentDescription = null,
                        tint = Ameixa,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Empresa / Refeitório",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ameixa
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo 1: Nome da empresa
            Text(
                text = "Nome da empresa",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = nomeEmpresa,
                onValueChange = { nomeEmpresa = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Ameixa,
                    unfocusedBorderColor = Color(0xFFEADBCE)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo 2: E-mail corporativo
            Text(
                text = "E-mail corporativo",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = emailCorporativo,
                onValueChange = { emailCorporativo = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Ameixa,
                    unfocusedBorderColor = Color(0xFFEADBCE)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo 3: Criar senha com Medidor de Força
            Text(
                text = "Criar senha",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Ameixa,
                    unfocusedBorderColor = Color(0xFFEADBCE)
                ),
                singleLine = true,
                visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                        Icon(
                            imageVector = if (senhaVisivel) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = "Alternar Visibilidade",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Medidor de Força da Senha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Ameixa)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Ameixa)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Ameixa)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Forte",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Grafite
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botão "Criar conta grátis"
            Button(
                onClick = {
                    navController.navigate(Destination.Home.route) {
                        // Torna a Home a unica tela da pilha ao entrar no app.
                        popUpTo(Destination.Home.route) { inclusive = true }
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
                Text(
                    text = "Criar conta grátis",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divisor com "já tem conta?"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEADBCE))
                Text(
                    text = "já tem conta?",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEADBCE))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botão "Entrar na conta"
            OutlinedButton(
                onClick = {
                    navController.navigate(Destination.Login.route) {
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
                    text = "Entrar na conta",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Rodapé com Termos e Política
            Text(
                text = "Ao criar conta, você concorda com os Termos de Uso e a Política de Privacidade",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
private fun CadastroPasso2ScreenPreview() {
    ComidaVivaTheme {
        CadastroPasso2Screen(rememberNavController())
    }
}
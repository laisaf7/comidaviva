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
import androidx.compose.material.icons.outlined.Construction
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
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
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

            // Barra Superior: Botão Voltar + Logo Centralizada
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
                Spacer(modifier = Modifier.width(40.dp)) // Balanço visual para a seta
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Textos da Marca e Boas-Vindas
            Text(
                text = "BEM-VINDO DE VOLTA",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Ameixa,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Entrar na conta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Grafite
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Acesse sua conta para continuar doando",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            AvisoSemAutenticacao()

            Spacer(modifier = Modifier.height(20.dp))

            // Campo 1: E-mail
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("E-mail", color = Color.Gray, fontSize = 14.sp) },
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

            Spacer(modifier = Modifier.height(14.dp))

            // Campo 2: Senha com Toggle de Visibilidade
            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                placeholder = { Text("Senha", color = Color.Gray, fontSize = 14.sp) },
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

            Spacer(modifier = Modifier.height(12.dp))

            // "Esqueci minha senha"
            Text(
                text = "Esqueci minha senha",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Ameixa,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { /* Ação Esqueci Senha */ }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botão "Entrar"
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
                    text = "Entrar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divisor com "ou"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEADBCE))
                Text(
                    text = "ou",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEADBCE))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botão "Criar conta grátis"
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

            Spacer(modifier = Modifier.height(28.dp))

            // Rodapé com Termos de Uso
            Text(
                text = "Ao entrar, você concorda com os Termos de Uso",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Faixa de aviso exibida nas telas de conta.
 */
@Composable
fun AvisoSemAutenticacao(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(TempAmenoFundo)
            .border(1.dp, Ambar.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Construction,
            contentDescription = null,
            tint = AmbarTexto,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Em desenvolvimento — sem autenticação",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AmbarTexto
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Toque em Entrar com os campos vazios para acessar o app.",
                fontSize = 12.sp,
                color = Grafite,
                lineHeight = 16.sp
            )
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    ComidaVivaTheme {
        LoginScreen(rememberNavController())
    }
}
package br.com.fiap.comidaviva.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.comidaviva.components.ComidaVivaBottomBar
import br.com.fiap.comidaviva.navigation.Destination
import br.com.fiap.comidaviva.navigation.navegarPelaBarra
import br.com.fiap.comidaviva.ui.theme.*

// Cores personalizadas extraídas da interface
private val DarkPurple = Color(0xFF38152E)
private val LightCream = Color(0xFFFBF8EE)
private val CardBg = Color(0xFF26101F)
private val AccentGold = Color(0xFFE5A638)
private val ItemBg = Color(0xFFF2EAE1)
private val TextDark = Color(0xFF38152E)

@Composable
fun ValidacaoQualidadeScreen(navController: NavController) { // Correção aqui
    var isFotoCapturada by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { ValidacaoQualidadeHeaderSection() },
        bottomBar = { ComidaVivaBottomBar(navController) },
        containerColor = LightCream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Card da Câmera / Preview da Foto
            CameraPreviewCard(
                isFotoCapturada = isFotoCapturada,
                onCapture = { isFotoCapturada = true }
            )

            // Checklist de Conformidade
            ChecklistSection()

            // Botão Principal de Ação
            ActionButton(
                isFotoCapturada = isFotoCapturada,
                onClick = {
                    // Se foto capturada, pode avançar para a próxima tela
                    if(isFotoCapturada) {
                        navController.navigate(Destination.AgendamentoColeta.route)
                    } else {
                        isFotoCapturada = true
                    }
                }
            )
        }
    }
}

@Composable
private fun ValidacaoQualidadeHeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AmeixaEscura,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Text(
            text = "INSPEÇÃO VISUAL",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Controle de Qualidade",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun CameraPreviewCard(
    isFotoCapturada: Boolean,
    onCapture: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(2.dp, AccentGold, RoundedCornerShape(16.dp))
            .clickable(enabled = !isFotoCapturada) { onCapture() },
        contentAlignment = Alignment.Center
    ) {
        if (!isFotoCapturada) {
            CameraOverlayCorners()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    tint = AccentGold,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Posicione o lote no enquadramento",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = AccentGold,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Foto registrada",
                    color = AccentGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Lote #2847  •  07/08/2026 14:23",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun CameraOverlayCorners() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(24.dp)
                .border(width = 2.dp, color = AccentGold)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
                .border(width = 2.dp, color = AccentGold)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(24.dp)
                .border(width = 2.dp, color = AccentGold)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(24.dp)
                .border(width = 2.dp, color = AccentGold)
        )
    }
}

@Composable
private fun ChecklistSection() {
    val items = listOf(
        "Alimento mantido sob controle térmico?",
        "Recipiente atóxico e vedado corretamente?",
        "Validade visível no rótulo ou embalagem?",
        "Quantidade confere com o registrado?"
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Checklist de Conformidade",
            color = TextDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        items.forEach { text ->
            ChecklistItem(text = text)
        }
    }
}

@Composable
private fun ChecklistItem(text: String) {
    // Comeca desmarcado: o operador precisa conferir cada item de verdade
    var isChecked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(ItemBg)
            .border(1.dp, TextDark.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .clickable { isChecked = !isChecked }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isChecked) Icons.Outlined.TaskAlt else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = null,
            tint = TextDark,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = TextDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ActionButton(
    isFotoCapturada: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFotoCapturada) ItemBg else DarkPurple,
            contentColor = if (isFotoCapturada) TextDark else Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Text(
            text = if (isFotoCapturada) "Foto Anexada ao Lote ✓" else "Fotografar Lote",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ValidacaoQualidadeScreenPreview() {
    ComidaVivaTheme() {
        ValidacaoQualidadeScreen(rememberNavController()) // Funciona agora
    }
}
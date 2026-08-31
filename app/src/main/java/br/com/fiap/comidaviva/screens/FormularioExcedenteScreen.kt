package br.com.fiap.comidaviva.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.comidaviva.components.ComidaVivaBottomBar
import br.com.fiap.comidaviva.navigation.Destination
import br.com.fiap.comidaviva.navigation.navegarPelaBarra
import br.com.fiap.comidaviva.ui.theme.*
import java.util.Locale

@Composable
fun FormularioExcedenteScreen(navController: NavController) {
    var categoriaSelecionada by remember { mutableStateOf("Hortifrúti") }
    var quantidade by remember { mutableStateOf("18") }
    var temperaturaSelecionada by remember { mutableStateOf("Resfriado") }

    // Horário limite guardado como hora e minuto, e não como texto fixo.
    var horaLimite by remember { mutableIntStateOf(17) }
    var minutoLimite by remember { mutableIntStateOf(0) }
    var mostrarSeletorHorario by remember { mutableStateOf(false) }

    val horarioFormatado = String.format(
        Locale.forLanguageTag("pt-BR"), "%02d:%02d", horaLimite, minutoLimite
    )

    // Fecha o teclado quando o usuário confirma a quantidade
    val gerenciadorDeFoco = LocalFocusManager.current

    if (mostrarSeletorHorario) {
        SeletorHorarioDialog(
            horaInicial = horaLimite,
            minutoInicial = minutoLimite,
            onConfirmar = { hora, minuto ->
                horaLimite = hora
                minutoLimite = minuto
                mostrarSeletorHorario = false
            },
            onCancelar = { mostrarSeletorHorario = false }
        )
    }

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
            // Header Roxo do Formulario
            item {
                FormularioHeaderSection()
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // 1. Seção Tipo de Alimento
                    Text(
                        text = "Tipo de Alimento",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Grafite
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val categorias = listOf(
                        "Refeição Pronta", "Hortifrúti",
                        "Pães / Lanches", "Embalados"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CategoriaPill(
                                titulo = categorias[0],
                                selecionado = categoriaSelecionada == categorias[0],
                                onClick = { categoriaSelecionada = categorias[0] },
                                modifier = Modifier.weight(1f)
                            )
                            CategoriaPill(
                                titulo = categorias[1],
                                selecionado = categoriaSelecionada == categorias[1],
                                onClick = { categoriaSelecionada = categorias[1] },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CategoriaPill(
                                titulo = categorias[2],
                                selecionado = categoriaSelecionada == categorias[2],
                                onClick = { categoriaSelecionada = categorias[2] },
                                modifier = Modifier.weight(1f)
                            )
                            CategoriaPill(
                                titulo = categorias[3],
                                selecionado = categoriaSelecionada == categorias[3],
                                onClick = { categoriaSelecionada = categorias[3] },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // 2. Seção Quantidade Estimada
                    Text(
                        text = "Quantidade estimada",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Grafite
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5DDD5), RoundedCornerShape(26.dp))
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = quantidade,
                                // Aceita apenas dígitos: "kg" já está fixo ao lado
                                onValueChange = { digitado ->
                                    quantidade = digitado.filter { it.isDigit() }.take(4)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { gerenciadorDeFoco.clearFocus() }
                                ),
                                cursorBrush = SolidColor(Ameixa),
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Grafite
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "kg",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // 3. Seção Temperatura de Armazenamento
                    Text(
                        text = "Temperatura de Armazenamento",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Grafite
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val opcoesTemperatura = listOf(
                        "Quente >60°C",
                        "Resfriado",
                        "Temp. Ambiente"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        opcoesTemperatura.forEach { opcao ->
                            TemperaturaCardOption(
                                titulo = opcao,
                                selecionado = temperaturaSelecionada == opcao,
                                onClick = { temperaturaSelecionada = opcao }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // 4. Seção Horário Limite para Coleta
                    Text(
                        text = "Horário limite para coleta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Grafite
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5DDD5), RoundedCornerShape(26.dp))
                            // Abre o relógio para escolher o horário de fato
                            .clickable {
                                gerenciadorDeFoco.clearFocus()
                                mostrarSeletorHorario = true
                            }
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = horarioFormatado,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Grafite
                            )
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = "Selecionar horário",
                                tint = Ameixa,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Botão Principal "Publicar Doação"
                    Button(
                        onClick = {
                            navController.navigate(Destination.DoacaoPublicada.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Ambar,
                            contentColor = Grafite
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = "Publicar Doação",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// MARK: - Subcomponentes

@Composable
fun FormularioHeaderSection() {
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
            text = "COZINHA / REFEITÓRIO",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Registrar Excedente",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Preencha em menos de 1 minuto",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun CategoriaPill(
    titulo: String,
    selecionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selecionado) Color(0xFFEBE0D8) else Color.White
    val borderColor = if (selecionado) Ameixa else Color(0xFFE5DDD5)
    val textColor = if (selecionado) Ameixa else Grafite

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = titulo,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun TemperaturaCardOption(
    titulo: String,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selecionado) Color(0xFFEBE0D8) else Color.White
    val borderColor = if (selecionado) Ameixa else Color(0xFFE5DDD5)
    val textColor = if (selecionado) Ameixa else Grafite

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(26.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Radio Button customizado
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (selecionado) Ameixa else Color.Gray, CircleShape)
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

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

/**
 * Relógio para escolher o horário limite da coleta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeletorHorarioDialog(
    horaInicial: Int,
    minutoInicial: Int,
    onConfirmar: (Int, Int) -> Unit,
    onCancelar: () -> Unit
) {
    val estado = rememberTimePickerState(
        initialHour = horaInicial,
        initialMinute = minutoInicial,
        is24Hour = true
    )

    Dialog(onDismissRequest = onCancelar) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Creme
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "HORÁRIO LIMITE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ameixa,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Até quando o lote pode ser retirado?",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(20.dp))

                TimePicker(
                    state = estado,
                    colors = TimePickerDefaults.colors(
                        selectorColor = Ameixa,
                        containerColor = Creme,
                        clockDialColor = Color.White,
                        periodSelectorSelectedContainerColor = Ameixa,
                        periodSelectorSelectedContentColor = Color.White,
                        timeSelectorSelectedContainerColor = Ameixa,
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContainerColor = Color.White,
                        timeSelectorUnselectedContentColor = Grafite
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onCancelar) {
                        Text(
                            text = "Cancelar",
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onConfirmar(estado.hour, estado.minute) },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Ameixa,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Confirmar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun FormularioExcedenteScreenPreview() {
    ComidaVivaTheme {
        FormularioExcedenteScreen(rememberNavController())
    }
}
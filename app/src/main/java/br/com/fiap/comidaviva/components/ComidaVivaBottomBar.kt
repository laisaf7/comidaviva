package br.com.fiap.comidaviva.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import br.com.fiap.comidaviva.navigation.Destination
import br.com.fiap.comidaviva.navigation.navegarPelaBarra
import br.com.fiap.comidaviva.ui.theme.Ameixa

/** Um item da barra inferior. */
private data class AbaInferior(
    val rota: String,
    val icone: ImageVector,
    val rotulo: String,
    /** Rotas que também acendem esta aba (telas filhas do mesmo fluxo). */
    val rotasRelacionadas: List<String> = emptyList()
)

private val ABAS = listOf(
    AbaInferior(
        rota = Destination.Home.route,
        icone = Icons.Default.Home,
        rotulo = "Início",
        rotasRelacionadas = listOf(
            Destination.Notificacoes.route,
            Destination.DoacoesEntregues.route
        )
    ),
    AbaInferior(
        rota = Destination.FormularioExcedente.route,
        icone = Icons.Default.Add,
        rotulo = "Registrar",
        rotasRelacionadas = listOf(Destination.DoacaoPublicada.route)
    ),
    AbaInferior(
        rota = Destination.ValidacaoQualidade.route,
        icone = Icons.Outlined.PhotoCamera,
        rotulo = "Câmera"
    ),
    AbaInferior(
        rota = Destination.AgendamentoColeta.route,
        icone = Icons.Outlined.Place,
        rotulo = "Mapa"
    ),
    AbaInferior(
        rota = Destination.DashboardImpacto.route,
        icone = Icons.Outlined.BarChart,
        rotulo = "Impacto"
    )
)

@Composable
fun ComidaVivaBottomBar(navController: NavController) {
    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        ABAS.forEach { aba ->
            val ativa = rotaAtual == aba.rota || rotaAtual in aba.rotasRelacionadas

            NavigationBarItem(
                selected = ativa,
                onClick = { navController.navegarPelaBarra(aba.rota) },
                icon = { Icon(aba.icone, contentDescription = aba.rotulo) },
                label = { Text(aba.rotulo, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Ameixa,
                    selectedTextColor = Ameixa,
                    indicatorColor = Ameixa.copy(alpha = 0.1f),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

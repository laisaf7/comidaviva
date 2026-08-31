package br.com.fiap.comidaviva.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.fiap.comidaviva.screens.*

@Composable
fun NavigationRoutes() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // O app abre direto na Home. Não há barreira de login: este é um MVP
        // acadêmico que precisa ser testado sem cadastro prévio.
        // As telas de abertura, login e cadastro continuam disponíveis pelo
        // ícone de conta no cabeçalho da Home.
        startDestination = Destination.Home.route
    ) {
        composable(Destination.Initial.route) {
            InitialScreen(navController = navController)
        }
        composable(Destination.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Destination.CadastroPasso1.route) {
            CadastroPasso1Screen(navController = navController)
        }
        composable(Destination.CadastroPasso2.route) {
            CadastroPasso2Screen(navController = navController)
        }
        composable(Destination.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Destination.FormularioExcedente.route) {
            FormularioExcedenteScreen(navController = navController)
        }
        composable(Destination.DoacaoPublicada.route) {
            DoacaoPublicadaScreen(navController = navController)
        }
        composable(Destination.ValidacaoQualidade.route) {
            ValidacaoQualidadeScreen(navController = navController)
        }
        composable(Destination.AgendamentoColeta.route) {
            AgendamentoColetaScreen(navController = navController)
        }
        composable(Destination.DashboardImpacto.route) {
            DashboardImpactoScreen(navController = navController)
        }
        composable(Destination.Notificacoes.route) {
            NotificacoesScreen(navController = navController)
        }
        composable(Destination.DoacoesEntregues.route) {
            DoacoesEntreguesScreen(navController = navController)
        }
    }
}
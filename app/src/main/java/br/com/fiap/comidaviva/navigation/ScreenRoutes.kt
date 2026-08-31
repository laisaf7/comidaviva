package br.com.fiap.comidaviva.navigation

import androidx.navigation.NavController

sealed class Destination(val route: String) {
    object Initial : Destination("abertura")
    object Login : Destination("login")
    object CadastroPasso1 : Destination("cadastro_passo1")
    object CadastroPasso2 : Destination("cadastro_passo2")
    object Home : Destination("home")
    object FormularioExcedente : Destination("formulario_excedente")
    object ValidacaoQualidade : Destination("validacao_qualidade")
    object AgendamentoColeta : Destination("agendamento_coleta")
    object DashboardImpacto : Destination("dashboard_impacto")
    object DoacaoPublicada : Destination("doacao_publicada")
    object Notificacoes : Destination("notificacoes")
    object DoacoesEntregues : Destination("doacoes_entregues")
}

/**
 * Navegação usada pela barra inferior.
 *
 * Sem isto, cada toque na barra empilha um novo destino — inclusive quando o
 * usuário toca na aba em que já está — e o botão "voltar" do Android passa a
 * percorrer um histórico enorme. `launchSingleTop` impede a duplicata e
 * `popUpTo(Home)` mantém a Home como raiz da pilha.
 */
fun NavController.navegarPelaBarra(rota: String) {
    if (currentDestination?.route == rota) return

    navigate(rota) {
        popUpTo(Destination.Home.route) {
            saveState = true
            inclusive = false
        }
        launchSingleTop = true
        restoreState = true
    }
}
package br.com.fiap.comidaviva.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.comidaviva.R
import br.com.fiap.comidaviva.ui.theme.Ambar
import br.com.fiap.comidaviva.ui.theme.AmbarTexto
import br.com.fiap.comidaviva.ui.theme.Ameixa

/**
 * Marca "Pulso" do ComidaViva.
 *
 * A arte é um vetor (`logo_comidaviva.xml`), não um ícone da biblioteca Material,
 * garantindo que a identidade seja a mesma em todas as telas e no ícone do app.
 *
 * @param claro use `true` sobre fundos escuros — troca a tigela Ameixa por Creme.
 */
@Composable
fun LogoComidaViva(
    modifier: Modifier = Modifier,
    tamanho: Dp = 28.dp,
    claro: Boolean = false
) {
    Image(
        painter = painterResource(
            id = if (claro) R.drawable.logo_comidaviva_claro else R.drawable.logo_comidaviva
        ),
        contentDescription = "ComidaViva",
        modifier = modifier.size(tamanho)
    )
}

/**
 * Assinatura horizontal: marca + wordmark "ComidaViva".
 *
 * @param claro use `true` sobre fundos escuros.
 */
@Composable
fun AssinaturaComidaViva(
    modifier: Modifier = Modifier,
    tamanhoLogo: Dp = 28.dp,
    tamanhoTexto: TextUnit = 24.sp,
    claro: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LogoComidaViva(tamanho = tamanhoLogo, claro = claro)

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Comida",
            fontSize = tamanhoTexto,
            fontWeight = FontWeight.Bold,
            color = if (claro) Color.White else Ameixa
        )
        Text(
            text = "Viva",
            fontSize = tamanhoTexto,
            fontWeight = FontWeight.Bold,
            color = if (claro) Ambar else AmbarTexto
        )
    }
}

# ComidaViva — Documentação Técnica

Aplicativo Android nativo desenvolvido como MVP para a disciplina de Desenvolvimento
Mobile (2TDS — FIAP), com foco em ESG.

---

## 1. Objetivo do aplicativo

O ComidaViva conecta refeitórios corporativos, restaurantes e supermercados que geram
excedente alimentar às ONGs e cozinhas comunitárias capazes de redistribuí-lo antes do
descarte.

O ciclo que o app cobre é:

```
Registrar excedente → Validar qualidade → Agendar coleta → Medir impacto
```

O diferencial do MVP não é apenas intermediar a doação, mas **quantificar o impacto**
gerado por ela e devolver esse número à empresa em formato de relatório ESG exportável.

---

## 2. Aplicação no contexto ESG

| Pilar | Como o app atua |
|---|---|
| **Ambiental (E)** | Cada quilo desviado do aterro deixa de gerar metano na decomposição. O Dashboard converte os quilos doados em CO₂ equivalente evitado. A integração com o Open-Meteo reduz perdas por quebra da cadeia térmica. |
| **Social (S)** | Converte excedente em refeições para ONGs parceiras, atacando insegurança alimentar. O cadastro tem perfil dedicado para ONGs, sem custo. |
| **Governança (G)** | Trilha auditável de cada lote: foto de inspeção, checklist de conformidade, QR Code de entrega, central de notificações e relatório mensal exportável em PDF. A tela de abertura referencia a **Lei nº 14.016/2020**, e o Dashboard mapeia a operação nos **ODS 2 (Fome Zero)** e **ODS 12 (Consumo e Produção Responsáveis)** da ONU. |

---

## 3. Tecnologia escolhida

| Item | Versão / escolha |
|---|---|
| Plataforma | Android nativo |
| Linguagem | Kotlin 2.2.10 |
| UI | Jetpack Compose (BOM 2026.02.01) + Material 3 |
| Navegação | Navigation Compose 2.9.0 |
| Build | Gradle 9.5 · AGP 9.3.2 |
| JDK | 25 (JetBrains Runtime do Android Studio) |
| `minSdk` / `targetSdk` | 28 / 37 |
| Rede | `HttpURLConnection` (Android SDK) |
| Parsing JSON | `org.json.JSONObject` (Android SDK) |
| Geração de PDF | `android.graphics.pdf.PdfDocument` (Android SDK) |
| Compartilhamento | `FileProvider` (AndroidX Core) |
| Persistência | `SharedPreferences` |

**Nenhuma biblioteca de terceiros foi adicionada.** Rede, JSON e PDF usam apenas classes
que já acompanham o Android SDK — sem Retrofit, OkHttp, Gson, iText ou PdfBox. As únicas
dependências do projeto são AndroidX/Compose e a biblioteca de ícones
`material-icons-extended`.

---

## 4. Serviço externo consumido

### Endereço

```
https://api.open-meteo.com/v1/forecast
```

API pública e gratuita de previsão do tempo. **Não exige chave de acesso, cadastro nem
back-end próprio** — atende diretamente à orientação do enunciado de usar serviços que
dispensem servidor.

### Por que este serviço

Alimento excedente tem uma janela de segurança que **encurta conforme a temperatura
ambiente sobe**. Sem dado externo, o app só poderia exibir um prazo fixo. Consultando a
previsão real do endereço da coleta, o ComidaViva calcula uma janela recomendada — que é
exatamente o "insight" que o pilar Ambiental do ESG pede.

### Requisição

```
GET https://api.open-meteo.com/v1/forecast
      ?latitude=-23.5613
      &longitude=-46.6565
      &current=temperature_2m,relative_humidity_2m
      &hourly=temperature_2m
      &daily=temperature_2m_max
      &timezone=America%2FSao_Paulo
      &forecast_days=1
```

As coordenadas correspondem ao ponto de coleta do cenário (Av. Paulista, 1374 — São Paulo/SP).

| Parâmetro | Função no app |
|---|---|
| `current` | Temperatura e umidade exibidas em destaque |
| `hourly` | Faixa com as próximas 6 horas |
| `daily` | Máxima do dia — base do cálculo de risco |
| `timezone` | Devolve horários já no fuso de São Paulo |

### Resposta (trecho real)

```json
{
  "current": {
    "time": "2026-08-30T17:45",
    "temperature_2m": 20.6,
    "relative_humidity_2m": 90
  },
  "hourly": {
    "time": ["2026-08-30T18:00", "2026-08-30T19:00"],
    "temperature_2m": [20.4, 20.1]
  },
  "daily": {
    "temperature_2m_max": [29.4]
  }
}
```

### Duas leituras do mesmo dado

O app interpreta a resposta de duas formas complementares:

**1. Faixa térmica** (`FaixaTemperatura`) — colore o indicador conforme a sensação do
momento:

| Temperatura atual | Faixa | Cor | Ícone |
|---|---|---|---|
| < 18 °C | Frio | Azul `#2E6F9E` | Floco de neve |
| 18 – 27,9 °C | Ameno | Âmbar `#9A600C` | Termômetro |
| ≥ 28 °C | Quente | Tijolo `#B03D26` | Chama |

**2. Risco térmico** (`RiscoTermico`) — define a janela de coleta a partir da **máxima do
dia**, não da temperatura do momento, porque a retirada pode ocorrer no horário mais
quente da tarde:

| Máxima prevista | Risco | Janela segura | Recomendação |
|---|---|---|---|
| < 25 °C | Baixo | até 4 h | Janela padrão pode ser mantida |
| 25 – 29,9 °C | Moderado | até 2 h | Antecipar coleta, usar caixa térmica |
| ≥ 30 °C | Alto | até 1 h | Coleta urgente, refrigeração ativa na rota |

### Comportamento offline

Toda resposta bem-sucedida é gravada em `SharedPreferences`. Se uma consulta posterior
falhar (sem internet, por exemplo), o app exibe a última leitura salva com o aviso
*"Última leitura salva no aparelho"*. Se não houver cache algum, mostra uma mensagem de
erro com botão **Tentar novamente**.

Isso garante que o avaliador consiga testar o app mesmo sem conexão.

---

## 5. Estrutura do projeto

```
app/src/main/java/br/com/fiap/comidaviva/
│
├── MainActivity.kt                    Activity única; monta o tema e o NavHost
│
├── navigation/
│   ├── ScreenRoutes.kt                Rotas (sealed class Destination) + navegarPelaBarra()
│   └── NavigationRoutes.kt            NavHost com as 12 telas
│
├── screens/                           Uma tela por arquivo
│   ├── InitialScreen.kt
│   ├── LoginScreen.kt
│   ├── CadastroPasso1Screen.kt
│   ├── CadastroPasso2Screen.kt
│   ├── HomeScreen.kt
│   ├── NotificacoesScreen.kt
│   ├── DoacoesEntreguesScreen.kt
│   ├── FormularioExcedenteScreen.kt
│   ├── DoacaoPublicadaScreen.kt
│   ├── ValidacaoQualidadeScreen.kt
│   ├── AgendamentoColetaScreen.kt
│   └── DashboardImpactoScreen.kt
│
├── components/                        Componentes reutilizados entre telas
│   ├── MarcaComidaViva.kt             Logotipo e assinatura da marca
│   ├── ComidaVivaBottomBar.kt         Barra de navegação inferior única
│   └── PrevisaoTempoCard.kt           Card e faixa da previsão do tempo
│
├── service/                           Regras de negócio e acesso a dados
│   ├── PrevisaoTempo.kt               Modelos, FaixaTemperatura e RiscoTermico
│   ├── OpenMeteoService.kt            HTTP, parsing JSON e cache
│   ├── DadosImpacto.kt                Histórico mensal e cálculo de variação
│   └── RelatorioPdfService.kt         Geração e compartilhamento do PDF
│
└── ui/theme/
    ├── Color.kt                       Paleta ComidaViva + faixas de temperatura
    ├── Theme.kt                       ColorScheme claro e escuro
    └── Type.kt                        Tipografia
```

Recursos relevantes em `app/src/main/res/`:

```
drawable/
├── logo_comidaviva.xml                Marca para fundos claros
├── logo_comidaviva_claro.xml          Marca para fundos escuros
├── ic_launcher_foreground.xml         Camada frontal do ícone adaptativo
├── ic_launcher_background.xml         Gradiente Ameixa do ícone
└── ic_launcher_monochrome.xml         Camada monocromática (Material You)

xml/
└── file_paths.xml                     Caminhos expostos pelo FileProvider
```

Cerca de **6.900 linhas de Kotlin** distribuídas em 25 arquivos.

### Camadas

```
    Tela (Composable)
          │  lê estado / dispara ação
          ▼
    ResultadoPrevisao          ← Carregando | Sucesso | Erro
          ▲
          │  devolve
    OpenMeteoService  ──HTTPS──►  api.open-meteo.com
          │
          └──►  SharedPreferences (cache da última resposta)

    DadosImpacto (histórico mensal)
          │
          └──►  RelatorioPdfService  ──►  PDF em cache  ──FileProvider──►  outro app
```

---

## 6. Navegação

### Rotas

Definidas em `ScreenRoutes.kt` como uma `sealed class`, evitando strings soltas:

```kotlin
sealed class Destination(val route: String) {
    object Initial            : Destination("abertura")
    object Login              : Destination("login")
    object CadastroPasso1     : Destination("cadastro_passo1")
    object CadastroPasso2     : Destination("cadastro_passo2")
    object Home               : Destination("home")
    object FormularioExcedente: Destination("formulario_excedente")
    object ValidacaoQualidade : Destination("validacao_qualidade")
    object AgendamentoColeta  : Destination("agendamento_coleta")
    object DashboardImpacto   : Destination("dashboard_impacto")
    object DoacaoPublicada    : Destination("doacao_publicada")
    object Notificacoes       : Destination("notificacoes")
    object DoacoesEntregues   : Destination("doacoes_entregues")
}
```

### Tela inicial: Home, sem login

O `startDestination` do `NavHost` é **`Home`**. O app abre direto na área operacional,
sem barreira de autenticação, para que possa ser avaliado sem cadastro prévio.

As telas de abertura, login e cadastro continuam implementadas e acessíveis pelo **ícone
de conta no canto superior direito da Home**.

### Barra inferior única

Toda a navegação por abas vive em um só componente, `components/ComidaVivaBottomBar.kt`.
A aba ativa é derivada da rota atual:

```kotlin
val entradaAtual by navController.currentBackStackEntryAsState()
val rotaAtual = entradaAtual?.destination?.route
val ativa = rotaAtual == aba.rota || rotaAtual in aba.rotasRelacionadas
```

Antes, cada uma das seis telas mantinha a própria cópia da barra com o item ativo marcado
à mão — o que fazia o botão "Início" perder as cores da paleta fora da Home e exigia
editar seis arquivos a cada ajuste.

Telas sem aba própria acendem a aba do fluxo a que pertencem: Notificações e Doações
Entregues acendem **Início**; Doação Publicada acende **Registrar**.

### Empilhamento

Os itens da barra usam a extensão `navegarPelaBarra()`:

```kotlin
fun NavController.navegarPelaBarra(rota: String) {
    if (currentDestination?.route == rota) return
    navigate(rota) {
        popUpTo(Destination.Home.route) { saveState = true; inclusive = false }
        launchSingleTop = true
        restoreState = true
    }
}
```

Sem isso, cada toque empilharia um destino novo — inclusive ao tocar na aba já ativa — e o
botão "voltar" do Android passaria a percorrer um histórico enorme.

### Fluxo principal

```
Home ──► Formulário de Excedente ──► Doação Publicada
  │
  ├─► Validação de Qualidade ──► Agendamento de Coleta
  │
  ├─► Dashboard de Impacto ──► (PDF) ──► app externo
  │
  ├─► (sino) Notificações
  ├─► (Ver entregues) Doações Entregues
  │
  └─► (ícone de conta) Abertura ──► Login ──────┐
                            └──► Cadastro 1 ──► Cadastro 2 ──► Home
```

---

## 7. Telas

### 7.1 Abertura — `InitialScreen.kt`
Cartão de visita da marca. Cabeçalho com gradiente, órbita desenhada em `Canvas` e a marca
oficial no centro. Abaixo, três cards apresentam os pilares ESG e o rodapé cita a
Lei nº 14.016/2020.
**Ações:** *Entrar na conta* e *Criar conta grátis*.

### 7.2 Login — `LoginScreen.kt`
E-mail e senha em `OutlinedTextField`, com alternância de visibilidade da senha.

Traz uma **faixa de aviso âmbar — "Em desenvolvimento — sem autenticação"** — deixando
explícito que a tela é protótipo de interface: não há validação de credenciais nem
persistência de usuário, e tocar em *Entrar* com os campos vazios leva à Home.

### 7.3 Cadastro — Passo 1 — `CadastroPasso1Screen.kt`
Seleção do tipo de conta (**Empresa** ou **ONG**) com indicador "PASSO 1 DE 2".

### 7.4 Cadastro — Passo 2 — `CadastroPasso2Screen.kt`
Nome da empresa, e-mail corporativo e senha, com indicador de força da senha.

### 7.5 Home — `HomeScreen.kt`
Tela principal. Cabeçalho escuro organizado em três linhas, para que nada fique apertado:

1. data, ícone de conta e sino de notificações (com contador);
2. assinatura **ComidaViva** ocupando a linha inteira;
3. **faixa de condição térmica** vinda do Open-Meteo — bolha colorida pela faixa (frio,
   ameno ou quente), temperatura atual, máxima do dia e janela de coleta recomendada.

Abaixo do cabeçalho: três KPIs, o banner *Registrar Novo Lote* e a lista **Doações
Ativas**, renderizada com `LazyColumn` + `items()`. O link **Ver entregues** leva ao
histórico de lotes já confirmados.

### 7.6 Notificações — `NotificacoesScreen.kt`
Central de avisos aberta pelo sino da Home. Organizada em dois blocos:

- **Fixadas** — os dois marcos do ciclo da doação, sempre no topo e com contorno colorido:
  *Postagem realizada* e *Entrega recebida*;
- **Recentes** — coleta a caminho, prazo encurtado pelo calor, entregas anteriores.

Cada aviso tem tipo (`POSTAGEM`, `ENTREGA`, `COLETA`, `PRAZO`), com ícone, cor e etiqueta
próprios.

### 7.7 Doações Entregues — `DoacoesEntreguesScreen.kt`
Histórico de lotes retirados e confirmados pelas ONGs. O cabeçalho traz três totais —
kg entregues, refeições e ONGs atendidas — **calculados a partir da lista**, não digitados:

```kotlin
val totalQuilos = DOACOES_ENTREGUES.sumOf { it.quilos }
val ongsAtendidas = DOACOES_ENTREGUES.map { it.ong }.distinct().size
```

Cada card mostra categoria, descrição, quilos, refeições, ONG receptora, lote e data, com
selo *Entregue*.

### 7.8 Registrar Excedente — `FormularioExcedenteScreen.kt`
Formulário de registro do lote:

- **Tipo de alimento** — pílulas selecionáveis;
- **Quantidade estimada** — campo numérico que aceita só dígitos. Com `singleLine = true`
  e `ImeAction.Done`, a tecla Enter funciona como **OK** e fecha o teclado, em vez de
  quebrar a linha;
- **Temperatura de armazenamento** — Quente / Resfriado / Ambiente;
- **Horário limite para coleta** — abre um **relógio Material 3** (`TimePicker`) dentro de
  um diálogo na paleta do app. O valor é guardado como hora e minuto e formatado em 24 h.
  Antes era o texto fixo `"05 : 00 PM"`, que nada alterava.

**Ação:** *Publicar Doação*.

### 7.9 Doação Publicada — `DoacaoPublicadaScreen.kt`
Confirmação com selo de sucesso e resumo do lote.

### 7.10 Validação de Qualidade — `ValidacaoQualidadeScreen.kt`
Controle de qualidade antes da coleta. Área de enquadramento simulando a câmera e
**checklist de conformidade** com quatro itens de governança.

Os itens **começam desmarcados** — o operador precisa conferir cada um de fato antes de
liberar o lote.

### 7.11 Agendamento de Coleta — `AgendamentoColetaScreen.kt`
Tela da integração com o serviço externo. Contém:

1. **Mapa da rota** — grid e trajeto pontilhado em `Canvas`, com pinos de origem e destino;
2. **Card de previsão do tempo** — dado ao vivo do Open-Meteo: temperatura colorida pela
   faixa térmica, umidade, máxima do dia, faixa das próximas 6 horas (cada hora tingida
   pela própria faixa), selo de risco e a janela de coleta recomendada;
3. **Pontos de coleta** — origem e destino com endereço, horário e distância;
4. **QR Code de entrega**.

### 7.12 Dashboard de Impacto — `DashboardImpactoScreen.kt`
Relatório ESG mensal, com três controles no topo:

| Botão | Função |
|---|---|
| **Mês** | Abre o seletor de período (Ago, Jul, Jun e Mai de 2026) |
| **Comparar** | Liga a comparação com o mês anterior |
| **PDF** | Gera o relatório e abre o menu de compartilhamento |

Conteúdo:

- **Seis KPIs** — kg salvos, refeições doadas, CO₂ evitado, ONGs parceiras, lotes
  publicados e taxa de coletas concluídas. Com o comparativo ligado, cada cartão ganha a
  variação percentual contra o mês anterior, em azul quando sobe e em tijolo quando cai;
- **Tabela comparativa** — surge apenas com o comparativo ativo, com as seis métricas
  lado a lado e a variação de cada uma;
- **Gráfico de barras** — kg salvos por mês, com alturas calculadas do histórico real e a
  barra do mês em exibição destacada em Âmbar;
- **Gráfico de linha** — refeições doadas acumuladas;
- **Alinhamento ODS 2 e 12** da ONU;
- **Fórmula do cálculo ambiental**:
  `Pegada de Carbono Evitada = kg de Comida Salva × Fator de Emissão de Metano`.

#### Exportação em PDF

`RelatorioPdfService` monta uma página A4 (595 × 842 pt) com `PdfDocument`, desenhando com
`Canvas` e `Paint` — as mesmas classes por trás dos gráficos em Compose. O documento traz
cabeçalho com a marca, os indicadores do mês com variação, o detalhamento operacional, a
distribuição semanal em barras, a tabela comparativa, os selos ODS e o rodapé legal.

O arquivo é gravado em `cacheDir/relatorios/` e exposto a outros aplicativos por um
`FileProvider` — desde a API 24 o Android proíbe compartilhar URIs `file://` diretamente.
A geração roda em `Dispatchers.IO` para não travar a interface, com indicador de carga no
botão e mensagem de erro em `Snackbar` caso falhe.

---

## 8. Identidade visual

### Marca

A marca "Pulso" é uma tigela com uma linha de crescimento ascendente. Está no projeto como
**vetor** (`logo_comidaviva.xml`), não como ícone da biblioteca Material, garantindo que a
identidade seja idêntica em todas as telas e no ícone do aplicativo.

Duas variações, expostas pelo composable `LogoComidaViva(claro = …)`:

| Variação | Tigela | Uso |
|---|---|---|
| Padrão | Ameixa `#4C1D3D` | Fundos claros |
| Clara | Creme `#FAF6EF` | Fundos escuros (cabeçalhos) |

`AssinaturaComidaViva()` combina a marca com o wordmark **Comida**Viva.

### Ícone do aplicativo

Ícone adaptativo com três camadas:

- **fundo** — gradiente Ameixa `#5C2447` → `#2A0E1F`;
- **frente** — marca em Creme e Âmbar, ocupando 62,5% do canvas dentro da zona segura;
- **monocromática** — versão de tom único para o Material You do Android 13+.

Como o `minSdk` é 28, o ícone adaptativo é sempre usado; os PNGs legados em `mipmap-*dpi`
foram removidos.

### Paleta

| Token | Hex | Uso |
|---|---|---|
| `Ameixa` | `#4C1D3D` | Cor primária: navegação, cabeçalhos, botões |
| `AmeixaEscura` | `#331127` | Cabeçalhos escuros e barra de status |
| `Ambar` | `#F2A33C` | Chamadas de ação operacionais |
| `AmbarTexto` | `#9A600C` | Texto âmbar sobre fundo claro (contraste) |
| `TijoloTexto` | `#B03D26` | Alertas e erros |
| `Creme` | `#FAF6EF` | Fundo principal |
| `Grafite` | `#1C1A22` | Tipografia sobre fundos claros |
| `TempFrio` | `#2E6F9E` | Faixa térmica fria e variações positivas |
| `TempAmeno` | `#9A600C` | Faixa térmica amena |
| `TempQuente` | `#B03D26` | Faixa térmica quente e variações negativas |

`Theme.kt` define `LightColorScheme` e `DarkColorScheme` completos, com tokens específicos
para o modo escuro (`FundoEscuro`, `SuperficieEscura`, `AmbarDark`).

---

## 9. Compilando e gerando o APK

### Pré-requisitos
- Android Studio com JDK 25 (o JBR que acompanha o Studio já serve)
- Android SDK com `compileSdk 37`

### APK de release

```bash
./gradlew clean assembleRelease
```

Saída: `app/build/outputs/apk/release/app-release.apk` — **2,6 MB**.

### Assinatura

O projeto acompanha o keystore `comidaviva.jks` na raiz, configurado em
`app/build.gradle.kts`:

| Campo | Valor |
|---|---|
| Arquivo | `comidaviva.jks` |
| Alias | `comidaviva` |
| Senha do keystore | `comidaviva2026` |
| Senha da chave | `comidaviva2026` |

> O keystore está versionado junto ao projeto **de propósito**, para que qualquer
> avaliador consiga recompilar a entrega. É um keystore acadêmico e **não deve ser usado
> em produção nem para publicar na Play Store**.

Sem essa configuração, o Gradle geraria `app-release-unsigned.apk`, que não instala em
nenhum aparelho.

### Otimização (R8)

O bloco `release` liga o R8:

```kotlin
optimization { enable = true }
```

Isso é **indispensável** neste projeto: a biblioteca `material-icons-extended` traz
milhares de ícones e, sem otimização, o APK sai com **45 MB**. Com o R8, apenas os ícones
efetivamente referenciados são empacotados e o arquivo cai para **2,6 MB**.

### Verificação da assinatura

```bash
apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
```

Resultado esperado: assinatura no esquema v2, com
`CN=ComidaViva, OU=2TDS FIAP, O=FIAP, L=Sao Paulo, ST=SP, C=BR`.

### Teste realizado

O APK de release foi instalado e executado em emulador **Pixel 6 Pro (API 36, 1440×3120)**,
sem nenhuma exceção fatal no `logcat`. Verificados na prática:

- integração com o Open-Meteo devolvendo dado real (20,5 °C, umidade 90 %, máxima 29,4 °C
  → faixa *Ameno*, risco moderado, janela de 2 h);
- navegação pelas cinco abas, com a aba correta acesa em cada tela;
- central de notificações com os dois avisos fixados;
- histórico de doações entregues com totais calculados;
- filtro de mês, comparativo mês a mês e **exportação em PDF**, que gera
  `ComidaViva-Ago-ESG.pdf` e abre o menu de compartilhamento do Android;
- relógio do horário limite alterando o valor exibido (17:00 → 14:00);
- checklist de conformidade iniciando com todos os itens desmarcados;
- aviso "Em desenvolvimento — sem autenticação" na tela de login.

---

## 10. Alinhamento com o conteúdo da fase

Praticamente todo o app usa apenas o que foi visto na fase:

| Conteúdo da fase | Onde aparece |
|---|---|
| Temas e cores (`Color.kt`, `Theme.kt`) | Paleta ComidaViva, faixas térmicas, modo claro e escuro |
| Telas em Jetpack Compose | As 12 telas |
| Navigation Compose (`NavHost`, rotas) | `navigation/` |
| Listas (`LazyColumn`, `items`) | Home, Notificações, Entregues, Agendamento, Dashboard |
| Estado (`remember`, `mutableStateOf`) | Formulários, filtros do Dashboard, previsão |
| `SharedPreferences` | Cache offline da previsão |

**Os conceitos que vão além da fase são poucos e todos justificados:**

| Recurso | Por que foi necessário |
|---|---|
| Chamada HTTP (`HttpURLConnection`) | O enunciado exige integração com um serviço existente, e não há como consumir uma API sem requisição de rede |
| `LaunchedEffect` + `Dispatchers.IO` | O Android proíbe acesso de rede e escrita de arquivo na thread principal |
| `PdfDocument` + `FileProvider` | Exportar o relatório ESG em PDF e entregá-lo a outro aplicativo |

Em todos os casos foram usadas **classes nativas do Android SDK** — nenhuma biblioteca
externa foi adicionada ao Gradle. O resultado alimenta um `mutableStateOf` comum, do
mesmo jeito que os formulários da fase.

---

## 11. Limitações conhecidas

Por se tratar de um MVP acadêmico, os pontos abaixo são intencionais:

- **Sem back-end próprio.** A única fonte externa é o Open-Meteo. Doações, ONGs,
  notificações e o histórico mensal do Dashboard são dados estáticos definidos no código.
- **Autenticação não funcional.** Login e cadastro são navegáveis, mas não validam
  credenciais nem persistem usuário — o que a própria tela declara em um aviso visível.
- **Dados não trafegam entre telas.** O que é preenchido no Formulário de Excedente não
  alimenta o Dashboard; cada tela mantém o próprio estado local.
- **Câmera simulada.** A tela de Validação de Qualidade simula a captura alternando um
  estado booleano, sem acessar a câmera do aparelho.
- **Mapa ilustrativo.** A rota é desenhada com `Canvas`, não é um mapa real com
  geolocalização.
- **Coordenadas fixas.** A previsão é consultada sempre para o endereço do cenário
  (Av. Paulista, São Paulo/SP), sem GPS.
- **Notificações internas.** A central de avisos vive dentro do app; não há notificações
  do sistema Android na barra de status.

### Evolução natural

1. Alimentar o Dashboard com dados reais via **Room**, persistindo os lotes registrados;
2. Substituir a captura simulada pela câmera do aparelho;
3. Emitir notificações do sistema com `NotificationManager`;
4. Adicionar autenticação real com um serviço BaaS;
5. Consultar as coordenadas do endereço da coleta em vez de usar valores fixos.

---

*Documento gerado para a entrega da Fase 01 — 2TDS FIAP.*

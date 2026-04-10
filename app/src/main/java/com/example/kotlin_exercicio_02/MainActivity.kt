package com.example.kotlin_exercicio_02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kotlin_exercicio_02.ui.theme.Kotlinexercicio02Theme
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kotlinexercicio02Theme {
                FinanceApp()
            }
        }
    }
}

data class Registro(val descricao: String, val valor: Double)
data class Sonho(val descricao: String, val custo: Double)

enum class AppRoute(val route: String, val label: String) {
    Inicio("inicio", "Inicio"),
    Ganhos("ganhos", "Ganhos"),
    Gastos("gastos", "Gastos"),
    Sonhos("sonhos", "Sonhos")
}

@Composable
fun FinanceApp() {
    val navController = rememberNavController()
    val ganhos = remember { mutableStateListOf<Registro>() }
    val gastos = remember { mutableStateListOf<Registro>() }
    val sonhos = remember { mutableStateListOf<Sonho>() }
    var nomeUsuario by remember { mutableStateOf("Gabriel") }

    val totalGanhos by remember { derivedStateOf { ganhos.sumOf { it.valor } } }
    val totalGastos by remember { derivedStateOf { gastos.sumOf { it.valor } } }
    val saldo by remember { derivedStateOf { totalGanhos - totalGastos } }

    val tabs = listOf(AppRoute.Inicio, AppRoute.Ganhos, AppRoute.Gastos, AppRoute.Sonhos)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val destination = backStackEntry?.destination

                tabs.forEach { tab ->
                    val selected = destination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = routeIcon(tab),
                                contentDescription = tab.label
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.30f),
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.22f)
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = AppRoute.Inicio.route
            ) {
                composable(AppRoute.Inicio.route) {
                    InicioScreen(
                        nomeUsuario = nomeUsuario,
                        onNomeChange = { nomeUsuario = it },
                        totalGanhos = totalGanhos,
                        totalGastos = totalGastos,
                        saldo = saldo
                    )
                }
                composable(AppRoute.Ganhos.route) {
                    RegistrosScreen(
                        titulo = "Ganhos do mes",
                        hintValor = "Valor recebido",
                        registros = ganhos,
                        onAdd = { descricao, valor -> ganhos.add(Registro(descricao, valor)) }
                    )
                }
                composable(AppRoute.Gastos.route) {
                    RegistrosScreen(
                        titulo = "Gastos do mes",
                        hintValor = "Valor gasto",
                        registros = gastos,
                        onAdd = { descricao, valor -> gastos.add(Registro(descricao, valor)) }
                    )
                }
                composable(AppRoute.Sonhos.route) {
                    SonhosScreen(
                        saldo = saldo,
                        sonhos = sonhos,
                        onAdd = { descricao, custo -> sonhos.add(Sonho(descricao, custo)) }
                    )
                }
            }
        }
    }
}

fun routeIcon(route: AppRoute): ImageVector {
    return when (route) {
        AppRoute.Inicio -> Icons.Filled.Home
        AppRoute.Ganhos -> Icons.Filled.AddCircle
        AppRoute.Gastos -> Icons.Filled.RemoveCircle
        AppRoute.Sonhos -> Icons.Filled.Favorite
    }
}

@Composable
fun InicioScreen(
    nomeUsuario: String,
    onNomeChange: (String) -> Unit,
    totalGanhos: Double,
    totalGastos: Double,
    saldo: Double
) {
    var editando by remember { mutableStateOf(false) }
    var novoNome by remember { mutableStateOf(nomeUsuario) }
    
    val percentualEconomia = if (totalGanhos > 0) {
        ((totalGanhos - totalGastos) / totalGanhos * 100).toInt()
    } else {
        0
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (editando) {
                    OutlinedTextField(
                        value = novoNome,
                        onValueChange = { novoNome = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 20.sp)
                    )
                    TextButton(
                        onClick = {
                            onNomeChange(novoNome)
                            editando = false
                        }
                    ) {
                        Text("Salvar")
                    }
                } else {
                    Column {
                        Text(
                            "Oi, $nomeUsuario!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Bem-vindo ao seu gerenciador financeiro",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = { editando = true }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar nome",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Saldo em Conta",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        money(saldo),
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (saldo >= 0) "✓ Você está no controle" else "⚠ Atenção ao saldo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    titulo = "Ganhos",
                    valor = totalGanhos,
                    icon = Icons.Filled.AddCircle,
                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    titulo = "Gastos",
                    valor = totalGastos,
                    icon = Icons.Filled.RemoveCircle,
                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Taxa de Economia",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Do seu ganho total",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "$percentualEconomia%",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (percentualEconomia >= 30) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            fontSize = 28.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Progress bar simples com cor
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { (percentualEconomia / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        color = if (percentualEconomia >= 30) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "💡 Dica do Dia",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Mantenha uma taxa de economia de pelo menos 20% do seu ganho mensal para construir um bom fundo de emergência.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun InfoCard(
    titulo: String,
    valor: Double,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = titulo,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                titulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                money(valor),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun RegistrosScreen(
    titulo: String,
    hintValor: String,
    registros: List<Registro>,
    onAdd: (String, Double) -> Unit
) {
    var descricao by remember { mutableStateOf("") }
    var valorTexto by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(titulo, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descricao") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = valorTexto,
            onValueChange = { valorTexto = it },
            label = { Text(hintValor) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val valor = valorTexto.replace(",", ".").toDoubleOrNull()
                if (descricao.isNotBlank() && valor != null && valor > 0.0) {
                    onAdd(descricao.trim(), valor)
                    descricao = ""
                    valorTexto = ""
                }
            }
        ) {
            Text("Adicionar")
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(registros) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.descricao)
                        Text(money(item.valor))
                    }
                }
            }
        }
    }
}

@Composable
fun SonhosScreen(
    saldo: Double,
    sonhos: List<Sonho>,
    onAdd: (String, Double) -> Unit
) {
    var descricao by remember { mutableStateOf("") }
    var custoTexto by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Sonhos e desejos", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Saldo atual: ${money(saldo)}")
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("O que voce quer") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = custoTexto,
            onValueChange = { custoTexto = it },
            label = { Text("Custo estimado") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val custo = custoTexto.replace(",", ".").toDoubleOrNull()
                if (descricao.isNotBlank() && custo != null && custo > 0.0) {
                    onAdd(descricao.trim(), custo)
                    descricao = ""
                    custoTexto = ""
                }
            }
        ) {
            Text("Adicionar sonho")
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(sonhos) { sonho ->
                val cabeNoSaldo = saldo >= sonho.custo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(sonho.descricao)
                        Text("Custo: ${money(sonho.custo)}")
                        Text(
                            if (cabeNoSaldo) "Status: cabe no saldo"
                            else "Status: ainda nao cabe no saldo"
                        )
                    }
                }
            }
        }
    }
}

fun money(value: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value)
}

@Preview(showBackground = true)
@Composable
fun FinanceAppPreview() {
    Kotlinexercicio02Theme {
        FinanceApp()
    }
}
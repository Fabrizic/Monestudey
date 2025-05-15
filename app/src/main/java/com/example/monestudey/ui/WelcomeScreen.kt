package com.example.monestudey.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monestudey.R
import com.example.monestudey.ui.theme.MonestudeyTheme

@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF333333))
            .padding(16.dp)
    ) {
        // Contenido centrado
        Column(
            modifier = Modifier
                .align(Alignment.Center) // Centra solo la columna con el logo y textos
                .padding(bottom = 100.dp), // espacio para que el botón no se sobreponga
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.monestudey_main_icon),
                contentDescription = "Logo de Monestudey",
                modifier = Modifier
                    .wrapContentSize()
                    .graphicsLayer(
                        scaleX = 1.3f,
                        scaleY = 1.3f
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Monestudey",
                color = Color(0xFFA4C468),
                fontWeight = FontWeight.Bold,
                fontSize = 56.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Gestiona tu dinero de forma simple y rápida",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onStartClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .width(273.dp)
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF009951)
            )
        ) {
            Text(
                "Iniciar",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    MonestudeyTheme {
        WelcomeScreen(
            onStartClick = {}
        )
    }
} 
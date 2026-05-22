package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "تذكر الأحلام",
            style = MaterialTheme.typography.titleLarge,
            color = WarmWhite
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "التقط أفكار عقلك الباطن. تتبع لحظات الوعي في الأحلام، واكتشف الأنماط، وحسّن ذاكرتك.",
            style = MaterialTheme.typography.bodyLarge,
            color = GrayText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(containerColor = SoftCyan, contentColor = DeepBlack),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text("ابدأ بتسجيل الأحلام")
        }
    }
}

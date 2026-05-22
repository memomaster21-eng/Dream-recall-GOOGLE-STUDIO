package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    var selectedGoal by remember { mutableStateOf<String?>(null) }
    val goals = listOf(
        "الأحلام الجلية (Lucid Dreaming)",
        "تذكر الأحلام (Dream Recall)",
        "التأمل الروحي (Spiritual Reflection)",
        "تتبع الكوابيس (Nightmare Tracking)"
    )

    Box(modifier = Modifier.fillMaxSize().auroraBackground()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Animated Moon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)) // Moon color
                    .breathingPulse(minScale = 0.95f, maxScale = 1.05f)
            ) {
                // Moon craters
                Box(modifier = Modifier.size(24.dp).align(Alignment.TopStart).offset(20.dp, 20.dp).clip(CircleShape).background(Color(0xFFCBD5E1)))
                Box(modifier = Modifier.size(16.dp).align(Alignment.BottomEnd).offset((-30).dp, (-40).dp).clip(CircleShape).background(Color(0xFFCBD5E1)))
                Box(modifier = Modifier.size(32.dp).align(Alignment.CenterEnd).offset((-10).dp, 10.dp).clip(CircleShape).background(Color(0xFFCBD5E1)))
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "مرحباً بك في عالم الأحلام",
                style = MaterialTheme.typography.headlineMedium,
                color = MoonWhite,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "التقط أفكار عقلك الباطن. تتبع لحظات الوعي في الأحلام، واكتشف الأنماط، وحسّن تواصلك مع ذاتك العميقة.",
                style = MaterialTheme.typography.bodyLarge,
                color = MoonWhite.copy(alpha=0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "ما هو هدفك الأساسي؟",
                style = MaterialTheme.typography.titleMedium,
                color = MoonWhite
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            goals.forEach { goal ->
                val isSelected = selectedGoal == goal
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isSelected) LucidBlue.copy(alpha=0.15f) else GlassWhite)
                        .border(1.dp, if (isSelected) LucidBlue.copy(alpha=0.5f) else GlassBorder, RoundedCornerShape(24.dp))
                        .clickable { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedGoal = goal 
                        }
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = goal,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) LucidBlue else MoonWhite.copy(alpha=0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFinish()
                },
                colors = ButtonDefaults.buttonColors(containerColor = LucidBlue, contentColor = DeepSpace),
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(32.dp)
            ) {
                Text("ابدأ الرحلة", style = MaterialTheme.typography.titleMedium)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

package com.example.ai

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiService(private val apiKey: String) {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun analyzeDream(dreamTitle: String, dreamContent: String, isLucid: Boolean, isRecurring: Boolean): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext "لم يتم إعداد مفتاح واجهة برمجة تطبيقات Gemini في الإعدادات."
        
        val prompt = """
            أنا سجلت الحلم التالي:
            العنوان: ${if (dreamTitle.isBlank()) "بدون عنوان" else dreamTitle}
            المحتوى: $dreamContent
            هل هو جلي: ${if (isLucid) "نعم" else "لا"}
            هل هو متكرر: ${if (isRecurring) "نعم" else "لا"}
            
            بصفتك محلل أحلام خبير ومستشار نفسي، قم بتقديم تحليل مختصر وإضاءات لهذا الحلم باللغة العربية.
            تحدث عن الرموز، والمشاعر المحتملة، وكيف يمكن أن يرتبط هذا بالوعي والنفسية. لا تُسهب كثيراً.
        """.trimIndent()
        
        try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "تعذر إتمام التحليل."
        } catch (e: Exception) {
            "حدث خطأ أثناء الاتصال بالذكاء الاصطناعي: ${e.message}"
        }
    }

    suspend fun generateShortNotification(dreamContent: String): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext "رسالة تحفيزية لتسجيل أحلامك."
        
        val prompt = """
            بناءً على الحلم التالي:
            "$dreamContent"
            
            اكتب رسالة إشعار (Notification) قصيرة جداً (نصف سطر)، غامضة ومحفزة، لتشجيعي على تسجيل وتذكر أحلامي اليوم.
            لا تكتب أي شيء آخر غير نص الإشعار، باللغة العربية.
        """.trimIndent()
        
        try {
            val response = generativeModel.generateContent(prompt)
            response.text?.trim() ?: "رسالة تحفيزية لتسجيل أحلامك."
        } catch (e: Exception) {
            "حتى الكوابيس تحمل رسائل... سجل أحلامك."
        }
    }
}

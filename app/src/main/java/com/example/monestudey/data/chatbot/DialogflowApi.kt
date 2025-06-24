package com.example.monestudey.data.chatbot

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*

class DialogflowApi(private val context: Context) {
    private val client = OkHttpClient()
    private var accessToken: String? = null
    private val sessionId = UUID.randomUUID().toString()
    
    suspend fun initialize() = withContext(Dispatchers.IO) {
        try {
            accessToken = generateAccessToken()
        } catch (e: Exception) {
            throw RuntimeException("Error al inicializar Dialogflow: ${e.message}")
        }
    }

    suspend fun sendMessage(text: String): String = withContext(Dispatchers.IO) {
        try {
            val token = accessToken ?: throw RuntimeException("No hay token de acceso")
            
            val requestBody = JSONObject().apply {
                put("queryInput", JSONObject().apply {
                    put("text", JSONObject().apply {
                        put("text", text)
                        put("languageCode", DialogflowConfig.LANGUAGE_CODE)
                    })
                })
            }.toString()

            val request = Request.Builder()
                .url("https://dialogflow.googleapis.com/v2/projects/${DialogflowConfig.PROJECT_ID}/agent/sessions/$sessionId:detectIntent")
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .post(requestBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                throw RuntimeException("Error HTTP: ${response.code} - ${response.message}")
            }
            
            val responseBody = response.body?.string() ?: throw RuntimeException("Respuesta vacía")
            val jsonResponse = JSONObject(responseBody)
            
            val fulfillmentText = jsonResponse
                .optJSONObject("queryResult")
                ?.optString("fulfillmentText")
                ?: "Lo siento, no pude procesar tu mensaje."
                
            return@withContext fulfillmentText
            
        } catch (e: Exception) {
            throw RuntimeException("Error al enviar mensaje a Dialogflow: ${e.message}")
        }
    }

    private fun generateAccessToken(): String {
        try {
            val credentialsJson = context.resources.openRawResource(
                context.resources.getIdentifier(
                    DialogflowConfig.CREDENTIALS_FILE.replace(".json", ""),
                    "raw",
                    context.packageName
                )
            ).bufferedReader().use { it.readText() }
            
            val credentials = JSONObject(credentialsJson)
            val privateKey = credentials.getString("private_key")
            val clientEmail = credentials.getString("client_email")
            
            val jwt = createJWT(clientEmail, privateKey)
            
            val tokenRequest = Request.Builder()
                .url("https://oauth2.googleapis.com/token")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(FormBody.Builder()
                    .add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                    .add("assertion", jwt)
                    .build())
                .build()

            val response = client.newCall(tokenRequest).execute()
            
            if (!response.isSuccessful) {
                throw RuntimeException("Error al obtener token: ${response.code}")
            }
            
            val responseBody = response.body?.string() ?: throw RuntimeException("Respuesta de token vacía")
            val tokenResponse = JSONObject(responseBody)
            
            return tokenResponse.getString("access_token")
            
        } catch (e: Exception) {
            throw RuntimeException("Error al generar token de acceso: ${e.message}")
        }
    }

    private fun createJWT(clientEmail: String, privateKey: String): String {
        val header = JSONObject().apply {
            put("alg", "RS256")
            put("typ", "JWT")
        }
        
        val now = System.currentTimeMillis() / 1000
        val claim = JSONObject().apply {
            put("iss", clientEmail)
            put("scope", "https://www.googleapis.com/auth/dialogflow")
            put("aud", "https://oauth2.googleapis.com/token")
            put("exp", now + 3600)
            put("iat", now)
        }
        
        val headerBase64 = android.util.Base64.encodeToString(
            header.toString().toByteArray(),
            android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP
        )
        
        val claimBase64 = android.util.Base64.encodeToString(
            claim.toString().toByteArray(),
            android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP
        )
        
        val signature = signJWT("$headerBase64.$claimBase64", privateKey)
        
        return "$headerBase64.$claimBase64.$signature"
    }

    private fun signJWT(payload: String, privateKey: String): String {
        try {
            // Decodificar la clave privada de Base64
            val privateKeyBytes = android.util.Base64.decode(
                privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("\n", "")
                    .replace(" ", ""),
                android.util.Base64.DEFAULT
            )
            
            val keyFactory = java.security.KeyFactory.getInstance("RSA")
            val keySpec = java.security.spec.PKCS8EncodedKeySpec(privateKeyBytes)
            val privateKeyObj = keyFactory.generatePrivate(keySpec)
            
            val signature = java.security.Signature.getInstance("SHA256withRSA")
            signature.initSign(privateKeyObj)
            signature.update(payload.toByteArray())
            
            return android.util.Base64.encodeToString(
                signature.sign(),
                android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP
            )
        } catch (e: Exception) {
            throw RuntimeException("Error al firmar JWT: ${e.message}")
        }
    }
} 
package com.goat.app.business.sync

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class SyncClient(val serverAddress: String = "http://127.0.0.1:8080") {
    private val notesEndpoint = "notes"
    private val foldersEndpoint = "folders"

    fun serverExists(): Boolean {
        val url = URL("$serverAddress/$notesEndpoint")
        val huc: HttpURLConnection = url.openConnection() as HttpURLConnection

        var responseCode: Int

        try {
            responseCode = huc.getResponseCode()
        }
        catch (e: java.net.ConnectException) {
            return false
        }
        return HttpURLConnection.HTTP_OK == responseCode
    }

    fun getNotes(): List<NoteMessage> {
        val client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(20))
            .build()

        val request = HttpRequest.newBuilder()
            .uri(URI.create("$serverAddress/$notesEndpoint"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val noteMsgs = Json.decodeFromString<List<NoteMessage>>(response.body())
        return noteMsgs
    }

    fun postNote(note: NoteMessage): Int {
        val string = Json.encodeToString(note)

        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$serverAddress/$notesEndpoint"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(string))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.statusCode()
    }

    fun deleteAllNotes(): Int {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$serverAddress/$notesEndpoint"))
            .DELETE()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.statusCode()
    }

    fun getFolders(): List<FolderMessage> {
        val client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(20))
            .build()

        val request = HttpRequest.newBuilder()
            .uri(URI.create("$serverAddress/$foldersEndpoint"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val folderMsgs = Json.decodeFromString<List<FolderMessage>>(response.body())
        return folderMsgs
    }

    fun postFolder(folder: FolderMessage): Int {
        val string = Json.encodeToString(folder)

        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$serverAddress/$foldersEndpoint"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(string))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.statusCode()
    }

    fun deleteAllFolders(): Int {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$serverAddress/$foldersEndpoint"))
            .DELETE()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.statusCode()
    }
}
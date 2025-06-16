package com.example.apptest2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import com.example.apptest2.BuildConfig

class MainActivity : AppCompatActivity() {

    private val updateJsonUrl = BuildConfig.CLOUDFRONT_JSON_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.checkUpdateButton).setOnClickListener {
            checkForUpdate()
        }
    }

    private fun checkForUpdate() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val conn = URL(updateJsonUrl).openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000

                val jsonStr = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(jsonStr)

                val latestVersion = json.optString("version")
                val apkUrl = json.optString("apkUrl")
                val description = json.optString("description")
                val branch = json.optString("branch")
                val currentVersion = BuildConfig.VERSION_NAME

                val isNew = isNewVersion(currentVersion, latestVersion)

                withContext(Dispatchers.Main) {
                    if (isNew) {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("업데이트 가능")
                            .setMessage("새 버전이 있습니다.\n\n버전: $latestVersion\n설명: $description")
                            .setPositiveButton("업데이트") { _, _ ->
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl)))
                            }
                            .setNegativeButton("나중에", null)
                            .show()
                    } else {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("최신 버전입니다")
                            .setMessage("브랜치: $branch\n버전: $latestVersion\n$description")
                            .setPositiveButton("확인", null)
                            .show()
                    }
                }
            } catch (e: Exception) {
                Log.e("UpdateCheck", "JSON 파싱 실패", e)
                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("업데이트 확인 실패")
                        .setMessage("인터넷 상태를 확인해주세요.")
                        .setPositiveButton("확인", null)
                        .show()
                }
            }
        }
    }

    private fun isNewVersion(current: String, latest: String): Boolean {
        val cur = current.split(".").mapNotNull { it.toIntOrNull() }
        val lat = latest.split(".").mapNotNull { it.toIntOrNull() }
        for (i in 0 until maxOf(cur.size, lat.size)) {
            val c = cur.getOrElse(i) { 0 }
            val l = lat.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}

package com.inspiredandroid.braincup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ChallengeDeeplinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent?.data
        val data = uri?.getQueryParameter("data")

        if (uri != null && data != null) {
            showChallenge(data, uri.toString())
        } else {
            showMenu()
        }

        finish()
    }

    private fun showChallenge(data: String, url: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("challenge", data)
        intent.putExtra("challengeUrl", url)
        startActivity(intent)
    }

    private fun showMenu() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

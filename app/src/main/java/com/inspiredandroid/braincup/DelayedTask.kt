package com.inspiredandroid.braincup

import android.os.AsyncTask

/**
 * Temporary solution until coroutines will work
 */
class DelayedTask : AsyncTask<() -> Unit, Int, () -> Unit>() {
    override fun doInBackground(vararg next: () -> Unit): () -> Unit {
        Thread.sleep(1000)
        return next[0]
    }

    override fun onPostExecute(result: () -> Unit) {
        result()
    }
}
package hellojdh.wintercoding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import hellojdh.wintercoding.net.FileDownload
import hellojdh.wintercoding.tool.BaseActivity
import kotlinx.android.synthetic.main.activity_detail.*
import okhttp3.*
import java.io.File
import java.io.IOException

/*
 * 상세 화면
 */
class DetailActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        titleText = intent.getStringExtra("title")
        url = intent.getStringExtra("url")
        Log.d(TAG, "Title = $title")
        Log.d(TAG, "Url = $url")

        detail_title.text = titleText
        Glide.with(this).load(url).into(detail_imageView)

        save_button.setOnClickListener { clickToDownload() }
    }

    private fun clickToDownload() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            return
        }

        val dir = File(SAVE_PATH)
        Log.d(TAG, "File path =" + dir.absolutePath)
        if (!dir.exists())
            dir.mkdirs()

        val target = File(SAVE_PATH, "/" + System.currentTimeMillis() + ".jpg")
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url!!)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                FileDownload.downFile(target, response.body(), this@DetailActivity)
                makeToast("갤러리에 저장되었습니다")
            }
        })
    }

    internal fun makeToast(t: String) {
        runOnUiThread { Toast.makeText(this, t, Toast.LENGTH_SHORT).show() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeToast("권한이 승인되었습니다.")
        } else {
            makeToast("권한을 승인시 다운로드가 가능합니다.")
        }
    }

    companion object {
        private val TAG = DetailActivity::class.java.simpleName
        private val SAVE_PATH = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                + File.separator + "winterCoding" + File.separator)
        private var url: String? = null
        private var titleText: String? = null
    }
}

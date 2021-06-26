package com.devventure.colormyviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.devventure.colormyviews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var pincelColor = R.color.grey
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var editor: SharedPreferences.Editor
    private var boxes = arrayOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBoxesArray()
        setupBox()
        setupButtonColor()
        requestUserPermissions()
        configureSharedPref()
        configureBoxBackground()
        configureShareBtn()
    }

    private fun initBoxesArray() {
        boxes = arrayOf(
            binding.boxOne, binding.boxTwo, binding.boxThree,
            binding.boxFour, binding.boxFive
        )
    }

    private fun setupButtonColor() {
        binding.btnRed.setOnClickListener {
            pincelColor = R.color.red
        }
        binding.btnYellow.setOnClickListener {
            pincelColor = R.color.yellow
        }
        binding.btnGreen.setOnClickListener {
            pincelColor = R.color.green
        }

    }

    private fun setupBox() {
        binding.boxOne.setOnClickListener {
            it.setBackgroundResource(pincelColor)
            editor.putInt(it.id.toString(), pincelColor)
        }
        binding.boxTwo.setOnClickListener {
            it.setBackgroundResource(pincelColor)
            editor.putInt(it.id.toString(), pincelColor)
        }
        binding.boxThree.setOnClickListener {
            it.setBackgroundResource(pincelColor)
            editor.putInt(it.id.toString(), pincelColor)
        }
        binding.boxFour.setOnClickListener {
            it.setBackgroundResource(pincelColor)
            editor.putInt(it.id.toString(), pincelColor)
        }
        binding.boxFive.setOnClickListener {
            it.setBackgroundResource(pincelColor)
            editor.putInt(it.id.toString(), pincelColor)
        }
    }

    private fun requestUserPermissions() {
        val permissions = getPermissionsList()
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(it),
                    REQUEST_CODE_CONST
                )
            }
        }
    }

    private fun getPermissionsList(): Array<String> {
        val info: PackageInfo =
            packageManager.getPackageInfo(
                applicationContext.packageName,
                PackageManager.GET_PERMISSIONS
            )
        return info.requestedPermissions
    }

    private fun configureSharedPref() {
        sharedPreferences = getSharedPreferences(COLORS_CONST, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    private fun configureBoxBackground() {
        boxes.forEach {
            findViewById<View>(it.id).setBackgroundResource(getColorBox(it.id.toString()))
        }
    }

    private fun configureShareBtn() {
        binding.floatingActionButton.setOnClickListener {
            val bitmap = getViewAsBitmap(binding.clBoxes)
            if (bitmap != null) {
                initScreenshot(bitmap, FILE_NAME_CONST)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        editor.apply()
    }

    private fun getColorBox(idBox: String): Int {
        return sharedPreferences.getInt(idBox, R.color.grey)
    }

    /**
     * Função recebe uma view e a converte em bitmap de acordo com suas dimensões.
     * @param mView view a ser convertida em bitmap
     * @return View em formato bitmap
     */
    private fun getViewAsBitmap(mView: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(mView.width, mView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDraw = mView.background
        if (bgDraw != null)
            bgDraw.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)
        mView.draw(canvas)
        return bitmap
    }

    /**
     * Função faz a validação URI vazio e aciona o compartilhamento
     */
    private fun initScreenshot(imageBitmap: Bitmap, filename: String) {
        var uri = saveScreenshot(imageBitmap, filename, this)
        if (uri == Uri.EMPTY) {
            Toast.makeText(applicationContext, URI_EXCEPTION_CONST, Toast.LENGTH_LONG)
                .show()
        } else {
            shareImageUri(uri)
        }
    }

    /**
     * Compartilhar imagem JPEG a partir de URI
     * @param uri Uri da imagem a ser compartilhada.
     */
    @SuppressLint("QueryPermissionsNeeded")
    private fun shareImageUri(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            type = URI_TYPE_CONST
        }

        applicationContext?.packageManager?.run {
            if (shareIntent.resolveActivity(this) != null)
                startActivity(Intent.createChooser(shareIntent, IMG_TITLE_CONST))
            else
                Toast.makeText(applicationContext, IMG_TITLE_EXCEPTION_CONST, Toast.LENGTH_LONG)
                    .show()
        }
    }

    companion object {
        const val IMG_TITLE_CONST = "Share images to.."
        const val IMG_TITLE_EXCEPTION_CONST = "Impossível executar"
        const val URI_TYPE_CONST = "image/*"
        const val URI_EXCEPTION_CONST = "Não foi encontrado um meio de compartilhamento"
        const val COLORS_CONST = "colors"
        const val FILE_NAME_CONST = "ScreenshotView"
        const val REQUEST_CODE_CONST = 101
    }
}
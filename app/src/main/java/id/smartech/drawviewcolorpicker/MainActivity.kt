package id.smartech.drawviewcolorpicker

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import id.smartech.drawviewcolorpicker.databinding.ActivityMainBinding
import petrov.kristiyan.colorpicker.ColorPicker
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main)
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        drawingSetUp()
    }

    private fun getScreenShotFromView(v: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            screenshot = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        return screenshot
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this , "Image has been saved to Gallery" , Toast.LENGTH_SHORT).show()

        }
    }

    private fun hideAll() {
        bind.fbSave.visibility = View.GONE
        bind.fbClear.visibility = View.GONE
        bind.fbRedo.visibility = View.GONE
        bind.fbUndo.visibility = View.GONE
        bind.fbColourPicker.visibility = View.GONE
    }

    private fun showAll() {
        bind.fbSave.visibility = View.VISIBLE
        bind.fbClear.visibility = View.VISIBLE
        bind.fbRedo.visibility = View.VISIBLE
        bind.fbUndo.visibility = View.VISIBLE
        bind.fbColourPicker.visibility = View.VISIBLE
    }

    private fun drawingSetUp() {
        bind.dvDrawview.setStrokeWidth(20f)

        val colors = ArrayList<String>()
        colors.add("#FFFFFF")
        colors.add("#000000")
        colors.add("#FFFF0000")
        colors.add("#FFE91E63")
        colors.add("#FF9C27B0")
        colors.add("#FFEA80FC")
        colors.add("#FF673AB7")
        colors.add("#FF3F51B5")
        colors.add("#FF2196F3")
        colors.add("#FF03A9F4")
        colors.add("#FF00BCD4")
        colors.add("#FF009688")
        colors.add("#FF4CAF50")
        colors.add("#FF8BC34A")
        colors.add("#FFCDDC39")
        colors.add("#FFFFEB3B")
        colors.add("#FFFFC107")
        colors.add("#FFFF9800")
        colors.add("#FFFF5722")
        colors.add("#FFFF9E80")

        bind.fbColourPicker.setOnClickListener {
            val colorPicker = ColorPicker(this)
            colorPicker.setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(object : ColorPicker.OnChooseColorListener{
                    override fun onChooseColor(position: Int, color: Int) {
                        bind.dvDrawview.setColor(color)
                        bind.fbColourPicker.setColorFilter(color)
                    }

                    override fun onCancel() {
                        colorPicker.dismissDialog()
                    }
                }).show()
        }

        bind.fbBackground.setOnClickListener {
            val colorPicker = ColorPicker(this)
            colorPicker.setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(object : ColorPicker.OnChooseColorListener{
                    override fun onChooseColor(position: Int, color: Int) {
                        bind.dvDrawview.setBackgroundColor(color)
                        bind.fbBackground.setColorFilter(color)
                    }

                    override fun onCancel() {
                        colorPicker.dismissDialog()
                    }
                }).show()
        }

        bind.fbClear.setOnClickListener {
            bind.dvDrawview.clearCanvas()
        }

        bind.fbUndo.setOnClickListener {
            bind.dvDrawview.undo()
        }

        bind.fbRedo.setOnClickListener {
            bind.dvDrawview.redo()
        }

        bind.fbSave.setOnClickListener {
            hideAll()
            val bitmap = getScreenShotFromView(bind.cvDraw)
            if(bitmap != null) {
                saveMediaToStorage(bitmap)
            }
            showAll()
        }
    }


}
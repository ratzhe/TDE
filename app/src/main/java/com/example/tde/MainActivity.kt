package com.example.tde

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap

class MainActivity : AppCompatActivity() {

    val imagem = 1
    private lateinit var imageView: ImageView
    private lateinit var tirar_foto_btn: Button
    private lateinit var filtro1_btn: Button
    private lateinit var filtro2_btn: Button
    private lateinit var tirar_filtro_btn: Button
    private var originalBitmap: Bitmap? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)

        tirar_foto_btn = findViewById(R.id.tirar_foto)
        tirar_foto_btn.setOnClickListener{
            dispatchTakePictureIntent()
        }

        filtro1_btn = findViewById(R.id.filtro1)
        filtro1_btn.setOnClickListener{
            val bitmap = this.imageView.drawToBitmap()
            this.imageView.setImageBitmap(applySepiaFilter(bitmap))
        }

        filtro2_btn = findViewById(R.id.filtro2)
        filtro2_btn.setOnClickListener {
            val currentBitmap = (imageView.drawable as? BitmapDrawable)?.bitmap
            currentBitmap?.let {
                val filteredBitmap = applyHighSaturationFilter(it)
                imageView.setImageBitmap(filteredBitmap)
            }
        }

        tirar_filtro_btn = findViewById(R.id.tirar_filtro)
        tirar_filtro_btn.setOnClickListener{
            originalBitmap?.let {
                imageView.setImageBitmap(it)
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, imagem)
            }
        }
    }

    private fun getOriginalBitmap(): Bitmap {
        return (imageView.drawable as? BitmapDrawable)?.bitmap ?: throw IllegalStateException("Imagem original não encontrada.")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagem && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            this.imageView.setImageBitmap(imageBitmap)
            originalBitmap = imageBitmap
        }
    }

    private fun applyHighSaturationFilter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        val paint = Paint()
        val saturacao = 2.0f //
        val matrix = ColorMatrix().apply {
            setSaturation(saturacao)
        }

        val filtro = ColorMatrixColorFilter(matrix)
        paint.colorFilter = filtro
        val srcRect = Rect(0, 0, width, height)
        val destRect = Rect(0, 0, width, height)

        canvas.drawBitmap(bitmap, srcRect, destRect, paint)

        return outputBitmap
    }

    private fun applySepiaFilter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val sepiaBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)

                val originalRed = Color.red(pixel)
                val originalGreen = Color.green(pixel)
                val originalBlue = Color.blue(pixel)

                val newRed = (originalRed * 0.393 + originalGreen * 0.769 + originalBlue * 0.189).toInt()
                val newGreen = (originalRed * 0.349 + originalGreen * 0.686 + originalBlue * 0.168).toInt()
                val newBlue = (originalRed * 0.272 + originalGreen * 0.534 + originalBlue * 0.131).toInt()

                val finalRed = newRed.coerceIn(0, 255)
                val finalGreen = newGreen.coerceIn(0, 255)
                val finalBlue = newBlue.coerceIn(0, 255)

                val sepiaPixel = Color.rgb(finalRed, finalGreen, finalBlue)

                sepiaBitmap.setPixel(x, y, sepiaPixel)
            }
        }

        return sepiaBitmap
    }

}
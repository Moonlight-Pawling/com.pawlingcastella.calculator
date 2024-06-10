package com.pawlingcastella.calculator

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import com.pawlingcastella.calculator.databinding.ActivityEditBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var changephotobutton: ImageButton
    private lateinit var deletephotobutton: ImageButton
    lateinit var Imageviewineditactivity: ImageView
    private lateinit var imageFile: File
    private val fileName = "official_profile_image.png"
    private lateinit var sharedPreferences: SharedPreferences
    val defaultname = "Asking to Stack Overflow"
    val defaultcorreo = "stack@overflow.com"
    val defaultweb = "https://stackoverflow.com"
    val defaultphone = "+01 2345678910"
    val defaultlat = 19.16571861761356
    val defaultlon = -96.11419823223545
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = "Editar perfil"
        }

        Imageviewineditactivity = findViewById(R.id.edit_profile_picture)

        val storageDirectory = filesDir
        val imageDirectory = File(storageDirectory, getString(R.string.profile_picture_path))

        imageFile = File(imageDirectory, fileName)
        setImageViewFromLocalFile()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        setFocusLats()


        binding.etnombre.setText(sharedPreferences.getString(getString(R.string.k_name), defaultname))
        binding.etcorreo.setText(sharedPreferences.getString(getString(R.string.k_email), defaultcorreo))
        binding.etsitioweb.setText(sharedPreferences.getString(getString(R.string.k_web), defaultweb))
        binding.etphone.setText(sharedPreferences.getString(getString(R.string.k_phone), defaultphone))
        binding.etlat.setText(sharedPreferences.getString(getString(R.string.k_lat), defaultlat.toString()))
        binding.etlon.setText(sharedPreferences.getString(getString(R.string.k_lon), defaultlon.toString()))

        changephotobutton = findViewById(R.id.changephotobutton)
        deletephotobutton = findViewById(R.id.deletephotobutton)

        changephotobutton.setOnClickListener {
            pickImage()
            /*val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply{
            addcategory(Intent.category_openable)
            type = "image/jpeg"
            }
            startActivityForResult(intent, 72)*/
        }

        deletephotobutton.setOnClickListener {
            removeImage()
            Imageviewineditactivity.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.img_avatar, null))
        }
    }

    private fun removeImage(){
        val storageDirectory = filesDir
        val imageDirectory = File(storageDirectory, getString(R.string.profile_picture_path))
        val imageFile = File(imageDirectory, fileName)

        if (imageFile.exists()) {
            imageFile.delete()
            MediaScannerConnection.scanFile(this, arrayOf(imageFile.absolutePath), null, null)
            Toast.makeText(this, "Imagen eliminada con éxito", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No hay imagen para eliminar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setFocusLats() {
        binding.etnombre.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                binding.etnombre.text?.let { binding.etnombre.setSelection(it.length) }
            }
        }

        binding.etcorreo.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                binding.etcorreo.text?.let { binding.etcorreo.setSelection(it.length) }
            }
        }

        binding.etsitioweb.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                binding.etsitioweb.text?.let { binding.etsitioweb.setSelection(it.length) }
            }
        }

        binding.etphone.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                binding.etphone.text?.let { binding.etphone.setSelection(it.length) }
            }
        }

        binding.etlat.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                binding.etlat.text?.let { binding.etlat.setSelection(it.length) }
            }
        }

        binding.etlon.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                binding.etlon.text?.let { binding.etlon.setSelection(it.length) }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit,menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun checkForExistingImage(fileName: String): Boolean {
        val storageDirectory = filesDir
        val imageDirectory = File(storageDirectory, getString(R.string.profile_picture_path))

        val imageFile = File(imageDirectory, fileName)
        return imageFile.exists()
    }
    private fun setImageViewFromLocalFile() { val doesExist = checkForExistingImage(fileName)
        if (doesExist) {
            try {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                Imageviewineditactivity.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                // Handle the case where the file doesn't exist (e.g., show a toast)
                e.printStackTrace()
            }
        } else {
            binding.editProfilePicture.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.img_avatar, null))
        }
    }
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickMediaLauncher.launch(intent)
    }

    private val pickMediaLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val imageUri = data?.data // Use safe call to handle null data
            if (imageUri != null) {
                saveImage(imageUri)
            } else {
                // Handle the case where no image was selected (e.g., user canceled)
            }
        }
    }

    private fun saveImage(imageUri: Uri) {
        try {

            // Actualizar la galería del dispositivo para mostrar la imagen guardada
            MediaScannerConnection.scanFile(this, arrayOf(imageFile.absolutePath), null, null)
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // Crear un nombre de archivo único para la imagen
            val fileName = "official_profile_image.png"

            // Obtener la carpeta de almacenamiento de imágenes
            val storageDirectory = filesDir
            val imageDirectory = File(storageDirectory, getString(R.string.profile_picture_path))
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs() // Create the directory if it doesn't exist
            }

            // Crear el archivo de imagen y escribir el bitmap
            val imageFile = File(imageDirectory, fileName)
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

            // Actualizar la galería del dispositivo para mostrar la imagen guardada
            MediaScannerConnection.scanFile(this, arrayOf(imageFile.absolutePath), null, null)


            // Mostrar un mensaje de éxito
            Toast.makeText(this, "Imagen guardada con éxito", Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException) {
            // Manejar la excepción de archivo no encontrado
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            // Manejar la excepción de E/S
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
        }
        setImageViewFromLocalFile()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save) {
            if (DataValidation()==true) {
                saveSharedPref()
                sendData()
            }
            //sendData()
        }
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    fun saveSharedPref(){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        with (sharedPreferences.edit()) {
            putString(getString(R.string.k_name), binding.etnombre.text.toString())
            putString(getString(R.string.k_email), binding.etcorreo.text.toString())
            putString(getString(R.string.k_web), binding.etsitioweb.text.toString())
            putString(getString(R.string.k_phone), binding.etphone.text.toString())
            putString(getString(R.string.k_lat), binding.etlat.text.toString())
            putString(getString(R.string.k_lon), binding.etlon.text.toString())
            apply()
        }
    }
    fun sendData() {
        val intent = Intent()
        /*intent.putExtra(getString(R.string.k_name), binding.etnombre.text.toString())
        intent.putExtra(getString(R.string.k_email), binding.etcorreo.text.toString())
        intent.putExtra(getString(R.string.k_web), binding.etsitioweb.text.toString())
        intent.putExtra(getString(R.string.k_phone), binding.etphone.text.toString())
        intent.putExtra(getString(R.string.k_lat), binding.etlat.text.toString())
        intent.putExtra(getString(R.string.k_lon), binding.etlon.text.toString())*/
        setResult(RESULT_OK,intent)
        finish()
    }

    private fun DataValidation(): Boolean{
        var ValidationBooleanStatus = true

        //Name Validation
        var nombre = binding.etnombre.text.toString().trim()

        if (nombre.length<3 && nombre.isNotEmpty()){
            binding.boxNombre.run {
                error = "El nombre debe contener al menos 3 caracteres"
                requestFocus()
            }
            ValidationBooleanStatus = false
        }
        else if (nombre.isEmpty()){
            binding.boxNombre.run {
                error = "El nombre no puede estar vacío"
                requestFocus()
            }
            ValidationBooleanStatus = false
        } else {
            binding.boxNombre.error = null
        }

        //eMail validation
        if (Patterns.EMAIL_ADDRESS.matcher(binding.etcorreo.text.toString()).matches() && binding.etcorreo.text.toString().isNotEmpty()){
            binding.boxCorreo.error = null
        } else if (binding.etcorreo.text.isNullOrEmpty()){
            binding.boxCorreo.run{
                error = "El correo no puede estar vacío"
                requestFocus()
            }
            ValidationBooleanStatus = false
        } else {
            binding.boxCorreo.run{
                error = "Formato inválido. " +
                        "Ej: algo.algo@servicio.dominio"
                requestFocus()
            }
            ValidationBooleanStatus = false
        }

        //Website validation
        if (Patterns.WEB_URL.matcher(binding.etsitioweb.text.toString()).matches() && binding.etsitioweb.text.toString().isNotEmpty()){
            binding.boxWeb.error = null
        } else if (binding.etsitioweb.text.isNullOrEmpty()){
            binding.boxWeb.run{
                error = "El sitio web no puede estar vacío"
                requestFocus()
            }
            ValidationBooleanStatus = false
        } else {
            binding.boxWeb.run{
                error = "Formato inválido. " +
                        "Ej: https://miuv.com/frivera"
                requestFocus()
            }
            ValidationBooleanStatus = false
        }

        //Phone validation
        var telephonicnumber = binding.etphone.text.toString().trim()
        if (Patterns.PHONE.matcher(binding.etphone.text.toString()).matches() && binding.etphone.text.toString().isNotEmpty() && telephonicnumber.length>=7){
            binding.boxContacto.error = null
        } else if (binding.etphone.text.isNullOrEmpty()){
            binding.boxContacto.run{
                error = "El teléfono no puede estar vacío"
                requestFocus()
            }
            ValidationBooleanStatus = false
        } else if (telephonicnumber.length<7){
            binding.boxContacto.run{
                error = "7 dígitos para telefono fijo. 10 para celular."
                requestFocus()
            }
            ValidationBooleanStatus = false
        } else {
            binding.boxContacto.run{
                error = "Formato inválido. " +
                        "Ej: +52 2291590846"
                requestFocus()
            }
            ValidationBooleanStatus = false
        }

        //Lat validation
        if (binding.etlat.text.toString().matches(Regex("^(-?[0-9]+)(\\.[0-9]+)\$")) && binding.etlat.text.toString().isNotEmpty() && (binding.etlat.text.toString().toDouble() <= 90 && binding.etlat.text.toString().toDouble() >= -90)){
            binding.boxLat.error = null
        } else if (binding.etlat.text.isNullOrEmpty()){
            binding.boxLat.run{
                error = "La latitud no puede estar vacía"
                requestFocus()
            }
            ValidationBooleanStatus = false
        } else if (binding.etlat.text.toString().toDouble() > 90 || binding.etlat.text.toString().toDouble() < -90){
            binding.boxLat.run{
                error = "La latitud debe estar entre -90 y 90"
                requestFocus()
            }
            ValidationBooleanStatus = false
        } else if (!(binding.etlat.text.toString().matches(Regex("^(-?[0-9]+)(\\.[0-9]+)\$")))) {
            binding.boxLat.run {
                error = "La latitud debe ser un número con punto decimal"
                requestFocus()
            }
            ValidationBooleanStatus = false
        }


        //Lon validation
        if (binding.etlon.text.toString().matches(Regex("^(-?[0-9]+)(\\.[0-9]+)\$")) && binding.etlon.text.toString().isNotEmpty() && (binding.etlon.text.toString().toDouble() <= 90 && binding.etlon.text.toString().toDouble() >= -90)){
            binding.boxLon.error = null
        } else if (binding.etlon.text.isNullOrEmpty()){
            binding.boxLon.run{
                error = "La longitud no puede estar vacía"
                requestFocus()
            }
            ValidationBooleanStatus = false
        } else if (binding.etlon.text.toString().toDouble() > 180 || binding.etlon.text.toString().toDouble() < -180){
            binding.boxLon.run{
                error = "La longitud debe estar entre -180° y 180°"
                requestFocus()
            }
            ValidationBooleanStatus = false
        } else if (!(binding.etlon.text.toString().matches(Regex("^(-?[0-9]+)(\\.[0-9]+)\$")))) {
            binding.boxLon.run {
                error = "La longitud debe ser un número con punto decimal"
                requestFocus()
            }
            ValidationBooleanStatus = false
        }

        return ValidationBooleanStatus
    }

}



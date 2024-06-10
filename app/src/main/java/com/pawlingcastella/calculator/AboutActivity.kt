package com.pawlingcastella.calculator

import android.app.SearchManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
//import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.pawlingcastella.calculator.databinding.ActivityAboutBinding
import java.io.File
import java.io.FileNotFoundException

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    private lateinit var sharedPreferences: SharedPreferences
    public var UbicationButtomTest: Int = 0
    /*private var lat: Double = 0.0
    private var lon: Double = 0.0*/
    private lateinit var Imageviewinmainactivity: ImageView
    private var mapbuttonclickactionable: Boolean = false
    private lateinit var imageFile: File
    private val fileName = "official_profile_image.png"
    var defaultname = "Asking to Stack Overflow"
    var defaultcorreo = "stack@overflow.com"
    var defaultweb = "https://stackoverflow.com"
    var defaultphone = "+01 2345678910"
    var defaultlat : Double = 19.16571861761356
    var defaultlon : Double = -96.11419823223545
    val sharedPrefLat : Double = 0.0
    val sharedPrefLon : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = "Acerca de..."
        }

        sharedPreferences = getDefaultSharedPreferences(this)
        getSharedPref()
        Imageviewinmainactivity = findViewById(R.id.main_profile_picture)
        //updateUI()
        val storageDirectory = filesDir
        val imageDirectory = File(storageDirectory, getString(R.string.profile_picture_path))
        imageFile = File(imageDirectory, fileName)
        setImageViewFromLocalFile()
        //lat = 19.16571861761356
        //lon = -96.11419823223545
        setupIntent()
        getUserData()
    }

    private fun getUserData() {
    }

    private fun setImageViewFromLocalFile() {
        val doesExist = checkForExistingImage(fileName)
        if (doesExist) {
            try {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                Imageviewinmainactivity.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                // Handle the case where the file doesn't exist (e.g., show a toast)
                e.printStackTrace()
            }
        } else {
            binding.mainProfilePicture.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.img_avatar,
                    null
                )
            )
        }
    }

    private fun checkForExistingImage(fileName: String): Boolean {
        val storageDirectory = filesDir
        val imageDirectory = File(storageDirectory, getString(R.string.profile_picture_path))
        val imageFile = File(imageDirectory, fileName)
        return imageFile.exists()
    }

    fun getSharedPref() {
        val sharedPreferences = getDefaultSharedPreferences(this)

        val sharedPrefName = sharedPreferences.getString(getString(R.string.k_name), defaultname)
        val sharedPrefCorreo = sharedPreferences.getString(getString(R.string.k_email), defaultcorreo)
        val sharedPrefWeb = sharedPreferences.getString(getString(R.string.k_web), defaultweb)
        val sharedPrefPhone = sharedPreferences.getString(getString(R.string.k_phone), defaultphone)
        val sharedPrefLat = sharedPreferences.getString(getString(R.string.k_lat), defaultlat.toString())
        val sharedPrefLon = sharedPreferences.getString(getString(R.string.k_lon), defaultlon.toString())

        updateUI(sharedPrefName!!, sharedPrefCorreo!!, sharedPrefWeb!!, sharedPrefPhone!!)
    }

    private fun updateUI(
        name: String = "Asking to Stack Overflow",
        correo: String = "stack@overflow.com",
        web: String = "https://stackoverflow.com",
        phone: String = "+01 2345678910"
    ) {
        binding.profileTvNombre.text = name
        binding.profileTvCorreo.text = correo
        binding.profileTvWeb.text = web
        binding.profileTvPhone.text = phone
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_about, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun launchIntent(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                getString(R.string.compatible_app_not_found_text), Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun setupIntent() {
        binding.profileTvNombre.setOnClickListener {
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, binding.profileTvNombre.text)
            }
            launchIntent(intent)
        }

        //Envio de correo
        binding.profileTvCorreo.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, binding.profileTvCorreo.text.toString())
                putExtra(Intent.EXTRA_SUBJECT, "Automatico INTENT")
                putExtra(Intent.EXTRA_TEXT, "Some text here")
            }
            launchIntent(intent)
        }

        //Sitio Web
        binding.profileTvWeb.setOnClickListener {
            val webText = Uri.parse(binding.profileTvWeb.text.toString())
            val intent = Intent(Intent.ACTION_VIEW, webText)
            launchIntent(intent)
        }

        //Telefono
        binding.profileTvPhone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                val phone = (it as TextView).text
                data = Uri.parse("tel:$phone")
            }
            launchIntent(intent)
        }

        //Mapa
        binding.profileMostrar.setOnClickListener {
            if (mapbuttonclickactionable == false) {
                val sharedPrefLat = sharedPreferences.getString(getString(R.string.k_lat), defaultlat.toString())
                val sharedPrefLon = sharedPreferences.getString(getString(R.string.k_lon), defaultlon.toString())
                binding.profileMostrar.text = "Lat: $sharedPrefLat \nLon: $sharedPrefLon"
                mapbuttonclickactionable = true
                // Crea una cadena Uri a partir de una cadena de intención. Usa el resultado para crear una intención.
                val mapcordinates: String = "$sharedPrefLat, $sharedPrefLon"
                val gmmIntentUri = Uri.parse("geo:$sharedPrefLat,$sharedPrefLon?q=$mapcordinates?z=17")
                // Crea una intención a partir de gmmIntentUri. Establece la acción en ACTION_VIEW
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                // Haz que la intención sea explícita configurando el paquete de Google Maps
                mapIntent.setPackage("com.google.android.apps.maps")

                // Intenta iniciar una actividad que pueda manejar la intención
                startActivity(mapIntent)
            } else if (mapbuttonclickactionable == true) {
                mapbuttonclickactionable = false
                binding.profileMostrar.text = "Mostrar Ubicación"
            }
        }

        //Abrir config location
        binding.profileAjustes.setOnClickListener {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            launchIntent(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
            }

            R.id.action_edit -> {
                val intent = Intent(this, EditActivity::class.java)
                editResult.launch(intent)
                /*intent.putExtra(
                    getString(R.string.k_name),
                    binding.profileTvNombre.text.toString().trim()
                )
                intent.putExtra(
                    getString(R.string.k_email),
                    binding.profileTvCorreo.text.toString().trim()
                )
                intent.putExtra(
                    getString(R.string.k_web),
                    binding.profileTvWeb.text.toString().trim()
                )
                intent.putExtra(
                    getString(R.string.k_phone),
                    binding.profileTvPhone.text.toString().trim()
                )
                intent.putExtra(getString(R.string.k_lat), lat.toString())
                intent.putExtra(getString(R.string.k_lon), lon.toString())

                //startActivity(Intent) <-Solo lanza una vista
                //startActivity(intent) //<- Lanza y espera una respuesta*/

            }

            R.id.action_delete -> {
                sharedPreferences.edit().clear().apply()
                removeImage()
                getSharedPref()
                val sharedPrefLat = sharedPreferences.getString(getString(R.string.k_lat), defaultlat.toString())
                val sharedPrefLon = sharedPreferences.getString(getString(R.string.k_lon), defaultlon.toString())
                mapbuttonclickactionable = false
                binding.profileMostrar.text = "Mostrar Ubicación"
                setImageViewFromLocalFile()
                Toast.makeText(this, "Datos eliminados con éxito", Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun removeImage(){
        val storageDirectory = filesDir
        val imageDirectory = File(storageDirectory, getString(R.string.profile_picture_path))
        val imageFile = File(imageDirectory, fileName)

        if (imageFile.exists()) {
            imageFile.delete()
            MediaScannerConnection.scanFile(this, arrayOf(imageFile.absolutePath), null, null)
            //Toast.makeText(this, "Imagen eliminada con éxito", Toast.LENGTH_SHORT).show()
        } else {
            //Toast.makeText(this, "No hay imagen para eliminar", Toast.LENGTH_SHORT).show()
        }
    }

    private val editResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                /*val name = it.data?.getStringExtra(getString(R.string.k_name))
                val correo = it.data?.getStringExtra(getString(R.string.k_email))
                val web = it.data?.getStringExtra(getString(R.string.k_web))
                val phone = it.data?.getStringExtra(getString(R.string.k_phone))
                lat = it.data?.getStringExtra(getString(R.string.k_lat))?.toDouble() ?: 0.0
                lon = it.data?.getStringExtra(getString(R.string.k_lon))?.toDouble() ?: 0.0*/
                setImageViewFromLocalFile()
                getSharedPref()
                mapbuttonclickactionable = false
                binding.profileMostrar.text = "Mostrar Ubicación"
            }
        }
}



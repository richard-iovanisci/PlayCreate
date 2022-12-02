package com.example.playcreate.ui

import android.app.Activity
import android.content.Context
import android.icu.text.CaseMap.Title
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.playcreate.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import com.example.playcreate.glide.Glide
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.util.Random
import kotlin.math.max

// XXX Write most of this file
class HomeFragment: Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var seekBar: SeekBar


    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        Log.d(javaClass.simpleName, "onViewCreated")

        //in case reAuth
        // empty fields
        binding.artistET.text.clear()
        binding.playlistET.text.clear()
        binding.artNameTV.text = ""
        // remove image
        binding.artistImage.setImageDrawable(null)
        //stage viewModel
        viewModel.resetLiveData()

        // on click listeners
        binding.searchBut.setOnClickListener{
            if(binding.artistET.text.isEmpty()){
                Toast.makeText(this.context, "Please Search for an Artist", Toast.LENGTH_SHORT).show()
                hideKeyboard()
                return@setOnClickListener
            }
            if(viewModel.getArtId().value?.isEmpty() == true){
                Toast.makeText(this.context, "No Valid Artist Id Found", Toast.LENGTH_SHORT).show()
                hideKeyboard()
                return@setOnClickListener
            }
            Log.d("Token for search artist", "${viewModel.getAccessToken().value}")
            Log.d("Term for search artist", "${binding.artistET.text.toString()}")

            //make request
            fetchArtistId(viewModel.getAccessToken().value, binding.artistET.text.toString())

            // hide keys
            hideKeyboard()
        }

        //seekbar
        seekBar = binding.numSongsSeek
        /*
        Stack Overflow for Structure
        */
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                Log.d("currentSeek", "$progress")
                viewModel.setNumSongs(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        // on click listeners
        binding.submitBut.setOnClickListener{
            if(binding.playlistET.text.isEmpty()){
                Toast.makeText(this.context, "Please Enter a Playlist Name", Toast.LENGTH_SHORT).show()
                hideKeyboard()
                return@setOnClickListener
            }

            if(binding.artistET.text.isEmpty()){
                Toast.makeText(this.context, "Please Set the Artist Seed", Toast.LENGTH_SHORT).show()
                hideKeyboard()
                return@setOnClickListener
            }

            Log.d("Token for submit", "${viewModel.getAccessToken().value}")
            Log.d("artist seed", "${viewModel.getArtId().value}")
            Log.d("genre seeds", "${viewModel.getGenre().value}")

            // parse and pick genre at random from artist's list
            var genreString = viewModel.getGenre().value!!.replace("\"", "")
            var list = genreString.split(", ")
            val indexSelection = (list.indices).random()
            val genreSeed = list[indexSelection].replace(" ", "%20")
            Log.d("selected genre", genreSeed)
            val token = viewModel.getAccessToken().value
            val artistSeed = viewModel.getArtId().value

            // fetch
            fetchRecommendations(token, artistSeed!!, genreSeed, binding.playlistET.text.toString())

            //hide keyboard
            hideKeyboard()
        }

        //edit text listeners
        binding.artistET.addTextChangedListener {
            if(it!!.isEmpty()){
                hideKeyboard()
            }
        }
        binding.playlistET.addTextChangedListener {
            if(it!!.isEmpty()){
                hideKeyboard()
            }
        }

        // observers
        viewModel.observeEmail().observe(
            viewLifecycleOwner,
            Observer {
                binding.testText.text = "Authenticated as: $it"
            }
        )
        viewModel.observeArtName().observe(
            viewLifecycleOwner,
            Observer {
                binding.artNameTV.text = it
            }
        )
        viewModel.observeImgUrl().observe(
            viewLifecycleOwner,
            Observer {
                Glide.glideFetch(it, binding.artistImage)
            }
        )
        viewModel.observeNumSongs().observe(
            viewLifecycleOwner,
            Observer {
                binding.submitBut.text = "Create $it-Song Playlist"
            }
        )

    }

    private fun fetchArtistId(token: String?, term: String) {
        Log.d("Status: ", "Please Wait...")
        if (token == null) {
            Log.i("Status: ", "Something went wrong - No Access Token found")
            return
        }

        val spotifySearchUrl = "https://api.spotify.com/v1/search?q=$term&type=artist&market=US&limit=1"


        GlobalScope.launch(Dispatchers.Default) {
            val url = URL(spotifySearchUrl)
            val httpsURLConnection = withContext(Dispatchers.IO) {url.openConnection() as HttpsURLConnection }
            httpsURLConnection.requestMethod = "GET"
            httpsURLConnection.addRequestProperty("Authorization", "Bearer $token")
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = false
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8
            withContext(Dispatchers.Main) {

                Log.d("response", ("${response}"))

                // grab artist strings
                val regex1 = Regex("\"id\" : \"(.*)\"")
                val resultList1 = regex1.findAll(response).map { it.groups.get(1)!!.value }.toList()

                val regex2 = Regex("\"name\" : \"(.*)\"")
                val resultList2 = regex2.findAll(response).map { it.groups.get(1)!!.value }.toList()

                if (resultList1.isEmpty()){
                    Log.d("error during search", "no artist returned (list empty)")
                    Toast.makeText(context, "Search Term Returned Nothing", Toast.LENGTH_SHORT).show()
                    binding.artistET.text.clear()
                    return@withContext
                }
                val artId = resultList1[0]
                val artName = resultList2[0]

                // grab image url
                val regex3 = Regex("\"url\" : \"(.*)\",")
                val resultList3 = regex3.findAll(response).map { it.groups.get(1)!!.value }.toList()

                val imgUrl = resultList3[0]

                Log.d("image url", "$artName: $imgUrl")

                // genre
                val regex4 = Regex("\"genres\" : \\[ (.*) \\]")
                val resultList4 = regex4.findAll(response).map { it.groups.get(1)!!.value }.toList()

                val seedGenre = resultList4[0]

                Log.d("image url", "$artName: $imgUrl")


                viewModel.setArtCreds(artId, artName, imgUrl, seedGenre)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchRecommendations(token: String?, artist: String, genre: String, playTitle: String) {
        Log.d("Status: ", "Please Wait...")
        if (token == null) {
            Log.i("Status: ", "Something went wrong - No Access Token found")
            return
        }

        val spotifySubmitUrl = "https://api.spotify.com/v1/recommendations?limit=${viewModel.getNumSongs().value}&market=US&seed_artists=$artist&seed_genres=$genre"

        GlobalScope.launch(Dispatchers.Default) {
            val url = URL(spotifySubmitUrl)
            Log.d("url to GET:", "$url")
            val httpsURLConnection = withContext(Dispatchers.IO) {url.openConnection() as HttpsURLConnection }
            httpsURLConnection.requestMethod = "GET"
            httpsURLConnection.addRequestProperty("Authorization", "Bearer $token")
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = false
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8
            withContext(Dispatchers.Main) {

                Log.d("response", ("${response}"))

                // grab track ids
                val regex1 = Regex("\"id\" : \"(.*)\",\\n.*\"is_local\" : false,")
                val resultList1 = regex1.findAll(response).map { it.groups.get(1)!!.value }.toList()

                Log.d("test track(s)", "${resultList1.size}")
                for(track in resultList1){
                    Log.d("track:", "$track")
                }

                // chain coroutines
                createPlaylist(token, playTitle, resultList1)

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPlaylist(token: String?, title: String, songs: List<String>) {
        Log.d("Status: ", "Please Wait...")
        if (token == null) {
            Log.i("Status: ", "Something went wrong - No Access Token found")
            return
        }

        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("name", "$title")
        jsonObject.put("description", "Created via PlayCreate")
        jsonObject.put("public", false)

        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://api.spotify.com/v1/users/${viewModel.getUserId().value}/playlists")
            Log.d("url to POST:", "$url")
            val httpsURLConnection = url.openConnection() as HttpsURLConnection
            httpsURLConnection.requestMethod = "POST"
            httpsURLConnection.addRequestProperty("Authorization", "Bearer $token")
            httpsURLConnection.setRequestProperty("Content-Type", "application/json") // The format of the content we're sending to the server
            httpsURLConnection.setRequestProperty("Accept", "application/json") // The format of response we want to get from the server
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = true

            // Send the JSON we created
            val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
            outputStreamWriter.write(jsonObjectString)
            outputStreamWriter.flush()

            // Check if the connection is successful
            val responseCode = httpsURLConnection.responseCode
            if (responseCode == 201) {
                val response = httpsURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8
                withContext(Dispatchers.Main) {

                    Log.d("Response :", response)

                    // grab playlist id
                    val regex1 = Regex("\"uri\" : \"spotify:playlist:(.*)\"")
                    val resultList1 = regex1.findAll(response).map { it.groups.get(1)!!.value }.toList()

                    Log.d("PlaylistId:", "${resultList1[0]}")

                    // chain coroutines
                    addSongsToPlaylist(token, resultList1[0], songs)

                }
            } else {
                Log.e("CONNECTION_ERROR", responseCode.toString())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addSongsToPlaylist(token: String?, playlist: String, songs: List<String>) {
        Log.d("Status: ", "Please Wait...")
        if (token == null) {
            Log.i("Status: ", "Something went wrong - No Access Token found")
            return
        }

        // no request body needed
        // but we do need to build uri string
        var uriString = "uris="
        for (song in songs){
            uriString += "spotify%3Atrack%3A$song%2C"
        }

        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://api.spotify.com/v1/playlists/$playlist/tracks?${uriString.dropLast(3)}") // training comma
            Log.d("url to POST:", "$url")
            val httpsURLConnection = url.openConnection() as HttpsURLConnection
            httpsURLConnection.requestMethod = "POST"
            httpsURLConnection.addRequestProperty("Authorization", "Bearer $token")
            httpsURLConnection.setRequestProperty("Content-Type", "application/json") // The format of the content we're sending to the server
            httpsURLConnection.setRequestProperty("Accept", "application/json") // The format of response we want to get from the server
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = true

            // Check if the connection is successful
            val responseCode = httpsURLConnection.responseCode
            if (responseCode == 201) {
                val response = httpsURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8
                withContext(Dispatchers.Main) {

                    Log.d("Response :", response)

                    // call cleanup
                    cleanupOnSuccess()

                }
            } else {
                Log.e("CONNECTION_ERROR", responseCode.toString())
            }
        }
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cleanupOnSuccess(){

        // empty fields
        binding.artistET.text.clear()
        binding.playlistET.text.clear()
        binding.artNameTV.text = ""
        //stage viewModel
        viewModel.resetLiveData()

        //seekbar to 10
        seekBar.progress = seekBar.min

        // hide keyboard
        hideKeyboard()

        Toast.makeText(this.context, "Playlist Posted to Spotify!", Toast.LENGTH_SHORT).show()
    }
}
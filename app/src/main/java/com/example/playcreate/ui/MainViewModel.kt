package com.example.playcreate.ui


import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.lifecycle.*
import com.example.playcreate.api.SpotifyApi
import com.example.playcreate.api.SpotifySong
import com.example.playcreate.api.SpotifySongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

// XXX Much to write
class MainViewModel : ViewModel() {
    private var id = MutableLiveData<String>()
    private var displayName = MutableLiveData<String>()
    private var email = MutableLiveData<String>()
    private var avatar = MutableLiveData<String>()
    private var accessToken = MutableLiveData<String>()
    private var artName = MutableLiveData<String>()
    private var artId = MutableLiveData<String>()
    private var imgUrl = MutableLiveData<String>()


    // set
    fun setUserCreds(i: String, dn: String, e: String, av: String, at: String){
        id.postValue(i)
        println("DEBUG -- setting id LD to $i")
        displayName.postValue(dn)
        email.postValue(e)
        avatar.postValue(av)
        accessToken.postValue(at)
    }
    fun setArtCreds(i: String, n: String, img: String){
        artId.postValue(i)
        artName.postValue(n)
        imgUrl.postValue(img)
    }


    // observe
    fun observeId(): LiveData<String>{
        return id
    }
    fun observeArtName(): LiveData<String>{
        return artName
    }
    fun observeImgUrl(): LiveData<String>{
        return imgUrl
    }

    // get
    fun getAccessToken(): LiveData<String>{
        return accessToken
    }
}
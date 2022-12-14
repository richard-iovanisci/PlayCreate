package com.example.playcreate.ui


import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.lifecycle.*
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
    private var seedG = MutableLiveData<String>()
    private var numSongs = MutableLiveData<Int>(5)


    // set
    fun resetLiveData(){
        artName.postValue("")
        numSongs.postValue(5)
        imgUrl.postValue("")
    }
    fun setUserCreds(i: String, dn: String, e: String, av: String, at: String){
        id.postValue(i)
        println("DEBUG -- setting id LD to $i")
        displayName.postValue(dn)
        email.postValue(e)
        avatar.postValue(av)
        accessToken.postValue(at)
    }
    fun setArtCreds(i: String, n: String, img: String, g: String){
        artId.postValue(i)
        artName.postValue(n)
        imgUrl.postValue(img)
        seedG.postValue(g)
    }
    fun setNumSongs(i: Int){
        numSongs.postValue(i)
    }


    // observe
    fun observeEmail(): LiveData<String>{
        return email
    }
    fun observeArtName(): LiveData<String>{
        return artName
    }
    fun observeImgUrl(): LiveData<String>{
        return imgUrl
    }
    fun observeNumSongs(): LiveData<Int>{
        return numSongs
    }

    // get
    fun getAccessToken(): LiveData<String>{
        return accessToken
    }
    fun getArtId(): LiveData<String>{
        return artId
    }
    fun getGenre(): LiveData<String>{
        return seedG
    }
    fun getUserId(): LiveData<String>{
        return id
    }
    fun getNumSongs(): LiveData<Int>{
        return numSongs
    }
}
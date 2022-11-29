package com.example.playcreate

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.playcreate.ui.MainViewModel
import kotlinx.android.synthetic.main.activity_details.*

// TODO -- need to get rid of this class and move to fragments... viewmodel scope is not shared across multiple activities!
class DetailsActivity : AppCompatActivity() {


    private val viewModel: MainViewModel by viewModels()
/*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val spotifyId = viewModel.getId().value
        val spotifyDisplayName = viewModel.getDisplayName().value
        val spotifyEmail = viewModel.getEmail().value
        val spotifyAvatarURL = viewModel.getAvatar().value
        val spotifyAccessToken = viewModel.getAccessToken().value

        viewModel.getId().observeForever(
        Observer {
            println("DEBUG -- $it has been observed!!!")
        }
        )

        spotify_id_textview.text = spotifyId
        spotify_displayname_textview.text = spotifyDisplayName
        spotify_email_textview.text = spotifyEmail
        if (spotifyAvatarURL == "") {
            spotify_avatar_url_textview.text = "Not Exist"
        }else {
            spotify_avatar_url_textview.text = spotifyAvatarURL
        }
        spotify_access_token_textview.text = spotifyAccessToken
    }

 */
}

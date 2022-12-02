### Rich Iovanisci
### [richard.iovanisci@utexas.edu](mailto:richard.iovanisci@utexas.edu)
### rbi237
### Team Members: N/A
<br>

---
<br>

## PlayCreate
**PlayCreate** is a Spotify client which takes an end-user-determined seed and creates a custom playlist which is pushed to their Spotify account. Once a seed is determined, a chain of API calls are kicked off on a separate thread to do the magic behind the scenes

<br>

---
<br>

## Third Party Libraries and Services
### `com.spotify.android:auth`
Similar to the preconfigured login screens we've used in other applications, this library allows me to create an `AuthenticationClient` to manage the login activity:
```java
AuthenticationClient.openLoginActivity(
	this,
	SpotifyConstants.AUTH_TOKEN_REQUEST_CODE,
	request
)
```
This keeps the login experience polished and provide a framework to pass in important parameters such as the scope of permissions for the session (e.g., playlist create/modify access) and retrieve the user's access token.

[This gem](https://johncodeos.com/how-to-add-sign-in-with-spotify-button-to-your-android-app-using-kotlin) was extraordinarily useful in getting me off the ground here.
### `com.github.bumptech.glide`
Not to much to say here, but worth mentioning. I'm using glide in an almost identical fashion to HW4 in order to draw the artist's image url to screen.
### `org.jetbrains.kotlinx`
Another one that doesn't require much explanation. All of the API requests are made using coroutines which required this library. One challenge here was properly chaining the coroutines so that the ViewModel was updated before the next one was executed.

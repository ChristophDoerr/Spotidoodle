package com.spotidoodle.team13.spotidoodle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class MainActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
        {
            // TODO: Replace with your client ID
            private static final String CLIENT_ID = "227c5a2e60684041932779eeaf4176e1";
            // TODO: Replace with your redirect URI
            private static final String REDIRECT_URI = "Spotidoodle-login://callback";

            private Player mPlayer;

            // Request code that will be used to verify if the result comes from correct activity
// Can be any integer
            private static final int REQUEST_CODE = 1337;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                        AuthenticationResponse.Type.TOKEN,
                        REDIRECT_URI);
                builder.setScopes(new String[]{"user-read-private", "streaming"});
                AuthenticationRequest request = builder.build();

                AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
            }

            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
                super.onActivityResult(requestCode, resultCode, intent);

                // Check if result comes from the correct activity
                if (requestCode == REQUEST_CODE) {
                    AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
                    if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                        Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                            @Override
                            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                                mPlayer = spotifyPlayer;
                                mPlayer.addConnectionStateCallback(MainActivity.this);
                                mPlayer.addNotificationCallback(MainActivity.this);
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            protected void onDestroy() {
                Spotify.destroyPlayer(this);
                super.onDestroy();
            }

            @Override
            public void onPlaybackEvent(PlayerEvent playerEvent) {
                Log.d("MainActivity", "Playback event received: " + playerEvent.name());
                switch (playerEvent) {
                    // Handle event type as necessary
                    default:
                        break;
                }
            }

            @Override
            public void onPlaybackError(Error error) {
                Log.d("MainActivity", "Playback error received: " + error.name());
                switch (error) {
                    // Handle error type as necessary
                    default:
                        break;
                }
            }

            @Override
            public void onLoggedIn() {
                Log.d("MainActivity", "User logged in");

                mPlayer.playUri(null, "spotify:track:4aSon118zcGeJoKINAmiav", 0, 0);
            }
            @Override
            public void onLoggedOut() {
                Log.d("MainActivity", "User logged out");
            }

            @Override
            public void onLoginFailed(Error e) {
                Log.d("MainActivity", "Login failed");
            }

            @Override
            public void onTemporaryError() {
                Log.d("MainActivity", "Temporary error occurred");
            }

            @Override
            public void onConnectionMessage(String message) {
                Log.d("MainActivity", "Received connection message: " + message);
            }
        }
package com.example.web_view.media;

import com.example.web_view.webrtc.SignalingClient;
import com.example.web_view.webrtc.WebRTC;

import org.webrtc.MediaStream;

public class Media {
    private static Media instance;

    private Media() {}
    public static Media get() {
        if (instance == null) {
            synchronized (SignalingClient.class) {
                if (instance == null) {
                    instance = new Media();
                }
            }
        }
        return instance;
    }

    public MediaStream getDisplayMediaStream(){
        MediaStream screenStream = WebRTC.get().getScreenStream();
        return screenStream;
    }
}

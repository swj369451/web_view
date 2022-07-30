package com.example.web_view.webrtc;

import android.content.Context;
import android.nfc.Tag;

import org.json.JSONObject;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PeerConnect {


    //连接器集合
    HashMap<String, PeerConnection> peerConnectionMap =new HashMap<>();

    //ice服务器
    List<PeerConnection.IceServer> iceServers;

    //单例
    private static PeerConnect instance;
    private PeerConnect() {}
    public static PeerConnect get(Context applicationContext) {
        if (instance == null) {
            synchronized (SignalingClient.class) {
                if (instance == null) {
                    instance = new PeerConnect();
                }
            }
        }
        instance.init();
        return instance;
    }


    public void init() {
        iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:101.35.181.216:8084").createIceServer());
        iceServers.add(new PeerConnection.IceServer("turn:139.9.45.150:3478","test","123"));
    }

    public PeerConnection createPeerConnection(JSONObject data) {
        String from = data.optString("from");
        PeerConnection pc = WebRTC.get().getPeerConnectionFactory().createPeerConnection(iceServers, new PeerConnectionAdapter(from));

        peerConnectionMap.put(from, pc);
        return pc;
    }


    public PeerConnection getPeerConnect(String from) {
        return peerConnectionMap.get(from);
    }

    public void disconnect(String form) {
        PeerConnection peerConnection = peerConnectionMap.get(form);
        if(peerConnection!=null){
            peerConnectionMap.remove(form);
            peerConnection.close();
        }
    }
}

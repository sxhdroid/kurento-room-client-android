package fi.vtt.nubomedia.kurentoroomclientandroid;

import android.util.Log;

import net.minidev.json.JSONObject;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;

import fi.vtt.nubomedia.jsonrpcwsandroid.JsonRpcNotification;
import fi.vtt.nubomedia.jsonrpcwsandroid.JsonRpcRequest;
import fi.vtt.nubomedia.jsonrpcwsandroid.JsonRpcResponse;
import fi.vtt.nubomedia.jsonrpcwsandroid.JsonRpcWebSocketClient;
import fi.vtt.nubomedia.utilitiesandroid.LooperExecutor;


/**
 * Base class for API classes that handles web socket connections and
 * Json-RPC requests and responses.
 */
public abstract class KurentoAPI implements JsonRpcWebSocketClient.WebSocketConnectionEvents {
    private static final String LOG_TAG = "KurentoAPI";
    protected JsonRpcWebSocketClient client = null;
    protected LooperExecutor executor = null;
    protected String wsUri = null;


    /**
     * Opens a web socket connection to the predefined URI as provided in the constructor.
     * The method responds immediately, whether or not the connection is opened.
     * The method isWebSocketConnected() should be called to ensure that the connection is open.
     */
    public void connectWebSocket() {
        try {
            if(isWebSocketConnected()){
                return;
            }
            URI uri = new URI(wsUri);
            client = new JsonRpcWebSocketClient(uri, this,executor);
            executor.execute(new Runnable() {
                public void run() {
                    client.connect();
                }
            });

        } catch (Exception exc){
            Log.e(LOG_TAG, "connectWebSocket", exc);
        }
    }

    /**
     * Method to check if the web socket connection is connected.
     *
     * @return true if the connection state is connected, and false otherwise.
     */
    public boolean isWebSocketConnected(){
        if(client!=null){
            return (client.getConnectionState().equals(JsonRpcWebSocketClient.WebSocketConnectionState.CONNECTED));
        } else {
            return false;
        }
    }

    /**
     * Attempts to close the web socket connection asynchronously.
     */
    public void disconnectWebSocket() {
        try {
            if(client!= null ) {
                executor.execute(new Runnable() {
                    public void run() {
                        client.disconnect(false);
                    }
                });
            }
        } catch (Exception exc){
            Log.e(LOG_TAG, "disconnectWebSocket", exc);
        } finally {
            ;
        }
    }


    /**
     *
     * @param method
     * @param namedParameters
     * @param id
     */
    protected void send(String method, HashMap<String, Object> namedParameters, int id){

        try {
            final JsonRpcRequest request = new JsonRpcRequest();
            request.setMethod(method);
            if(namedParameters!=null) {
                request.setNamedParams(namedParameters);
            }
            if(id>=0) {
                request.setId(new Integer(id));
            }
            executor.execute(new Runnable() {
                public void run() {
                    if(isWebSocketConnected()) {
                        client.sendRequest(request);
                    }
                }
            });
        } catch (Exception exc){
            Log.e(LOG_TAG, "send: "+method, exc);
        }
    }


    /* WEB SOCKET CONNECTION EVENTS */

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        ;
    }

    @Override
    public void onRequest(JsonRpcRequest request) {
        ;
    }

    @Override
    public void onResponse(JsonRpcResponse response) {
        ;
    }

    @Override
    public void onNotification(JsonRpcNotification notification) {
        ;
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        ;
    }

    @Override
    public void onError(Exception e) {
        Log.e(LOG_TAG, "onError: "+e.getMessage(), e);
    }



}

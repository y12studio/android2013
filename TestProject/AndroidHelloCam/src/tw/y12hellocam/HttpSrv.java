package tw.y12hellocam;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.WebSocket.StringCallback;
import com.koushikdutta.async.http.libcore.RequestHeaders;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServer.WebSocketRequestCallback;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

public class HttpSrv extends Service {

	private static final String TAG = "HttpSrv";

	private Handler _handler;
	private PowerManager.WakeLock wakeLock;
	private final IBinder binder = null;
	private AsyncHttpServer server;

	public HttpSrv() {
		super();

		_handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// msg.getData().getInt("state")
			}
		};
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "HttpSrv onStartCommand");
		int r = START_STICKY;
		if (server != null) {
			return r;
		}

		// Get a wake lock to stop the cpu going to sleep
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "HttpSrv");
		wakeLock.acquire();
		_handler.post(new Runnable() {

			@Override
			public void run() {
				startHttpSrv();
			}
		});

		return r;

	}

	public void onDestroy() {
		try {
			if (wakeLock != null) {
				wakeLock.release();
				wakeLock = null;
			}

			if (server != null) {
				_handler.post(new Runnable() {
					@Override
					public void run() {
						stopHttpSrv();

					}
				});
			} else {
				Log.i(TAG, "HttpSrv not running");
			}
		} catch (Exception e) {
			Log.e(TAG, "Error stopping httpsrv", e);
		}
	}

	private void startHttpSrv() {
		Log.i(TAG, "HttpSrv start....");
		server = new AsyncHttpServer();
		server.get("/", new HttpServerRequestCallback() {

			@Override
			public void onRequest(AsyncHttpServerRequest req,
					AsyncHttpServerResponse response) {
				response.send("<html><body><h2>AndroidHelloCam " + new Date()
						+ "</h2><p><a href=\"/www/\">www</a></p></body></html>");
			}
		});

		AndroidAsyncUtils.setupAssetWebRoot(getBaseContext(), server, "www",
				"hellowebsocket.html");

		server.websocket("/ws", new WebSocketRequestCallback() {
			@Override
			public void onConnected(final WebSocket webSocket,
					RequestHeaders headers) {
				webSocket.setStringCallback(new StringCallback() {
					@Override
					public void onStringAvailable(String s) {
						webSocket.send(s + new Date());
					}
				});
			}
		});

		// listen on port 8888
		server.listen(8888);
		// browsing http://localhost:8888
		Log.i(TAG, "HttpSrv started.");
	}

	private void stopHttpSrv() {
		if (server != null) {
			server.stop();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

}

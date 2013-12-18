package tw.y12hellocam;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

public class AndroidAsyncUtils {

	public static void setupAssetWebRoot(final Context ctx,
			AsyncHttpServer server, final String dirName,final String indexFileName) {
		
		final String prefix = "/"+dirName+"/";
		
		server.addAction("GET", prefix + ".*",
				new HttpServerRequestCallback() {
					@Override
					public void onRequest(AsyncHttpServerRequest req,
							final AsyncHttpServerResponse resp) {
						// req.getPath() return /www/haha.html
						// System.out.println(req.getPath());
						String path = req.getPath();
						if (path.equals(prefix)) {
							path = prefix+indexFileName;
						}
						try {
							InputStream is = ctx.getAssets().open(
									path.substring(1));
							resp.responseCode(200);
							resp.getHeaders()
									.getHeaders()
									.add("Content-Type",
											AsyncHttpServer
													.getContentType(path));
							Util.pump(is, resp, new CompletedCallback() {
								@Override
								public void onCompleted(Exception ex) {
									resp.end();
								}
							});
						} catch (IOException e) {
							e.printStackTrace();
							resp.responseCode(404);
							resp.end();
							return;
						}
					}
				});
	}
}

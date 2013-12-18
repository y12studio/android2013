package tw.y12hellocam;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpResponse;

public class MainActivity extends Activity implements CvCameraViewListener2 {

	private static final String TAG = "MainActivity";
	private Button btnProcess;
	private ImageView imgView1;
	private int count;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case BaseLoaderCallback.SUCCESS:
				mOpenCvCameraView.enableView();
				break;
			default:
				super.onManagerConnected(status);
				break;
			}

		}
	};
	private CameraBridgeViewBase mOpenCvCameraView;
	private Button btnTake;
	private ImageView imgView2;
	private OnClickListener clTake = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//showWebPage("http://localhost:8888/");
		}
	};
	private OnClickListener clProcess = new OnClickListener() {

		@Override
		public void onClick(View v) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnProcess = (Button) findViewById(R.id.buttonProcess);
		btnProcess.setOnClickListener(clProcess);
		btnTake = (Button) findViewById(R.id.buttonTake);
		btnTake.setOnClickListener(clTake);

		imgView1 = (ImageView) findViewById(R.id.imageView1);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (UI.ENABLE_OPENCV) {
			// load OpenCV engine and init OpenCV library
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_7,
					getApplicationContext(), mLoaderCallback);
		}

		if (UI.ENABLE_HTTPD) {
			Log.i(TAG, "Start Service");
			Intent intent = new Intent(MainActivity.this, HttpSrv.class);
			// intent.putExtra(__PORT,__PORT_DEFAULT);
			startService(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onCameraViewStarted(int width, int height) {

	}

	@Override
	public void onCameraViewStopped() {

	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// Mat dst = procBlur(inputFrame);
		Mat dst = procGray(inputFrame);
		count++;
		Log.i("Frame count", "" + count);
		return dst;
	}

	private Mat procBlur(CvCameraViewFrame inputFrame) {
		Mat src = inputFrame.rgba();
		Mat graySrc = inputFrame.gray();
		Mat dst = new Mat();
		int blurKsize = 7;
		Imgproc.medianBlur(src, dst, blurKsize);
		// python
		// cv2.Laplacian(graySrc, cv2.cv.CV_8U, graySrc, ksize = edgeKsize)
		// int edgeKsize = 5;
		// Imgproc.Laplacian(graySrc, dst, CvType.CV_8U,edgeKsize, 0, 1);
		return dst;
	}

	private Mat procGray(CvCameraViewFrame inputFrame) {
		Mat graySrc = inputFrame.gray();
		return graySrc;
	}

	private void showWebPage(final String url) {
		// url is the URL to download. The callback will be invoked on the UI
		// thread
		// once the download is complete.
		AsyncHttpClient.getDefaultInstance().getString(url,
				new AsyncHttpClient.StringCallback() {
					// Callback is invoked with any exceptions/errors, and the
					// result, if available.
					public void onCompleted(Exception e,
							AsyncHttpResponse response, String result) {
						if (e != null) {
							e.printStackTrace();
							return;
						}
						System.out.println(url + " GET: " + result);
					}

				});
	}

}

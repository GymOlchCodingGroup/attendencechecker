package attendencechecker.grabber;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import attendencechecker.observer.Observable;
import attendencechecker.observer.ObservableImpl;
import attendencechecker.observer.Observer;

public class QRCodeGrabber extends Thread implements Observable<String> {
	private Webcam webcam;
	private Result result;
	private Result oldResult;
	private static final int INTERVAL_IN_MILLI_SECONDS = 100;

	private ObservableImpl<String> observableImpl;

	public QRCodeGrabber(Webcam webcam) {
		observableImpl = new ObservableImpl<String>();
		super.setDaemon(true);
		this.webcam = webcam;
		
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(INTERVAL_IN_MILLI_SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Result actResult = null;
			BufferedImage image = null;
			if (webcam.isOpen()) {
				if ((image = webcam.getImage()) != null) {

					LuminanceSource source = new BufferedImageLuminanceSource(image);
					BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

					try {
						actResult = new MultiFormatReader().decode(bitmap);
					} catch (NotFoundException e) {
						// fall thru, it means there is no QR code in image
					}
					if (actResult != null && !actResult.equals(oldResult)) {
						result = actResult;
						notifyObservers(actResult.getText());
						oldResult = actResult;
					}
				}
			}
		}
	}

	public Result getResult() {
		return result;
	}

	public void register(Observer<String> observer) {
		observableImpl.register(observer);
	}

	public void unregister(Observer<String> observer) {
		observableImpl.unregister(observer);
	}

	public void notifyObservers(String text) {
		observableImpl.notifyObservers(text);
	}
}

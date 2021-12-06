package attendencechecker.grabber;

import java.util.Scanner;

import attendencechecker.observer.Observable;
import attendencechecker.observer.ObservableImpl;
import attendencechecker.observer.Observer;

public class ScannerGrabber extends Thread implements Observable<String> {
	private static final long INTERVAL_IN_MILLI_SECONDS = 0;
	private String text = "";

	private ObservableImpl<String> observableImpl;

	public ScannerGrabber() {
		super.setDaemon(true);
		super.setName("Scanner-Grabber-Thread");
		observableImpl = new ObservableImpl<String>();
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(INTERVAL_IN_MILLI_SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Scanner scan = new Scanner(System.in);
			if (scan.hasNext()) {
				String actText = scan.nextLine();
				if (!text.equals(actText)) {
					text = scan.nextLine();
					System.out.println(text);
					notifyObservers(text);
					scan.close();
				}
			}
		}
	}

	public void register(Observer<String> observer) {
		observableImpl.register(observer);

	}

	public void unregister(Observer<String> observer) {
		observableImpl.unregister(observer);

	}

	public void notifyObservers(String message) {
		observableImpl.notifyObservers(message);

	}

}

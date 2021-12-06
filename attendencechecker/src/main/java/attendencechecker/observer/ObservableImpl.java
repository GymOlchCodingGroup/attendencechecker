package attendencechecker.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObservableImpl<Message> implements Observable<Message> {
	private List<Observer<Message>> observers;

	public ObservableImpl() {
		observers = new ArrayList<Observer<Message>>();
		observers = Collections.synchronizedList(observers);
	}

	public void register(Observer<Message> observer) {
		synchronized (observers) {
			observers.add(observer);
		}

	}

	public void unregister(Observer<Message> observer) {
		synchronized (observers) {
			observers.remove(observer);
		}
	}

	public void notifyObservers(Message message) {
		synchronized (observers) {
			for (Observer<Message> observer : observers) {
				observer.update(message);
			}
		}
	}
}

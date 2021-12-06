package attendencechecker.observer;

public interface Observable<Message> {
	void register(Observer<Message> observer);
	void unregister(Observer<Message> observer);
	void notifyObservers(Message message);
}

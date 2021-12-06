package attendencechecker.observer;

public interface Observer<M> {
	void update(M message);
}

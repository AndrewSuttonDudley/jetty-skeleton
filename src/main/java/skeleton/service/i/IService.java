package skeleton.service.i;

import java.util.List;

public interface IService<T> {

	public void delete(T entity);

	public List<T> findAll();

	public List<T> findAll(int page, int size);

	public T findOne(Long id);

	public T save(T entity);
}

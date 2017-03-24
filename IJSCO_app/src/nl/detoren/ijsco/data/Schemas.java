package nl.detoren.ijsco.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class Schemas implements Collection<Schema>, Iterable<Schema> {

	private ArrayList<Schema> schemas;

	public Schemas() {
		schemas = new ArrayList<>();
	}

	@Override
	public boolean add(Schema arg0) {
		if (arg0 != null && !contains(arg0)) {
			schemas.add(arg0);
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Schema> arg0) {
		if (arg0 != null) {
			for (Schema schema : arg0) {
				add(schema);
			}
			return true;
		}
		return false;
	}

	@Override
	public void clear() {
		schemas = new ArrayList<>();
	}

	@Override
	public boolean contains(Object arg0) {
		if (arg0 instanceof Schema && schemas != null) {
			for (Schema s : schemas) {
				if (s.equals(arg0)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		try {
			@SuppressWarnings("unchecked")
			Collection<Schema> Schemas = (Collection<Schema>) arg0;
			for (Schema s : Schemas) {
				if (!contains(s))
					return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean isEmpty() {
		if (schemas != null && schemas.size() != 0)
			return true;
		else
			return false;
	}

	@Override
	public Iterator<Schema> iterator() {
		if (schemas != null)
			return schemas.iterator();
		else
			return null;
	}

	@Override
	public boolean remove(Object arg0) {
		// TODO Auto-generated method stub
		if (arg0 instanceof Schema) {
			schemas.remove(arg0);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		try {
			boolean result = true;
			@SuppressWarnings("unchecked")
			Collection<Schema> Schemas = (Collection<Schema>) arg0;
			for (Schema s : Schemas) {
				result = result & remove(s);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object[] toArray() {
		if (schemas != null)
			return schemas.toArray();
		else
			return null;
	}

	@Override
	public int size() {
		return schemas != null ? schemas.size() : 0;
	}

	public Schema get(int i) {
		if (schemas != null && i < schemas.size())
			return schemas.get(i);
		return null;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void sort() {
		Collections.sort(schemas, new Comparator<Schema>() {
			@Override
			public int compare(Schema arg0, Schema arg1) {
				if (arg0.getGroepen() > arg1.getGroepen()) {
					return -1;
				} else if (arg0.getGroepen() < arg1.getGroepen()) {
					return 1;
				}
				else if (arg0.getByes() > arg1.getByes()) {
					return -1;
				} else if (arg0.getByes() < arg1.getByes()) {
					return 1;
				}
				return 0;
			}
		});
	}
}
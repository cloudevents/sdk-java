package io.cloudevents.types;

/**
 * 
 * @author fabiojose
 *
 */
public class Much {

	private String wow;

	public String getWow() {
		return wow;
	}

	public void setWow(String wow) {
		this.wow = wow;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((wow == null) ? 0 : wow.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Much other = (Much) obj;
		if (wow == null) {
			if (other.wow != null)
				return false;
		} else if (!wow.equals(other.wow))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Much [wow=" + wow + "]";
	}
}

package com.bbb.repositorywrapper;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

/**
 * @author Prashanth K Bhoomula
 * @Version: 1.0
 * @Modified Date: June 19, 2012
 */
public class RepositoryItemWrapper {
	//private static final long serialVersionUID = -5367156789287566490L;
	protected RepositoryItem originalItem = null;
	//private final String wrapperName = null;

	/**
	 *
	 *
	 * @param originalItem
	 */
	public RepositoryItemWrapper(final RepositoryItem originalItem) {
		this.originalItem = originalItem;
	}

	/**
	 * @param pOriginalItem
	 *            the originalItem to set
	 */
	protected void setOriginalItem(final RepositoryItem pOriginalItem) {
		this.originalItem = pOriginalItem;
	}

	/**
	 *
	 *
	 *
	 * @return
	 */
	public RepositoryItem getOriginalItem() {
		return this.originalItem;
	}

	/**
	 *
	 *
	 * @param propertyName
	 * @param propertyValue
	 */
	public void setProperty(final String propertyName, final Object propertyValue) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is null."));
		}
		if (!MutableRepositoryItem.class.isInstance(this.originalItem)) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is not the instance of class \"atg.repository.MutableRepositoryItem\"."));
		}
		((MutableRepositoryItem) this.originalItem).setPropertyValue(propertyName,
				propertyValue);
	}

	/**
	 *
	 *
	 * @param propertyName
	 * @return
	 */
	public Date getDate(final String propertyName) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is null."));
		}
		final Object propertyValue = this.originalItem.getPropertyValue(propertyName);
		if (propertyValue == null) {
			return null;
		} else {
			return (Date) propertyValue;
		}
	}

	/**
	 *
	 *
	 * @param propertyName
	 * @return
	 */
	public Timestamp getTimestamp(final String propertyName) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is null."));
		}
		final Object propertyValue = this.originalItem.getPropertyValue(propertyName);
		if (propertyValue == null) {
			return null;
		} else {
			return (Timestamp) propertyValue;
		}
	}

	/**
	 *
	 *
	 * @param propertyName
	 * @return
	 */
	public int getInt(final String propertyName) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is null."));
		}
		final Object propertyValue = this.originalItem.getPropertyValue(propertyName);
		if (propertyValue == null) {
			return 0;
		} else {
			return ((Integer) propertyValue).intValue();
		}
	}

	/**
	 *
	 *
	 * @param propertyName
	 * @return
	 */
	public long getLong(final String propertyName) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is null."));
		}
		final Object propertyValue = this.originalItem.getPropertyValue(propertyName);
		if (propertyValue == null) {
			return 0L;
		} else {
			return ((Long) propertyValue).longValue();
		}
	}

	/**
	 *
	 *
	 * @param propertyName
	 * @return
	 */
	public float getFloat(final String propertyName) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is null."));
		}
		final Object propertyValue = this.originalItem.getPropertyValue(propertyName);
		if (propertyValue == null) {
			return 0F;
		} else {
			return ((Float) propertyValue).floatValue();
		}
	}

	/**
	 *
	 *
	 * @param propertyName
	 * @return
	 */
	public double getDouble(final String propertyName) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is null."));
		}
		final Object propertyValue = this.originalItem.getPropertyValue(propertyName);
		if (propertyValue == null) {
			return 0D;
		} else {
			return ((Double) propertyValue).doubleValue();
		}
	}

	/**
	 *
	 *
	 * @param propertyName
	 * @return
	 */
	public String getString(final String propertyName) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is null."));
		}
		final Object propertyValue = this.originalItem.getPropertyValue(propertyName);
		if (propertyValue == null) {
			return null;
		} else {
			return (String) propertyValue;
		}
	}

	/**
	 *
	 *
	 *
	 * @param propertyName
	 * @return
	 */
	public boolean getBoolean(final String propertyName) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException("The original repository item of this wrapper is null."));
		}
		final Object propertyValue = this.originalItem.getPropertyValue(propertyName);
		if (propertyValue == null) {
			return false;
		}

		return ((Boolean) propertyValue).booleanValue();
	}

	/**
	 *
	 *
	 * @param propertyName
	 * @return
	 */
	public <T> List<T> getList(final String propertyName) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is null."));
		}
		final Object propertyValue = this.originalItem.getPropertyValue(propertyName);
		if (propertyValue == null) {
			return null;
		} else {
			return (List<T>) propertyValue;
		}
	}

	/**
	 *
	 *
	 * @param propertyName
	 * @return
	 */
	public <K, V> Map<K, V> getMap(final String propertyName) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is null."));
		}
		final Object propertyValue = this.originalItem.getPropertyValue(propertyName);
		if (propertyValue == null) {
			return null;
		} else {
			return (Map<K, V>) propertyValue;
		}
	}

	/**
	 *
	 *
	 * @param propertyName
	 * @return
	 */
	public <T> Set<T> getSet(final String propertyName) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is null."));
		}
		final Object propertyValue = this.originalItem.getPropertyValue(propertyName);
		if (propertyValue == null) {
			return null;
		} else {
			return (Set<T>) propertyValue;
		}
	}

	/**
	 *
	 *
	 * @param propertyName
	 * @return
	 */
	public RepositoryItem getRepositoryItem(final String propertyName) {
		if (this.originalItem == null) {
			throw (new CustomRepositoryException(
					"The original repository item of this wrapper is null."));
		}
		final Object propertyValue = this.originalItem.getPropertyValue(propertyName);
		if (propertyValue == null) {
			return null;
		} else {
			return (RepositoryItem) propertyValue;
		}
	}
}

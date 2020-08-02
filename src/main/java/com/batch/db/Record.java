package com.batch.db;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Record implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;
	private String column1;
	private String column2;

	public Record() {
		super();
	}

	public Record(Integer id, String column1, String column2) {
		super();
		this.id = id;
		this.column1 = column1;
		this.column2 = column2;
	}

	public Record(String column1, String column2) {
		super();
		this.column1 = column1;
		this.column2 = column2;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getColumn1() {
		return column1;
	}

	public void setColumn1(String column1) {
		this.column1 = column1;
	}

	public String getColumn2() {
		return column2;
	}

	public void setColumn2(String column2) {
		this.column2 = column2;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Record other = (Record) obj;
		if (column1 == null) {
			if (other.column1 != null)
				return false;
		} else if (!column1.equals(other.column1))
			return false;
		if (column2 == null) {
			if (other.column2 != null)
				return false;
		} else if (!column2.equals(other.column2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Record [id=" + id + ", column1=" + column1 + ", column2=" + column2 + "]";
	}

}

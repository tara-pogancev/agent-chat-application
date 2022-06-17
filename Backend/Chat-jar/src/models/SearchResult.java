package models;

import java.io.Serializable;

public class SearchResult implements Serializable {

	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	public String title;
	public String location;
	public Double price;
	
	public SearchResult() {
		super();
	}

	public SearchResult(String title, String location, Double price) {
		super();
		this.title = title;
		this.location = location;
		this.price = price;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "SearchResult [title=" + title + ", location=" + location + ", price=" + price + "]";
	}
	
	
	
}

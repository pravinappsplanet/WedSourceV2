package in.appsplanet.wedsource.pojo;

import java.io.Serializable;

public class Vendor implements Serializable {

	// "id": "38",
	// "name": "Amplified Soul Makeup + Hair Couture",
	// "phone_no": "647-668-6815",
	// "sec_phone_no": "",
	// "address": "",
	// "email": "shobana@amplifiedsoul.ca",
	// "website": "http://www.amplifiedsoul.ca",
	// "cat_id": "2",
	// "country": "INDIA",
	// "type": "VD"

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String phone;
	private String address;
	private String email;
	private String website;
	private String country;
	private Category category;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * @param website
	 *            the website to set
	 */
	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPDFData(){
		return "Name:"+name+"\n"+
				"Phone:"+phone+"\n"+
				"Address:"+address+"\n"+
				"Email:"+email+"\n"+
				"Website:"+website+"\n";

	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}

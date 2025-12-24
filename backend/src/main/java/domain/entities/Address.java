package domain.entities;

public class Address {
	private String street;
	 private String streetNumber;
	 private String city;
	 private String postalCode;
	 private String country;

	 private double latitude;
	 private double longitude;
	 
	 public Address() {}
	 public Address(String street, String streetNumber, String city, String postalCode, String country) {
		 this.street = street;
		 this.streetNumber = streetNumber;
		 this.city = city;
		 this.postalCode = postalCode;
		 this.country = country;
	 }
	 public Address(double latitude, double longitude) {
		 this.latitude = latitude;
		 this.longitude = longitude;
	 }
	 public Address(String street, String streetNumber, String city, String postalCode, String country, 
			 		double latitude, double longitude) {
		 this.street = street;
		 this.streetNumber = streetNumber;
		 this.city = city;
		 this.postalCode = postalCode;
		 this.country = country;
		 
		 this.latitude = latitude;
		 this.longitude = longitude;
	 }
	 
	 public String getStreet() {return street;}
	 public String getStreetNumber() {return streetNumber;}
	 public String getCity() {return city;}
	 public String getPostalCode() {return postalCode;}
	 public String getCountry() {return country;}
	 public double getLatitude() {return latitude;}
	 public double getLongitude() {return longitude;}
	 
	 public void setStreet(String street) {this.street = street;}
	 public void setStreetNumber(String streetNumber) {this.streetNumber = streetNumber;}
	 public void setCity(String city) {this.city = city;}
	 public void setPostalCode(String postalCode) {this.postalCode = postalCode;}
	 public void setCountry(String country) {this.country = country;}
	 public void setLatitude(double latitude) {this.latitude = latitude;}
	 public void setLongitude(double longitude) {this.longitude = longitude;}
}

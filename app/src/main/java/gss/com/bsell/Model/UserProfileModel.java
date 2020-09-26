package gss.com.bsell.Model;

public class UserProfileModel {
    String Id;
    String Name;
    String Address;
    String Mobile;
    String  Email;
    String Image;
    String  City;
    String State;
    String Pin;
    String Country;




    //profile data

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getId() {
        return Id;
    }
    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getAddr() {
        return Address;
    }
    public void setAddr(String address) {
        Address = address;
    }

    public String getMobile() {
        return Mobile;
    }
    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }
    public void setEmail(String email) {
        Email = email;
    }

    public String getCity() {
        return City;
    }
    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }
    public void setState(String states) {
        State = states;
    }

    public String getPin() {
        return Pin;
    }
    public void setPin(String pincode) {
        Pin = pincode;
    }

    public String getCountry() {
        return Country;
    }
    public void setCountry(String country) {
        Country = country;
    }

}

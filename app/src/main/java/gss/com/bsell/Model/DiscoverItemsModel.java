package gss.com.bsell.Model;

public class DiscoverItemsModel {
    String Image;
    String Value;
    String Description;
    String Location;
    String ProductId;
    String Title;
    String Lat;
    String Lon;
    String ProductSenderId;
    String CatagoryId;
    String Userdemand;
    String MobileNo;
    String Username;
    String ProductPrice;
    String SimilarProductId;
    String ProductCatIcon;





    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    int type;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String product_name) {
        Title = product_name;
    }


    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }


    public String getLon() {
        return Lon;
    }

    public void setLon(String longitude) {
        Lon = longitude;
    }


    public String getProductSenderId() {
        return ProductSenderId;
    }

    public void setProductSenderId(String userid) {
        ProductSenderId = userid;
    }

    public String getCatagoryId() {
        return CatagoryId;
    }

    public void setCatagoryId(String category) {
        CatagoryId = category;
    }


    public String getUserDemand() { return Userdemand; }
    public void setUserDemand(String user_demands) { Userdemand = user_demands; }

    public String getMobile() { return MobileNo; }
    public void setMobile(String mobile) { MobileNo = mobile; }

    public String getProductPrice() { return ProductPrice; }
    public void setProductPrice(String price) { ProductPrice = price; }

    public String getSimilarProductId() { return SimilarProductId; }
    public void setSimilarProductId(String sm_product) { SimilarProductId = sm_product; }

    public String getProductCatIcon() { return ProductCatIcon; }
    public void setProductCatIcon(String icon) { ProductCatIcon = icon; }


}

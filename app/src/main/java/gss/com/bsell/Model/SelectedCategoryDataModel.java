package gss.com.bsell.Model;

public class SelectedCategoryDataModel {
    String CatId, CatType, CatData, PreviousCategoryId;


    public String getCatId() {
        return CatId;
    }
    public void setCatId(String category_id) {
        CatId = category_id;
    }

    public String getCatType() {
        return CatType;
    }
    public void setCatType(String category_name) {
        CatType = category_name;
    }

    public String getCatData() {
        return CatData;
    }
    public void setCatData(String attribute_name) {
        CatData = attribute_name;
    }

    public String getPreviousCategoryId() {
        return PreviousCategoryId;
    }
    public void setPreviousCategoryId(String prevcat) { PreviousCategoryId = prevcat; }
}

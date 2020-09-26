package gss.com.bsell.Model;

public class CategoryDisplayModel {
    String CatName,CatValue;

    public CategoryDisplayModel(String catName, String catValue) {
        CatName = catName;
        CatValue = catValue;
    }

    public String getCatName() {
        return CatName;
    }

    public void setCatName(String catName) {
        CatName = catName;
    }

    public String getCatValue() {
        return CatValue;
    }

    public void setCatValue(String catValue) {
        CatValue = catValue;
    }
}

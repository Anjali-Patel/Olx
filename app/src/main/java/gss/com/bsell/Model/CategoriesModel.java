package gss.com.bsell.Model;

public class CategoriesModel {

    private String cat_id,cat,icon, Attribute, Next_cat_id, Current_Cat_id;

    public String getCat_id() {
        return cat_id;
    }
    public void setCat_id(String cat_id) { this.cat_id = cat_id; }

    public String getCat() {
        return cat;
    }
    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAttribute() { return Attribute; }
    public void setAttribute(String attribute_name) { this.Attribute = attribute_name; }

    public String getNext_cat_id() { return Next_cat_id; }
    public void setNext_cat_id(String next_category_id) { this.Next_cat_id = next_category_id; }

    public String getCurrent_Cat_id() { return Current_Cat_id; }
    public void setCurrent_Cat_id(String current_cat_id) { this.Current_Cat_id = current_cat_id; }
}

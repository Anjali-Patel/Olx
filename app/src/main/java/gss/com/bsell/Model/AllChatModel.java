package gss.com.bsell.Model;

public class AllChatModel {
    private String sender_id;
    private String receiver_id;
    private String ReceiverName;
    private String msg;
    private String seen_status;
    private String created_date;
    private String sender_name;
    private String product_id;
    private String category_id;
    private String image;
    String receiver_name;
    String UserDemand;
    String ChatCount;



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReceiverName() {
        return ReceiverName;
    }

    public void setReceiverName(String created_date) {
        this.ReceiverName = created_date;
    }

    public String getSeen_status() {
        return seen_status;
    }

    public void setSeen_status(String seen_status) {
        this.seen_status = seen_status;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }


    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getUserDemand() {
        return UserDemand;
    }

    public void setUserDemand(String userdemand) {
        this.UserDemand = userdemand;
    }

    public String getChatCount() {
        return ChatCount;
    }

    public void setChatCount(String chatcount) {
        this.ChatCount = chatcount;
    }
}

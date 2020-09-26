package lockscreenads.models;

public class ImageModel {
    private String id, title, description, imageName,isLike,likeCount,imageServerName;
    private boolean isDeleted;


    public ImageModel(String id, String title, String description,
                      String imageName, String isLike, String likeCount, boolean deleted,String imageServerName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageName = imageName;
        this.isLike = isLike;
        this.likeCount = likeCount;
        isDeleted = deleted;
        this.imageServerName = imageServerName;
    }


    public String getImageServerName() {
        return imageServerName;
    }

    public void setImageServerName(String imageServerName) {
        this.imageServerName = imageServerName;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}

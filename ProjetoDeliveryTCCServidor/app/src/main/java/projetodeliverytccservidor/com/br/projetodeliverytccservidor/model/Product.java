package projetodeliverytccservidor.com.br.projetodeliverytccservidor.model;

/**
 * Created by daniel on 25/02/18.
 */

public class Product {

    private String description;
    private String name;
    private String image;
    private String price;
    private String discount;
    private String menuId;

    public Product() {

    }

    public Product(String description, String name, String image, String price, String discount, String menuId) {
        this.description = description;
        this.name = name;
        this.image = image;
        this.price = price;
        this.discount = discount;
        this.menuId = menuId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}

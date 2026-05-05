package com.example.ranking.model;

public class RankedProduct {
    private String product_id;
    private String title;
    private String category;
    private Double price;
    private Double ranking_score;

    public RankedProduct() {
    }

    public RankedProduct(String product_id, String title, String category, Double price, Double ranking_score) {
        this.product_id = product_id;
        this.title = title;
        this.category = category;
        this.price = price;
        this.ranking_score = ranking_score;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Object priceObj) {
        if (priceObj == null) {
            this.price = 0.0;
        } else if (priceObj instanceof Number) {
            this.price = ((Number) priceObj).doubleValue();
        } else if (priceObj instanceof String) {
            try {
                this.price = Double.parseDouble((String) priceObj);
            } catch (NumberFormatException e) {
                this.price = 0.0;
            }
        } else {
            this.price = 0.0;
        }
    }

    public Double getRanking_score() {
        return ranking_score;
    }

    public void setRanking_score(Object scoreObj) {
        if (scoreObj == null) {
            this.ranking_score = 0.0;
        } else if (scoreObj instanceof Number) {
            this.ranking_score = ((Number) scoreObj).doubleValue();
        } else {
            this.ranking_score = 0.0;
        }
    }

    @Override
    public String toString() {
        return "RankedProduct{" +
                "product_id='" + product_id + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", ranking_score=" + ranking_score +
                '}';
    }
}

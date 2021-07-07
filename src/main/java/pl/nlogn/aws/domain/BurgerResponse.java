package pl.nlogn.aws.domain;

import java.util.Objects;

public class BurgerResponse {
    private final String originalUrl;
    private final String image;

    public BurgerResponse(String originalUrl, String image) {
        this.originalUrl = originalUrl;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BurgerResponse that = (BurgerResponse) o;
        return originalUrl.equals(that.originalUrl) && image.equals(that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalUrl, image);
    }

    @Override
    public String toString() {
        return "BurgerResponse{" +
                "originalUrl='" + originalUrl + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getImage() {
        return image;
    }
}

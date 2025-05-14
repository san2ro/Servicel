package cu.arr.etecsa.api.etecsa.promo.model;

public class Promotion {
    private String backgroundUrl;
    private String svgUrl;
    private String url;

    public Promotion(String backgroundUrl, String svgUrl, String url) {
        this.backgroundUrl = backgroundUrl;
        this.svgUrl = svgUrl;
        this.url = url;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public String getSvgUrl() {
        return svgUrl;
    }

    public String getUrl() {
        return url;
    }
}

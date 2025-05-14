package cu.arr.etecsa.api.etecsa.promo;

import android.app.Activity;
import cu.arr.etecsa.api.etecsa.promo.callback.PromotionCallback;
import cu.arr.etecsa.api.etecsa.promo.model.Promotion;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OperatorPromo {

    private static final String URL = "https://www.etecsa.cu";

    public static void async(Activity activity, PromotionCallback callback) {
        new Thread(() -> extractDateHtml(activity, callback)).start();
    }

    private static void extractDateHtml(Activity a, PromotionCallback callback) {
        WeakReference<Activity> activityRef = new WeakReference<>(a);
        List<Promotion> promo = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(URL).get();
            Elements carouselItems = doc.select(".carousel-item");
            for (Element item : carouselItems) {
                String style = item.select("div[style]").attr("style");
                if (style == null || style.isEmpty()) continue;

                String background = extractBackgroundUrl(style);
                if (!(background.startsWith("http://") || background.startsWith("https://"))) {
                    background = URL + background;
                }

                String svg = item.select(".mipromocion img").attr("src");
                if (!(svg.startsWith("http://") || svg.startsWith("https://"))) {
                    svg = URL + svg;
                }

                String link = item.select(".mipromocion a").attr("href");
                String fullUrl = URL + link;

                promo.add(new Promotion(background, svg, fullUrl));
            }
        } catch (IOException err) {
            err.printStackTrace();
        }

        Activity activity = activityRef.get();
        if (activity != null) {
            activity.runOnUiThread(
                    () -> callback.onResult(promo.isEmpty() ? Collections.emptyList() : promo));
        }
    }

    private static String extractBackgroundUrl(String style) {
        if (style == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("url\\('(.+)'\\);");
        Matcher matcher = pattern.matcher(style);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}

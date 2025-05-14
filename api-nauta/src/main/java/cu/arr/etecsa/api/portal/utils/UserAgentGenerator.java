package cu.arr.etecsa.api.portal.utils;

import android.content.Context;
import android.os.Build;

public class UserAgentGenerator {
    
    private static final String BASE_USER_AGENT = "Mozilla/5.0";
    private static final String WEBKIT_VERSION = "AppleWebKit/537.36";
    private static final String CHROME_VERSION = "Chrome/110.0.0.0";
    private static final String SAFARI_VERSION = "Mobile Safari/537.36";

    public static String generateUserAgent(Context context) {
        String osInfo =
                String.format(
                        "(Linux; U; Android %s; %s; %s Build/%s)",
                        Build.VERSION.RELEASE,
                        context.getResources().getConfiguration().locale.getLanguage(),
                        Build.MODEL,
                        Build.ID);

        return String.format(
                "%s %s %s (KHTML, like Gecko) %s %s %s",
                BASE_USER_AGENT,
                osInfo,
                WEBKIT_VERSION,
                "Version/4.0",
                CHROME_VERSION,
                SAFARI_VERSION);
    }
}

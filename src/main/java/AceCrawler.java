import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.modle.RecommendItem;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ChiYuan
 * Date: 2021/01/12 下午 04:55
 * To change this template use File | Settings | File Templates.
 */
public class AceCrawler {
    public static void main(String[] args) {
        AceCrawler aceCrawler = new AceCrawler();
        aceCrawler.start();
    }

    private void start() {
        Set<RecommendItem> recommendItemSet;
        recommendItemSet = craw();
        StringBuffer stringBuffer = new StringBuffer();
        for (RecommendItem recommendItem : recommendItemSet) {
            stringBuffer.append(recommendItem.getName() + ":" + recommendItem.getPrice());
            telegramBot(recommendItem.getName() + ":" + recommendItem.getPrice());
        }
    }

    private void telegramBot(String str) {
        String pageUrl = "https://api.telegram.org/bot1435379477:AAGNi5b8j5QXgweboySPNW5xCM66a01rkkA/sendMessage?chat_id=-1001447611421&text=" + str;
        try {
            System.out.println(str);
            Connection.Response response = Jsoup.connect(pageUrl).ignoreContentType(true).method(Connection.Method.GET).timeout(30000).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<RecommendItem> craw() {
        Set<RecommendItem> recommendItemSet = new HashSet<>();
        String[] pageUrlArray = {"https://ace.io/polarisex/quote/getKline?baseCurrencyId=1&tradeCurrencyId=2&type=1&limit=1"
                , "https://www.ace.io/polarisex/quote/getKline?baseCurrencyId=1&tradeCurrencyId=4&type=1&limit=1"};
        for (String pageUrl : pageUrlArray) {
            try {
                Connection.Response response = Jsoup.connect(pageUrl).ignoreContentType(true).method(Connection.Method.GET).timeout(60000).execute();
                if (response.statusCode() == 200) {
                    String pageContent = response.body();
                    JsonObject jsonObject = new Gson().fromJson(pageContent, JsonObject.class);
                    Double price = jsonObject.get("attachment").getAsJsonArray().get(0).getAsJsonObject().get("current").getAsDouble();
                    String name = "null";
                    if (pageUrl.contains("tradeCurrencyId=2")) {
                        name = "BTC";
                    } else if (pageUrl.contains("tradeCurrencyId=4")) {
                        name = "ETH";
                    }
                    RecommendItem recommendItem = new RecommendItem();
                    recommendItem.setName(name);
                    recommendItem.setPrice(price);
                    recommendItemSet.add(recommendItem);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return recommendItemSet;
    }
}
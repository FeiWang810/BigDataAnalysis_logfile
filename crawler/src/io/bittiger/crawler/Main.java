package io.bittiger.crawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


public class Main {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";
    private static final String USER_AGENT2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) CriOS/56.0.2924.75 Mobile/14E5239e Safari/602.1";
    public static void main(String[] args) throws IOException {

                String requestUrl = "https://sfbay.craigslist.org/d/apts-housing-for-rent/search/apa";
                //syncCrawl(requestUrl);
                asyncCrawl(requestUrl);

    }

    private static String htmlTitle(Document dom) {
        Element node = dom.select("title").first();
        if (node != null && node.text().length() > 0) {
            return node.text();
        }
        return null;
    }

    private static void asyncCrawl(String requestUrl) {
        try{
            //step1:  get response from http request url
            String response = sendHttpGetRequest(requestUrl);
            //System.out.println("Async html response= " + response);

            //async step2:store response in files or document based no SQL, hdfs

            //async step3: parse response with Jsoup
            Document doc = getDomFromContent(response, requestUrl);
            String pageTitle = htmlTitle(doc);
            System.out.println("Async html title= " + pageTitle);

            //title: #sortable-results > ul > li:nth-child(1) > p > a
            //#sortable-results > ul > li:nth-child(2) > p > a
            //price: #sortable-results > ul > li:nth-child(1) > p > span.result-meta > span.result-price
            //detail:
            //hood: #sortable-results > ul > li:nth-child(1) > p > span.result-meta > span.result-hood

            for(int index = 1; index <= 20; index++){
                String title_ele_path = "#sortable-results > ul > li:nth-child(" + Integer.toString(index) + ") > p > a";
                Element title_ele = doc.select(title_ele_path).first();
                System.out.println("title = " + title_ele.text());

                String price_ele_path = "#sortable-results > ul > li:nth-child(" + Integer.toString(index) + ") > p > span.result-meta > span.result-price";
                Element price_ele = doc.select(price_ele_path).first();
                System.out.println("price = " + price_ele.text());

                String detailUrl = title_ele.attr("href");
                System.out.println("detail url = " + detailUrl);

                String hood_ele_path = "#sortable-results > ul > li:nth-child(" + Integer.toString(index) + ") > p > span.result-meta > span.result-hood";
                Element hood_ele = doc.select(hood_ele_path).first();
                System.out.println("hood = " + hood_ele.text());

                System.out.println("\n");

            }

        } catch (Exception e){

        }
    }


    private static Document getDomFromContent(String content, String url) {
        return Jsoup.parse(content, url);
    }

    private static String sendHttpGetRequest(String url) throws Exception {
        URL urlObj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        int i = 0;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            i++;
        }
        System.out.println("number of line : " + Integer.toString(i));
        in.close();

        String responseStr = response.toString();
        return responseStr;
    }


}

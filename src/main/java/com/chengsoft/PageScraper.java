package com.chengsoft;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class PageScraper {

    public static final String PAGE_REGEX = "/beer/([a-zA-Z0-9\\-]+)/(\\d+)/(\\d+)/(\\d+)/";
    private OkHttpClient client = new OkHttpClient();

    public List<String> findReviews(String html) {
        Document document = Jsoup.parse(html);
        Elements divs = document.select("div.reviews-container>div>div>div:not([style=''])");
        return divs.stream()
                .filter(e -> !e.attr("style").equals("padding: 0px 0px 0px 0px;"))
                .map(Element::text)
                .collect(toList());
    }

    public int findMaxPage(String html) {
        Document document = Jsoup.parse(html);
        Elements anchors = document.select("a.ballno");
        return anchors.stream()
                .map(e -> e.text().trim())
                .filter(text -> text.matches("\\d+"))
                .mapToInt(Integer::valueOf)
                .max().orElse(0);
    }

    public List<String> findPages(String html, int max) {
        int maxPage = findMaxPage(html);
        if (maxPage == 0) {
            return Collections.emptyList();
        }

        int effectiveMax = Math.min(maxPage, max);
        Document document = Jsoup.parse(html);
        String firstPageRelativeUrl = document.selectFirst("a.ballno").attr("href");
        return IntStream.rangeClosed(2, effectiveMax)
                .boxed()
                .map(i -> firstPageRelativeUrl.replaceFirst(PAGE_REGEX, "/beer/$1/$2/$3/" + i + "/"))
                .collect(Collectors.toList());
    }

    public List<String> downloadAllReviews(String baseUrl, String beerPageUrl) {
        String firstPageHtml = getHtml(beerPageUrl);
        List<String> firstPageReviews = findReviews(firstPageHtml);

        List<String> pageUrls = findPages(firstPageHtml, 10);
        Stream<String> otherReviews = pageUrls.parallelStream()
                .map(url -> baseUrl + url)
                .map(this::getHtml)
                .flatMap(html -> findReviews(html).stream());

        return Stream.concat(firstPageReviews.stream(), otherReviews).collect(Collectors.toList());
    }

    private String getHtml(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("Error trying to call " + url);
        }
    }
}

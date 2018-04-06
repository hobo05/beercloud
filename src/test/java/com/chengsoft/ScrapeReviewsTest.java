package com.chengsoft;

import okhttp3.OkHttpClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ScrapeReviewsTest {

    private OkHttpClient client;
    private PageScraper pageScraper;
    private String blackIpaHtml;
    private String leffeHtml;

    @Before
    public void setup() throws IOException {
        client = new OkHttpClient();
        Path blackIPAPath = Paths.get("src/test/resources/blackipa.html");
        Path leffePath = Paths.get("src/test/resources/leffe.html");
        blackIpaHtml = new String(Files.readAllBytes(blackIPAPath), "utf-8");
        leffeHtml = new String(Files.readAllBytes(leffePath), "utf-8");
        pageScraper = new PageScraper();
    }

    @Test
    public void findReviews() {
        List<String> reviews = pageScraper.findReviews(blackIpaHtml);
        assertThat(reviews.size()).isEqualTo(10);
    }

    @Test
    public void findPages() {
        List<String> urls = pageScraper.findPages(blackIpaHtml, 10);
        assertThat(urls).containsExactly(
                "/beer/st-peters-crafted-black-ipa/409702/1/2/",
                "/beer/st-peters-crafted-black-ipa/409702/1/3/",
                "/beer/st-peters-crafted-black-ipa/409702/1/4/"
        );
    }

    @Test
    public void findMaxPage() {
        int maxPage = pageScraper.findMaxPage(leffeHtml);
        assertThat(maxPage).isEqualTo(71);
    }

}

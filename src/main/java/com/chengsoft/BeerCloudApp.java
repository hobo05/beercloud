package com.chengsoft;


import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.filter.StopWordFilter;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class BeerCloudApp
{

    public static void main( String[] args )
    {

        PageScraper pageScraper = new PageScraper();
        List<String> allReviews = pageScraper.downloadAllReviews("https://www.ratebeer.com",
                "https://www.ratebeer.com/beer/uprising-treason-west-coast-ipa-bottle/413968/",
                20);

        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        StopWordFilter stopWordFilter = new StopWordFilter(Arrays.asList(
                "this",
                "the",
                "not",
                "from",
                "but",
                "very",
                "for",
                "in",
                "and",
                "met",
                "bit",
                "some",
                "a"
        ));
        frequencyAnalyzer.addFilter(stopWordFilter);
        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(allReviews);
        final Dimension dimension = new Dimension(600, 600);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setBackgroundColor(Color.WHITE);
        wordCloud.setBackground(new CircleBackground(300));
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));
        wordCloud.setFontScalar(new SqrtFontScalar(20, 60));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile("target/uprising_wordcloud.png");
    }
}

package net.mortalsilence.indiepim.server.news;

import com.rometools.fetcher.impl.HashMapFeedInfoCache;
import com.rometools.fetcher.impl.HttpURLFeedFetcher;
import com.rometools.opml.feed.opml.Opml;
import com.rometools.opml.feed.opml.Outline;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.WireFeedInput;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 01.02.2015
 * Time: 21:37
 */
public class RomeTest {

    public static void main(String[] args) {
        WireFeedInput input = new WireFeedInput();
        try {
            final Opml feed = (Opml)input.build(new File("c:\\Users\\AmIEvil\\ownCloud\\owncloud-news-subs.opml"));
            List<Outline> outlines = feed.getOutlines();
            SyndFeed syndFeed = new HttpURLFeedFetcher(HashMapFeedInfoCache.getInstance()).retrieveFeed(new URL(outlines.get(0).getChildren().get(0).getHtmlUrl()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

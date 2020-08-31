/*
 * MIT License
 *
 * Copyright (c) 2018 Soojeong Shin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.techno.vginv.utils;

/**
 * Store ConstantStrings for the NewsFeed app.
 */

public class Constants {

    /**
     * Create a private constructor because no one should ever create a {@link Constants} object.
     */
    private Constants() {
    }

    /**  Extract the key associated with the JSONObject */
    static final String JSON_KEY_RESPONSE = "response";
    static final String JSON_KEY_RESULTS = "results";
    static final String JSON_KEY_WEB_TITLE = "webTitle";
    static final String JSON_KEY_SECTION_NAME = "sectionName";
    static final String JSON_KEY_WEB_PUBLICATION_DATE = "webPublicationDate";
    static final String JSON_KEY_WEB_URL = "webUrl";
    static final String JSON_KEY_TAGS = "tags";
    static final String JSON_KEY_FIELDS = "fields";
    static final String JSON_KEY_THUMBNAIL = "thumbnail";
    static final String JSON_KEY_TRAIL_TEXT = "trailText";

    /** Read timeout for setting up the HTTP request */
    static final int READ_TIMEOUT = 10000; /* milliseconds */

    /** Connect timeout for setting up the HTTP request */
    static final int CONNECT_TIMEOUT = 15000; /* milliseconds */

    /** HTTP response code when the request is successful */
    static final int SUCCESS_RESPONSE_CODE = 200;

    /** Request method type "GET" for reading information from the server */
    static final String REQUEST_METHOD_GET = "GET";

    /** URL for news data from the guardian data set */
    public static final String NEWS_REQUEST_URL = "https://content.guardianapis.com/search";

    /** Parameters */
    public static final String QUERY_PARAM = "q";
    public static final String ORDER_BY_PARAM = "order-by";
    public static final String PAGE_SIZE_PARAM = "page-size";
    public static final String ORDER_DATE_PARAM = "order-date";
    public static final String FROM_DATE_PARAM = "from-date";
    public static final String SHOW_FIELDS_PARAM = "show-fields";
    public static final String FORMAT_PARAM = "format";
    public static final String SHOW_TAGS_PARAM = "show-tags";
    public static final String API_KEY_PARAM = "api-key";
    public static final String SECTION_PARAM = "section";

    /** The show fields we want our API to return */
    public static final String SHOW_FIELDS = "thumbnail,trailText";

    /** The format we want our API to return */
    public static final String FORMAT = "json";

    /** The show tags we want our API to return */
    public static final String SHOW_TAGS = "contributor";

    /** API Key */
    public static final String API_KEY = "cd1b6e57-f87f-4f62-a45f-ccbcc9fd1efd"; // Use your API Key when API rate limit exceeded

    /** Default number to set the image on the top of the textView */
    public static final int DEFAULT_NUMBER = 0;

    /** ConstantStrings value for each fragment */
    public static final int HOME = 0;
    public static final int WORLD = 1;
    public static final int SCIENCE = 2;
    public static final int SPORT = 3;
    public static final int ENVIRONMENT = 4;
    public static final int SOCIETY = 5;
    public static final int FASHION = 6;
    public static final int BUSINESS = 7;
    public static final int CULTURE = 8;

    public static String projectResponse = "{\n" +
            "    \"response\": {\n" +
            "        \"status\": \"ok\",\n" +
            "        \"userTier\": \"developer\",\n" +
            "        \"total\": 50859,\n" +
            "        \"startIndex\": 1,\n" +
            "        \"pageSize\": 3,\n" +
            "        \"currentPage\": 1,\n" +
            "        \"pages\": 16953,\n" +
            "        \"orderBy\": \"newest\",\n" +
            "        \"results\": [\n" +
            "            {\n" +
            "                \"id\": \"business/live/2019/oct/22/uk-companies-brexit-uncertainty-sterling-pound-public-finances-factories-business-live\",\n" +
            "                \"type\": \"liveblog\",\n" +
            "                \"sectionId\": \"business\",\n" +
            "                \"sectionName\": \"Business\",\n" +
            "                \"webPublicationDate\": \"2019-10-22T20:05:50Z\",\n" +
            "                \"webTitle\": \"Pound drops below $1.29 after Brexit vote; factory downturn worsens – business live\",\n" +
            "                \"webUrl\": \"https://www.theguardian.com/business/live/2019/oct/22/uk-companies-brexit-uncertainty-sterling-pound-public-finances-factories-business-live\",\n" +
            "                \"apiUrl\": \"https://content.guardianapis.com/business/live/2019/oct/22/uk-companies-brexit-uncertainty-sterling-pound-public-finances-factories-business-live\",\n" +
            "                \"fields\": {\n" +
            "                    \"trailText\": \"Rolling coverage of the latest business and financial news, as home delivery firm Just Eat receives a rival takeover offer\",\n" +
            "                    \"thumbnail\": \"https://media.guim.co.uk/10486f4522683fb0ecb0c46075b0549380757f6d/0_67_1000_600/500.jpg\"\n" +
            "                },\n" +
            "                \"tags\": [\n" +
            "                    {\n" +
            "                        \"id\": \"profile/graemewearden\",\n" +
            "                        \"type\": \"contributor\",\n" +
            "                        \"webTitle\": \"Graeme Wearden\",\n" +
            "                        \"webUrl\": \"https://www.theguardian.com/profile/graemewearden\",\n" +
            "                        \"apiUrl\": \"https://content.guardianapis.com/profile/graemewearden\",\n" +
            "                        \"references\": [],\n" +
            "                        \"bio\": \"<p>Graeme Wearden tracks the latest world business, economic and financial news in our daily liveblog. Previously he worked as a technology journalist at CNET Networks</p>\",\n" +
            "                        \"bylineImageUrl\": \"https://static.guim.co.uk/sys-images/Guardian/Pix/contributor/2014/5/7/1399476600504/Graeme-Wearden.jpg\",\n" +
            "                        \"bylineLargeImageUrl\": \"https://uploads.guim.co.uk/2017/10/06/Graeme-Wearden,-L.png\",\n" +
            "                        \"firstName\": \"wearden\",\n" +
            "                        \"lastName\": \"\"\n" +
            "                    }\n" +
            "                ],\n" +
            "                \"isHosted\": false,\n" +
            "                \"pillarId\": \"pillar/news\",\n" +
            "                \"pillarName\": \"News\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": \"business/nils-pratley-on-finance/2019/oct/22/the-just-eat-takeover-battle-is-promising-a-delicious-showdown\",\n" +
            "                \"type\": \"article\",\n" +
            "                \"sectionId\": \"business\",\n" +
            "                \"sectionName\": \"Business\",\n" +
            "                \"webPublicationDate\": \"2019-10-22T18:31:04Z\",\n" +
            "                \"webTitle\": \"The Just Eat takeover battle is promising a delicious showdown\",\n" +
            "                \"webUrl\": \"https://www.theguardian.com/business/nils-pratley-on-finance/2019/oct/22/the-just-eat-takeover-battle-is-promising-a-delicious-showdown\",\n" +
            "                \"apiUrl\": \"https://content.guardianapis.com/business/nils-pratley-on-finance/2019/oct/22/the-just-eat-takeover-battle-is-promising-a-delicious-showdown\",\n" +
            "                \"fields\": {\n" +
            "                    \"trailText\": \"With two big rival offers on the plate the food delivery giant can afford to sit back and await another helping\",\n" +
            "                    \"thumbnail\": \"https://media.guim.co.uk/c61f42c53012deb38a1c231723c8235ba6e3eee3/0_159_3955_2373/500.jpg\"\n" +
            "                },\n" +
            "                \"tags\": [\n" +
            "                    {\n" +
            "                        \"id\": \"profile/nilspratley\",\n" +
            "                        \"type\": \"contributor\",\n" +
            "                        \"webTitle\": \"Nils Pratley\",\n" +
            "                        \"webUrl\": \"https://www.theguardian.com/profile/nilspratley\",\n" +
            "                        \"apiUrl\": \"https://content.guardianapis.com/profile/nilspratley\",\n" +
            "                        \"references\": [],\n" +
            "                        \"bio\": \"<p>Nils Pratley is the Guardian's financial editor. Read his opinions on markets, boardroom winner and losers and business values on his <a href=\\\"http://www.guardian.co.uk/business/nils-pratley-on-finance\\\">Nils Pratley on Finance blog</a>. Follow him on Twitter at <a href=\\\"http://twitter.com/nilspratley\\\">@nilspratley</a> and <a href=\\\"https://plus.google.com/112046473834754672586?rel=author\\\">on Google+</a></p>\",\n" +
            "                        \"bylineImageUrl\": \"https://static.guim.co.uk/sys-images/Guardian/Pix/pictures/2014/4/17/1397749337655/NilsPratley.jpg\",\n" +
            "                        \"bylineLargeImageUrl\": \"https://uploads.guim.co.uk/2017/10/09/Nils-Pratley,-R.png\",\n" +
            "                        \"firstName\": \"pratley\",\n" +
            "                        \"lastName\": \"\"\n" +
            "                    }\n" +
            "                ],\n" +
            "                \"isHosted\": false,\n" +
            "                \"pillarId\": \"pillar/news\",\n" +
            "                \"pillarName\": \"News\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": \"business/2019/oct/22/deliveroos-highest-paid-director-gets-57-pay-rise-despite-losses\",\n" +
            "                \"type\": \"article\",\n" +
            "                \"sectionId\": \"business\",\n" +
            "                \"sectionName\": \"Business\",\n" +
            "                \"webPublicationDate\": \"2019-10-22T18:00:34Z\",\n" +
            "                \"webTitle\": \"Deliveroo's highest paid director gets 57% pay rise despite losses\",\n" +
            "                \"webUrl\": \"https://www.theguardian.com/business/2019/oct/22/deliveroos-highest-paid-director-gets-57-pay-rise-despite-losses\",\n" +
            "                \"apiUrl\": \"https://content.guardianapis.com/business/2019/oct/22/deliveroos-highest-paid-director-gets-57-pay-rise-despite-losses\",\n" +
            "                \"fields\": {\n" +
            "                    \"trailText\": \"Will Shu understood to be unnamed person who also received £8.3m in share options\",\n" +
            "                    \"thumbnail\": \"https://media.guim.co.uk/8be28368427664a6668c7ee822b8e160c3a97403/0_332_6720_4032/500.jpg\"\n" +
            "                },\n" +
            "                \"tags\": [\n" +
            "                    {\n" +
            "                        \"id\": \"profile/sarahbutler\",\n" +
            "                        \"type\": \"contributor\",\n" +
            "                        \"webTitle\": \"Sarah Butler\",\n" +
            "                        \"webUrl\": \"https://www.theguardian.com/profile/sarahbutler\",\n" +
            "                        \"apiUrl\": \"https://content.guardianapis.com/profile/sarahbutler\",\n" +
            "                        \"references\": [],\n" +
            "                        \"bio\": \"<p>Sarah Butler is a Guardian journalist, writing about retail companies, consumer goods and workers' rights. Twitter&nbsp;<a href=\\\"https://twitter.com/whatbutlersaw\\\">@whatbutlersaw</a></p>\",\n" +
            "                        \"bylineImageUrl\": \"https://uploads.guim.co.uk/2017/12/27/Sarah-Butler.jpg\",\n" +
            "                        \"bylineLargeImageUrl\": \"https://uploads.guim.co.uk/2017/12/27/Sarah_Butler,_R.png\",\n" +
            "                        \"firstName\": \"Sarah\",\n" +
            "                        \"lastName\": \"Butler\",\n" +
            "                        \"twitterHandle\": \"whatbutlersaw\"\n" +
            "                    }\n" +
            "                ],\n" +
            "                \"isHosted\": false,\n" +
            "                \"pillarId\": \"pillar/news\",\n" +
            "                \"pillarName\": \"News\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";

}

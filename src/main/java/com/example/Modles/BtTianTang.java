package com.example.Modles;

import com.example.InterFaces.RunInterFace;
import com.example.MyClass;
import com.example.OkNetWork.OkHttpManager;
import org.apache.commons.io.FileUtils;
import org.apache.http.util.TextUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.selector.Html;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/7/17.
 */
public class BtTianTang implements RunInterFace {
    String savePath = "";

    @Override
    public void run() {
//        for (int i = 1; i <= 731; i++) {
//            Spider.create(new MyClass())
//                    //从"https://github.com/code4craft"开始抓
//                    .addUrl("http://www.bttiantang.com/?PageNo=" + i)
//                    //开启5个线程抓取
//                    .thread(6)
//                    //启动爬虫
//                    .run();
//        }
        Spider.create(new MyClass())
                //从"https://github.com/code4craft"开始抓
                .addUrl("http://www.bttiantang.com/")
                //开启5个线程抓取
                .thread(6)
                //启动爬虫
                .run();
    }


    @Override
    public void content(final Page page) {
        if (!TextUtils.isEmpty(page.getHtml().xpath("//form//input[@name='action']/@value").toString())
                && !TextUtils.isEmpty(page.getHtml().xpath("//form//input[@name='id']/@value").toString())
                && !TextUtils.isEmpty(page.getHtml().xpath("//form//input[@name='uhash']/@value").toString())) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpManager okHttpManager = new OkHttpManager();
                    Map<String, String> map = new HashMap<>();
                    map.put("action", page.getHtml().xpath("//form//input[@name='action']/@value").toString());
                    map.put("id", page.getHtml().xpath("//form//input[@name='id']/@value").toString());
                    map.put("uhash", page.getHtml().xpath("//form//input[@name='uhash']/@value").toString());


                    okHttpManager.downFile("http://www.bttiantang.com//download3.php", map, savePath);

                }
            }).start();
        } else if (page.getHtml().xpath("//div[@class='tinfo']//a/@href").all().size() > 0) {
            savePath = new String();
            List<String> list = page.getHtml().xpath("//ul[@class='moviedteail_list']//li").all();
            String directoriesName = page.getHtml().xpath("//div[@class='moviedteail_tt']//h1/text()").toString();
            directoriesName = directoriesName.replaceAll("/", "");
            directoriesName = directoriesName.replaceAll(":", "");
            directoriesName = directoriesName.replaceAll("：", "");
            if (list.size() >= 7) {
                final String filePath = OkHttpManager.defaultPath + Html.create(list.get(3)).xpath("//a/text()").toString() + "\\" + Html.create(list.get(2)).xpath("//a/text()").toString() + "\\" + directoriesName;
                savePath = filePath;
//                File file = new File(filePath);
//                if (!file.exists()) {
//                    file.mkdirs();
//                }


                try {
                    StringBuffer txtContent = new StringBuffer();
                    for (int i = 0; i < list.size(); i++) {
                        switch (i) {
                            case 0:
                                txtContent.append("又名:" + Html.create(list.get(i)).xpath("//a/text()").toString() + "\n");
                                break;
                            case 1:
                                txtContent.append("标签:" + Html.create(list.get(i)).xpath("//a/text()").toString()+ "\n");
                                break;
                            case 2:
                                txtContent.append("地区:" + Html.create(list.get(i)).xpath("//a/text()").toString()+ "\n");
                                break;
                            case 3:
                                txtContent.append("年份:" + Html.create(list.get(i)).xpath("//a/text()").toString()+ "\n");
                                break;
                            case 4:
                                txtContent.append("导演:" + Html.create(list.get(i)).xpath("//a/text()").toString()+ "\n");
                                break;
                            case 5:
                                txtContent.append("编剧:" + Html.create(list.get(i)).xpath("//a/text()").toString()+ "\n");
                                break;
                            case 6:
                                txtContent.append("主演:" + Html.create(list.get(i)).xpath("//a/text()").toString()+ "\n");
                                break;
                        }
                    }
                    txtContent.append("豆瓣评分:" + page.getHtml().xpath("//em[@class='fm']/text()").toString()+ "\n");
                    FileUtils.write(new File(filePath + "\\介绍.txt"), txtContent, Charset.forName("UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpManager manager = new OkHttpManager();
                        manager.downFile(page.getHtml().xpath("//div[@class='moviedteail_img']//a//img/@src").toString(), null, filePath);
                    }
                }).start();
                page.addTargetRequests(page.getHtml().xpath("//div[@class='tinfo']//a/@href").all());
            } else {
                page.addTargetRequests(page.getHtml().xpath("//div[@class='tinfo']//a/@href").all());
            }
        } else if (page.getHtml().xpath("//div[@class='ml']").regex("http://www.bttiantang.com/subject/.{3,10}html").all().size() > 0) {
            //匹配出所有 适合条件的链接
            List<String> list = page.getHtml().xpath("//div[@class='ml']").regex("http://www.bttiantang.com/subject/.{3,10}html").all();
            //过滤重复链接
            HashSet hashSet = new HashSet(list);
            list.clear();
            list.addAll(hashSet);
            page.addTargetRequests(list);
        }
    }
}

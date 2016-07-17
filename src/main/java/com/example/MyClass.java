package com.example;

import com.example.InterFaces.RunInterFace;
import com.example.Modles.BtTianTang;
import org.apache.log4j.PropertyConfigurator;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class MyClass implements PageProcessor {
    private static RunInterFace interFace = new BtTianTang();
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(20).setSleepTime(500).setTimeOut(20000);

    public static void main(String[] args) {

        PropertyConfigurator.configure(System.getProperty("user.dir") + "\\src\\main\\java\\log4j.properties");
        interFace.run();

    }

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        interFace.content(page);
    }

    @Override
    public Site getSite() {
        return site;
    }
}
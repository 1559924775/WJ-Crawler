package com.uestc.service.impl;

import com.uestc.entity.DoubanPage;
import com.uestc.entity.Page;
import com.uestc.service.IProcessService;
import com.uestc.util.HtmlUtil;
import com.uestc.util.LoadPropertyUtil;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 解析html页面得到想要的信息
 * @author 王俊
 */
@Scope("prototype")
@Service
public class DoubanProcessService implements IProcessService {
    public void process(Page page1) {
        DoubanPage page=(DoubanPage)page1;
        String content = page.getContent();

        HtmlCleaner htmlCleaner = new HtmlCleaner();
        TagNode rootNode = htmlCleaner.clean(content);

        //获取电影名  //*[@id="content"]/h1/span[1]
        String moveName= getFieldByRegex(rootNode,"parseMoveName","moveNameRegex");
        page.setMoveName(moveName);

        //获取评分  //*[@id="interest_sectl"]/div[1]/div[2]/strong
        String score= getFieldByRegex(rootNode,"parseScore","scoreRegex");
        page.setScore(score);

        //获取影片类型    //*[@id="info"]/span[4]
        String type= getFieldByRegex(rootNode,"parseType","typeRegex");
        page.setType(type);

        //获取评分人数    //*[@id="interest_sectl"]/div[1]/div[2]/div/div[2]/a/span
        String  commentnumber= getFieldByRegex(rootNode,"parseCommentnumber","commentnumberRegex");
        page.setCommentnumber( commentnumber);
    }
    private String getFieldByRegex(TagNode rootNode,String xPath,String regex){
        return  HtmlUtil.getFieldByRegex(rootNode, LoadPropertyUtil.getDouban( xPath), LoadPropertyUtil.getDouban(regex));
    }
}

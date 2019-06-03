package com.uestc.service.impl;


import com.uestc.entity.Page;
import com.uestc.entity.ZonghengPage;
import com.uestc.service.IProcessService;
import com.uestc.util.HtmlUtil;
import com.uestc.util.LoadPropertyUtil;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 解析纵横html
 * @author 王俊
 */
@Service
@Scope("prototype")
public class ZonghengProcessService implements IProcessService {
    @Override
    public void process(Page page1) {
        ZonghengPage page=(ZonghengPage)page1;
        String content = page.getContent();
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        TagNode rootNode = htmlCleaner.clean(content);

        //获取书名  /html/body/div[2]/div[5]/div[1]/div[1]/div[1]/div[2]/div[1]
        String name= getFieldByRegex(rootNode,"parseName","regex");
        page.setName(name);
        //System.out.println(name);

        //获取作者  /html/body/div[2]/div[5]/div[1]/div[2]/div[1]/div[2]/a
        String author= getFieldByRegex(rootNode,"parseAuthor","regex");
        page.setAuthor(author);
        //System.out.println(author);

        //获取书类型    /html/body/div[2]/div[5]/div[1]/div[1]/div[1]/div[2]/div[2]/a[2]
        String type= getFieldByRegex(rootNode,"parseType","regex");
        page.setType(type);
        //System.out.println(type);

        //获取字数    /html/body/div[2]/div[5]/div[1]/div[1]/div[1]/div[2]/div[3]/span[1]/i
        String  number= getFieldByRegex(rootNode,"parseNumber","regex");
        page.setNumber(number);
        //System.out.println(number);


        //获取总推荐数    /html/body/div[2]/div[5]/div[1]/div[1]/div[1]/div[2]/div[3]/span[2]/i
        String recommend= getFieldByRegex(rootNode,"parseRecommend","regex");
        page.setRecommend(recommend);
        //System.out.println(recommend);

        //获取总点击量    /html/body/div[2]/div[5]/div[1]/div[1]/div[1]/div[2]/div[3]/span[3]/i
        String click= getFieldByRegex(rootNode,"parseClick","regex");
        page.setClick(click);
        //System.out.println( click);

        //获取周推荐   /html/body/div[2]/div[5]/div[1]/div[1]/div[1]/div[2]/div[3]/span[4]/i
        String  recommend_week= getFieldByRegex(rootNode,"parseRecommend_week","regex");
        page.setRecommend_week(recommend_week);
        //System.out.println(recommend_week);

    }

    private String getFieldByRegex(TagNode rootNode,String xPath,String regex){
        return  HtmlUtil.getFieldByRegex(rootNode, LoadPropertyUtil.getZongheng( xPath), LoadPropertyUtil.getZongheng(regex));
    }



}

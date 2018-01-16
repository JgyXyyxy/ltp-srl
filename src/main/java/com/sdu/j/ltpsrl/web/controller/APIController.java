package com.sdu.j.ltpsrl.web.controller;


import com.sdu.j.ltpsrl.web.service.SRLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class APIController {

    @Autowired
    SRLService srlService;

    @RequestMapping(value = "/input",method = RequestMethod.GET)
    public @ResponseBody String  parse(@RequestParam("text") String text) throws InterruptedException {
        if (!srlService.getStatus()){
            srlService.start();
        }

        String[] strings = text.split("。");
        for (String b :strings) {
            b = b+"。";
            System.out.println(b);
            srlService.putSentence(b);
        }

        return "ok";
    }

    public static void main(String[] args) {
        String a = "1961年8月4日出生，美国民主党籍政治家，第44任美国总统，为美国历史上第一位非洲裔总统。" +
                "1991年，奥巴马以优等生荣誉从哈佛法学院毕业。2007年2月10日，宣布参加2008年美国总统选举。2008年11月4日正式当选美国总统。";
        String[] strings = a.split("。");
        for (String b :strings
             ) {System.out.println(b);

        }
    }
}

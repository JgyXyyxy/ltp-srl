package com.sdu.j.ltpsrl.web.service;

import com.sdu.j.ltpsrl.conn.LtpAPIConnectionFactory;
import com.sdu.j.ltpsrl.conn.ResultCache;
import com.sdu.j.ltpsrl.entity.SyntaxResult;
import com.sdu.j.ltpsrl.entity.Timenode;
import com.sdu.j.ltpsrl.entity.TransElement;
import com.sdu.j.ltpsrl.util.ParseXml;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class SRLService {

    private final Logger log = Logger.getLogger(SRLService.class);
    private final ResultCache resltCache = ResultCache.newInstance();
    private final BlockingQueue<String> requestCache = new LinkedBlockingQueue<>();
    private boolean status = false;

    private List<Timenode> timeline = new ArrayList();

    public void putSentence(String text) throws InterruptedException {
        log.info("put request: " + text);
        requestCache.put(text);
    }

    public boolean getStatus() {
        return status;
    }

    public void parseSingleSentence(String text) {
        TransElement element = LtpAPIConnectionFactory.configConnection(text).openConnection();
        resltCache.putElement(element);
    }

    public void start() {
        status = true;
        new Thread(new PullService()).start();
        new Thread(new RequestService()).start();
    }

    class PullService implements Runnable {


        @Override
        public void run() {
            int count = 0;
            log.info("start PullService");
            while (status) {
                TransElement element = resltCache.pollElement();
                if (element != null) {
                    try {
                        SyntaxResult syntaxResult = ParseXml.parseSyntax(element.getBody());
                        try {
                            syntaxResult.dotrans();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    count++;
                }
            }
            log.info("close PullService succ,complete " + count + " element pull");
        }
    }

    class RequestService implements Runnable {

        @Override
        public void run() {
            int count = 0;
            log.info("start RequestService");
            while (status) {
                try {
                    String requestText = requestCache.poll(2, TimeUnit.SECONDS);
                    if (requestText != null) {
                        log.debug("get request: " + requestText);
                        parseSingleSentence(requestText);
                        count++;
                        Thread.sleep(50);
                    }
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
            log.info("close requestService succ,complete " + count + " request send");
        }

    }
}

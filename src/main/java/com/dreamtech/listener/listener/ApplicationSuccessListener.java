package com.dreamtech.listener.listener;

import com.dreamtech.anno.Listener;
import com.dreamtech.exceptions.MINIExceptionProcessor;
import com.dreamtech.listener.event.EnvironmentSuccessEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * mini-spring成功启动 打印banner
 */
@Listener
public class ApplicationSuccessListener implements ApplicationListener<EnvironmentSuccessEvent> {

    private static final String COLOR_OUTPUT_PREFIX = "\033[34;1m";
    private static final String WHITE_OUTPUT_SUFFIX = "\033[0m";
    private static final String HELLO = "Hello mini-spring";
    private static final String VERSION = "1.0";
    private static final String DELIMITER = " :: ";

    @Override
    public void onApplicationEvent(EnvironmentSuccessEvent applicationEvent) {
        try {
            printBanner();
        } catch (Exception e) {
            MINIExceptionProcessor.getInstance().putException("print banner error", e);
        }
    }

    private void printBanner() throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("banner.txt");
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println(COLOR_OUTPUT_PREFIX + HELLO + DELIMITER + VERSION + WHITE_OUTPUT_SUFFIX);
    }
}

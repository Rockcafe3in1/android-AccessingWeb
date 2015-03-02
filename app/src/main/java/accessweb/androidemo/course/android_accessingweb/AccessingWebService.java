package accessweb.androidemo.course.android_accessingweb;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Accessing web page by using HttpURLConneciton and URL class
 * int Java stander. by the way Http connection and its operation
 * must not exist in main thread, that is the reason to use Service
 * to reach goal
 *
 * Input: Web page address, "http://www.vogella.com"
 * Output: String of web page, String webPage
 * Resource: URL, HttpURLConnection, permission to access internet
 *
 * @see android.app.Service
 * */
public class AccessingWebService extends Service {

    public static final String ADDRESS_INTENT_EXTR = "Web page address";
    // Defining the communication channel
//    private AccessingWebService mawService;
    private String mWebPageAddress;
    private AccessingWebBinder mBinder;

    public AccessingWebService() {
//        mawService = new AccessingWebService();
        mBinder = new AccessingWebBinder();
    }

    /**
     * onBind callback method which was originally from Service
     *
     * @param intent the Intent could carry parameter
     * */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        mWebPageAddress = intent.getStringExtra(ADDRESS_INTENT_EXTR);
        if(!isLegalAddress(mWebPageAddress)) {
            return null;
        }

        // Encapsulating instance of AccessingWebService into IBinder
        // Declare subclass which is inherilization from IBinder
        return (IBinder)mBinder;
    }

    public class AccessingWebBinder extends Binder {
        // Declare method accessed by 'client'
        public AccessingWebService getService() {
            // Return this instance of AccessingWebService
            return AccessingWebService.this;
        }

    }

    private boolean isLegalAddress(String webPageAddress) {
        // Prefix must be 'http://' or 'https://'
        return true;
    }

    public StringBuffer accessWeb() {
        StringBuffer webPage = null;

        // Read web page from internet
        try {
            // TODO: The web address will be initialized by invoker
            URL url = new URL(mWebPageAddress);

            // Obtaining instance of HttpURLConnection
            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            // Read web page and printed on screen
            webPage = readStream(con.getInputStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return webPage;
    }

    /**
     * Read input stream and print it on screen by line
     * */
    private StringBuffer readStream(InputStream webPageStream) {
        StringBuffer stringBufferContainer = null;
        BufferedReader reader = null;

        reader = new BufferedReader(new InputStreamReader(webPageStream));
        String line = "";
        try {
            while((line = reader.readLine()) != null) {
                System.out.println(line);

                // TODO: Printing it on screen
                // Feedback web page in text format to main activity
                stringBufferContainer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBufferContainer;
    }
}

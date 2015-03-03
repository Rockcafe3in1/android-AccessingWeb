package accessweb.androidemo.course.android_accessingweb;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class AccessingWeb extends Activity {

    private static final String TAG = "AccessingWeb";
    private static final int MSG_ON_RECEIVED_NEW_WEB = 1000;
    private AccessingWebService.AccessingWebBinder mAwBinder;
    private TextView mWebPageText;
    private Handler mAccessingWebHandler;
    private StringBuffer mWebpageContainer;
    /* Create interface to get feedback from AccessWebService */
    private OnWebPageReceivedListener mWebPageReceivedListener = new OnWebPageReceivedListener() {

        /**
         * Receive web page data
         *
         * @param webPage the textual form of web page
         * */
        @Override
        public void onReceived(StringBuffer webPage) {
            // Only the original thread that created a view hierarchy
            // can touch its views. It throws CalledFromWrongThreadException

            // TODO: post web page by message handler
            // Tell main thread to access web page
            // from WebpageContainer
            Message msg = new Message();
            msg.arg1 = MSG_ON_RECEIVED_NEW_WEB;
            // TODO: synchronizing data and UI
            mWebpageContainer = webPage;
            mAccessingWebHandler.sendMessage(msg);
//            mWebPageText.setText(webPage.toString());
        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Once connecting to service successfully
            // Obtain targeting service 'communication channel'
            // Convert IBinder to instance of AccessingWebService
            mAwBinder = (AccessingWebService.AccessingWebBinder) service;

            // Setup feedback channel to get web page from service
            mAwBinder.registerFeedBack(mWebPageReceivedListener);

            // Everything is ok now
            visitWebPage(mAwBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Release the instance of AccessingWebService
            mAwBinder = null;
        }
    };

    public AccessingWeb() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessing_web);

        mWebPageText = (TextView) findViewById(R.id.webpage);

        // TODO: To solve wrong thread exception in onReceived method
        // using message handle to update text view in main thread
        mAccessingWebHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.arg1 == MSG_ON_RECEIVED_NEW_WEB) {
                    // synchronized accessing
                    mWebPageText.setText(mWebpageContainer.toString());
                }
                return false;
            }
        });

        // Demonstrating purpose do not run it in main thread
        // Unfortunately it throw android.os.NetworkOnMainThreadException
        // because of our Http connection performed in ui thread.
        // TODO: Considering service encapsulating HttpURLConnection and URL

        // Bind to AccessingWebService
        // bindService() and unBindService();
        Intent intent = new Intent();
        intent.setPackage(this.getPackageName());
        intent.setClassName(this, this.getPackageName() + ".AccessingWebService");
        intent.putExtra(AccessingWebService.ADDRESS_INTENT_EXTR, "http://www.vogella.com");
        // TODO: Check the if the class name is correct
        if (!bindService(intent, mServiceConnection, BIND_AUTO_CREATE)) {
            Log.i(TAG, "Cannot bind service: " + intent.getClass().getName());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        visitWebPage(mAwBinder);
    }

    @Override
    protected void onStop() {
        unbindService(mServiceConnection);
        super.onStop();
    }

    /**
     * Update web page in textual format to main screen
     *
     * @param awBinder the binder which is originally from service
     */
    private void visitWebPage(AccessingWebService.AccessingWebBinder awBinder) {
        if (awBinder != null) {

            // TODO: Catch unhandled exception in method accessWeb.
            // ISSUE: It still throws android.os.NetworkOnMainThreadException
            // However HttpURLConnection and URL class already encapsulated
            // in Service without in main thread. So what's wrong?

            // Performing internet operation in working thread
            awBinder.accessWebInWorkerThread();
//            mAwBinder.runLongTimeOperation();
//            charSequence = (CharSequence) mAwBinder.getService().accessWeb();
//            mWebPageText.setText(charSequence);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accessing_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

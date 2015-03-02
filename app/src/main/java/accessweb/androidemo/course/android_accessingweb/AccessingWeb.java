package accessweb.androidemo.course.android_accessingweb;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class AccessingWeb extends Activity {

    private AccessingWebService.AccessingWebBinder mAwBinder;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Once connecting to service successfully
            // Obtain targeting service 'communication channel'
            // Convert IBinder to instance of AccessingWebService
            mAwBinder = (AccessingWebService.AccessingWebBinder) service;
            updateWebPage();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Release the instance of AccessingWebService
            mAwBinder = null;
        }
    };
    private TextView mWebPageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessing_web);

        mWebPageText = (TextView) findViewById(R.id.webpage);

        // Demonstrating purpose do not run it in main thread
        // Unfortunately it throw android.os.NetworkOnMainThreadException
        // because of our Http connection performed in ui thread.
        // TODO: Considering service encapsulating HttpURLConnection and URL

        // Bind to AccessingWebService
        // bindService() and unBindService();
        Intent intent = new Intent();
        intent.setPackage(this.getPackageName());
        intent.setClassName(this, "AccessingWebService");
        intent.putExtra(AccessingWebService.ADDRESS_INTENT_EXTR, "http://www.vogella.com");
        // TODO: Check if class name is correct one
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWebPage();
    }

    @Override
    protected void onStop() {
        unbindService(mServiceConnection);
        super.onStop();
    }

    /**
     * Update web page in textual format
     * */
    private void updateWebPage() {
        CharSequence charSequence = null;
        if (mAwBinder != null) {
            charSequence = (CharSequence) mAwBinder.getService().accessWeb();
            mWebPageText.setText(charSequence);
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

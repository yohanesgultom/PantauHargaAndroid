package id.pantauharga.android.aktivitas;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.ViewConfiguration;
import android.widget.ListView;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.pantauharga.android.R;

/**
 * Created by widodo on 3/31/16.
 */
public class Notification extends BaseActivityLocation {

    private ActionBar aksibar;

    @Bind(R.id.toolbar)
    Toolbar tolbar;
    @Bind(R.id.listNotification)
    ListView listNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_notification);
        ButterKnife.bind(Notification.this);
        munculMenuAction(Notification.this);

        if (tolbar != null) {
            Notification.this.setSupportActionBar(tolbar);
        }

        aksibar = Notification.this.getSupportActionBar();
        assert aksibar != null;
        aksibar.setDisplayHomeAsUpEnabled(true);
        aksibar.setTitle(R.string.notification_title);

    }


    //MENAMPILKAN MENU ACTION BAR
    private void munculMenuAction(Context context) {

        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKey = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKey != null) {
                menuKey.setAccessible(true);
                menuKey.setBoolean(config, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

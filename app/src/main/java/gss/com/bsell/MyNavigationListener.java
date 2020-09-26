package gss.com.bsell;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

public class MyNavigationListener implements NavigationView.OnNavigationItemSelectedListener {
    private Context context;
    MyNavigationListener(Context context){
        this.context =  context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            /*case R.id.nav_about_us:
                Intent intent1 = new Intent(context, AboutUsActivity.class);
                context.startActivity(intent1);
                break;
            case R.id.nav_contact_us:
                Intent intent2 = new Intent(context, PhotoListActivity.class);
                context.startActivity(intent2);
                break;*/
            case R.id.nav_logout:
                Intent intent3 = new Intent(context, SelectionScreen.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent3);
                break;

        }
        return false;
    }


}

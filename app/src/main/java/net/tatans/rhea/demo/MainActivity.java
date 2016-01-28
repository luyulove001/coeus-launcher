package net.tatans.rhea.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.tatans.coeus.launcher.R;
import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.coeus.speaker.Speaker;


public class MainActivity extends TatansActivity {
    @ViewInject(id= R.id.text ,click="btnClick")
    TextView textView;
    private Speaker speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        speaker=Speaker.getInstance(this);
    }

    public void btnClick(View view){
        speaker.speech("天天想你.");
    }
}

package com.james.calculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.example.james.calculator.R;


public class PrefsSettings extends AppCompatActivity implements ColorChooserDialog.ColorCallback {

    SharedPreferences prefs;
    int actionbar, navbar, text;
    boolean automem, history;
    Button action, nav, txt, reset;
    SwitchCompat mem, hist;
    ImageView a, n, t;

    @Override
    public void onColorSelection(ColorChooserDialog dialog, int color) {
        int title = dialog.getTitle();
        if (title == R.string.actionbr) {
            actionbar = color;
        } else if (title == R.string.navbr) {
            navbar = color;
        } else if (title == R.string.txt) {
            text = color;
        }
        refresh();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Preferences");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        action = (Button) findViewById(R.id.button);
        nav = (Button) findViewById(R.id.button2);
        txt = (Button) findViewById(R.id.button3);
        reset = (Button) findViewById(R.id.button21);

        hist = (SwitchCompat) findViewById(R.id.switch2);
        mem = (SwitchCompat) findViewById(R.id.switch1);

        a = (ImageView) findViewById(R.id.imageView2);
        n = (ImageView) findViewById(R.id.imageView3);
        t = (ImageView) findViewById(R.id.imageView4);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        actionbar = prefs.getInt("actionbar", getResources().getColor(R.color.primary));
        navbar = prefs.getInt("navbar", getResources().getColor(R.color.accent));
        text = prefs.getInt("text", getResources().getColor(R.color.text));

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorChooserDialog.Builder(PrefsSettings.this, R.string.actionbr)
                        .titleSub(R.string.actionbr)
                        .preselect(actionbar)
                        .show();
            }
        });

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorChooserDialog.Builder(PrefsSettings.this, R.string.navbr)
                        .titleSub(R.string.navbr)
                        .preselect(navbar)
                        .show();
            }
        });

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorChooserDialog.Builder(PrefsSettings.this, R.string.txt)
                        .titleSub(R.string.txt)
                        .preselect(text)
                        .show();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionbar = getResources().getColor(R.color.primary);
                navbar = getResources().getColor(R.color.accent);
                text = getResources().getColor(R.color.text);
                refresh();
            }
        });

        history = prefs.getBoolean("history", true);
        automem = prefs.getBoolean("automem", false);

        hist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                history = isChecked;
                refresh();
            }
        });

        mem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                automem = isChecked;
                refresh();
            }
        });

        refresh();
    }

    public void refresh() {
        hist.setChecked(history);
        mem.setChecked(automem);
        a.setColorFilter(actionbar, PorterDuff.Mode.MULTIPLY);
        n.setColorFilter(navbar, PorterDuff.Mode.MULTIPLY);
        t.setColorFilter(text, PorterDuff.Mode.MULTIPLY);

        prefs.edit().putInt("actionbar", actionbar).apply();
        prefs.edit().putInt("navbar", navbar).apply();
        prefs.edit().putInt("text", text).apply();

        prefs.edit().putBoolean("history", history).apply();
        prefs.edit().putBoolean("automem", automem).apply();
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    private Intent getParentActivityIntentImpl() {
        return new Intent(PrefsSettings.this, MainActivity.class);
    }
}

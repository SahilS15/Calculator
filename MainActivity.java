package com.james.calculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.james.calculator.R;

import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    SharedPreferences prefs;
    FloatingActionButton fab;
    RecyclerView recycler;

    int actionbar, navbar, text;
    float offset;
    boolean history;

    ArrayList<String> equations, titles;

    EquationAdapter eq;

    AlertDialog newAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        equations = new ArrayList<>();
        titles = new ArrayList<>();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        actionbar = prefs.getInt("actionbar", getResources().getColor(R.color.primary));
        navbar = prefs.getInt("navbar", getResources().getColor(R.color.accent));
        text = prefs.getInt("text", getResources().getColor(R.color.text));

        history = prefs.getBoolean("history", true);
        if (history) {
            loadArchivedArrays();
        }

        if (isColorDark(actionbar)) {
            this.setTheme(R.style.Theme_AppCompat_NoActionBar);
        }

        setContentView(R.layout.activity_main);

        recycler = (RecyclerView) findViewById(R.id.recycler);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setBackgroundTintList(ColorStateList.valueOf(navbar));

        findViewById(R.id.fl).setBackgroundColor(headerColor(text));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        offset = size.y;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setTitle("Calculator");
        toolbar.setBackgroundColor(actionbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(darkColor(actionbar));
            getWindow().setNavigationBarColor(navbar);
        }

        recycler.setLayoutManager(new GridLayoutManager(this, 1));
        eq = new EquationAdapter(MainActivity.this, equations, titles, actionbar, text, navbar, history);
        recycler.setAdapter(eq);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int pos = viewHolder.getPosition();
                eq.removeItem(pos);
            }
        }).attachToRecyclerView(recycler);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.new_equation_item, null);

                LinearLayout bg = (LinearLayout) view.findViewById(R.id.bg);
                bg.setBackgroundColor(headerColor(text));

                final AppCompatEditText tev = (AppCompatEditText) view.findViewById(R.id.title);
                tev.setBackgroundColor(actionbar);
                tev.setTextColor(headerColor(actionbar));

                final CustomEditText aset = (CustomEditText) view.findViewById(R.id.equation);
                aset.setTextColor(text);
                aset.setHintTextColor(text);

                Button cancel = (Button) view.findViewById(R.id.cancel);
                cancel.setTextColor(navbar);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newAlert.isShowing()) {
                            newAlert.dismiss();
                        }
                    }
                });

                Button create = (Button) view.findViewById(R.id.create);
                create.setTextColor(navbar);
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newAlert.isShowing()) {
                            String eqe = aset.getText().toString();

                            try {
                                eq.addItem(tev.getText().toString(), eqe + " = " + String.valueOf(new ExpressionBuilder(eqe).build().evaluate()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            newAlert.dismiss();
                        }
                    }
                });

                newAlert = new AlertDialog.Builder(v.getContext()).setView(view).create();
                newAlert.show();
            }
        });
    }

    public int darkColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.tutorial).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this, FirstTime.class));
                return true;
            }
        });
        menu.findItem(R.id.setting).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this, PrefsSettings.class));
                return true;
            }
        });
        menu.findItem(R.id.about).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this, About.class));
                return true;
            }
        });
        return result;
    }

    public boolean isColorDark(int color){
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114* Color.blue(color))/255;
        return darkness >= 0.5;
    }

    private int textColor(int color) {
        if (isColorDark(color)) {
            return getResources().getColor(R.color.text_light);
        } else {
            return getResources().getColor(R.color.text);
        }
    }

    private int headerColor(int color) {
        if (isColorDark(color)) {
            return getResources().getColor(R.color.header_light);
        } else {
            return getResources().getColor(R.color.header);
        }
    }

    private void loadArchivedArrays() {
        equations.clear();

        int size = prefs.getInt("equation_size", 0);

        for(int i=0;i<size;i++) {
            equations.add(prefs.getString("equation_" + String.valueOf(i), null));
            titles.add(prefs.getString("title_" + String.valueOf(i), null));
        }
    }
}

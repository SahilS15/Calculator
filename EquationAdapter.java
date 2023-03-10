package com.james.calculator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.james.calculator.R;

import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;

public class EquationAdapter extends RecyclerView.Adapter<EquationAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<String> equations, titles;
    int color, text, accent;
    AlertDialog newAlert;
    boolean history;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View v, title, equation;
        public ViewHolder(View v, View title, View equation) {
            super(v);
            this.v = v;
            this.title = title;
            this.equation = equation;
        }
    }

    public EquationAdapter(Activity activity, ArrayList<String> equations, ArrayList<String> titles, int color, int text, int accent, boolean history) {
        this.activity = activity;
        context = activity.getApplicationContext();
        this.equations = equations;
        this.titles = titles;
        this.color = color;
        this.text = text;
        this.accent = accent;
        this.history = history;
    }

    @Override
    public EquationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.equation_item, parent, false);
        return new ViewHolder(v, v.findViewById(R.id.title), v.findViewById(R.id.equation));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TextView title = ((TextView)holder.title);
        title.setBackgroundColor(color);
        title.setTextColor(headerColor(color));
        title.setText(titles.get(position));
        ((TextView) holder.equation).setText(equations.get(position));

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View buttons = v.findViewById(R.id.buttons);
                if (buttons.getVisibility() == View.VISIBLE) {
                    buttons.setVisibility(View.GONE);
                } else {
                    buttons.setVisibility(View.VISIBLE);

                    Button edit = (Button) v.findViewById(R.id.edit);
                    edit.setTextColor(accent);
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View view = activity.getLayoutInflater().inflate(R.layout.new_equation_item, null);

                            LinearLayout bg = (LinearLayout) view.findViewById(R.id.bg);
                            bg.setBackgroundColor(headerColor(text));

                            final AppCompatEditText tev = (AppCompatEditText) view.findViewById(R.id.title);
                            tev.setText(titles.get(holder.getAdapterPosition()));
                            tev.setBackgroundColor(color);
                            tev.setTextColor(headerColor(color));

                            final CustomEditText aset = (CustomEditText) view.findViewById(R.id.equation);
                            aset.setText(equations.get(holder.getAdapterPosition()).split(" = ")[0]);
                            aset.setTextColor(text);
                            aset.setHintTextColor(text);

                            Button cancel = (Button) view.findViewById(R.id.cancel);
                            cancel.setTextColor(accent);
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (newAlert.isShowing()) {
                                        newAlert.dismiss();
                                    }
                                }
                            });

                            Button create = (Button) view.findViewById(R.id.create);
                            create.setTextColor(accent);
                            create.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (newAlert.isShowing()) {
                                        String eqe = aset.getText().toString();
                                        addItem(tev.getText().toString(), eqe + " = " + String.valueOf(new ExpressionBuilder(eqe).build().evaluate()));
                                        refresh();

                                        newAlert.dismiss();
                                    }
                                }
                            });

                            newAlert = new AlertDialog.Builder(v.getContext()).setView(view).create();
                            newAlert.show();
                        }
                    });

                    Button delete = (Button) v.findViewById(R.id.delete);
                    delete.setTextColor(accent);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeItem(holder.getAdapterPosition());
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return equations.size();
    }

    public void addItem(String title, String equation) {
        equations.add(0, equation);
        titles.add(0, title);
        notifyItemInserted(0);

        if (history) {
            refresh();
        }
    }

    public void removeItem(int position) {
        final String equation = equations.get(position);
        final String title = titles.get(position);

        equations.remove(position);
        titles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, equations.size());

        if (history) {
            refresh();
        }

        Snackbar.make(activity.findViewById(R.id.fab), "Equation Deleted", Snackbar.LENGTH_LONG).setActionTextColor(accent).setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(title, equation);
            }
        }).show();
    }

    private int headerColor(int color) {
        if (isColorDark(color)) {
            return context.getResources().getColor(R.color.header_light);
        } else {
            return context.getResources().getColor(R.color.header);
        }
    }

    public boolean isColorDark(int color){
        double darkness = 1-(0.299* Color.red(color) + 0.587*Color.green(color) + 0.114* Color.blue(color))/255;
        return darkness >= 0.5;
    }

    private boolean refresh() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();

        edit.putInt("equation_size", equations.size());

        for(int i = 0; i < equations.size();i++) {
            edit.remove("equation_" + String.valueOf(i));
            edit.putString("equation_" + String.valueOf(i), equations.get(i));

            edit.remove("title_" + String.valueOf(i));
            edit.putString("title_" + String.valueOf(i), titles.get(i));
        }

        return edit.commit();
    }
}


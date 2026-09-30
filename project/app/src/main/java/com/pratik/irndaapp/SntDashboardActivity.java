package com.pratik.irndaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;
import android.text.Editable;
import android.text.TextWatcher;
import java.util.*;

public class SntDashboardActivity extends Activity {

    LinearLayout list;
    EditText search;

    final String[][] MODULES = {
        {"SIGNALLING FUNDAMENTALS",
         "Fail-safe principles, interlocking, route locking, approach locking, section locking, signals, points, tracks and block working."},

        {"SIGNAL & POINT",
         "Signal control and proving, point operation and detection, correspondence checks and structured fault isolation."},

        {"RELAY & CIRCUITS",
         "HPR, DPR, ECR, WNR, WRR, NWKR, RWKR and associated relay logic. Verify all safety-critical wiring against the approved circuit."},

        {"RRI / PANEL",
         "Panel indications, route setting, relay-room correspondence, route/section locking and common failure analysis."},

        {"ELECTRONIC INTERLOCKING",
         "Vital and non-vital architecture, input/output interfaces, diagnostics, redundancy, communication and maintenance concepts."},

        {"KAVACH / TCAS",
         "Train protection architecture, onboard and trackside equipment, RFID, radio communication, signal and authority interfaces."},

        {"MSDAC",
         "Axle detection, evaluator, field units, reset, indications, relay interfaces, communication, diagnostics and failure isolation."},

        {"BPAC / HASSDAC",
         "Block proving, axle detection, UFSBI interfaces, reset, indications, communication and maintenance concepts."},

        {"UFSBI / BLOCK INSTRUMENTS",
         "Block section equipment, communication, indications, operation and structured troubleshooting."},

        {"MAINTAINER TOOLKIT",
         "Multimeter, insulation tester, crimping, ferruling, soldering, relay handling, cable identification and restoration checklists."},

        {"FAULT FINDER",
         "Symptom → observations → equipment/make/model → circuit/image → relay/contact/terminal tracing. Never guess safety-critical wiring."},

        {"MANUALS & SOURCES",
         "Railway, RDSO, CAMTECH and manufacturer references with source and page information."},

        {"AI ASSISTANT",
         "Online assistance for complex questions, document research and image/circuit-based troubleshooting."},

        {"JE / SSE MODE",
         "Functional testing, correspondence testing, failure analysis, preventive maintenance, MTBF/MTTR and documentation."}
    };

    int dp(float x) {
        return (int)(x * getResources().getDisplayMetrics().density + 0.5f);
    }

    GradientDrawable cardBackground() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.WHITE);
        g.setCornerRadius(dp(16));
        g.setStroke(dp(1), Color.rgb(225,230,236));
        return g;
    }

    TextView text(String s, float size, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(Color.rgb(35,45,58));
        t.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL);
        t.setPadding(dp(14), dp(7), dp(14), dp(7));
        return t;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16),dp(18),dp(16),dp(24));
        root.setBackgroundColor(Color.rgb(245,247,250));

        TextView title = text("IR S&T FIELD ASSISTANT",25,true);
        title.setTextColor(Color.rgb(15,55,95));
        root.addView(title);

        TextView subtitle = text(
            "Railway Signalling • Maintenance • Troubleshooting",
            14,false);
        subtitle.setTextColor(Color.rgb(85,95,108));
        root.addView(subtitle);

        search = new EditText(this);
        search.setHint("Search relay, equipment, fault or module…");
        search.setSingleLine(true);
        search.setPadding(dp(14),0,dp(14),0);

        GradientDrawable searchBg = new GradientDrawable();
        searchBg.setColor(Color.WHITE);
        searchBg.setCornerRadius(dp(14));
        search.setBackground(searchBg);

        LinearLayout.LayoutParams searchLp =
            new LinearLayout.LayoutParams(-1,dp(52));
        searchLp.setMargins(0,dp(14),0,dp(10));
        root.addView(search,searchLp);

        TextView source = text(
            "OFFLINE KNOWLEDGE  •  OFFICIAL SOURCES  •  ONLINE ASSISTANCE",
            11,true);
        source.setTextColor(Color.rgb(35,105,80));
        root.addView(source);

        ScrollView scroll = new ScrollView(this);
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(list);

        LinearLayout.LayoutParams scrollLp =
            new LinearLayout.LayoutParams(-1,0,1);
        root.addView(scroll,scrollLp);

        setContentView(root);
        render("");

        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int a,int c,int d){}
            public void onTextChanged(CharSequence s,int a,int b,int c){
                render(s.toString());
            }
            public void afterTextChanged(Editable e){}
        });
    }

    void render(String query) {
        list.removeAllViews();

        String q = query.toLowerCase(Locale.ROOT).trim();

        for (final String[] m : MODULES) {

            String all = (m[0] + " " + m[1]).toLowerCase(Locale.ROOT);

            if (!q.isEmpty() && !all.contains(q))
                continue;

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(6),dp(7),dp(6),dp(7));
            card.setBackground(cardBackground());

            TextView h = text(m[0],16,true);
            h.setTextColor(Color.rgb(18,72,115));

            TextView d = text(m[1],13,false);
            d.setTextColor(Color.rgb(75,85,98));

            card.addView(h);
            card.addView(d);

            LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(-1,-2);
            lp.setMargins(0,dp(6),0,dp(6));

            list.addView(card,lp);

            card.setOnClickListener(v -> showModule(m[0],m[1]));
        }
    }

    void showModule(String title,String description) {

        String warning =
            "\n\nFIELD SAFETY NOTE\n" +
            "This is a reference and decision-support tool. " +
            "For safety-critical work always follow the approved " +
            "station-specific circuit, Railway instructions, " +
            "RDSO/CAMTECH guidance and the equipment manufacturer's " +
            "approved manual. Do not act on an uncertain circuit.";

        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(description + warning)
            .setPositiveButton("OK",null)
            .show();
    }
}

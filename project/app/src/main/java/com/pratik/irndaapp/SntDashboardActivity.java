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

        {"RELAY & EQUIPMENT LIBRARY",
         "Searchable verified library of relays and signalling equipment: function, application, system, make/model, relay style, front/back contacts, coil voltage, coil resistance, pick-up/drop-away data, working values, socket/base, maintenance checks and official source/page reference."},

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

        {"STATION PROFILE",
         "Save station-specific PI/RRI/EI, relay family, equipment makes and indoor/outdoor configuration."},

        {"FAULT FINDER",
         "Profile-aware indoor and outdoor troubleshooting using station-specific system, relay and equipment information."},

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
        g.setCornerRadius(dp(18));
        g.setStroke(dp(1), Color.rgb(220,226,233));
        return g;
    }

    GradientDrawable headerBackground() {
        GradientDrawable g = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{Color.rgb(7,38,68),Color.rgb(18,91,126)});
        g.setCornerRadius(dp(22));
        return g;
    }

    int iconForModule(String title) {
        if (title.contains("SIGNAL") || title.contains("POINT"))
            return R.drawable.ic_signal_thumb;
        if (title.contains("RELAY") || title.contains("CIRCUIT"))
            return R.drawable.ic_relay_thumb;
        if (title.contains("KAVACH") || title.contains("MSDAC") || title.contains("BPAC"))
            return R.drawable.ic_equipment_thumb;
        return R.drawable.ic_tools_thumb;
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

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(18),dp(16),dp(18),dp(16));
        header.setBackground(headerBackground());

        ImageView headerIcon = new ImageView(this);
        headerIcon.setImageResource(R.drawable.ic_signal_thumb);
        headerIcon.setPadding(dp(4),dp(4),dp(4),dp(4));
        header.addView(headerIcon,new LinearLayout.LayoutParams(dp(72),dp(72)));

        LinearLayout headerText = new LinearLayout(this);
        headerText.setOrientation(LinearLayout.VERTICAL);

        TextView title = text("IR S&T FIELD ASSISTANT",23,true);
        title.setTextColor(Color.WHITE);
        title.setPadding(dp(8),0,0,dp(2));
        headerText.addView(title);

        TextView subtitle = text(
            "Railway Signalling • Maintenance • Troubleshooting",
            12,false);
        subtitle.setTextColor(Color.rgb(225,238,248));
        subtitle.setPadding(dp(8),0,0,0);
        headerText.addView(subtitle);

        header.addView(headerText,new LinearLayout.LayoutParams(0,-2,1));
        root.addView(header,new LinearLayout.LayoutParams(-1,-2));


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
        TextView section = text("S&T KNOWLEDGE & FIELD TOOLS",13,true);
        section.setTextColor(Color.rgb(15,72,105));
        section.setPadding(dp(4),dp(8),dp(4),dp(4));
        list.addView(section);
        scroll.addView(list);

        LinearLayout.LayoutParams scrollLp =
            new LinearLayout.LayoutParams(-1,0,1);
        root.addView(scroll,scrollLp);

        TextView footer = text(
            "DEVELOPED BY PRATIK MUKHERJEE (SIM/ASN/ER)",
            10,true);
        footer.setGravity(Gravity.CENTER);
        footer.setTextColor(Color.rgb(15,72,105));
        footer.setPadding(0,dp(10),0,0);
        root.addView(footer,new LinearLayout.LayoutParams(-1,-2));

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
            card.setPadding(dp(8),dp(9),dp(8),dp(9));
            card.setBackground(cardBackground()); card.setElevation(dp(4));

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);

            ImageView icon = new ImageView(this);
            icon.setImageResource(iconForModule(m[0]));
            icon.setPadding(dp(9),dp(9),dp(9),dp(9));

            GradientDrawable iconBg = new GradientDrawable();
            iconBg.setColor(Color.rgb(235,243,249));
            iconBg.setCornerRadius(dp(14));
            icon.setBackground(iconBg);

            row.addView(icon,new LinearLayout.LayoutParams(dp(66),dp(66)));

            LinearLayout words = new LinearLayout(this);
            words.setOrientation(LinearLayout.VERTICAL);

            TextView h = text(m[0],15,true);
            h.setTextColor(Color.rgb(18,72,115));

            TextView d = text(m[1],12,false);
            d.setTextColor(Color.rgb(75,85,98));

            words.addView(h);
            words.addView(d);
            row.addView(words,new LinearLayout.LayoutParams(0,-2,1));

            card.addView(row);

            LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(-1,-2);
            lp.setMargins(0,dp(7),0,dp(7));

            list.addView(card,lp);

            card.setOnClickListener(v -> showModule(m[0],m[1]));
        }
    }

    void showRelayLibrary() {
    final String[][] relays = {
        {"WKR1", "Point Detection Relay", "RRI", "Siemens", "Point detection / correspondence"},
        {"K-50", "Point Circuit / Control Relay", "RRI", "Siemens", "Point control circuit"},
        {"QN1", "Plug-in Signalling Relay", "TCAS / KAVACH", "Verify make/model", "Interface application"},
        {"QNA1", "Plug-in Signalling Relay", "TCAS / KAVACH", "Verify make/model", "Interface application"},
        {"NWKR", "Normal Detection Relay", "Point Detection", "Station-specific", "Normal position detection"},
        {"RWKR", "Reverse Detection Relay", "Point Detection", "Station-specific", "Reverse position detection"},
        {"WNR", "Relay Designation", "Relay Interlocking", "Station-specific", "Function must be verified from approved circuit"},
        {"WRR", "Relay Designation", "Relay Interlocking", "Station-specific", "Function must be verified from approved circuit"},
        {"HPR", "Relay Designation", "RRI / Relay Logic", "Station-specific", "Function must be verified from approved circuit"},
        {"DPR", "Relay Designation", "RRI / Relay Logic", "Station-specific", "Function must be verified from approved circuit"},
        {"ECR", "Relay Designation", "RRI / Relay Logic", "Station-specific", "Function must be verified from approved circuit"}
    };

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(20, 18, 20, 20);
    root.setBackgroundColor(Color.rgb(245, 247, 250));

    TextView title = new TextView(this);
    title.setText("RELAY & EQUIPMENT LIBRARY");
    title.setTextSize(22);
    title.setTextColor(Color.rgb(20, 45, 80));
    title.setTypeface(null, android.graphics.Typeface.BOLD);
    root.addView(title);

    TextView subtitle = new TextView(this);
    subtitle.setText("Search by designation, function, system or make/model.");
    subtitle.setTextSize(13);
    subtitle.setTextColor(Color.DKGRAY);
    subtitle.setPadding(0, 6, 0, 12);
    root.addView(subtitle);

    EditText search = new EditText(this);
    search.setHint("Search relay / function / system...");
    search.setSingleLine(true);
    root.addView(search);

    ScrollView scroll = new ScrollView(this);
    LinearLayout list = new LinearLayout(this);
    list.setOrientation(LinearLayout.VERTICAL);
    list.setPadding(0, 12, 0, 12);
    scroll.addView(list);
    root.addView(scroll, new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

    TextView footer = new TextView(this);
    footer.setText("DEVELOPED BY PRATIK MUKHERJEE (SIM/ASN/ER)");
    footer.setTextSize(12);
    footer.setTextColor(Color.rgb(20, 45, 80));
    footer.setGravity(Gravity.CENTER);
    footer.setPadding(0, 12, 0, 4);
    root.addView(footer);

    final Runnable[] render = new Runnable[1];

    render[0] = new Runnable() {
        public void run() {
            String q = search.getText().toString().trim().toLowerCase();
            list.removeAllViews();

            int count = 0;

            for (String[] r : relays) {
                String searchable =
                    r[0] + " " + r[1] + " " + r[2] + " " + r[3] + " " + r[4];

                if (q.length() > 0 &&
                    !searchable.toLowerCase().contains(q)) {
                    continue;
                }

                count++;

                TextView card = new TextView(SntDashboardActivity.this);
                card.setText(
                    r[0] + "  •  " + r[1] +
                    "\n" + r[2] + "  |  " + r[3] +
                    "\n" + r[4]
                );
                card.setTextSize(14);
                card.setTextColor(Color.rgb(35, 45, 58));
                card.setPadding(18, 16, 18, 16);
                card.setBackgroundColor(Color.WHITE);
                card.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 10);
                list.addView(card, lp);

                final String designation = r[0];
                final String function = r[1];
                final String system = r[2];
                final String make = r[3];
                final String application = r[4];

                card.setOnClickListener(v ->
                    showRelayDetail(
                        designation,
                        function,
                        system,
                        make,
                        application));
            }

            if (count == 0) {
                TextView empty = new TextView(SntDashboardActivity.this);
                empty.setText("No matching relay/equipment found.");
                empty.setTextSize(14);
                empty.setTextColor(Color.DKGRAY);
                empty.setPadding(12, 24, 12, 24);
                list.addView(empty);
            }
        }
    };

    search.addTextChangedListener(new android.text.TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            render[0].run();
        }
        public void afterTextChanged(android.text.Editable s) {}
    });

    render[0].run();
    Button forecastButton = new Button(this);
    forecastButton.setText("PREDICTIVE MAINTENANCE / AI FORECAST");
    forecastButton.setTextSize(13);
    forecastButton.setOnClickListener(v -> showPredictiveMaintenance());

    root.addView(forecastButton, new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));

    setContentView(root);
}

void showPredictiveMaintenance() {
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(20, 18, 20, 20);
    root.setBackgroundColor(Color.rgb(245, 247, 250));

    TextView title = new TextView(this);
    title.setText("PREDICTIVE MAINTENANCE / AI FORECAST");
    title.setTextSize(21);
    title.setTextColor(Color.rgb(20, 45, 80));
    title.setTypeface(null, android.graphics.Typeface.BOLD);
    root.addView(title);

    TextView intro = new TextView(this);
    intro.setText("Enter observed equipment and maintenance information for a structured risk assessment.");
    intro.setTextSize(13);
    intro.setTextColor(Color.DKGRAY);
    intro.setPadding(0, 8, 0, 14);
    root.addView(intro);

    ScrollView scroll = new ScrollView(this);
    LinearLayout form = new LinearLayout(this);
    form.setOrientation(LinearLayout.VERTICAL);
    form.setPadding(4, 4, 4, 12);
    scroll.addView(form);

    EditText equipment = new EditText(this);
    equipment.setHint("Equipment / relay / system");
    equipment.setSingleLine(true);
    form.addView(equipment);

    EditText faultCount = new EditText(this);
    faultCount.setHint("Fault occurrences in recent period");
    faultCount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
    faultCount.setSingleLine(true);
    form.addView(faultCount);

    EditText symptom = new EditText(this);
    symptom.setHint("Repeated / intermittent symptom");
    symptom.setSingleLine(false);
    form.addView(symptom);

    EditText measurement = new EditText(this);
    measurement.setHint("Observed measurement / test result");
    measurement.setSingleLine(false);
    form.addView(measurement);

    EditText maintenance = new EditText(this);
    maintenance.setHint("Last maintenance / inspection observation");
    maintenance.setSingleLine(false);
    form.addView(maintenance);

    EditText history = new EditText(this);
    history.setHint("Previous fault history / remarks");
    history.setSingleLine(false);
    form.addView(history);

    Button analyse = new Button(this);
    analyse.setText("ANALYSE FAILURE RISK");
    form.addView(analyse);

    TextView result = new TextView(this);
    result.setTextSize(14);
    result.setTextColor(Color.rgb(35, 45, 58));
    result.setPadding(12, 18, 12, 18);
    form.addView(result);

    analyse.setOnClickListener(v -> {
        String eq = equipment.getText().toString().trim();
        String fc = faultCount.getText().toString().trim();
        String sym = symptom.getText().toString().trim();
        String meas = measurement.getText().toString().trim();
        String maint = maintenance.getText().toString().trim();
        String hist = history.getText().toString().trim();

        if (eq.length() == 0) {
            result.setText("Enter the equipment / relay / system name first.");
            return;
        }

        int faults = 0;
        try {
            if (fc.length() > 0) {
                faults = Integer.parseInt(fc);
            }
        } catch (Exception ignored) {
            faults = 0;
        }

        StringBuilder out = new StringBuilder();

        out.append("FAILURE FORECAST\n\n");
        out.append("Equipment: ").append(eq).append("\n\n");

        if (faults >= 3) {
            out.append("Risk indicator: RECURRENT FAULT PATTERN DETECTED\n");
        } else if (faults > 0) {
            out.append("Risk indicator: FAULT HISTORY PRESENT\n");
        } else {
            out.append("Risk indicator: INSUFFICIENT FAULT HISTORY\n");
        }

        if (sym.length() > 0) {
            out.append("\nObserved symptom:\n");
            out.append(sym).append("\n");
        }

        if (meas.length() > 0) {
            out.append("\nMeasurement evidence:\n");
            out.append(meas).append("\n");
            out.append("Compare the observed value with the applicable approved specification.\n");
        }

        out.append("\nRECOMMENDED CHECKING PATH\n");
        out.append("1. Confirm exact station and system configuration.\n");
        out.append("2. Check present indication, alarm and event history.\n");
        out.append("3. Compare with previous fault records.\n");
        out.append("4. Check the approved indoor circuit or interface.\n");
        out.append("5. Check location-box and field interface where applicable.\n");
        out.append("6. Verify outdoor equipment and correspondence.\n");
        out.append("7. Record measured values against the approved specification.\n");
        out.append("8. Perform only authorised corrective maintenance.\n");

        if (maint.length() > 0 || hist.length() > 0) {
            out.append("\nHISTORICAL INFORMATION CAPTURED\n");
            out.append("Maintenance observations and previous fault history have been recorded for future trend analysis.\n");
        }

        out.append("\nCORRECTIVE / PREVENTIVE ACTION\n");
        out.append("Verify the actual failure evidence before replacing a component. ");
        out.append("For recurring faults, record the event, measured values, equipment state and corrective action ");
        out.append("so future trend analysis can identify developing patterns.\n");

        out.append("\nAI FORECAST STATUS\n");
        out.append("This version provides rule-based decision support. ");
        out.append("A future AI model can analyse accumulated station history and measurement trends. ");
        out.append("No exact future failure date is predicted without sufficient verified historical data.\n");

        out.append("\nSAFETY\n");
        out.append("Do not bypass, bridge, short, force or defeat any vital circuit or safety function. ");
        out.append("Follow the approved station circuit, control table, Railway instructions, ");
        out.append("RDSO/CAMTECH guidance and manufacturer documentation.");

        result.setText(out.toString());
    });

    root.addView(scroll, new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

    TextView footer = new TextView(this);
    footer.setText("DEVELOPED BY PRATIK MUKHERJEE (SIM/ASN/ER)");
    footer.setTextSize(12);
    footer.setTextColor(Color.rgb(20, 45, 80));
    footer.setGravity(Gravity.CENTER);
    footer.setPadding(0, 10, 0, 4);
    root.addView(footer);

    setContentView(root);
}

void showRelayDetail(
        String designation,
        String function,
        String system,
        String make,
        String application) {

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(20, 18, 20, 20);
    root.setBackgroundColor(Color.rgb(245, 247, 250));

    TextView title = new TextView(this);
    title.setText(designation);
    title.setTextSize(24);
    title.setTextColor(Color.rgb(20, 45, 80));
    title.setTypeface(null, android.graphics.Typeface.BOLD);
    root.addView(title);

    TextView summary = new TextView(this);
    summary.setText(
        function +
        "\n\nSystem: " + system +
        "\nMake / Model: " + make +
        "\nApplication: " + application
    );
    summary.setTextSize(15);
    summary.setTextColor(Color.rgb(35, 45, 58));
    summary.setPadding(8, 14, 8, 18);
    root.addView(summary);

    ScrollView scroll = new ScrollView(this);
    LinearLayout details = new LinearLayout(this);
    details.setOrientation(LinearLayout.VERTICAL);
    details.setPadding(8, 4, 8, 20);
    scroll.addView(details);

    addRelayField(details, "Designation / Relay Type", designation);
    addRelayField(details, "Function / Application", function);
    addRelayField(details, "System / Interlocking Type", system);
    addRelayField(details, "Make / Model / Version", make);
    addRelayField(details, "Relay Style / Construction", "To be verified");
    addRelayField(details, "Front Contacts", "To be verified");
    addRelayField(details, "Back Contacts", "To be verified");
    addRelayField(details, "Coil Voltage", "To be verified");
    addRelayField(details, "Coil Resistance", "To be verified");
    addRelayField(details, "Pick-up Voltage / Current", "To be verified");
    addRelayField(details, "Drop-away Voltage / Current", "To be verified");
    addRelayField(details, "Normal Working Voltage / Current", "To be verified");
    addRelayField(details, "Contact Rating", "To be verified");
    addRelayField(details, "Socket / Base / Coding", "To be verified");
    addRelayField(details, "Dimensions / Physical Identification", "To be verified");
    addRelayField(details, "Maintenance / Testing Notes", "To be verified");
    addRelayField(details, "Official Source / Document / Page", "To be added after verification");
    addRelayField(details, "Verification Status", "Pending official-source verification");

    TextView safety = new TextView(this);
    safety.setText(
        "SAFETY NOTE\n\n" +
        "Relay designation and function can be station/circuit specific. " +
        "Do not infer a vital function from the relay abbreviation alone. " +
        "Approved station circuit, control table, Railway instructions, " +
        "RDSO/CAMTECH guidance and manufacturer documentation take precedence."
    );
    safety.setTextSize(13);
    safety.setTextColor(Color.rgb(110, 50, 20));
    safety.setPadding(12, 18, 12, 18);
    details.addView(safety);

    root.addView(scroll, new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

    TextView footer = new TextView(this);
    footer.setText("DEVELOPED BY PRATIK MUKHERJEE (SIM/ASN/ER)");
    footer.setTextSize(12);
    footer.setTextColor(Color.rgb(20, 45, 80));
    footer.setGravity(Gravity.CENTER);
    footer.setPadding(0, 10, 0, 4);
    root.addView(footer);

    setContentView(root);
}

void addRelayField(LinearLayout parent, String label, String value) {
    TextView field = new TextView(this);
    field.setText(label + "\n" + value);
    field.setTextSize(14);
    field.setTextColor(Color.rgb(35, 45, 58));
    field.setPadding(14, 12, 14, 12);
    field.setBackgroundColor(Color.WHITE);

    LinearLayout.LayoutParams lp =
        new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    lp.setMargins(0, 0, 0, 6);
    parent.addView(field, lp);
}

void showStationProfile() {
        final String[] keys = {"station","interlocking","ei_make","relay","relay_nomenclature","rack","panel","point_machine","signal","detection","block","kavach","power","outdoor","other"};
        final String[] labels = {"Station Name / Code","Interlocking: PI / RRI / EI","Interlocking / EI Make, Model, Version","Relay Family / Siemens K-series","Station Relay Nomenclature / Designation","Relay Room Rack / Shelf Details","Panel / VDU Make and Type","Point Machine Make / Type","Signal / Lamp / LED Type","Track Detection / MSDAC / Track Circuit","BPAC / HASSDAC / UFSBI / Block System","KAVACH / TCAS Make and Version","Signalling Power Supply Arrangement","Outdoor Equipment / Location Box Details","Other Indoor / Outdoor Equipment"};
        final android.content.SharedPreferences sp = getSharedPreferences("station_profile", MODE_PRIVATE);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL); box.setBackgroundColor(Color.rgb(245,247,250));
        box.setPadding(dp(12),dp(4),dp(12),dp(4));
        final EditText[] fields = new EditText[keys.length];
        for(int i=0;i<keys.length;i++) {
            fields[i]=new EditText(this);
            fields[i].setHint(labels[i]); fields[i].setTextSize(14); fields[i].setTextColor(Color.rgb(35,45,58));
            fields[i].setSingleLine(false);
            fields[i].setText(sp.getString(keys[i],""));
            box.addView(fields[i],new LinearLayout.LayoutParams(-1,dp(52)));
        }
        ScrollView sv=new ScrollView(this);
        sv.addView(box);
        new AlertDialog.Builder(this)
            .setTitle("STATION PROFILE • CONFIGURATION")
            .setMessage("Enter the actual station configuration. Fault Finder will use this profile before diagnosis.")
            .setView(sv)
            .setNegativeButton("CANCEL",null)
            .setPositiveButton("SAVE",(d,w)->{
                android.content.SharedPreferences.Editor e=sp.edit();
                for(int i=0;i<keys.length;i++) e.putString(keys[i],fields[i].getText().toString().trim());
                e.apply();
                Toast.makeText(this,"Station profile saved.",Toast.LENGTH_SHORT).show();
            }).show();
    }

    String stationProfileSummary() {
        android.content.SharedPreferences sp=getSharedPreferences("station_profile",MODE_PRIVATE);
        String st=sp.getString("station",""), in=sp.getString("interlocking",""), ei=sp.getString("ei_make",""), re=sp.getString("relay",""), rn=sp.getString("relay_nomenclature",""), ra=sp.getString("rack",""), pa=sp.getString("panel",""), pm=sp.getString("point_machine",""), si=sp.getString("signal",""), de=sp.getString("detection",""), bl=sp.getString("block",""), ka=sp.getString("kavach",""), po=sp.getString("power",""), ou=sp.getString("outdoor",""), ot=sp.getString("other","");
        if(st.isEmpty() && in.isEmpty() && re.isEmpty()) return "STATION PROFILE: Not configured. Complete the profile before installation-specific diagnosis.\n\n";
        return "ACTIVE STATION PROFILE\nStation: "+st+"\nInterlocking: "+in+"\nEI/Interlocking Make: "+ei+"\nRelay Family: "+re+"\nRelay Nomenclature: "+rn+"\nRack/Shelf: "+ra+"\nPanel/VDU: "+pa+"\nPoint Machine: "+pm+"\nSignal Type: "+si+"\nDetection: "+de+"\nBlock System: "+bl+"\nKAVACH: "+ka+"\nPower: "+po+"\nOutdoor: "+ou+"\nOther: "+ot+"\n\n";
    }

    void showStationAwareFaultFinder() {
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(20, 18, 20, 20);
    root.setBackgroundColor(Color.rgb(245, 247, 250));

    TextView title = new TextView(this);
    title.setText("STATION-AWARE FAULT FINDER");
    title.setTextSize(21);
    title.setTextColor(Color.rgb(20, 45, 80));
    title.setTypeface(null, android.graphics.Typeface.BOLD);
    root.addView(title);

    TextView info = new TextView(this);
    info.setText("First identify the station configuration. Diagnosis will then use the selected system context.");
    info.setTextSize(13);
    info.setTextColor(Color.DKGRAY);
    info.setPadding(0, 7, 0, 12);
    root.addView(info);

    ScrollView scroll = new ScrollView(this);
    LinearLayout form = new LinearLayout(this);
    form.setOrientation(LinearLayout.VERTICAL);
    form.setPadding(4, 4, 4, 16);
    scroll.addView(form);

    TextView stationLabel = new TextView(this);
    stationLabel.setText("STATION / LOCATION");
    stationLabel.setTextSize(13);
    stationLabel.setTextColor(Color.rgb(20, 45, 80));
    form.addView(stationLabel);

    EditText station = new EditText(this);
    station.setHint("Station name / code");
    station.setSingleLine(true);
    form.addView(station);

    TextView interlockingLabel = new TextView(this);
    interlockingLabel.setText("INTERLOCKING TYPE");
    interlockingLabel.setTextSize(13);
    interlockingLabel.setTextColor(Color.rgb(20, 45, 80));
    form.addView(interlockingLabel);

    Spinner interlocking = new Spinner(this);
    String[] interlockingOptions = {
        "PI - Panel Interlocking",
        "RRI - Route Relay Interlocking",
        "EI - Electronic Interlocking",
        "Not Known / Need Verification"
    };
    interlocking.setAdapter(new ArrayAdapter<String>(
        this,
        android.R.layout.simple_spinner_dropdown_item,
        interlockingOptions
    ));
    form.addView(interlocking);

    TextView relayLabel = new TextView(this);
    relayLabel.setText("RELAY / INTERFACE FAMILY");
    relayLabel.setTextSize(13);
    relayLabel.setTextColor(Color.rgb(20, 45, 80));
    form.addView(relayLabel);

    Spinner relay = new Spinner(this);
    String[] relayOptions = {
        "Siemens K-Series / K50",
        "WKR / NWKR / RWKR",
        "QN / QNA",
        "Other relay family",
        "No relay information available"
    };
    relay.setAdapter(new ArrayAdapter<String>(
        this,
        android.R.layout.simple_spinner_dropdown_item,
        relayOptions
    ));
    form.addView(relay);

    TextView areaLabel = new TextView(this);
    areaLabel.setText("FAULT AREA");
    areaLabel.setTextSize(13);
    areaLabel.setTextColor(Color.rgb(20, 45, 80));
    form.addView(areaLabel);

    Spinner area = new Spinner(this);
    String[] areaOptions = {
        "Indoor / Relay Room",
        "EI / Equipment Room",
        "Outdoor / Location Box / Field",
        "Both Indoor and Outdoor",
        "Not Known"
    };
    area.setAdapter(new ArrayAdapter<String>(
        this,
        android.R.layout.simple_spinner_dropdown_item,
        areaOptions
    ));
    form.addView(area);

    TextView faultLabel = new TextView(this);
    faultLabel.setText("FAULT TYPE");
    faultLabel.setTextSize(13);
    faultLabel.setTextColor(Color.rgb(20, 45, 80));
    form.addView(faultLabel);

    Spinner fault = new Spinner(this);
    String[] faultOptions = {
        "Signal not clearing",
        "Point not moving / detection fault",
        "Track circuit / axle counter fault",
        "EI / interlocking fault",
        "BPAC / HASSDAC / UFSBI fault",
        "KAVACH / TCAS fault",
        "Other S&T fault"
    };
    fault.setAdapter(new ArrayAdapter<String>(
        this,
        android.R.layout.simple_spinner_dropdown_item,
        faultOptions
    ));
    form.addView(fault);

    EditText symptom = new EditText(this);
    symptom.setHint("Exact indication / symptom / alarm / relay state");
    symptom.setSingleLine(false);
    form.addView(symptom);

    EditText history = new EditText(this);
    history.setHint("Previous occurrence / frequency / maintenance history");
    history.setSingleLine(false);
    form.addView(history);

    Button analyse = new Button(this);
    analyse.setText("START STATION-AWARE DIAGNOSIS");
    form.addView(analyse);

    TextView result = new TextView(this);
    result.setTextSize(14);
    result.setTextColor(Color.rgb(35, 45, 58));
    result.setPadding(12, 18, 12, 18);
    form.addView(result);

    analyse.setOnClickListener(v -> {
        String stn = station.getText().toString().trim();
        String il = interlocking.getSelectedItem().toString();
        String rl = relay.getSelectedItem().toString();
        String ar = area.getSelectedItem().toString();
        String ft = fault.getSelectedItem().toString();
        String sy = symptom.getText().toString().trim();
        String hi = history.getText().toString().trim();

        if (stn.length() == 0) {
            result.setText("Enter the station name / code first.");
            return;
        }

        StringBuilder r = new StringBuilder();

        r.append("STATION-AWARE FAULT ANALYSIS\n\n");
        r.append("Station: ").append(stn).append("\n");
        r.append("Interlocking: ").append(il).append("\n");
        r.append("Relay / Interface: ").append(rl).append("\n");
        r.append("Fault Area: ").append(ar).append("\n");
        r.append("Fault Type: ").append(ft).append("\n");

        if (sy.length() > 0) {
            r.append("\nObserved indication:\n").append(sy).append("\n");
        }

        if (hi.length() > 0) {
            r.append("\nPrevious history:\n").append(hi).append("\n");
        }

        r.append("\nDIAGNOSTIC PATH\n");

        if (ft.startsWith("Signal")) {
            r.append("1. Confirm signal indication and route status.\n");
            r.append("2. Check route conditions and route locking.\n");
            r.append("3. Check relevant track detection.\n");
            r.append("4. Check point detection / correspondence.\n");
            r.append("5. Trace approved signal control logic.\n");
            r.append("6. Check relay / EI output and corresponding field interface.\n");
            r.append("7. Compare relay-room indication with outdoor equipment.\n");
        } else if (ft.startsWith("Point")) {
            r.append("1. Confirm commanded position.\n");
            r.append("2. Verify interlocking permission.\n");
            r.append("3. Check point control output.\n");
            r.append("4. Trace approved control circuit to location box.\n");
            r.append("5. Check point machine supply and operation.\n");
            r.append("6. Check detection and correspondence.\n");
            r.append("7. Compare indoor indication with actual field position.\n");
        } else if (ft.startsWith("Track")) {
            r.append("1. Confirm affected section indication.\n");
            r.append("2. Identify track circuit / axle counter equipment.\n");
            r.append("3. Check evaluator / interface status.\n");
            r.append("4. Check communication and event diagnostics.\n");
            r.append("5. Verify field detection and correspondence.\n");
            r.append("6. Follow only authorised reset procedure where applicable.\n");
        } else if (ft.startsWith("EI")) {
            r.append("1. Confirm EI make, model and version.\n");
            r.append("2. Check diagnostic / alarm information.\n");
            r.append("3. Identify affected function or I/O.\n");
            r.append("4. Check approved interface and power/status indications.\n");
            r.append("5. Check communication / event logs.\n");
            r.append("6. Verify corresponding field equipment.\n");
        } else if (ft.startsWith("BPAC")) {
            r.append("1. Check both-end indications.\n");
            r.append("2. Check block interface status.\n");
            r.append("3. Check communication / equipment health.\n");
            r.append("4. Check approved relay/interface path.\n");
            r.append("5. Review event history.\n");
            r.append("6. Follow authorised block reset / restoration procedure.\n");
        } else if (ft.startsWith("KAVACH")) {
            r.append("1. Confirm onboard / trackside context.\n");
            r.append("2. Check equipment health and diagnostic indication.\n");
            r.append("3. Check communication status.\n");
            r.append("4. Check location / RFID related indication where applicable.\n");
            r.append("5. Check signalling interface and event records.\n");
            r.append("6. Verify trackside correspondence.\n");
        } else {
            r.append("1. Identify the exact failed function.\n");
            r.append("2. Confirm the station-specific approved circuit/interface.\n");
            r.append("3. Trace indoor-to-outdoor correspondence.\n");
            r.append("4. Record measured values and equipment status.\n");
        }

        r.append("\nCONFIGURATION-AWARE CHECK\n");
        if (il.contains("RRI") || il.contains("PI")) {
            r.append("Relay/interlocking circuit tracing should use the approved station drawing.\n");
        } else if (il.startsWith("EI")) {
            r.append("Prioritise approved EI diagnostic information, I/O status and event logs.\n");
        } else {
            r.append("Interlocking type must be confirmed before making a system-specific conclusion.\n");
        }

        if (rl.contains("K-Series") || rl.contains("WKR")) {
            r.append("Relay family information is available; exact relay designation and circuit function must still be verified from the approved drawing.\n");
        } else if (rl.contains("No relay")) {
            r.append("Relay information is unavailable; do not assume a relay type or function.\n");
        }

        r.append("\nFAILURE FORECAST\n");
        if (hi.length() > 0 || sy.length() > 0) {
            r.append("A recurring/intermittent pattern can be assessed from the recorded history and symptoms.\n");
            r.append("Record every occurrence, measured value and corrective action for future trend analysis.\n");
        } else {
            r.append("Insufficient historical evidence for a meaningful failure forecast.\n");
        }

        r.append("\nCORRECTIVE MEASURE\n");
        r.append("Verify the actual cause before replacing equipment. ");
        r.append("Use approved testing and maintenance procedures and document the result.\n");

        r.append("\nSAFETY\n");
        r.append("Never bypass, bridge, short, force or defeat an interlocking or safety function. ");
        r.append("Use the approved station circuit, control table, Railway instructions, ");
        r.append("RDSO/CAMTECH guidance and manufacturer documentation.");

        result.setText(r.toString());
    });

    root.addView(scroll, new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

    TextView footer = new TextView(this);
    footer.setText("DEVELOPED BY PRATIK MUKHERJEE (SIM/ASN/ER)");
    footer.setTextSize(12);
    footer.setTextColor(Color.rgb(20, 45, 80));
    footer.setGravity(Gravity.CENTER);
    footer.setPadding(0, 10, 0, 4);
    root.addView(footer);

    setContentView(root);
}

void startFaultDiagnosis() {
        android.content.SharedPreferences sp=getSharedPreferences("station_profile",MODE_PRIVATE);
        String station=sp.getString("station","");
        String interlocking=sp.getString("interlocking","");
        String relay=sp.getString("relay","");
        if(station.isEmpty() || interlocking.isEmpty()) {
            new AlertDialog.Builder(this)
                .setTitle("STATION PROFILE REQUIRED")
                .setMessage("First enter Station Name/Code and Interlocking Type (PI/RRI/EI) in STATION PROFILE.")
                .setNegativeButton("CLOSE",null)
                .setPositiveButton("OPEN PROFILE",(d,w)->showStationProfile()).show();
            return;
        }
        final String[] faults={
            "Signal not clearing",
            "Point not moving / detection fault",
            "Track circuit / axle counter fault",
            "EI / interlocking fault",
            "BPAC / HASSDAC / UFSBI fault",
            "KAVACH / TCAS fault",
            "Other S&T fault"
        };
        new AlertDialog.Builder(this)
            .setTitle("STEP 1 - SELECT FAULT")
            .setItems(faults,(d,which)->askFaultLocation(faults[which],station,interlocking,relay))
            .setNegativeButton("CANCEL",null).show();
    }

    void askFaultLocation(String fault,String station,String interlocking,String relay) {
        final String[] locations={
            "Indoor / Relay Room / EI Room",
            "Outdoor / Location Box / Field",
            "Both indoor and outdoor",
            "Not sure yet"
        };
        new AlertDialog.Builder(this)
            .setTitle("STEP 2 - WHERE IS THE SYMPTOM?")
            .setMessage("Station: "+station+"\nSystem: "+interlocking+"\nRelay profile: "+relay)
            .setItems(locations,(d,which)->askFaultIndication(fault,locations[which],station,interlocking,relay))
            .setNegativeButton("BACK",(d,w)->startFaultDiagnosis()).show();
    }

    void askFaultIndication(String fault,String location,String station,String interlocking,String relay) {
        final EditText input=new EditText(this);
        input.setHint("Example: S1 not clearing / point 101 stuck Normal / section occupied");
        input.setSingleLine(false);
        input.setMinLines(3);
        new AlertDialog.Builder(this)
            .setTitle("STEP 3 - EXACT SYMPTOM / INDICATION")
            .setMessage("Fault: "+fault+"\nLocation: "+location+"\n\nEnter the exact panel/VDU, relay-room or outdoor indication.")
            .setView(input)
            .setNegativeButton("BACK",(d,w)->askFaultLocation(fault,station,interlocking,relay))
            .setPositiveButton("ANALYSE",(d,w)->showDiagnosisResult(fault,location,input.getText().toString().trim(),station,interlocking,relay))
            .show();
    }

    void showDiagnosisResult(String fault,String location,String symptom,String station,String interlocking,String relay) {
        String path;
        if(fault.startsWith("Signal")) {
            path="SIGNAL PATH\nPanel/VDU -> route conditions -> track detection -> point detection -> signal control -> relay/EI output -> relay room -> location box -> outdoor signal -> field correspondence.";
        } else if(fault.startsWith("Point")) {
            path="POINT PATH\nPanel command -> interlocking permission -> control relay/output -> approved circuit -> location box -> point machine -> control/motor supply -> movement -> detection -> NWKR/RWKR or equivalent -> panel correspondence.";
        } else if(fault.startsWith("Track")) {
            path="DETECTION PATH\nPanel section status -> detection point/field unit -> evaluator -> communication -> relay/interface -> event log -> field correspondence -> authorised reset conditions.";
        } else if(fault.startsWith("EI")) {
            path="EI PATH\nEI make/model/version -> diagnostic/alarm -> affected function -> power/status -> approved I/O/interface -> communication -> field equipment -> event log.";
        } else if(fault.startsWith("BPAC")) {
            path="BLOCK PATH\nBoth-end indications -> block interface -> communication -> equipment health -> relay/interface -> event log -> authorised block procedure -> field correspondence.";
        } else if(fault.startsWith("KAVACH")) {
            path="KAVACH PATH\nLoco ID/version -> onboard status -> radio -> RFID/location -> signalling interface -> diagnostic log -> trackside correspondence.";
        } else {
            path="GENERAL S&T PATH\nSymptom -> applicable system -> approved circuit/interface -> indoor equipment -> location box/interface -> outdoor equipment -> field correspondence -> first abnormal condition.";
        }
        String relayNote=relay.isEmpty()
            ? "\nRelay family is not recorded. Do not assume relay type or designation."
            : "\nRecorded relay profile: "+relay+". Exact relay/contact function must still be verified from the approved station circuit.";
        String result=
            "DIAGNOSTIC SUMMARY\n\n"+
            "Station: "+station+"\n"+
            "System: "+interlocking+"\n"+
            "Fault: "+fault+"\n"+
            "Location: "+location+"\n"+
            "Reported indication: "+(symptom.isEmpty()?"Not entered":symptom)+"\n\n"+
            path+relayNote+
            "\n\nNEXT CHECK\nFind the first condition in this chain that does not correspond with the approved circuit/control table or actual field equipment. Record the observation before changing anything."+
            "\n\nIF UNCERTAIN\nUpload the relevant approved circuit/control table, panel/VDU image, relay-room indication, location-box/field circuit or equipment diagnostic screen."+
            "\n\nSAFETY\nNever bypass, bridge, force, short or defeat an interlocking/safety function. Follow authorised Railway and manufacturer procedures.";
        showTechnicalModule("FAULT DIAGNOSIS",result);
    }

    void showModule(String title,String description) {

        if ("STATION PROFILE".equals(title)) {
            showStationProfile();
            return;
        }

        if ("FAULT FINDER".equals(title)) {
            showStationAwareFaultFinder();
            return;


        }

        if ("RELAY & EQUIPMENT LIBRARY".equals(title)) {
            showRelayLibrary();
            return;
        }
        if ("ELECTRONIC INTERLOCKING".equals(title)) {
            showTechnicalModule("ELECTRONIC INTERLOCKING",
                "ELECTRONIC INTERLOCKING — FIELD GUIDE\n\n" +
                "1. BASIC ARCHITECTURE\nEI uses application logic to establish interlocking conditions and interfaces with field equipment. A typical architecture contains vital logic, vital inputs/outputs, non-vital indications/alarms and diagnostic facilities. Exact architecture depends on the approved manufacturer and system version.\n\n" +
                "2. VITAL FUNCTIONS\nVital functions include safety-critical proving and control such as signal and point control. Safety-related application logic must be treated according to the approved EI design and application data.\n\n" +
                "3. NON-VITAL FUNCTIONS\nLocal indications, alarms, diagnostic information and certain external interfaces may be non-vital. Never assume that an indication is vital or non-vital without checking the approved system documentation.\n\n" +
                "4. FIELD INTERFACES\nTypical interfaces may include signal outputs, point-control interfaces, track-related inputs, detection inputs, level-crossing interfaces and communication with other signalling equipment. The actual interface arrangement is manufacturer/model specific.\n\n" +
                "5. DIAGNOSTICS\nWhen an EI failure occurs, first record the exact alarm, diagnostic code, affected equipment/function and time. Do not reset repeatedly without recording the evidence.\n\n" +
                "6. FAULT FINDER\nEI function failed → record diagnostic/alarm → identify affected signal/point/track → check corresponding field indication → check approved input/output/interface → check communication/power condition → compare redundant/system status where applicable → identify the first abnormal condition.\n\n" +
                "7. TESTING\nFunctional testing verifies application logic and correspondence with actual connected equipment. FAT and SAT are part of the approved EI testing process. Testing must follow the authorised test procedure and approved control/selection table.\n\n" +
                "8. WHAT SHOULD I UPLOAD?\nFor detailed diagnosis upload the EI make/model/version, exact diagnostic screen or alarm, relevant approved interface/application document, control table and—where applicable—the field circuit or equipment drawing.\n\n" +
                "SAFETY\nNever bypass a vital function, force an output or repeatedly reset an EI to clear a fault without following the authorised procedure. The approved EI manual, application data, Railway instructions and manufacturer documentation take precedence over this reference guide.");
            return;
        }

        if ("RRI / PANEL".equals(title)) {
            showTechnicalModule("RRI / PANEL",
                "RRI / PANEL FIELD GUIDE\n\n" +
                "PANEL → RELAY ROOM → FIELD CORRESPONDENCE\n\n" +
                "1. PANEL INDICATION\nRecord the exact signal, point, track, route or crank-handle indication before troubleshooting.\n\n" +
                "2. ROUTE SETTING\nRequired conditions, point positions, detection and locking must be proved before the relevant signal can clear. Exact logic is installation-specific.\n\n" +
                "3. ROUTE LOCKING\nOnce established, the route remains protected until the authorised release conditions are satisfied.\n\n" +
                "4. APPROACH LOCKING\nApproach locking protects against unsafe route alteration after the relevant approach condition is established. Exact release logic must be checked from the approved circuit.\n\n" +
                "5. FAULT FINDER — SIGNAL NOT CLEARING\nCheck: track indications → point positions → point detection → conflicting route/signal conditions → route locking → approach locking → relay-room correspondence → first missing condition in the approved circuit.\n\n" +
                "6. WHAT CIRCUIT SHOULD I UPLOAD?\nFor detailed diagnosis upload the approved Control Table/Route Chart, Signal Control Circuit, Point Control & Detection Circuit and Route/Approach Locking Circuit. A relay-room shelf/rack drawing may also be required.\n\n" +
                "SAFETY\nNever bypass or bridge an interlocking/safety circuit to restore an indication. Follow the approved station circuit, control table, SEM/Railway instructions and authorised maintenance procedure.");
            return;
        }

        if ("SIGNAL & POINT".equals(title)) {
            showTechnicalModule(
                "SIGNAL & POINT",
                "POINT OPERATION & DETECTION\n\n" +
                "1. OPERATOR COMMAND\n" +
                "Normal/Reverse command is initiated from the approved panel/VDU/control system.\n\n" +
                "2. INTERLOCKING CHECK\n" +
                "The interlocking must permit the point operation. Existing route/track/crank-handle/other locking conditions are proved according to the approved circuit.\n\n" +
                "3. CONTROL CIRCUIT\n" +
                "The appropriate point control output operates the point-control/contactor circuit.\n\n" +
                "4. POINT MACHINE\n" +
                "The point machine moves the switch rails towards the commanded position.\n\n" +
                "5. DETECTION\n" +
                "After the point reaches the required position, the corresponding detection is obtained.\n\n" +
                "6. RELAY-ROOM PROVING\n" +
                "Typical relay-based schemes use NWKR for Normal detection and RWKR for Reverse detection. Exact circuit implementation must be checked against the station's approved drawing.\n\n" +
                "7. CORRESPONDENCE\n" +
                "The indication should correspond to the actual point position. A mismatch or flashing/out-of-correspondence indication must be treated as a fault condition until properly verified.\n\n" +
                "FAULT FINDER — POINT NOT GOING REVERSE\n\n" +
                "Panel/VDU → command indication → control relay/output → location-box/control supply → point machine operation → detection at site → RWKR indication → correspondence/proving.\n\n" +
                "CHECK IN THIS ORDER\n" +
                "• What exactly does the panel show?\n" +
                "• Is the point currently Normal or Reverse?\n" +
                "• Is the point free from route/track/crank-handle locking?\n" +
                "• Is the Reverse control command reaching the approved control circuit?\n" +
                "• Is the location-box supply healthy?\n" +
                "• Does the machine actually operate?\n" +
                "• Is Reverse detection obtained at site?\n" +
                "• Is RWKR picking up in the relay room?\n" +
                "• Does the indication correspond with the physical point position?\n\n" +
                "IMPORTANT: Never bypass, bridge or alter a safety circuit merely to restore indication. Follow the approved station circuit, Railway instructions and authorised disconnection/reconnection procedure."
            );
            return;
        }

        if ("RELAY & CIRCUITS".equals(title)) {
            showTechnicalModule(
                "RELAY & CIRCUITS",
                "RELAY BASICS\n\n" +
                "PICKED / ENERGISED = relay coil is energised and its contacts are in the corresponding picked condition.\n\n" +
                "DROPPED / DE-ENERGISED = relay coil is not energised and its contacts are in the corresponding dropped condition.\n\n" +
                "FRONT CONTACT = contact which is closed in the relay's picked condition.\n\n" +
                "BACK CONTACT = contact which is closed in the relay's dropped condition.\n\n" +
                "COMMON FIELD METHOD\n" +
                "Do not start by randomly checking relays. Start from the observed symptom and trace the approved circuit from source → controlling condition → relay coil → contact → next stage.\n\n" +
                "POINT-RELATED RELAYS\n\n" +
                "WLR — Point electrically-locking function in typical relay-interlocking schemes. Its exact energisation/drop sequence is circuit dependent.\n\n" +
                "NWKR — Normal point indication relay in typical schemes. It proves Normal detection when the relevant conditions are satisfied.\n\n" +
                "RWKR — Reverse point indication relay in typical schemes. It proves Reverse detection when the relevant conditions are satisfied.\n\n" +
                "WNKR / WRKR — Point-location detection relays used in typical relay schemes to convey Normal/Reverse detection from the point location.\n\n" +
                "WNR / WRR — Point-control relay designations found in relay-interlocking schemes; exact function and circuit position must be verified from the approved circuit for that installation.\n\n" +
                "SIGNAL / ROUTE RELAY LOGIC\n\n" +
                "A signal is not cleared merely because the signal command exists. Route conditions, point position, track conditions, locking and other interlocking conditions are proved according to the approved control table/circuit.\n\n" +
                "HPR / DPR / ECR\n\n" +
                "These abbreviations must NOT be assigned a universal function from the letters alone. Different signalling schemes/manufacturer drawings can use relay designations differently. The app will therefore require the approved circuit/drawing before giving a safety-critical interpretation.\n\n" +
                "RELAY FAULT TRACING\n\n" +
                "1. Identify the failed function.\n" +
                "2. Identify the expected relay state from the approved circuit.\n" +
                "3. Check whether the coil is receiving the required authorised feed.\n" +
                "4. If the coil is healthy, trace the relevant contact onward.\n" +
                "5. Check the next relay/input/output condition.\n" +
                "6. Compare indoor indication with field condition.\n" +
                "7. Record the actual measurement and relay state before changing anything.\n\n" +
                "SAFETY RULE\n" +
                "A relay abbreviation, colour, voltage or contact number shown here is reference information only. The station-specific approved circuit and authorised Railway procedure always take precedence."
            );
            return;
        }

        String warning =
            description +
            "\\n\\nFIELD SAFETY NOTE\\n" +
            "This is a reference and decision-support tool. For safety-critical work always follow the approved station-specific circuit, Railway instructions, RDSO/CAMTECH guidance and the equipment manufacturer's approved manual.";

        showTechnicalModule(title, warning);
    }

    void showTechnicalModule(String title,String message) {
        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(8),dp(4),dp(8),dp(4));

        ScrollView sv=new ScrollView(this);
        TextView t=text(message,14,false);
        t.setTextColor(Color.rgb(45,55,68));
        t.setPadding(dp(8),dp(8),dp(8),dp(8));
        sv.addView(t);

        box.addView(sv,new LinearLayout.LayoutParams(-1,0,1));

        TextView footer=text(
            "DEVELOPED BY PRATIK MUKHERJEE (SIM/ASN/ER)",
            10,true);
        footer.setGravity(Gravity.CENTER);
        footer.setTextColor(Color.rgb(15,72,105));
        footer.setPadding(0,dp(10),0,dp(4));
        box.addView(footer);

        new AlertDialog.Builder(this)
            .setTitle(title)
            .setView(box)
            .setPositiveButton("CLOSE",null)
            .show();
    }
}

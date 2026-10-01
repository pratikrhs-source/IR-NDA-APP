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

    void showStationProfile() {
        final String[] keys = {"station","interlocking","relay","panel","point_machine","detection","block","kavach","other"};
        final String[] labels = {"Station Name / Code","Interlocking: PI / RRI / EI","Relay Family / Siemens K-series","Panel / VDU","Point Machine Make / Type","Track Detection / MSDAC / Track Circuit","BPAC / HASSDAC / UFSBI / Block System","KAVACH / TCAS Make and Version","Other Indoor / Outdoor Equipment"};
        final android.content.SharedPreferences sp = getSharedPreferences("station_profile", MODE_PRIVATE);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(12),dp(4),dp(12),dp(4));
        final EditText[] fields = new EditText[keys.length];
        for(int i=0;i<keys.length;i++) {
            fields[i]=new EditText(this);
            fields[i].setHint(labels[i]);
            fields[i].setSingleLine(false);
            fields[i].setText(sp.getString(keys[i],""));
            box.addView(fields[i],new LinearLayout.LayoutParams(-1,dp(52)));
        }
        ScrollView sv=new ScrollView(this);
        sv.addView(box);
        new AlertDialog.Builder(this)
            .setTitle("STATION PROFILE")
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
        String st=sp.getString("station",""), in=sp.getString("interlocking",""), re=sp.getString("relay",""), pa=sp.getString("panel",""), pm=sp.getString("point_machine",""), de=sp.getString("detection",""), bl=sp.getString("block",""), ka=sp.getString("kavach",""), ot=sp.getString("other","");
        if(st.isEmpty() && in.isEmpty() && re.isEmpty()) return "STATION PROFILE: Not configured. Complete the profile before installation-specific diagnosis.\n\n";
        return "ACTIVE STATION PROFILE\nStation: "+st+"\nInterlocking: "+in+"\nRelay: "+re+"\nPanel/VDU: "+pa+"\nPoint Machine: "+pm+"\nDetection: "+de+"\nBlock System: "+bl+"\nKAVACH: "+ka+"\nOther: "+ot+"\n\n";
    }

    void showModule(String title,String description) {

        if ("STATION PROFILE".equals(title)) {
            showStationProfile();
            return;
        }

        if ("FAULT FINDER".equals(title)) {
            showTechnicalModule("FAULT FINDER",
                stationProfileSummary() +
                "S&T FAULT FINDER — PROFILE-AWARE INDOOR + OUTDOOR TROUBLESHOOTING\n\n" +
                "1. FIRST STEP\nIdentify station type PI/RRI/EI, relay family, panel/VDU, detection system, block system, equipment makes and indoor/outdoor arrangement before diagnosis.\n\n" +
                "2. UNIVERSAL FLOW\nSYMPTOM → STATION PROFILE → EQUIPMENT IDENTITY → PANEL/INDICATION → INDOOR LOGIC → APPROVED CIRCUIT → LOCATION BOX/INTERFACE → OUTDOOR EQUIPMENT → FIELD CORRESPONDENCE → FIRST ABNORMAL CONDITION.\n\n" +
                "3. SIGNAL NOT CLEARING\nTrack detection → point position/detection → route conditions → locking → signal control → relay/EI condition → relay-room correspondence → location box/interface → outdoor signal.\n\n" +
                "4. POINT NOT MOVING\nPanel command → interlocking permission → applicable control relay/output → approved circuit → location box → outdoor point machine → control/motor supply → detection → applicable NWKR/RWKR or equivalent → panel correspondence.\n\n" +
                "5. TRACK / AXLE COUNTER\nIdentify installed detection system first. Then section indication → detection points → field unit → evaluator → communication → relay/interface → event log → authorised reset → outdoor correspondence.\n\n" +
                "6. EI FAULT\nIdentify EI make/model/version → exact alarm → affected function → power/status → approved I/O/interface → communication → field equipment correspondence → event log.\n\n" +
                "7. RELAY FAULT\nNever assume HPR/DPR/ECR/WNR/WRR/NWKR/RWKR or another relay abbreviation has a universal function. Use the station profile and approved circuit.\n\n" +
                "8. OUTDOOR SUPPORT\nLocation boxes, point machines, signal units, detection equipment, trackside interfaces, cables and field connections are included.\n\n" +
                "9. REQUIRED INPUTS\nStation profile → exact symptom → panel/VDU indication → equipment make/model → approved Control Table/Route Chart → signal/point circuit → relay-room drawing → location-box/field circuit → diagnostic screen/event log.\n\n" +
                "10. AI RULE\nIf station type, equipment identity or circuit is unclear, request the missing information or approved drawing instead of guessing.\n\n" +
                "SAFETY\nNever bypass, bridge, force, short or defeat a vital safety circuit. Follow the approved station-specific circuit, Railway instructions, manufacturer manual and authorised procedure.");
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
        ScrollView sv=new ScrollView(this);
        TextView t=text(message,14,false);
        t.setTextColor(Color.rgb(45,55,68));
        t.setPadding(dp(8),dp(8),dp(8),dp(8));
        sv.addView(t);

        new AlertDialog.Builder(this)
            .setTitle(title)
            .setView(sv)
            .setPositiveButton("CLOSE",null)
            .show();
    }
}

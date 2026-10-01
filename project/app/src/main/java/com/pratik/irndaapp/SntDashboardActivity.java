package com.pratik.irndaapp;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.*;
import java.util.*;

public class SntDashboardActivity extends Activity {

    private final int NAVY = Color.rgb(5, 31, 58);
    private final int BLUE = Color.rgb(10, 91, 170);
    private final int GOLD = Color.rgb(246, 184, 45);
    private final int BG = Color.rgb(241, 245, 249);
    private final int TEXT = Color.rgb(22, 35, 50);
    private final int MUTED = Color.rgb(92, 108, 125);
    private LinearLayout root;
    private LinearLayout content;
    private EditText search;
    private TextView title;
    private final ArrayList<Module> modules = new ArrayList<>();

    static class Module {
        String category, name, icon, desc;
        Module(String c,String n,String i,String d){
            category=c; name=n; icon=i; desc=d;
        }
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        buildModules();
        showHome();
    }

    private void buildModules() {
        if(!modules.isEmpty()) return;

        add("SIGNALS & POINTS","Signalling Fundamentals","▣","Basic railway signalling principles, aspects, routes, interlocking and field practice.");
        add("SIGNALS & POINTS","Signal & Point","⚙","Signals, aspects, point operation, detection, correspondence and troubleshooting.");
        add("SIGNALS & POINTS","Point Machine","◆","Electric point machine, operation, detection, adjustment, locking and faults.");
        add("SIGNALS & POINTS","Signal LED","●","LED signal units, aspects, lighting, checking and common failures.");

        add("INTERLOCKING & RELAYS","Relay & Circuits","▤","Relay principles, contacts, WLR, NWKR/RWKR, route and signal circuits.");
        add("INTERLOCKING & RELAYS","Relay & Equipment Library","▦","Reference for important signalling relays and equipment.");
        add("INTERLOCKING & RELAYS","RRI / Panel","▥","Route setting, locking, panel indications and relay-room tracing.");
        add("INTERLOCKING & RELAYS","Electronic Interlocking","▧","EI architecture, vital logic, field interfaces and diagnostics.");

        add("TRACK & DETECTION","Track Circuit","═","Track feed, relay/evaluator, track leads, bonding and fault tracing.");
        add("TRACK & DETECTION","Insulated Rail Joints","╫","Block joints, insulated joints, glued joints and insulation fault diagnosis.");
        add("TRACK & DETECTION","MSDAC / Axle Counter","◎","Detection points, evaluator, section status, reset and diagnostics.");
        add("TRACK & DETECTION","Signalling Cable","≋","Cable types, termination, insulation resistance, joints and fault tracing.");

        add("BLOCK & AUTOMATIC","BPAC / HASSDAC","⇄","Block proving, axle counting, station interface and troubleshooting.");
        add("BLOCK & AUTOMATIC","UFSBI / Block Instruments","⇆","Universal fail-safe block interface and conventional block equipment.");
        add("BLOCK & AUTOMATIC","Automatic Signalling","→","Automatic block sections, signal sequence and field equipment.");
        add("BLOCK & AUTOMATIC","Station Classification","⌂","Station/interlocking classes and signalling arrangements.");

        add("LEVEL CROSSING","L/C Gate – ELB / MLB","⚠","Gate equipment, locking, approach/back locking and fault diagnosis.");
        add("LEVEL CROSSING","Gate Communication & Warning","☎","Bell, buzzer, indication, communication and warning arrangements.");

        add("POWER & SAFETY","IPS / Signalling Power","▣","IPS, charger, battery, DCDB/ACDB, inverter and power failure checks.");
        add("POWER & SAFETY","Fire Alarm System","♢","Fire detection, alarm indication and signalling installation safety.");
        add("POWER & SAFETY","Earthing & Surge Protection","⏚","Earthing, bonding, lightning and surge protection principles.");

        add("MODERN SYSTEMS","KAVACH / TCAS","◉","Trackside, onboard, RFID, LEU, radio and fault-finding overview.");
        add("MODERN SYSTEMS","Outdoor Equipment Library","⌘","Location boxes, junction boxes, signals, point equipment and field assets.");

        add("FIELD TOOLS","Fault Finder","⌕","Structured fault diagnosis for signals, points, tracks, relays and systems.");
        add("FIELD TOOLS","Station Profile","⌂","Save station-specific S&T configuration and equipment information.");
        add("FIELD TOOLS","Maintainer Toolkit","⚒","Field checklists, measurements, maintenance records and quick references.");
        add("FIELD TOOLS","Manuals & Sources","▤","RDSO/CAMTECH/manual reference section.");
        add("FIELD TOOLS","JE / SSE Mode","★","Quick technical checklist and supervisory field reference.");
    }

    private void add(String c,String n,String i,String d){ modules.add(new Module(c,n,i,d)); }

    private void showHome() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(18),dp(14),dp(18),dp(16));
        header.setBackground(round(NAVY,0,0,0,0,22));

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);

        SignalArtView art = new SignalArtView(this);
        top.addView(art,new LinearLayout.LayoutParams(dp(70),dp(70)));

        LinearLayout ht = new LinearLayout(this);
        ht.setOrientation(LinearLayout.VERTICAL);
        ht.setPadding(dp(12),0,0,0);

        TextView app = text("IR S&T FIELD ASSISTANT",20,Color.WHITE,true);
        TextView sub = text("Railway Signalling • Field Maintenance",12,Color.rgb(205,220,235),false);
        ht.addView(app);
        ht.addView(sub);
        top.addView(ht,new LinearLayout.LayoutParams(0,-2,1));

        header.addView(top);

        search = new EditText(this);
        search.setSingleLine(true);
        search.setHint("Search equipment, fault, relay, system...");
        search.setTextSize(14);
        search.setPadding(dp(14),0,dp(12),0);
        search.setBackground(round(Color.WHITE,1,Color.rgb(215,225,235),0,0,12));
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,dp(46));
        sp.topMargin=dp(14);
        header.addView(search,sp);

        search.setOnEditorActionListener((v,a,e)->{ filterHome(search.getText().toString()); return true; });
        search.setOnKeyListener((v,key,event)->{
            filterHome(search.getText().toString());
            return false;
        });

        root.addView(header,new LinearLayout.LayoutParams(-1,-2));

        ScrollView sv=new ScrollView(this);
        content=new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12),dp(12),dp(12),dp(24));
        sv.addView(content);
        root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));

        setContentView(root);
        renderHome("");
    }

    private void renderHome(String q) {
        content.removeAllViews();
        String last="";
        for(Module m:modules){
            if(q.length()>0 && !(m.name+" "+m.desc+" "+m.category).toLowerCase().contains(q.toLowerCase())) continue;
            if(!m.category.equals(last)){
                TextView cat=text(m.category,13,GOLD,true);
                cat.setPadding(dp(4),dp(10),0,dp(7));
                content.addView(cat);
                last=m.category;
            }
            content.addView(moduleCard(m));
        }

        if(content.getChildCount()==0){
            TextView no=text("No matching S&T topic found.\nTry relay, point, signal, IPS, cable, MSDAC, KAVACH or fault.",15,MUTED,false);
            no.setGravity(Gravity.CENTER);
            no.setPadding(dp(20),dp(50),dp(20),dp(50));
            content.addView(no);
        }

        LinearLayout footer=new LinearLayout(this);
        footer.setOrientation(LinearLayout.VERTICAL);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0,dp(20),0,dp(8));
        footer.addView(text("IR S&T FIELD ASSISTANT",13,NAVY,true));
        footer.addView(text("DEVELOPED BY PRATIK MUKHERJEE (SIM/ASN/ER)",11,MUTED,false));
        content.addView(footer);
    }

    private void filterHome(String q){ renderHome(q); }

    private View moduleCard(final Module m){
        LinearLayout card=new LinearLayout(this);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(12),dp(10),dp(10),dp(10));
        card.setBackground(round(Color.WHITE,1,Color.rgb(218,226,235),0,0,14));
        card.setElevation(dp(2));

        TextView icon=text(m.icon,25,BLUE,true);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(round(Color.rgb(232,241,251),0,0,0,0,12));
        card.addView(icon,new LinearLayout.LayoutParams(dp(48),dp(48)));

        LinearLayout t=new LinearLayout(this);
        t.setOrientation(LinearLayout.VERTICAL);
        t.setPadding(dp(12),0,dp(6),0);
        t.addView(text(m.name,15,TEXT,true));
        TextView d=text(m.desc,11,MUTED,false);
        d.setMaxLines(2);
        t.addView(d);
        card.addView(t,new LinearLayout.LayoutParams(0,-2,1));

        TextView arrow=text("›",28,MUTED,false);
        card.addView(arrow,new LinearLayout.LayoutParams(dp(25),-2));

        card.setOnClickListener(v->showModule(m));
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);
        cp.bottomMargin=dp(8);
        content.addView(card,cp);
        return new Space(this);
    }

    private void showModule(Module m){
        if(m.name.equals("Fault Finder")) { showFaultFinder(); return; }
        if(m.name.equals("Station Profile")) { showStationProfile(); return; }
        if(m.name.equals("Relay & Equipment Library")) { showRelayLibrary(); return; }
        showInformation(m);
    }

    private void baseScreen(String screenTitle){
        root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        LinearLayout bar=new LinearLayout(this);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(8),0,dp(12),0);
        bar.setBackgroundColor(NAVY);

        TextView back=text("‹",38,Color.WHITE,false);
        back.setGravity(Gravity.CENTER);
        back.setOnClickListener(v->showHome());
        bar.addView(back,new LinearLayout.LayoutParams(dp(52),dp(58)));

        title=text(screenTitle,18,Color.WHITE,true);
        bar.addView(title,new LinearLayout.LayoutParams(0,-2,1));
        bar.addView(text("⋮",26,Color.WHITE,false),new LinearLayout.LayoutParams(dp(30),-2));
        root.addView(bar);

        content=new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12),dp(12),dp(12),dp(20));

        ScrollView sv=new ScrollView(this);
        sv.addView(content);
        root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }

    private void showInformation(Module m){
        baseScreen(m.name);

        addHero(m.name,m.desc);

        if(m.name.equals("Point Machine")) pointContent();
        else if(m.name.equals("Signal LED")) signalLedContent();
        else if(m.name.equals("Track Circuit")) trackContent();
        else if(m.name.equals("Insulated Rail Joints")) jointContent();
        else if(m.name.equals("Signalling Cable")) cableContent();
        else if(m.name.equals("IPS / Signalling Power")) ipsContent();
        else if(m.name.equals("L/C Gate – ELB / MLB")) lcContent();
        else if(m.name.equals("Automatic Signalling")) autoContent();
        else if(m.name.equals("KAVACH / TCAS")) kavachContent();
        else if(m.name.equals("MSDAC / Axle Counter")) axleContent();
        else if(m.name.equals("Electronic Interlocking")) eiContent();
        else if(m.name.equals("Relay & Circuits")) relayContent();
        else if(m.name.equals("Signal & Point")) signalPointContent();
        else if(m.name.equals("RRI / Panel")) rriContent();
        else if(m.name.equals("BPAC / HASSDAC")) bpacContent();
        else if(m.name.equals("UFSBI / Block Instruments")) ufsbiContent();
        else if(m.name.equals("Fire Alarm System")) fireContent();
        else if(m.name.equals("Outdoor Equipment Library")) outdoorContent();
        else if(m.name.equals("Station Classification")) stationClassContent();
        else if(m.name.equals("Earthing & Surge Protection")) earthContent();
        else if(m.name.equals("Maintainer Toolkit")) toolkitContent();
        else if(m.name.equals("JE / SSE Mode")) jeContent();
        else if(m.name.equals("Manuals & Sources")) sourceContent();
        else genericContent(m);
    }

    private void addHero(String h,String sub){
        LinearLayout box=new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16),dp(14),dp(16),dp(14));
        box.setBackground(round(NAVY,0,0,0,0,16));
        box.addView(text(h,20,Color.WHITE,true));
        box.addView(text(sub,12,Color.rgb(210,225,240),false));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.bottomMargin=dp(12);
        content.addView(box,p);
    }

    private void section(String h,String body){
        LinearLayout c=new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(14),dp(12),dp(14),dp(12));
        c.setBackground(round(Color.WHITE,1,Color.rgb(218,226,235),0,0,14));
        c.addView(text(h,15,NAVY,true));
        TextView b=text(body,12,TEXT,false);
        b.setPadding(0,dp(8),0,0);
        c.addView(b);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.bottomMargin=dp(10);
        content.addView(c,p);
    }

    private void bullet(String h,String body){
        section("• "+h,body);
    }

    private void genericContent(Module m){
        section("Overview",
                m.desc+"\n\nUse the approved station drawing, circuit diagram and equipment manual for exact values, wiring and adjustment limits.");
        section("Field Checks",
                "1. Confirm the symptom at the panel/VDU.\n2. Identify the affected equipment and location.\n3. Check supply and indications.\n4. Trace the approved circuit from indoor equipment towards the field.\n5. Record observations before restoration.");
        section("Common Fault Approach",
                "Symptom → indication → supply → interface → relay/logic → cable → field equipment → physical condition.");
        section("Safety",
                "Do not bypass vital circuits or interlocking. Follow approved maintenance instructions, testing procedures and railway safety rules.");
    }

    private void relayContent(){
        section("Relay Fundamentals","A signalling relay provides electrical logic/interface while maintaining fail-safe behaviour. Always identify the exact approved circuit before interpreting a relay designation.");
        bullet("Picked / Dropped","Picked = relay energised. Dropped = relay de-energised. Actual logic depends on the approved circuit.");
        bullet("Contacts","Front and back contacts are used to establish or interrupt circuit paths. Never assume a contact function from the relay name alone.");
        bullet("Important Relays","WLR, NWKR, RWKR, WNKR, WRKR, WNR, WRR, HPR, DPR and ECR may occur in different applications. Verify station-specific function from the circuit.");
        bullet("Fault Tracing","Start from the symptom, check supply, fuse/MCB, relay energisation, contact condition, wiring/termination and downstream equipment.");
        section("Typical Checks","Relay indication → coil supply → contact output → cable continuity/insulation where applicable → receiving equipment.");
        section("Safety","Never short, bridge or force a vital relay/contact for normal operation. Use approved testing procedure and authorised isolation.");
    }

    private void signalPointContent(){
        section("Signal","Signal aspect is governed by the interlocking logic, route conditions and relevant detection/locking conditions. Check the approved control table and circuit.");
        section("Point","Point operation includes command, motor operation, physical movement, detection and correspondence.");
        section("Detection","Normal and reverse detection must correspond with the actual point position before the interlocking can permit the relevant route.");
        section("Fault Path","Panel/VDU indication → route condition → point command → relay/EI output → location box → point machine → detection → return indication.");
        section("Safety","Do not defeat detection, locking or correspondence. Any adjustment must follow the approved maintenance manual.");
    }

    private void pointContent(){
        section("Operation","Typical sequence: command → control circuit → point machine motor → mechanical movement → detection → correspondence → indication.");
        bullet("Normal / Reverse","Verify commanded position, actual tongue position, detection and panel/VDU indication.");
        bullet("Detection","Check NWKR/RWKR or equivalent detection interface as applicable to the installation.");
        bullet("Common Faults","Point not moving, moves only one direction, no detection, intermittent detection, correspondence failure, abnormal indication.");
        bullet("Field Tracing","Panel/VDU → relay/EI interface → cable → location box → point machine → motor/gear/mechanical portion → detection contacts.");
        section("Adjustment","Exact stroke, contact adjustment, voltage/current and mechanical limits are equipment-specific. Use the approved point-machine manual; do not use generic values.");
        section("Safety","Ensure required protection/isolation before working on point machine or moving parts.");
    }

    private void signalLedContent(){
        section("LED Signal","LED signal units provide the optical aspect. Different approved designs may have different modules, connectors and monitoring arrangements.");
        section("Checks","Aspect at panel/VDU → lamp proving/monitoring → supply → LED unit → connector/termination → cable.");
        section("Common Faults","Aspect not displaying, wrong aspect indication, intermittent LED, lamp proving failure, cable/termination fault.");
        section("Maintenance","Inspect mounting, lens/hood, wiring, connectors and indication. Follow the approved LED signal maintenance instruction.");
    }

    private void trackContent(){
        section("Track Circuit","A track circuit detects the occupied/clear condition of a defined rail section using an approved electrical arrangement.");
        section("Main Elements","Track feed/evaluator → rail section → insulated joints/bonds → relay/evaluator end → indication/interlocking interface.");
        section("Fault Approach","Check indication → supply/feed → relay/evaluator → track leads → rail bonds → insulated joints → rail condition.");
        section("Common Faults","False occupied, failure to clear, intermittent occupancy, low insulation, broken/poor bond, joint problem.");
        section("Safety","Railway track and signalling circuits are safety-critical. Do not bypass a track circuit to obtain a clear indication.");
    }

    private void jointContent(){
        section("Insulated Rail Joints","Insulated joints electrically separate adjacent track-circuit sections. Joint design depends on the approved rail and signalling arrangement.");
        bullet("Block Joint","Used to separate block/track circuit sections as specified for the installation.");
        bullet("Insulated Joint","Provides electrical separation between rail sections while maintaining mechanical continuity.");
        bullet("Glued Insulated Joint","A bonded insulated rail joint using an approved adhesive/fibreglass assembly; inspect for mechanical and electrical integrity.");
        bullet("Checks","Joint condition → insulation → rail connections → bonds → track-circuit indication → leakage/shorting possibility.");
        section("Reference","For exact joint construction, dimensions and maintenance limits use the applicable RDSO/approved Engineering and S&T joint manual.");
    }

    private void cableContent(){
        section("Signalling Cable","Signalling cables carry vital and non-vital circuits between relay room, equipment room, location boxes and field equipment.");
        section("Field Checks","Identify cable → verify core/tag → termination condition → continuity → insulation resistance where authorised → route/joint condition.");
        section("Common Faults","Open core, short between cores, earth fault, low insulation, damaged sheath, loose termination, water ingress.");
        section("Cable Fault Tracing","Divide the route into sections and test systematically rather than disturbing multiple terminations at once.");
        section("Safety","Use the approved cable schedule and testing procedure. Do not disturb unidentified vital cores.");
    }

    private void ipsContent(){
        section("IPS – Integrated Power Supply","IPS supports signalling loads through regulated power conversion, batteries and protection arrangements. Exact architecture varies by approved system.");
        section("Main Blocks","Incoming supply → protection/changeover → charger/SMPS → battery → distribution → signalling loads.");
        section("Checks","Input supply → protection → charger status → battery voltage/health → DC/AC outputs → load current → alarms.");
        section("Common Faults","Mains failure, charger fault, battery low, overload, output missing, alarm indication, changeover problem.");
        section("Safety","Battery and power circuits can carry hazardous energy. Follow electrical isolation and approved IPS maintenance instructions.");
    }

    private void lcContent(){
        section("L/C Gate","Signalling equipment at a level crossing may include gate control, locking, detection, communication and warning interfaces.");
        bullet("ELB / MLB","Exact equipment configuration is installation-specific. Identify the approved type and circuit before troubleshooting.");
        bullet("Locking","Check gate status, approach/back locking and the interlocking condition as applicable.");
        bullet("Warning","Verify bell/buzzer, visual indication and communication arrangements where provided.");
        section("Fault Approach","Gate indication → supply → control/interface → field equipment → gate position/locking → return indication.");
        section("Safety","Never defeat gate protection or interlocking to restore normal indication.");
    }

    private void autoContent(){
        section("Automatic Signalling","Automatic signalling divides the railway into controlled sections and uses train detection to control successive signal aspects.");
        section("Core Chain","Train detection → section status → interlocking/automatic logic → signal aspect → following section protection.");
        section("Equipment","Depending on installation: track circuits, axle counters, automatic signal units, relays/EI, power and communication equipment.");
        section("Fault Finding","Identify affected section → check detection status → signal indication → relay/EI status → field equipment → cable/power.");
        section("Safety","Never manually create a clear condition or bypass train detection.");
    }

    private void axleContent(){
        section("MSDAC / Axle Counter","Axle-counting systems determine section occupancy from axle detection points and an evaluator/processor.");
        section("Main Elements","Detection points/sensors → evaluator → communication/interface → section indication → interlocking.");
        section("Faults","Section occupied, failure to clear, detector fault, communication fault, evaluator alarm, reset requirement.");
        section("Diagnosis","Read diagnostic indication → identify section/detection point → check supply/communication → inspect field equipment → follow approved reset procedure.");
        section("Safety","Reset procedures are system-specific and must be performed only under authorised operating/maintenance conditions.");
    }

    private void eiContent(){
        section("Electronic Interlocking","EI uses computer-based vital logic to implement interlocking functions and interface with field equipment.");
        section("Architecture","Vital processing → I/O/field interface → relay/solid-state interface → outdoor equipment → diagnostic/event logging.");
        section("Field Interface","Signals, points, track detection, level crossing, block systems and other approved interfaces may be connected.");
        section("Diagnostics","Use system diagnostics/event logs to identify module, channel, interface or field-side abnormalities.");
        section("Fault Approach","Panel/VDU symptom → EI diagnostic → affected I/O → interface → cable → field equipment.");
        section("Safety","Do not alter vital configuration/data or bypass interlocking. Follow the approved EI maintenance and testing manual.");
    }

    private void rriContent(){
        section("RRI / Panel","Relay Route Relay Interlocking uses relay logic to establish safe routes and locking conditions. Panel/VDU provides operator indications and commands.");
        section("Route Setting","Route request → route conditions → points commanded → detection/correspondence → route locking → signal clearance.");
        section("Route Locking","Once established, relevant points are protected against conflicting movement until the permitted release conditions are met.");
        section("Fault Tracing","Panel indication → route condition → point detection → track condition → relay room circuit → field equipment.");
        section("Useful Documents","Control Table/Route Chart, Signal Control Circuit, Point Control & Detection Circuit, Route/Approach Locking Circuit and relay-room drawings.");
    }

    private void bpacContent(){
        section("BPAC / HASSDAC","Block proving systems establish train/section status between stations using approved axle detection and communication arrangements.");
        section("Main Elements","Axle detection → evaluator/interface → station equipment → communication → block indication/interlocking.");
        section("Common Issues","Communication failure, axle count mismatch, section occupied, reset requirement, interface indication fault.");
        section("Fault Approach","Station indication → communication → evaluator → detection point → power → cable/interface.");
        section("Safety","Follow the approved block working and reset procedure; do not force a clear block condition.");
    }

    private void ufsbiContent(){
        section("UFSBI / Block Interface","UFSBI provides a fail-safe interface between block equipment and interlocking/related signalling systems.");
        section("Concept","Vital input/output exchange → fail-safe interface logic → block status/command → interlocking.");
        section("Checks","Power → module/interface indication → communication → input/output status → connected block equipment.");
        section("Safety","Exact terminal/function mapping must be taken from the approved station drawing and UFSBI manual.");
    }

    private void fireContent(){
        section("Fire Alarm","Signalling installations may use fire detection and alarm systems for relay rooms, equipment rooms and other protected spaces.");
        section("Main Functions","Detection → local indication → audible/visual alarm → interface to monitoring arrangement where provided.");
        section("Checks","Panel status → detector/alarm indication → supply → loop/interface → event history.");
        section("Maintenance","Keep detectors unobstructed, check alarms as per approved schedule and record tests.");
    }

    private void outdoorContent(){
        section("Outdoor Equipment Library","A field maintainer should be able to identify equipment before opening or testing a circuit.");
        bullet("Signals","Signal post, LED unit, location box, junction/termination arrangement.");
        bullet("Points","Point machine, detection components, location box, point wiring.");
        bullet("Track","Track leads, bonds, insulated joints, glued joints, track circuit equipment.");
        bullet("Axle Counter","Detection points, junction/interface equipment and evaluator connection.");
        bullet("Level Crossing","Gate equipment, ELB/MLB, warning and communication equipment.");
        bullet("Power","Outdoor power distribution, batteries/field supply and protection.");
    }

    private void stationClassContent(){
        section("Station & Signalling Arrangement","Station classification and signalling arrangement depend on the railway operating pattern and approved scheme.");
        section("Typical Information to Record","Station code/name, class/category as officially applicable, interlocking type, number of lines, points, signals, detection system, block system, automatic/absolute signalling, KAVACH and level crossings.");
        section("Important","Do not infer a station's official class from appearance. Record the classification from railway records/approved documents.");
    }

    private void earthContent(){
        section("Earthing & Bonding","Proper earthing, bonding and surge protection protect equipment and help maintain reliable signalling operation.");
        section("Checks","Earth connection → bonding continuity → corrosion/loose termination → surge protection status → approved earth measurement.");
        section("Important","Exact acceptable resistance/measurement depends on the applicable railway standard and installation. Use the approved specification rather than a generic number.");
    }

    private void toolkitContent(){
        section("Before Attending a Fault","Know the affected signal/point/track/section, recent history, panel indication and relevant circuit/drawing.");
        section("Field Checklist","Safety protection → identify equipment → verify symptom → check supply → inspect connections → test systematically → restore → verify normal operation → record.");
        section("Useful Records","Fault time, equipment ID, indication, measurements, action taken, replacement, restoration time and recurrence.");
        section("Golden Rule","Never replace or adjust a component merely because its name appears in the fault description. Confirm the circuit and symptom first.");
    }

    private void jeContent(){
        section("JE / SSE Field Review","Use this screen for supervisory checks and maintenance review.");
        section("Daily Review","Pending faults → repeated failures → equipment overdue for attention → safety observations → temporary arrangements → material requirement.");
        section("Fault Review","What failed? Where? Why? Was it intermittent? What was measured? What was replaced? Did the same fault recur?");
        section("Documentation","Maintain clear records and refer to approved drawings, maintenance manuals and inspection schedules.");
    }

    private void sourceContent(){
        section("Reference Sources","Use current approved railway documents for exact technical requirements.");
        bullet("RDSO / CAMTECH","Handbooks, maintenance instructions, technical specifications and safety guidance.");
        bullet("Approved Equipment Manual","Make/model-specific installation, adjustment, testing and maintenance instructions.");
        bullet("Station Drawings","Control table, circuit diagrams, cable plan, location box wiring, relay-room drawings and equipment layout.");
        section("Important","This app is a field reference. Where an approved railway instruction conflicts with a generic explanation here, the approved instruction governs.");
    }

    private void showFaultFinder(){
        baseScreen("Fault Finder");
        addHero("FIELD FAULT DIAGNOSIS","Select the symptom and follow a structured checking path.");

        String[] faults={
            "Signal not clearing / Signal at ON",
            "Wrong signal aspect",
            "Point not operating",
            "Point not detected / correspondence failure",
            "Track circuit / axle counter fault",
            "Route not setting / route locking",
            "EI / RRI / Panel fault",
            "MSDAC / BPAC / UFSBI fault",
            "KAVACH related issue",
            "IPS / power failure",
            "L/C Gate fault",
            "Cable / insulation fault"
        };

        for(String f:faults){
            TextView b=buttonRow("⚠  "+f);
            b.setOnClickListener(v->faultFlow(f));
            content.addView(b);
        }
    }

    private TextView buttonRow(String s){
        TextView b=text(s+"   ›",14,TEXT,true);
        b.setGravity(Gravity.CENTER_VERTICAL);
        b.setPadding(dp(14),0,dp(10),0);
        b.setBackground(round(Color.WHITE,1,Color.rgb(215,225,235),0,0,12));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(52));
        p.bottomMargin=dp(8);
        content.addView(b,p);
        return b;
    }

    private void faultFlow(String f){
        baseScreen(f);
        addHero("CHECKING PATH","Use the approved circuit/drawing and applicable safety procedure.");

        String[] steps;
        if(f.contains("Signal")) steps=new String[]{
            "Confirm exact signal identity and aspect indication.",
            "Check route setting and route locking conditions.",
            "Verify point operation and detection/correspondence.",
            "Check track circuit / axle counter status.",
            "Check relevant relay/EI diagnostic and control circuit.",
            "Trace towards location box, cable and signal equipment.",
            "Restore and verify the complete route/signal sequence."
        };
        else if(f.contains("Point")) steps=new String[]{
            "Confirm commanded Normal/Reverse position.",
            "Check panel/VDU command and indication.",
            "Check point control output and relay/EI interface.",
            "Check cable/termination and location box.",
            "Check point machine supply and operation.",
            "Check mechanical movement and detection.",
            "Verify correspondence after restoration."
        };
        else steps=new String[]{
            "Confirm the exact affected equipment/section.",
            "Record the symptom and indication before disturbing anything.",
            "Check approved power supply and protection.",
            "Check interface/relay/EI/communication status.",
            "Check cable, termination and field equipment.",
            "Follow the equipment-specific diagnostic procedure.",
            "Verify normal operation and record the action taken."
        };

        int n=1;
        for(String s:steps){
            LinearLayout row=new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            TextView num=text(String.valueOf(n++),16,Color.WHITE,true);
            num.setGravity(Gravity.CENTER);
            num.setBackground(round(Color.rgb(18,145,95),0,0,0,0,20));
            row.addView(num,new LinearLayout.LayoutParams(dp(38),dp(38)));
            row.addView(text(s,13,TEXT,false),new LinearLayout.LayoutParams(0,-2,1));
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
            p.bottomMargin=dp(10);
            content.addView(row,p);
        }

        section("Safety","Do not bypass vital conditions, short contacts, force detection or create a false clear indication. Follow authorised railway procedures.");
    }

    private void showRelayLibrary(){
        baseScreen("Relay & Equipment Library");
        addHero("S&T EQUIPMENT REFERENCE","Select an item for its field-use summary.");

        String[][] data={
            {"WKR","Point detection / correspondence","Verify exact function from approved circuit."},
            {"NWKR","Normal detection relay","Normal position detection; station-specific circuit."},
            {"RWKR","Reverse detection relay","Reverse position detection; station-specific circuit."},
            {"WLR","Point/route related relay","Function depends on approved interlocking circuit."},
            {"WNKR / WRKR","Point control logic","Verify exact circuit and interlocking application."},
            {"WNR / WRR","Relay designation","Do not infer function from letters alone."},
            {"HPR / DPR / ECR","Relay logic/interface","Verify from approved circuit and equipment documentation."},
            {"K-50 / K-series","Plug-in relay family","Exact application depends on approved design."}
        };

        for(String[] x:data){
            LinearLayout c=new LinearLayout(this);
            c.setOrientation(LinearLayout.VERTICAL);
            c.setPadding(dp(14),dp(12),dp(14),dp(12));
            c.setBackground(round(Color.WHITE,1,Color.rgb(215,225,235),0,0,14));
            c.addView(text(x[0],17,NAVY,true));
            c.addView(text(x[1],12,BLUE,true));
            c.addView(text(x[2],11,MUTED,false));
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
            p.bottomMargin=dp(8);
            content.addView(c,p);
        }
    }

    private void showStationProfile(){
        baseScreen("Station Profile");
        addHero("STATION S&T DATABASE","Save the configuration of your station for faster fault diagnosis.");

        String[] labels={
            "Station Name / Code","Station Class","Interlocking Type","EI Make / Model / Version",
            "Number of Lines","Number of Points","Number of Signals","Track Detection",
            "Block System","Automatic / Absolute Signalling","Point Machine Make / Type",
            "IPS / Power Arrangement","KAVACH / TCAS","L/C Gates","Remarks"
        };

        for(String l:labels){
            EditText e=new EditText(this);
            e.setHint(l);
            e.setTextSize(13);
            e.setSingleLine(false);
            e.setPadding(dp(12),0,dp(12),0);
            e.setBackground(round(Color.WHITE,1,Color.rgb(210,220,230),0,0,10));
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(48));
            p.bottomMargin=dp(7);
            content.addView(e,p);
        }

        Button save=new Button(this);
        save.setText("SAVE STATION PROFILE");
        save.setTextColor(Color.WHITE);
        save.setBackground(round(BLUE,0,0,0,0,12));
        content.addView(save,new LinearLayout.LayoutParams(-1,dp(52)));

        save.setOnClickListener(v->Toast.makeText(this,"Station profile saved for this session.",Toast.LENGTH_SHORT).show());
    }

    private TextView text(String s,float size,int color,boolean bold){
        TextView t=new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));
        return t;
    }

    private GradientDrawable round(int fill,int stroke,int strokeColor,int a,int b,int radius){
        return makeRound(fill,stroke,strokeColor,radius);
    }

    private GradientDrawable round(int fill,int stroke,int strokeColor,int a,int b,int c,int radius){
        return makeRound(fill,stroke,strokeColor,radius);
    }

    private GradientDrawable round(int fill,int stroke,int strokeColor,int a,int b,int c,int d,int radius){
        return makeRound(fill,stroke,strokeColor,radius);
    }

    private GradientDrawable makeRound(int fill,int stroke,int strokeColor,int radius){
        GradientDrawable g=new GradientDrawable();
        g.setColor(fill);
        if(stroke>0) g.setStroke(stroke,strokeColor);
        g.setCornerRadius(dp(radius));
        return g;
    }

    private int dp(int n){ return (int)(n*getResources().getDisplayMetrics().density+0.5f); }

    @Override
    public void onBackPressed(){
        showHome();
    }

    public static class SignalArtView extends View {
        Paint p=new Paint(3);
        public SignalArtView(Context c){super(c);}
        protected void onDraw(Canvas c){
            super.onDraw(c);
            float d=getResources().getDisplayMetrics().density;
            p.setStrokeWidth(4*d);
            p.setColor(Color.LTGRAY);
            c.drawLine(35*d,8*d,35*d,62*d,p);
            p.setStrokeWidth(2*d);
            c.drawLine(23*d,62*d,47*d,62*d,p);
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.rgb(235,55,55));
            c.drawCircle(35*d,20*d,7*d,p);
            p.setColor(Color.rgb(70,205,105));
            c.drawCircle(35*d,39*d,7*d,p);
            p.setStyle(Paint.Style.STROKE);
            p.setColor(Color.WHITE);
            c.drawRect(22*d,7*d,48*d,52*d,p);
            p.setStyle(Paint.Style.FILL);
        }
    }
}

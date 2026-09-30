package com.pratik.irndaapp;

import android.app.*;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.os.Handler;
import android.content.*;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.text.*;
import androidx.core.content.FileProvider;
import org.json.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.security.MessageDigest;

public class MainActivity extends Activity {
    LinearLayout root, datesBox, profileBox;
    EditText name, designation, pf, level, basic, da, month;
    TextView ratePreview, dashboardText;
    ArrayList<DutyDate> dates = new ArrayList<>();
    SharedPreferences sp;
    Handler handler = new Handler();
    File lastPdf;
    Uri lastPdfUri;
    boolean loading = false;
    SpeechRecognizer speechRecognizer;
    EditText aiCommand;
    Spinner aiLanguage;

    int NAVY=Color.rgb(18,43,68), BLUE=Color.rgb(25,103,190), GOLD=Color.rgb(220,164,28), BG=Color.rgb(244,247,251), TEXT=Color.rgb(35,45,55), GREEN=Color.rgb(39,126,82), RED=Color.rgb(190,60,60), WHITE=Color.WHITE;
    boolean darkMode=false;
    int CARD=Color.WHITE, BORDER=Color.rgb(225,231,239);

    // Offline commercial license secret. Keep this value private when distributing the license generator.
    static final String LICENSE_SECRET = "PRATIK_IR_NDA_2026_OFFLINE_LICENSE_V2";

    int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
    GradientDrawable bg(int color,float radius){ GradientDrawable g=new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(radius)); g.setStroke(dp(1),BORDER); return g; }
    TextView tv(String s,float size,boolean bold){ TextView t=new TextView(this); t.setText(tr(s)); t.setTextSize(size); t.setTextColor(TEXT); t.setTypeface(null,bold?Typeface.BOLD:Typeface.NORMAL); t.setPadding(dp(6),dp(5),dp(6),dp(5)); return t; }
    EditText input(String hint,String value){ EditText e=new EditText(this); e.setHint(tr(hint)); e.setText(value); e.setTextSize(15); e.setSingleLine(true); e.setTextColor(TEXT); e.setHintTextColor(Color.rgb(135,145,155)); e.setPadding(dp(12),dp(8),dp(12),dp(8)); e.setBackground(bg(CARD,12)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,dp(4),0,dp(4)); e.setLayoutParams(p); return e; }
    Button btn(String text){ Button b=new Button(this); b.setText(tr(text)); b.setTextSize(13); b.setAllCaps(false); b.setTextColor(darkMode?WHITE:NAVY); b.setPadding(dp(8),dp(4),dp(8),dp(4)); b.setMinHeight(dp(46)); b.setBackground(bg(CARD,14)); return b; }
    LinearLayout card(){ LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(dp(14),dp(12),dp(14),dp(12)); l.setBackground(bg(CARD,18)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(dp(9),dp(6),dp(9),dp(6)); l.setLayoutParams(p); return l; }
    TextView sectionTitle(String s){ TextView t=tv(s,17,true); t.setTextColor(NAVY); t.setPadding(0,dp(2),0,dp(8)); return t; }

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        sp=getSharedPreferences("nda",0);
        darkMode=sp.getBoolean("dark_mode",false); applyThemeColors();
        if(isActivated()) showSplash(); else showActivation();
    }

    String tr(String s){
        int lang=sp==null?0:sp.getInt("ui_lang",0); if(lang==0||s==null)return s;
        if(lang==1){
            String[] a={"EMPLOYEE PROFILE","First use: fill these manually. The app does not pre-fill personal details.","Name","Designation","P.F. A/C No.","PC 7 Level","Basic Pay (₹)","DA (%)","💾  Save Profile","💰 NDA RATE PREVIEW","MONTH","LOAD","AUTOMATIC WEEKLY NIGHT DUTY","Every week starts:","Start:","  End:","Ends after:","same day","+1 day","+2 days","+3 days","⚡  SAVE & FILL THIS MONTH","Turn OFF automatic weekly fill","🤖 AI DUTY ASSISTANT","🎤 Speak","✨  UNDERSTAND & ADD DUTY","🔁  UNDERSTAND & SAVE WEEKLY PATTERN","ℹ️ HOW NDA IS CALCULATED","NIGHT DUTY ENTRIES","＋  ADD ANOTHER DATE","💾  SAVE THIS MONTH","✓  CALCULATE NDA UNITS","📄  GENERATE & SAVE PDF","↗  SHARE SAVED PDF","🗂  MONTHLY HISTORY","CLEAR CURRENT MONTH","ⓘ  ABOUT / VERSION 1.2.8","📋  COPY PREVIOUS MONTH PATTERN","🛠 MONTH & DATA TOOLS","🔒  LOCK MONTH","🔓  UNLOCK MONTH","⚠️  CHECK ERRORS","⬆ Backup","⬇ Restore","UI Language","🌙 Dark Mode","☀ Light Mode","TIME SPELLS","＋ Add another time to this date","Remarks (optional)"};
            String[] b={"কর্মী প্রোফাইল","প্রথমবার এগুলি নিজে পূরণ করুন। অ্যাপ ব্যক্তিগত তথ্য আগে থেকে পূরণ করে না।","নাম","পদবি","পি.এফ. অ্যাকাউন্ট নম্বর","PC 7 লেভেল","বেসিক পে (₹)","DA (%)","💾  প্রোফাইল সংরক্ষণ","💰 NDA রেট","মাস","লোড","স্বয়ংক্রিয় সাপ্তাহিক নাইট ডিউটি","প্রতি সপ্তাহের শুরু:","শুরু:","  শেষ:","শেষ হবে:","একই দিন","+১ দিন","+২ দিন","+৩ দিন","⚡  সংরক্ষণ ও এই মাস পূরণ","স্বয়ংক্রিয় সাপ্তাহিক পূরণ বন্ধ","🤖 AI ডিউটি সহায়ক","🎤 বলুন","✨  বুঝে ডিউটি যোগ করুন","🔁  বুঝে সাপ্তাহিক প্যাটার্ন সংরক্ষণ","ℹ️ NDA কীভাবে হিসাব হয়","নাইট ডিউটি এন্ট্রি","＋  আরও একটি তারিখ যোগ করুন","💾  এই মাস সংরক্ষণ করুন","✓  NDA ইউনিট হিসাব করুন","📄  PDF তৈরি ও সংরক্ষণ","↗  সংরক্ষিত PDF শেয়ার","🗂  মাসিক ইতিহাস","বর্তমান মাস পরিষ্কার করুন","ⓘ  ABOUT / VERSION 1.2.8","📋  আগের মাসের প্যাটার্ন কপি","🛠 মাস ও ডেটা টুলস","🔒  মাস লক করুন","🔓  মাস আনলক করুন","⚠️  ভুল পরীক্ষা করুন","⬆ ব্যাকআপ","⬇ রিস্টোর","UI ভাষা","🌙 ডার্ক মোড","☀ লাইট মোড","সময়ের স্পেল","＋ এই তারিখে আরও সময় যোগ করুন","Remarks (ঐচ্ছিক)"};
            for(int i=0;i<a.length;i++)if(s.equals(a[i]))return b[i];
        } else if(lang==2){
            String[] a={"EMPLOYEE PROFILE","Name","Designation","P.F. A/C No.","PC 7 Level","Basic Pay (₹)","DA (%)","💾  Save Profile","💰 NDA RATE PREVIEW","MONTH","LOAD","AUTOMATIC WEEKLY NIGHT DUTY","Every week starts:","Start:","  End:","Ends after:","same day","+1 day","+2 days","+3 days","⚡  SAVE & FILL THIS MONTH","Turn OFF automatic weekly fill","🤖 AI DUTY ASSISTANT","🎤 Speak","✨  UNDERSTAND & ADD DUTY","🔁  UNDERSTAND & SAVE WEEKLY PATTERN","ℹ️ HOW NDA IS CALCULATED","NIGHT DUTY ENTRIES","＋  ADD ANOTHER DATE","💾  SAVE THIS MONTH","✓  CALCULATE NDA UNITS","📄  GENERATE & SAVE PDF","↗  SHARE SAVED PDF","🗂  MONTHLY HISTORY","CLEAR CURRENT MONTH","ⓘ  ABOUT / VERSION 1.2.8","📋  COPY PREVIOUS MONTH PATTERN","🛠 MONTH & DATA TOOLS","🔒  LOCK MONTH","🔓  UNLOCK MONTH","⚠️  CHECK ERRORS","⬆ Backup","⬇ Restore","UI Language","🌙 Dark Mode","☀ Light Mode","TIME SPELLS","＋ Add another time to this date","Remarks (optional)"};
            String[] b={"कर्मचारी प्रोफ़ाइल","नाम","पदनाम","पी.एफ. खाता संख्या","PC 7 स्तर","बेसिक पे (₹)","DA (%)","💾  प्रोफ़ाइल सेव करें","💰 NDA रेट","महीना","लोड","स्वचालित साप्ताहिक नाइट ड्यूटी","हर सप्ताह शुरू:","शुरू:","  समाप्त:","समाप्ति:","उसी दिन","+1 दिन","+2 दिन","+3 दिन","⚡  सेव करें और इस महीने भरें","स्वचालित साप्ताहिक भरना बंद करें","🤖 AI ड्यूटी सहायक","🎤 बोलें","✨  समझें और ड्यूटी जोड़ें","🔁  समझें और साप्ताहिक पैटर्न सेव करें","ℹ️ NDA की गणना कैसे होती है","नाइट ड्यूटी एंट्री","＋  दूसरी तारीख जोड़ें","💾  यह महीना सेव करें","✓  NDA यूनिट गणना करें","📄  PDF बनाएं और सेव करें","↗  सेव किया PDF शेयर करें","🗂  मासिक इतिहास","वर्तमान महीना साफ करें","ⓘ  ABOUT / VERSION 1.2.8","📋  पिछले महीने का पैटर्न कॉपी करें","🛠 महीना और डेटा टूल्स","🔒  महीना लॉक करें","🔓  महीना अनलॉक करें","⚠️  त्रुटियां जांचें","⬆ बैकअप","⬇ रिस्टोर","UI भाषा","🌙 डार्क मोड","☀ लाइट मोड","समय स्पेल","＋ इस तारीख में और समय जोड़ें","Remarks (वैकल्पिक)"};
            for(int i=0;i<a.length;i++)if(s.equals(a[i]))return b[i];
        }
        return s;
    }

    void applyThemeColors(){
        if(darkMode){ BG=Color.rgb(18,24,31); CARD=Color.rgb(31,40,50); TEXT=Color.rgb(235,240,245); BORDER=Color.rgb(65,78,92); }
        else { BG=Color.rgb(244,247,251); CARD=Color.WHITE; TEXT=Color.rgb(35,45,55); BORDER=Color.rgb(225,231,239); }
    }

    String deviceId(){
        String id=Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        return id==null||id.trim().isEmpty()?"UNKNOWN-DEVICE":id.trim().toUpperCase(Locale.ROOT);
    }

    String licenseHash(String id,String expiry){
        long h1=2166136261L, h2=2166136261L;
        String a=LICENSE_SECRET+"|"+id+"|"+expiry;
        String b=expiry+"|"+id+"|"+LICENSE_SECRET;
        for(int i=0;i<a.length();i++){h1^=a.charAt(i);h1=(h1*16777619L)&0xFFFFFFFFL;}
        for(int i=0;i<b.length();i++){h2^=b.charAt(i);h2=(h2*16777619L)&0xFFFFFFFFL;}
        return String.format(Locale.US,"%08X%08X",h1,h2);
    }

    String licenseKeyForDevice(String id,String expiry){
        String raw=expiry+licenseHash(id,expiry);
        return raw.substring(0,4)+"-"+raw.substring(4,8)+"-"+raw.substring(8,12)+"-"+raw.substring(12,16)+"-"+raw.substring(16,20);
    }

    String normalizeLicense(String k){
        if(k==null)return "";
        return k.replaceAll("[^A-Za-z0-9]","").toUpperCase(Locale.ROOT);
    }

    String licenseExpiry(String key){
        String n=normalizeLicense(key);
        if(n.length()!=20)return "";
        return n.substring(0,8);
    }

    boolean validDate(String ymd){
        try{ new SimpleDateFormat("yyyyMMdd",Locale.US).parse(ymd); return ymd.length()==8; }catch(Exception e){return false;}
    }

    String todayYmd(){ return new SimpleDateFormat("yyyyMMdd",Locale.US).format(new Date()); }

    boolean licenseMatches(String entered,String id){
        String n=normalizeLicense(entered);
        if(n.length()!=20)return false;
        String expiry=n.substring(0,8);
        if(!validDate(expiry))return false;
        String expected=normalizeLicense(licenseKeyForDevice(id,expiry));
        return n.equals(expected);
    }

    boolean isLicenseExpired(){
        String key=sp.getString("license_key","");
        if(!licenseMatches(key,deviceId()))return true;
        String expiry=licenseExpiry(key);
        if("99991231".equals(expiry))return false;
        try{
            Date exp=new SimpleDateFormat("yyyyMMdd",Locale.US).parse(expiry);
            Date today=new SimpleDateFormat("yyyyMMdd",Locale.US).parse(todayYmd());
            return today.after(exp);
        }catch(Exception e){return true;}
    }

    boolean isActivated(){
        if(!sp.getBoolean("license_activated",false))return false;
        String saved=sp.getString("license_device_id","");
        String key=sp.getString("license_key","");
        if(!saved.equals(deviceId()) || !licenseMatches(key,deviceId()))return false;
        String today=todayYmd();
        String last=sp.getString("license_last_date","");
        if(last.compareTo(today)>0)return false; // basic clock rollback protection
        if(isLicenseExpired())return false;
        sp.edit().putString("license_last_date",today).apply();
        return true;
    }

    void showActivation(){
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        LinearLayout page=new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER_HORIZONTAL);
        page.setPadding(dp(22),dp(28),dp(22),dp(24));
        page.setBackgroundColor(BG);

        TextView title=tv("IR NDA APP",28,true); title.setTextColor(NAVY); title.setGravity(Gravity.CENTER); page.addView(title,new LinearLayout.LayoutParams(-1,-2));
        TextView sub=tv("LICENSE ACTIVATION",16,true); sub.setTextColor(BLUE); sub.setGravity(Gravity.CENTER); page.addView(sub,new LinearLayout.LayoutParams(-1,-2));
        TextView info=tv("This app requires a one-time offline activation.\nNo internet or account is required.",13,false); info.setGravity(Gravity.CENTER); info.setPadding(0,dp(14),0,dp(16)); page.addView(info,new LinearLayout.LayoutParams(-1,-2));

        LinearLayout c=card();
        c.addView(sectionTitle("YOUR DEVICE ID"));
        TextView did=tv(deviceId(),14,true); did.setTextColor(NAVY); did.setGravity(Gravity.CENTER); did.setPadding(dp(10),dp(12),dp(10),dp(12)); did.setBackground(bg(Color.rgb(239,247,255),12)); c.addView(did);
        Button copy=btn("📋  COPY DEVICE ID"); copy.setOnClickListener(v->{((android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(android.content.ClipData.newPlainText("IR NDA Device ID",deviceId()));toast("Device ID copied. Send it to the developer.");}); c.addView(copy);
        c.addView(tv("Send this Device ID to the developer. You will receive a license key for this phone.",12,false));
        page.addView(c);

        LinearLayout lc=card(); lc.addView(sectionTitle("ENTER LICENSE KEY"));
        EditText key=input("XXXX-XXXX-XXXX-XXXX-XXXX",""); key.setInputType(android.text.InputType.TYPE_CLASS_TEXT|android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS); key.setGravity(Gravity.CENTER); key.setTextSize(18); lc.addView(key);
        Button activate=btn("🔐  ACTIVATE APP"); activate.setTextColor(WHITE); activate.setBackgroundColor(GREEN);
        activate.setOnClickListener(v->{
            String entered=normalizeLicense(key.getText().toString());
            if(!licenseMatches(entered,deviceId())){
                toast("Invalid license key for this device.");
                return;
            }
            String expiry=licenseExpiry(entered);
            if(!"99991231".equals(expiry)){
                try{
                    Date exp=new SimpleDateFormat("yyyyMMdd",Locale.US).parse(expiry);
                    Date today=new SimpleDateFormat("yyyyMMdd",Locale.US).parse(todayYmd());
                    if(today.after(exp)){ toast("This license has expired."); return; }
                }catch(Exception e){ toast("Invalid license key."); return; }
            }
            sp.edit().putBoolean("license_activated",true).putString("license_device_id",deviceId()).putString("license_key",entered).putString("license_last_date",todayYmd()).apply();
            Toast.makeText(this,"Activation successful.",Toast.LENGTH_LONG).show();
            showSplash();
        });
        lc.addView(activate);
        page.addView(lc);

        TextView foot=tv("© PRATIK MUKHERJEE (SIM/ASN/ER), 2026\nOffline license system • Version 1.2.8",11,false); foot.setGravity(Gravity.CENTER); foot.setTextColor(Color.GRAY); foot.setPadding(0,dp(18),0,0); page.addView(foot);
        ScrollView sv=new ScrollView(this); sv.addView(page); setContentView(sv);
    }

    void showSplash(){
        LinearLayout splash=new LinearLayout(this);
        splash.setOrientation(LinearLayout.VERTICAL);
        splash.setGravity(Gravity.CENTER);
        splash.setBackgroundColor(NAVY);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        if(Build.VERSION.SDK_INT>=19) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        ImageView logo=new ImageView(this);
        logo.setImageResource(com.pratik.irndaapp.R.drawable.ic_launcher);
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        splash.addView(logo,new LinearLayout.LayoutParams(dp(280),dp(280)));
        TextView title=tv("IR NDA APP",25,true); title.setTextColor(WHITE); title.setGravity(Gravity.CENTER); splash.addView(title);
        TextView by=tv("প্রতীক",18,true); by.setTextColor(Color.rgb(240,210,110)); by.setGravity(Gravity.CENTER); splash.addView(by);
        TextView copyright=tv("© PRATIK MUKHERJEE (SIM/ASN/ER), 2026",11,false); copyright.setTextColor(Color.LTGRAY); copyright.setGravity(Gravity.CENTER); copyright.setPadding(0,dp(14),0,0); splash.addView(copyright);
        setContentView(splash);
        handler.postDelayed(()->{
            if(Build.VERSION.SDK_INT>=19) getWindow().getDecorView().setSystemUiVisibility(0);
            startActivity(new Intent(this, SntDashboardActivity.class)); finish();
        },2000);
    }

    void build(){
        ScrollView sv=new ScrollView(this); sv.setFillViewport(true); root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(BG); sv.addView(root); setContentView(sv);

        LinearLayout header=new LinearLayout(this); header.setOrientation(LinearLayout.VERTICAL); header.setGravity(Gravity.CENTER); header.setPadding(dp(14),dp(16),dp(14),dp(16)); header.setBackgroundColor(NAVY);
        TextView h=tv("IR NDA APP",24,true); h.setTextColor(WHITE); h.setGravity(Gravity.CENTER); header.addView(h);
        TextView h2=tv("প্রতীক  •  NIGHT DUTY ALLOWANCE",12,true); h2.setTextColor(Color.rgb(240,210,110)); h2.setGravity(Gravity.CENTER); header.addView(h2); root.addView(header);

        TextView sub=tv("Offline • No login • Your entries stay on this phone",12,false); sub.setGravity(Gravity.CENTER); sub.setPadding(5,dp(8),5,dp(8)); root.addView(sub);

        LinearLayout dash=card(); dash.addView(sectionTitle("📊 MONTHLY DASHBOARD"));
        TextView dashText=tv("Loading month summary…",13,false); dashboardText=dashText; dashText.setPadding(dp(12),dp(10),dp(12),dp(10)); dashText.setBackground(bg(darkMode?Color.rgb(35,48,62):Color.rgb(239,247,255),14)); dash.addView(dashText);
        Button refreshDash=btn("↻ Refresh Dashboard"); refreshDash.setOnClickListener(v->updateDashboard(dashText)); dash.addView(refreshDash); root.addView(dash);

        profileBox=card(); profileBox.addView(sectionTitle("EMPLOYEE PROFILE"));
        TextView pt=tv("First use: fill these manually. The app does not pre-fill personal details.",12,false); pt.setTextColor(Color.DKGRAY); profileBox.addView(pt);
        name=input("Name",sp.getString("name","")); designation=input("Designation",sp.getString("desig","")); pf=input("P.F. A/C No.",sp.getString("pf","")); level=input("PC 7 Level",sp.getString("level","")); basic=input("Basic Pay (₹)",sp.getString("basic","")); da=input("DA (%)",sp.getString("da",""));
        profileBox.addView(name); profileBox.addView(designation); profileBox.addView(pf); profileBox.addView(level); profileBox.addView(basic); profileBox.addView(da);
        Button saveProfile=btn("💾  Save Profile"); saveProfile.setTextColor(WHITE); saveProfile.setBackground(bg(BLUE,14)); saveProfile.setOnClickListener(v->{saveProfile();updateRatePreview();toast("Profile saved on this phone.");}); profileBox.addView(saveProfile); root.addView(profileBox);

        LinearLayout rateCard=card(); rateCard.addView(sectionTitle("💰 NDA RATE PREVIEW"));
        ratePreview=tv("Enter Basic Pay and DA % above to see the exact NDA rate.",14,false); ratePreview.setPadding(dp(12),dp(10),dp(12),dp(10)); ratePreview.setBackground(bg(Color.rgb(239,247,255),14)); rateCard.addView(ratePreview);
        rateCard.addView(tv("Formula: DA amount = Basic Pay × DA%;  NDA rate = (Basic Pay + DA amount) ÷ 200.  1 NDA unit = 6 eligible night-duty hours.",11,false));
        TextWatcher rateWatcher=new SimpleWatcher(){ public void afterTextChanged(Editable e){ updateRatePreview(); } }; basic.addTextChangedListener(rateWatcher); da.addTextChangedListener(rateWatcher);
        root.addView(rateCard); updateRatePreview();

        LinearLayout monthCard=card(); monthCard.addView(sectionTitle("MONTH"));
        LinearLayout mr=new LinearLayout(this); mr.setGravity(Gravity.CENTER_VERTICAL); String initialMonth=sp.getString("last_month",new SimpleDateFormat("MMMM, yyyy",Locale.ENGLISH).format(new Date())); month=input("Month & Year",initialMonth); month.setFocusable(false); month.setClickable(true); month.setInputType(android.text.InputType.TYPE_NULL); month.setOnClickListener(v->pickMonth()); mr.addView(month,new LinearLayout.LayoutParams(0,-2,1)); Button load=btn("LOAD"); load.setOnClickListener(v->switchMonth()); mr.addView(load,new LinearLayout.LayoutParams(dp(86),-2)); monthCard.addView(mr);
        Button copyPrev=btn("📋  COPY PREVIOUS MONTH PATTERN"); copyPrev.setOnClickListener(v->copyPreviousMonth()); monthCard.addView(copyPrev);
        root.addView(monthCard);

        LinearLayout weekly=card(); weekly.addView(sectionTitle("AUTOMATIC WEEKLY NIGHT DUTY"));
        weekly.addView(tv("Set your regular weekly duty once. The app will fill the selected month automatically. Extra duties can still be added manually below.",12,false));
        LinearLayout wr1=new LinearLayout(this); wr1.setGravity(Gravity.CENTER_VERTICAL);
        wr1.addView(tv("Every week starts:",13,true),new LinearLayout.LayoutParams(dp(125),-2));
        Spinner startDaySpinner=new Spinner(this);
        String[] dayNames={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        ArrayAdapter<String> dayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dayNames);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); startDaySpinner.setAdapter(dayAdapter);
        int savedDow=sp.getInt("weekly_start_dow",Calendar.THURSDAY); startDaySpinner.setSelection(savedDow-1); wr1.addView(startDaySpinner,new LinearLayout.LayoutParams(0,-2,1)); weekly.addView(wr1);
        LinearLayout wr2=new LinearLayout(this); wr2.setGravity(Gravity.CENTER_VERTICAL);
        EditText ws=input("Start time","22:00"); ws.setFocusable(false); ws.setClickable(true); ws.setInputType(android.text.InputType.TYPE_NULL);
        EditText we=input("End time","06:00"); we.setFocusable(false); we.setClickable(true); we.setInputType(android.text.InputType.TYPE_NULL);
        wr2.addView(tv("Start:",13,true),new LinearLayout.LayoutParams(dp(55),-2)); wr2.addView(ws,new LinearLayout.LayoutParams(0,-2,1));
        wr2.addView(tv("  End:",13,true),new LinearLayout.LayoutParams(dp(55),-2)); wr2.addView(we,new LinearLayout.LayoutParams(0,-2,1)); weekly.addView(wr2);
        LinearLayout wr3=new LinearLayout(this); wr3.setGravity(Gravity.CENTER_VERTICAL);
        wr3.addView(tv("Ends after:",13,true),new LinearLayout.LayoutParams(dp(90),-2));
        Spinner endOffset=new Spinner(this); String[] offsets={"same day","+1 day","+2 days","+3 days"};
        ArrayAdapter<String> offAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,offsets); offAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); endOffset.setAdapter(offAdapter);
        int savedOffset=sp.getInt("weekly_end_offset",2); endOffset.setSelection(savedOffset); wr3.addView(endOffset,new LinearLayout.LayoutParams(0,-2,1)); weekly.addView(wr3);
        Button applyWeekly=btn("⚡  SAVE & FILL THIS MONTH"); applyWeekly.setTextColor(WHITE); applyWeekly.setBackgroundColor(GREEN);
        applyWeekly.setOnClickListener(v->{
            int dow=startDaySpinner.getSelectedItemPosition()+1; int off=endOffset.getSelectedItemPosition(); String st=ws.getText().toString(); String et=we.getText().toString();
            if(mins(st)<0||mins(et)<0){toast("Please choose valid start/end times.");return;}
            sp.edit().putBoolean("weekly_enabled",true).putInt("weekly_start_dow",dow).putString("weekly_start_time",st).putString("weekly_end_time",et).putInt("weekly_end_offset",off).apply();
            applyWeeklyPattern(true); toast("Weekly duty pattern saved and this month filled automatically.");
        }); weekly.addView(applyWeekly);
        Button disableWeekly=btn("Turn OFF automatic weekly fill"); disableWeekly.setOnClickListener(v->{sp.edit().putBoolean("weekly_enabled",false).apply();toast("Automatic weekly fill turned off. Existing entries remain.");}); weekly.addView(disableWeekly);
        root.addView(weekly);

        LinearLayout ai=card();
        ai.addView(sectionTitle("🤖 AI DUTY ASSISTANT"));
        ai.addView(tv("Speak or type naturally. The assistant can understand common English, বাংলা and हिन्दी duty phrases and turn them into dates/times or a weekly pattern. Voice recognition languages depend on the speech service installed on the phone.",12,false));
        LinearLayout air=new LinearLayout(this); air.setGravity(Gravity.CENTER_VERTICAL);
        aiLanguage=new Spinner(this);
        String[] aiLangs={"Auto / Device language","বাংলা (India)","हिन्दी (India)","English (India)","অসমীয়া (India)","ગુજરાતી (India)","ಕನ್ನಡ (India)","മലയാളം (India)","मराठी (India)","ଓଡ଼ିଆ (India)","ਪੰਜਾਬੀ (India)","தமிழ் (India)","తెలుగు (India)","اردو (India)"};
        ArrayAdapter<String> aiAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,aiLangs); aiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); aiLanguage.setAdapter(aiAdapter);
        air.addView(aiLanguage,new LinearLayout.LayoutParams(0,-2,1));
        Button speak=btn("🎤 Speak"); speak.setOnClickListener(v->startVoiceInput()); air.addView(speak,new LinearLayout.LayoutParams(dp(110),-2));
        ai.addView(air);
        aiCommand=input("Example: Thursday night 10 PM to Saturday 6 AM",""); aiCommand.setSingleLine(false); aiCommand.setMinLines(2); aiCommand.setMaxLines(4); ai.addView(aiCommand);
        Button aiApply=btn("✨  UNDERSTAND & ADD DUTY"); aiApply.setTextColor(WHITE); aiApply.setBackgroundColor(GREEN); aiApply.setOnClickListener(v->understandAiText(aiCommand.getText().toString())); ai.addView(aiApply);
        Button aiWeekly=btn("🔁  UNDERSTAND & SAVE WEEKLY PATTERN"); aiWeekly.setOnClickListener(v->understandWeeklyText(aiCommand.getText().toString())); ai.addView(aiWeekly);
        root.addView(ai);

        LinearLayout manage=card(); manage.addView(sectionTitle("🛠 MONTH & DATA TOOLS"));
        LinearLayout mrow=new LinearLayout(this);
        Button lockBtn=btn(monthLocked()?"🔓  UNLOCK MONTH":"🔒  LOCK MONTH"); lockBtn.setOnClickListener(v->{ if(monthLocked()){sp.edit().remove("locked_"+monthKey()).apply();toast("Month unlocked.");}else{saveCurrentMonth();sp.edit().putBoolean("locked_"+monthKey(),true).apply();toast("Month locked. Unlock it to edit.");} build(); }); mrow.addView(lockBtn,new LinearLayout.LayoutParams(0,-2,1));
        Button checkBtn=btn("⚠️  CHECK ERRORS"); checkBtn.setOnClickListener(v->showValidation()); mrow.addView(checkBtn,new LinearLayout.LayoutParams(0,-2,1)); manage.addView(mrow);
        LinearLayout brow=new LinearLayout(this);
        Button backup=btn("⬆ Backup"); backup.setOnClickListener(v->backupData()); brow.addView(backup,new LinearLayout.LayoutParams(0,-2,1));
        Button restore=btn("⬇ Restore"); restore.setOnClickListener(v->restoreData()); brow.addView(restore,new LinearLayout.LayoutParams(0,-2,1));
        Button dark=btn(darkMode?"☀ Light Mode":"🌙 Dark Mode"); dark.setOnClickListener(v->{darkMode=!darkMode;sp.edit().putBoolean("dark_mode",darkMode).apply();applyThemeColors();build();}); brow.addView(dark,new LinearLayout.LayoutParams(0,-2,1)); manage.addView(brow);
        LinearLayout lrow=new LinearLayout(this); lrow.addView(tv("UI Language",13,true),new LinearLayout.LayoutParams(dp(95),-2)); Spinner uiLang=new Spinner(this); String[] uiLangs={"English","বাংলা","हिन्दी"}; ArrayAdapter<String> uiAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,uiLangs); uiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); uiLang.setAdapter(uiAdapter); int lp=sp.getInt("ui_lang",0); uiLang.setSelection(lp); uiLang.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> p){} public void onItemSelected(android.widget.AdapterView<?> p,View v,int pos,long id){if(pos!=sp.getInt("ui_lang",0)){sp.edit().putInt("ui_lang",pos).apply();build();}}}); lrow.addView(uiLang,new LinearLayout.LayoutParams(0,-2,1)); manage.addView(lrow);
        Button about=btn("ⓘ  ABOUT / VERSION 1.2.8"); about.setOnClickListener(v->showAbout()); manage.addView(about); root.addView(manage);

        LinearLayout info=card(); info.addView(sectionTitle("ℹ️ HOW NDA IS CALCULATED")); info.addView(tv("Only duty between 22:00 and 06:00 counts. Railway's 10-minute weightage per eligible hour means 6 eligible night hours = 1 NDA unit.\n\nAmount = [(Basic Pay + (Basic Pay × DA%)) ÷ 200] × NDA Units. The PDF prints NDA units only; amount is kept in monthly history.",13,false)); root.addView(info);

        LinearLayout duty=card(); duty.addView(sectionTitle("NIGHT DUTY ENTRIES"));
        duty.addView(tv("Use one date card for each date. Add multiple time spells inside the same date card. Example: 04/09 → 00:00–06:00 + 22:00–24:00 stays on ONE row.",12,false));
        datesBox=new LinearLayout(this); datesBox.setOrientation(LinearLayout.VERTICAL); duty.addView(datesBox);
        Button addDate=btn("＋  ADD ANOTHER DATE"); addDate.setOnClickListener(v->addDateCard("",true)); addDate.setTextColor(NAVY); duty.addView(addDate); root.addView(duty);

        Button saveMonth=btn("💾  SAVE THIS MONTH"); saveMonth.setOnClickListener(v->{saveCurrentMonth();toast("This month's duty entries have been saved.");}); root.addView(saveMonth);
        Button calc=btn("✓  CALCULATE NDA UNITS"); calc.setTextColor(WHITE); calc.setBackground(bg(GREEN,14)); calc.setOnClickListener(v->showSummary()); root.addView(calc);
        Button pdf=btn("📄  GENERATE & SAVE PDF"); pdf.setTextColor(WHITE); pdf.setBackgroundColor(BLUE); pdf.setOnClickListener(v->generatePdf()); root.addView(pdf);
        Button share=btn("↗  SHARE SAVED PDF"); share.setOnClickListener(v->shareLastPdf()); root.addView(share);
        Button history=btn("🗂  MONTHLY HISTORY"); history.setTextColor(NAVY); history.setBackground(bg(Color.rgb(231,240,250),14)); history.setOnClickListener(v->showHistory()); root.addView(history);
        Button clear=btn("CLEAR CURRENT MONTH"); clear.setTextColor(RED); clear.setBackground(bg(Color.rgb(255,244,244),14)); clear.setOnClickListener(v->{ if(monthLocked()){toast("Unlock this month before clearing.");return;} new AlertDialog.Builder(this).setTitle("Clear this month?").setMessage("All unsaved and saved entries for "+month.getText().toString()+" will be removed from this phone.").setNegativeButton("CANCEL",null).setPositiveButton("CLEAR",(d,w)->{deleteMonth(month.getText().toString()); dates.clear(); renderDates(); addDateCard("",true);}).show(); }); root.addView(clear);
        TextView foot=tv("PDF: A4 landscape • 24 dates per page • units only • amount stays in history",12,false); foot.setGravity(Gravity.CENTER); foot.setPadding(5,dp(10),5,dp(4)); root.addView(foot);
        TextView copyrightFoot=tv("© PRATIK MUKHERJEE (SIM/ASN/ER), 2026",11,false); copyrightFoot.setTextColor(Color.GRAY); copyrightFoot.setGravity(Gravity.CENTER); copyrightFoot.setPadding(5,0,5,dp(14)); root.addView(copyrightFoot);

        loadMonth(month.getText().toString());
        updateDashboard(dashText);
    }

    void startVoiceInput(){
        if(!SpeechRecognizer.isRecognitionAvailable(this)){toast("Voice recognition is not available on this phone. Please install/enable a speech recognition service.");return;}
        if(speechRecognizer!=null){try{speechRecognizer.destroy();}catch(Exception ignored){}}
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new android.speech.RecognitionListener(){
            public void onReadyForSpeech(Bundle b){}
            public void onBeginningOfSpeech(){}
            public void onRmsChanged(float r){}
            public void onBufferReceived(byte[] b){}
            public void onEndOfSpeech(){}
            public void onError(int e){toast("Voice recognition could not understand that. Please try again.");}
            public void onResults(Bundle b){ArrayList<String> r=b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION); if(r!=null&&!r.isEmpty()){aiCommand.setText(r.get(0)); toast("Voice captured. Now tap UNDERSTAND & ADD DUTY.");}}
            public void onPartialResults(Bundle b){}
            public void onEvent(int t,Bundle b){}
        });
        Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        String locale=selectedAiLocale(); if(locale!=null)i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,locale);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,locale==null?Locale.getDefault().toLanguageTag():locale);
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak your night-duty details");
        speechRecognizer.startListening(i);
    }

    String selectedAiLocale(){
        if(aiLanguage==null)return null; int p=aiLanguage.getSelectedItemPosition();
        switch(p){
            case 1:return "bn-IN"; case 2:return "hi-IN"; case 3:return "en-IN"; case 4:return "as-IN";
            case 5:return "gu-IN"; case 6:return "kn-IN"; case 7:return "ml-IN"; case 8:return "mr-IN";
            case 9:return "or-IN"; case 10:return "pa-IN"; case 11:return "ta-IN"; case 12:return "te-IN"; case 13:return "ur-IN";
            default:return Locale.getDefault().toLanguageTag();
        }
    }

    String normalizeAi(String x){
        if(x==null)return ""; String s=x.toLowerCase(Locale.ROOT);
        String src="০১২৩৪৫৬৭৮৯१२३४५६७८९"; String dst="01234567890123456789";
        StringBuilder b=new StringBuilder(); for(int i=0;i<s.length();i++){char c=s.charAt(i);int k=src.indexOf(c);b.append(k>=0?dst.charAt(k):c);} return b.toString();
    }

    int aiHour(String text,int fallback){
        java.util.regex.Matcher m=java.util.regex.Pattern.compile("(?<!\\d)([0-9]{1,2})(?:\\s*[:.](\\d{1,2}))?").matcher(text);
        if(!m.find())return fallback; int h=Integer.parseInt(m.group(1)); int mm=m.group(2)==null?0:Integer.parseInt(m.group(2)); if(h>24||mm>59)return fallback;
        String around=text.substring(Math.max(0,m.start()-12),Math.min(text.length(),m.end()+12));
        if((around.contains("pm")||around.contains("p.m")||around.contains("রাত")||around.contains("রাত্র")||around.contains("রাতে")||around.contains("রাতের")||around.contains("रात")||around.contains("रात्रि")||around.contains("रात में"))&&h<12)h+=12;
        if((around.contains("am")||around.contains("a.m")||around.contains("সকাল")||around.contains("ভোর")||around.contains("सुबह")||around.contains("प्रातः"))&&h==12)h=0;
        return h*60+mm;
    }

    ArrayList<Integer> findAiTimes(String text){
        ArrayList<Integer> out=new ArrayList<>(); java.util.regex.Matcher m=java.util.regex.Pattern.compile("(?<!\\d)([0-9]{1,2})(?:\\s*[:.](\\d{1,2}))?\\s*(am|pm|a\\.m\\.|p\\.m\\.|টা|টায়|बजे|घंटे)?").matcher(text);
        while(m.find()){int h=Integer.parseInt(m.group(1));int mm=m.group(2)==null?0:Integer.parseInt(m.group(2));if(h>24||mm>59)continue;String token=(m.group(3)==null?"":m.group(3).toLowerCase(Locale.ROOT));String around=text.substring(Math.max(0,m.start()-10),Math.min(text.length(),m.end()+10));if((token.startsWith("p")||around.contains("রাত")||around.contains("রাত্র")||around.contains("रात")||around.contains("रात्रि"))&&h<12)h+=12;if((token.startsWith("a")||around.contains("সকাল")||around.contains("ভোর")||around.contains("सुबह"))&&h==12)h=0;out.add(h*60+mm);}return out;
    }

    int aiDay(String text){
        String[] keys={"sunday|রবিবার|रविवार|asom|deuta", "monday|সোমবার|सोमवार", "tuesday|মঙ্গলবার|मंगलवार", "wednesday|বুধবার|बुधवार", "thursday|বৃহস্পতিবার|বৃহস্পতি|गुरुवार|गुरूवार", "friday|শুক্রবার|शुक्रवार", "saturday|শনিবার|शनिवार"};
        for(int i=0;i<keys.length;i++){for(String k:keys[i].split("\\|"))if(text.contains(k))return i+1;} return -1;
    }

    int dayOffsetFromTo(String text){
        int a=aiDay(text); if(a<0)return 0; int b=-1; String[] parts=text.split("(?:to|until|through|থেকে|পর্যন্ত|से|तक)"); if(parts.length>1)b=aiDay(parts[parts.length-1]); if(b<0)return 0; return (b-a+7)%7;
    }

    void understandWeeklyText(String raw){
        String s=normalizeAi(raw); int dow=aiDay(s); ArrayList<Integer> times=findAiTimes(s); if(dow<0||times.size()<2){toast("I could not detect the weekly day and two times. Example: Thursday night 10 PM to Saturday 6 AM.");return;}
        int start=times.get(0), end=times.get(1); int off=dayOffsetFromTo(s); if(off==0&&end<=start)off=2; if(off>3)off=3;
        sp.edit().putBoolean("weekly_enabled",true).putInt("weekly_start_dow",dow).putString("weekly_start_time",String.format(Locale.ENGLISH,"%02d:%02d",start/60,start%60)).putString("weekly_end_time",String.format(Locale.ENGLISH,"%02d:%02d",end/60,end%60)).putInt("weekly_end_offset",off).apply();
        applyWeeklyPattern(true); toast("AI understood the weekly pattern and filled this month.");
    }

    void understandAiText(String raw){
        String s=normalizeAi(raw); int dow=aiDay(s); ArrayList<Integer> times=findAiTimes(s); if(times.size()<2){toast("Please say/type a date or weekday with From and To times.");return;}
        Calendar c=Calendar.getInstance(); c.set(Calendar.DAY_OF_MONTH,1); int targetDow=dow<0?c.get(Calendar.DAY_OF_WEEK):dow; int diff=(targetDow-c.get(Calendar.DAY_OF_WEEK)+7)%7; if(dow<0){diff=0;}
        c.add(Calendar.DAY_OF_MONTH,diff); String date=new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).format(c.getTime()); DutyDate d=findDate(date); if(d==null){d=new DutyDate();d.date=date;dates.add(d);} String f=String.format(Locale.ENGLISH,"%02d:%02d",times.get(0)/60,times.get(0)%60);String t=String.format(Locale.ENGLISH,"%02d:%02d",times.get(1)/60,times.get(1)%60);addSpellIfMissing(d,f,t);renderDates();saveCurrentMonthSilently();toast("AI-assisted duty entry added for "+date+".");
    }

    void saveProfile(){ sp.edit().putString("name",name.getText().toString().trim()).putString("desig",designation.getText().toString().trim()).putString("pf",pf.getText().toString().trim()).putString("level",level.getText().toString().trim()).putString("basic",basic.getText().toString().trim()).putString("da",da.getText().toString().trim()).apply(); }

    void pickMonth(){
        Calendar c=Calendar.getInstance();
        try{ Date d=new SimpleDateFormat("MMMM, yyyy",Locale.ENGLISH).parse(month.getText().toString().trim()); if(d!=null)c.setTime(d); }catch(Exception ignored){}
        DatePickerDialog dlg=new DatePickerDialog(this,(v,y,m,d)->{ Calendar x=Calendar.getInstance(); x.set(y,m,1); month.setText(new SimpleDateFormat("MMMM, yyyy",Locale.ENGLISH).format(x.getTime())); sp.edit().putString("last_month",month.getText().toString()).apply(); switchMonth(); },c.get(Calendar.YEAR),c.get(Calendar.MONTH),1); dlg.show();
    }

    String monthKey(){ return month.getText().toString().trim().toUpperCase(Locale.ENGLISH); }
    void switchMonth(){ sp.edit().putString("last_month",month.getText().toString()).apply(); loadMonth(monthKey()); if(dashboardText!=null)updateDashboard(dashboardText); }

    void loadMonth(String key){
        loading=true; dates.clear(); String raw=sp.getString("month_data_"+key,"");
        if(!raw.isEmpty()){
            try{
                JSONArray arr=new JSONArray(raw);
                for(int i=0;i<arr.length();i++){
                    JSONObject o=arr.getJSONObject(i); DutyDate d=new DutyDate(); d.date=o.optString("date",""); d.remarks=o.optString("remarks",""); JSONArray ss=o.optJSONArray("spells"); if(ss!=null)for(int j=0;j<ss.length();j++){JSONObject q=ss.getJSONObject(j); d.spells.add(new Spell(q.optString("from",""),q.optString("to","")));} dates.add(d);
                }
            }catch(Exception ignored){}
        }
        if(dates.isEmpty()){
            if(sp.getBoolean("weekly_enabled",false)){
                // Start from an empty list; the weekly generator will fill the month.
            }else{
                DutyDate d=new DutyDate(); d.spells.add(new Spell("22:00","06:00")); dates.add(d);
            }
        }
        if(sp.getBoolean("weekly_enabled",false)) applyWeeklyPattern(false);
        renderDates();
        loading=false;
        if(dashboardText!=null)updateDashboard(dashboardText);
    }

    void saveCurrentMonthSilently(){ if(loading)return; saveMonthData(monthKey()); }
    void saveCurrentMonth(){ if(monthLocked()){toast("Month is locked. Unlock it before saving changes.");return;} saveMonthData(monthKey()); saveProfile(); }
    void saveMonthData(String key){
        if(key==null||key.isEmpty())return; JSONArray arr=new JSONArray();
        try{ for(DutyDate d:dates){ JSONObject o=new JSONObject(); o.put("date",d.date); o.put("remarks",d.remarks); JSONArray ss=new JSONArray(); for(Spell s:d.spells){JSONObject q=new JSONObject();q.put("from",s.from);q.put("to",s.to);ss.put(q);} o.put("spells",ss); arr.put(o); } sp.edit().putString("month_data_"+key,arr.toString()).apply(); }catch(Exception ignored){}
    }
    void deleteMonth(String key){ sp.edit().remove("month_data_"+key).remove("history_units_"+key).remove("history_amount_"+key).remove("history_night_"+key).apply(); }

    void applyWeeklyPattern(boolean currentMonthOnly){
        if(monthLocked()){toast("Unlock this month before editing.");return;}
        int startDow=sp.getInt("weekly_start_dow",Calendar.THURSDAY);
        String startTime=sp.getString("weekly_start_time","22:00");
        String endTime=sp.getString("weekly_end_time","06:00");
        int endOffset=sp.getInt("weekly_end_offset",2);
        Calendar first=Calendar.getInstance();
        try{ Date md=new SimpleDateFormat("MMMM, yyyy",Locale.ENGLISH).parse(month.getText().toString()); if(md!=null)first.setTime(md); }catch(Exception ignored){}
        first.set(Calendar.DAY_OF_MONTH,1); first.set(Calendar.HOUR_OF_DAY,0); first.set(Calendar.MINUTE,0); first.set(Calendar.SECOND,0); first.set(Calendar.MILLISECOND,0);
        Calendar cursor=(Calendar)first.clone();
        int diff=(startDow-cursor.get(Calendar.DAY_OF_WEEK)+7)%7;
        cursor.add(Calendar.DAY_OF_MONTH,diff);
        // Include the previous week's occurrence so a duty that starts in the
        // previous month but continues into this month is also filled.
        cursor.add(Calendar.DAY_OF_MONTH,-7);
        Calendar monthEnd=(Calendar)first.clone(); monthEnd.add(Calendar.MONTH,1);
        while(cursor.before(monthEnd)){
            Calendar occurrenceEnd=(Calendar)cursor.clone(); occurrenceEnd.add(Calendar.DAY_OF_MONTH,endOffset);
            if(!occurrenceEnd.before(first)) addWeeklyOccurrence(cursor,startTime,endTime,endOffset,first,monthEnd);
            cursor.add(Calendar.DAY_OF_MONTH,7);
        }
        if(currentMonthOnly){ renderDates(); saveCurrentMonthSilently(); }
    }

    void addWeeklyOccurrence(Calendar start,String startTime,String endTime,int endOffset,Calendar monthStart,Calendar monthEnd){
        int sm=mins(startTime), em=mins(endTime); if(sm<0||em<0)return;
        Calendar end=(Calendar)start.clone(); end.add(Calendar.DAY_OF_MONTH,endOffset);
        // Add the portions date-by-date, preserving any manually entered extra duties.
        Calendar d=(Calendar)start.clone();
        while(!d.after(end)){
            if(!d.before(monthStart) && d.before(monthEnd)){
                String ds=new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).format(d.getTime());
                int from,to;
                if(sameDate(d,start)) { from=sm; to= endOffset==0 ? em : 1440; }
                else if(sameDate(d,end)) { from=0; to=em; }
                else { from=0; to=1440; }
                // Only retain night-duty portions; for the user's normal pattern this gives
                // Thu 22:00-24:00, Fri 00:00-06:00 + 22:00-24:00, Sat 00:00-06:00.
                ArrayList<int[]> parts=nightPartsForDate(d,start,end,sm,em,endOffset);
                if(!parts.isEmpty()){
                    DutyDate target=findDate(ds); if(target==null){target=new DutyDate();target.date=ds;dates.add(target);}
                    for(int[] part:parts){ String f=String.format(Locale.ENGLISH,"%02d:%02d",part[0]/60,part[0]%60); String t=part[1]==1440?"24:00":String.format(Locale.ENGLISH,"%02d:%02d",part[1]/60,part[1]%60); addSpellIfMissing(target,f,t); }
                }
            }
            d.add(Calendar.DAY_OF_MONTH,1);
        }
    }

    ArrayList<int[]> nightPartsForDate(Calendar date,Calendar start,Calendar end,int sm,int em,int endOffset){
        ArrayList<int[]> out=new ArrayList<>();
        // The recurring occurrence is continuous from startTime to endTime on endOffset days.
        if(sameDate(date,start)){
            int e=(endOffset==0)?em:1440; if(sm<1440 && e>1320)out.add(new int[]{Math.max(sm,1320),Math.min(e,1440)});
        }else if(sameDate(date,end)){
            if(em>0)out.add(new int[]{0,Math.min(em,360)});
        }else{
            out.add(new int[]{0,360});
        }
        // If a full intermediate day is followed by a separate night segment, the normal
        // weekly pattern's 22:00-24:00 segment is generated by the continuous occurrence only
        // when the occurrence spans that day. For the standard 32-hour pattern, add 22:00-24:00
        // on the day after the start.
        if(endOffset>=1 && sameDate(date,addDays(start,1)) && sm>=0){
            out.add(new int[]{1320,1440});
        }
        return out;
    }

    int endOffsetValue(Calendar start,Calendar end){ return (int)((end.getTimeInMillis()-start.getTimeInMillis())/(24L*60L*60L*1000L)); }
    int dayIndex(Calendar c){return c.get(Calendar.DAY_OF_WEEK);}
    boolean sameDate(Calendar a,Calendar b){return a.get(Calendar.YEAR)==b.get(Calendar.YEAR)&&a.get(Calendar.DAY_OF_YEAR)==b.get(Calendar.DAY_OF_YEAR);}
    Calendar addDays(Calendar c,int n){Calendar x=(Calendar)c.clone();x.add(Calendar.DAY_OF_MONTH,n);return x;}
    DutyDate findDate(String ds){for(DutyDate d:dates)if(d.date.equals(ds))return d;return null;}
    void addSpellIfMissing(DutyDate d,String from,String to){for(Spell s:d.spells)if(s.from.equals(from)&&s.to.equals(to))return;d.spells.add(new Spell(from,to));}

    void renderDates(){ datesBox.removeAllViews(); for(int i=0;i<dates.size();i++) renderDateCard(dates.get(i),i); }

    void addDateCard(String date,boolean addDefaultSpell){
        if(monthLocked()){toast("Unlock this month before editing.");return;}
        DutyDate d=new DutyDate(); d.date=date; if(addDefaultSpell)d.spells.add(new Spell("22:00","06:00")); dates.add(d); renderDates(); saveCurrentMonthSilently();
    }

    void renderDateCard(DutyDate d,int index){
        LinearLayout card=card(); card.setBackgroundColor(CARD);
        LinearLayout top=new LinearLayout(this); top.setGravity(Gravity.CENTER_VERTICAL);
        EditText date=input("Date",d.date); date.setFocusable(false); date.setClickable(true); date.setInputType(android.text.InputType.TYPE_NULL); top.addView(date,new LinearLayout.LayoutParams(0,-2,1));
        Button del=btn("×"); del.setTextColor(RED); del.setMinWidth(dp(48)); top.addView(del,new LinearLayout.LayoutParams(dp(52),-2)); card.addView(top);
        date.setEnabled(!monthLocked()); del.setEnabled(!monthLocked()); date.setOnClickListener(v->{if(monthLocked())return;pickDate(date,()->{d.date=date.getText().toString(); saveCurrentMonthSilently();});});
        del.setOnClickListener(v->{if(monthLocked())return;dates.remove(d); renderDates(); saveCurrentMonthSilently();});

        LinearLayout spellsBox=new LinearLayout(this); spellsBox.setOrientation(LinearLayout.VERTICAL); card.addView(spellsBox);
        TextView label=tv("TIME SPELLS",12,true); label.setTextColor(BLUE); card.addView(label);
        for(int j=0;j<d.spells.size();j++) renderSpell(spellsBox,d,j);
        Button addSpell=btn("＋ Add another time to this date"); addSpell.setEnabled(!monthLocked()); addSpell.setOnClickListener(v->{if(monthLocked())return;d.spells.add(new Spell("22:00","06:00")); renderDates(); saveCurrentMonthSilently();}); card.addView(addSpell);
        EditText rem=input("Remarks (optional)",d.remarks); rem.setEnabled(!monthLocked()); card.addView(rem); rem.addTextChangedListener(new SimpleWatcher(){public void afterTextChanged(Editable e){d.remarks=e.toString();saveCurrentMonthSilently();}});
        datesBox.addView(card);
    }

    void renderSpell(LinearLayout box,DutyDate d,int idx){
        Spell s=d.spells.get(idx); LinearLayout line=new LinearLayout(this); line.setGravity(Gravity.CENTER_VERTICAL);
        EditText f=input("From",s.from), t=input("To",s.to); f.setFocusable(false);f.setClickable(true);f.setInputType(android.text.InputType.TYPE_NULL); t.setFocusable(false);t.setClickable(true);t.setInputType(android.text.InputType.TYPE_NULL);
        line.addView(f,new LinearLayout.LayoutParams(0,-2,1)); line.addView(tv("  →  ",13,true),new LinearLayout.LayoutParams(dp(42),-2)); line.addView(t,new LinearLayout.LayoutParams(0,-2,1));
        Button del=btn("×"); del.setTextColor(RED); del.setMinWidth(dp(42)); line.addView(del,new LinearLayout.LayoutParams(dp(44),-2)); box.addView(line);
        f.setEnabled(!monthLocked()); t.setEnabled(!monthLocked()); del.setEnabled(!monthLocked()); f.setOnClickListener(v->{if(monthLocked())return;pickTime(f,false,null,()->{s.from=f.getText().toString();saveCurrentMonthSilently();});});
        t.setOnClickListener(v->{if(monthLocked())return;pickTime(t,true,f,()->{s.to=t.getText().toString();saveCurrentMonthSilently();});});
        del.setOnClickListener(v->{if(monthLocked())return;if(d.spells.size()<=1){toast("Keep at least one time spell. Delete the whole date instead.");return;} d.spells.remove(idx); renderDates(); saveCurrentMonthSilently();});
    }

    interface Done { void run(); }
    void pickDate(EditText field,Done done){
        Calendar cal=Calendar.getInstance(); try{Date d=new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).parse(field.getText().toString());if(d!=null)cal.setTime(d);}catch(Exception ignored){}
        new DatePickerDialog(this,(v,y,m,day)->{Calendar c=Calendar.getInstance();c.set(y,m,day);field.setText(new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).format(c.getTime()));done.run();},cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show();
    }
    void pickTime(EditText field,boolean isTo,EditText from,Done done){
        Calendar c=Calendar.getInstance(); String old=field.getText().toString(); try{int mm=mins(old);if(mm>=0)c.set(Calendar.HOUR_OF_DAY,mm/60);if(mm>=0)c.set(Calendar.MINUTE,mm%60);}catch(Exception ignored){}
        new TimePickerDialog(this,(v,h,m)->{String val=String.format(Locale.ENGLISH,"%02d:%02d",h,m);if(isTo&&h==0&&m==0&&from!=null&&mins(from.getText().toString())>=1320)val="24:00";field.setText(val);done.run();},c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
    }

    abstract class SimpleWatcher implements TextWatcher { public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int b,int c){} }
    static class DutyDate { String date="",remarks=""; ArrayList<Spell> spells=new ArrayList<>(); }
    static class Spell { String from,to; Spell(String f,String t){from=f;to=t;} }

    int mins(String s){ try{String[] x=s.trim().split(":");if(x.length!=2)return -1;int h=Integer.parseInt(x[0]),m=Integer.parseInt(x[1]);if(m<0||m>59)return -1;if(h==24&&m==0)return 1440;if(h<0||h>23)return -1;return h*60+m;}catch(Exception e){return -1;} }
    double nightMinutes(String from,String to){
        int a=mins(from),b=mins(to);if(a<0||b<0)return -1;int end=b;if(end==0&&a>=1320)end=1440;else if(end<=a)end+=1440;double total=0;
        for(int base=-1440;base<=1440;base+=1440){int ns=1320+base,ne=1800+base;int os=Math.max(a,ns),oe=Math.min(end,ne);if(oe>os)total+=oe-os;}return total;
    }
    ArrayList<DutyDate> validDates(){
        ArrayList<DutyDate> out=new ArrayList<>(); for(DutyDate d:dates){if(d.date.trim().isEmpty())continue;boolean any=false;for(Spell s:d.spells){if(s.from.trim().isEmpty()&&s.to.trim().isEmpty())continue;double n=nightMinutes(s.from,s.to);if(n<0){toast("Invalid time on "+d.date+". Use the clock picker.");return new ArrayList<>();}if(n>0)any=true;}if(!any){toast("No eligible 22:00–06:00 duty found on "+d.date);return new ArrayList<>();}out.add(d);}out.sort((x,y)->x.date.compareTo(y.date));return out;
    }
    double dateNightMinutes(DutyDate d){double m=0;for(Spell s:d.spells){double n=nightMinutes(s.from,s.to);if(n>0)m+=n;}return m;}
    double totalNightMinutes(ArrayList<DutyDate> ds){double m=0;for(DutyDate d:ds)m+=dateNightMinutes(d);return m;}
    double units(ArrayList<DutyDate> ds){return totalNightMinutes(ds)/360.0;}
    String fmtUnits(double x){return String.format(Locale.ENGLISH,"%.2f",x);}
    String fmtHours(double h){int m=(int)Math.round(h*60);return (m/60)+"h"+(m%60>0?" "+(m%60)+"m":"");}
    String fmtWeight(double h){int m=(int)Math.round(h*10);return (m/60)+"h"+(m%60>0?" "+(m%60)+"m":"");}
    double basicPay(){ try{return Double.parseDouble(basic.getText().toString().replace(",",""));}catch(Exception e){return Double.NaN;} }
    double daPercent(){ try{return Double.parseDouble(da.getText().toString().replace(",",""));}catch(Exception e){return Double.NaN;} }
    double daAmount(){ double bp=basicPay(), pct=daPercent(); if(Double.isNaN(bp)||Double.isNaN(pct))return Double.NaN; return bp*pct/100.0; }
    double hourlyNdaRate(){ double bp=basicPay(), damt=daAmount(); if(Double.isNaN(bp)||Double.isNaN(damt))return Double.NaN; return (bp+damt)/200.0; }
    double rawAmount(double u){ double rate=hourlyNdaRate(); return Double.isNaN(rate)?Double.NaN:u*rate; }
    double amount(double u){ double raw=rawAmount(u); return Double.isNaN(raw)?Double.NaN:Math.round(raw); }
    String money(double x){return Double.isNaN(x)?"Not calculated":String.format(Locale.ENGLISH,"₹ %,.0f",x);}
    String money2(double x){return Double.isNaN(x)?"Not calculated":String.format(Locale.ENGLISH,"₹ %,.2f",x);}
    void updateRatePreview(){
        if(ratePreview==null)return; double bp=basicPay(), damt=daAmount(), rate=hourlyNdaRate();
        if(Double.isNaN(bp)||Double.isNaN(damt)||Double.isNaN(rate)){ratePreview.setText("Enter Basic Pay and DA % above to see the exact NDA rate.");return;}
        ratePreview.setText(String.format(Locale.ENGLISH,"Basic Pay        ₹ %,.2f\nDA (%s%%)         ₹ %,.2f\nBasic + DA       ₹ %,.2f\n\nNDA rate / 1 unit   ₹ %,.2f",bp,da.getText().toString().trim(),damt,bp+damt,rate));
    }

    void showSummary(){
        ArrayList<DutyDate> a=validDates();if(a.isEmpty())return;double nm=totalNightMinutes(a)/60.0,u=units(a),rate=hourlyNdaRate(),raw=rawAmount(u),amt=amount(u);
        String rateText=Double.isNaN(rate)?"Not calculated":String.format(Locale.ENGLISH,"₹ %,.2f",rate);
        new AlertDialog.Builder(this).setTitle("NDA SUMMARY").setMessage("Dates: "+a.size()+"\nTotal eligible night duty: "+fmtHours(nm)+"\nNDA weightage: "+fmtWeight(nm)+"\n\nTOTAL NDA UNITS: "+fmtUnits(u)+"\nNDA rate per unit: "+rateText+"\nExact amount: "+money2(raw)+"\nRounded amount: "+money(amt)+"\n\nFormula: [(Basic + Basic × DA%) / 200] × NDA units. PDF prints units only.").setPositiveButton("OK",null).show();
    }

    void generatePdf(){
        ArrayList<DutyDate> a=validDates(); if(a.isEmpty())return; double u=units(a), raw=rawAmount(u), amt=amount(u);
        new AlertDialog.Builder(this).setTitle("PDF PREVIEW")
            .setMessage("Month: "+month.getText().toString()+"\nDates: "+a.size()+"\nNight duty: "+fmtHours(totalNightMinutes(a)/60.0)+"\nNDA units: "+fmtUnits(u)+"\nNDA rate: "+money2(hourlyNdaRate())+"/unit\nAmount (history only): "+money(amt)+"\n\nPDF: A4 landscape, 24 rows/page, units only.")
            .setNegativeButton("CANCEL",null).setPositiveButton("SAVE PDF",(d,w)->savePdfNow()).show();
    }

    void savePdfNow(){
        try{
            ArrayList<DutyDate>a=validDates();
            if(a.isEmpty()){toast("Add at least one valid duty date/time first.");return;}
            saveCurrentMonth();
            double u=units(a),amt=amount(u);
            saveHistory(a,u,amt);

            PdfDocument doc=new PdfDocument();
            int idx=0,page=1;
            while(idx<a.size()){
                int end=Math.min(idx+24,a.size());
                PdfDocument.Page pg=doc.startPage(new PdfDocument.PageInfo.Builder(842,595,page++).create());
                drawPage(pg.getCanvas(),a,idx,end,u);
                doc.finishPage(pg);
                idx=end;
            }

            String safe=monthKey().replaceAll("[^A-Za-z0-9]+","_");
            String fileName="IR_NDA_"+safe+".pdf";
            lastPdf=null;
            lastPdfUri=null;

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                ContentValues values=new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME,fileName);
                values.put(MediaStore.Downloads.MIME_TYPE,"application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH,Environment.DIRECTORY_DOWNLOADS+"/IR NDA APP");
                values.put(MediaStore.Downloads.IS_PENDING,1);

                Uri uri=getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,values);
                if(uri==null)throw new IOException("Could not create PDF in Downloads");

                try(OutputStream out=getContentResolver().openOutputStream(uri)){
                    if(out==null)throw new IOException("Could not open PDF output");
                    doc.writeTo(out);
                }

                ContentValues done=new ContentValues();
                done.put(MediaStore.Downloads.IS_PENDING,0);
                getContentResolver().update(uri,done,null,null);
                lastPdfUri=uri;
            }else{
                File dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File appDir=new File(dir,"IR NDA APP");
                if(!appDir.exists() && !appDir.mkdirs())throw new IOException("Could not create Downloads folder");
                File file=new File(appDir,fileName);
                try(OutputStream out=new FileOutputStream(file)){doc.writeTo(out);}
                lastPdf=file;
            }

            doc.close();
            toast("PDF saved in Downloads / IR NDA APP\n"+fileName);
        }catch(Exception e){
            toast("PDF save error: "+e.getMessage());
        }
    }

    boolean monthLocked(){ return sp.getBoolean("locked_"+monthKey(),false); }

    void updateDashboard(TextView out){ if(out==null)return; ArrayList<DutyDate> a=validDatesNoToast(); double h=totalNightMinutes(a)/60.0,u=units(a),r=hourlyNdaRate(),amt=amount(u); String rate=Double.isNaN(r)?"—":money2(r); String am=Double.isNaN(amt)?"—":money(amt); out.setText(""+month.getText()+"\n\nNight duty     "+fmtHours(h)+"\nNDA units      "+fmtUnits(u)+"\nNDA rate       "+rate+" / unit\nEstimated NDA  "+am+"\n\n"+(monthLocked()?"🔒 Month is locked":"✏️ Month is editable")); }
    ArrayList<DutyDate> validDatesNoToast(){ ArrayList<DutyDate> out=new ArrayList<>(); for(DutyDate d:dates){ if(d.date.trim().isEmpty())continue; boolean any=false; for(Spell s:d.spells){double n=nightMinutes(s.from,s.to);if(n>0)any=true;} if(any)out.add(d);} out.sort((x,y)->x.date.compareTo(y.date)); return out; }

    void showValidation(){
        ArrayList<String> w=new ArrayList<>(); HashSet<String> seen=new HashSet<>();
        for(DutyDate d:dates){ if(d.date.trim().isEmpty()){w.add("Blank date found.");continue;} if(!seen.add(d.date))w.add("Duplicate date: "+d.date);
            ArrayList<int[]> spans=new ArrayList<>(); for(Spell s:d.spells){double n=nightMinutes(s.from,s.to); if(mins(s.from)<0||mins(s.to)<0)w.add("Invalid time on "+d.date+": "+s.from+" → "+s.to); else if(n<=0)w.add("No 22:00–06:00 eligible time on "+d.date+": "+s.from+" → "+s.to); int a=mins(s.from),b=mins(s.to); if(a>=0&&b>=0){int e=(b==0&&a>=1320)?1440:b;if(e<=a)e+=1440;spans.add(new int[]{a,e});}}
            for(int i=0;i<spans.size();i++)for(int j=i+1;j<spans.size();j++)if(Math.max(spans.get(i)[0],spans.get(j)[0])<Math.min(spans.get(i)[1],spans.get(j)[1]))w.add("Overlapping spells on "+d.date);
        }
        if(w.isEmpty())w.add("✓ No obvious date/time errors found.\n\nThe app will count only eligible 22:00–06:00 minutes.");
        new AlertDialog.Builder(this).setTitle("DUTY CHECK").setMessage(joinLines(w)).setPositiveButton("OK",null).show();
    }
    String joinLines(ArrayList<String> x){StringBuilder b=new StringBuilder();for(String s:x)b.append("• ").append(s).append("\n");return b.toString();}

    void copyPreviousMonth(){
        if(monthLocked()){toast("Unlock this month before editing.");return;}
        Calendar c=Calendar.getInstance(); try{Date d=new SimpleDateFormat("MMMM, yyyy",Locale.ENGLISH).parse(month.getText().toString());if(d!=null)c.setTime(d);}catch(Exception ignored){}
        c.add(Calendar.MONTH,-1); String prev=new SimpleDateFormat("MMMM, yyyy",Locale.ENGLISH).format(c.getTime()).toUpperCase(Locale.ENGLISH); String raw=sp.getString("month_data_"+prev,"");
        if(raw.isEmpty()){toast("No saved duty pattern found for "+prev+".");return;}
        try{JSONArray arr=new JSONArray(raw); dates.clear(); Calendar target=Calendar.getInstance(); Date cur=new SimpleDateFormat("MMMM, yyyy",Locale.ENGLISH).parse(month.getText().toString()); if(cur!=null)target.setTime(cur); target.set(Calendar.DAY_OF_MONTH,1); int max=target.getActualMaximum(Calendar.DAY_OF_MONTH); for(int i=0;i<arr.length();i++){JSONObject o=arr.getJSONObject(i);String ds=o.optString("date","");Calendar dc=Calendar.getInstance();dc.setTime(new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).parse(ds));int day=dc.get(Calendar.DAY_OF_MONTH);if(day>max)continue;DutyDate nd=new DutyDate();target.set(Calendar.DAY_OF_MONTH,day);nd.date=new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).format(target.getTime());nd.remarks=o.optString("remarks","");JSONArray ss=o.optJSONArray("spells");if(ss!=null)for(int j=0;j<ss.length();j++){JSONObject q=ss.getJSONObject(j);nd.spells.add(new Spell(q.optString("from",""),q.optString("to","")));}dates.add(nd);}renderDates();saveCurrentMonthSilently();toast("Previous month's duty pattern copied.");}catch(Exception e){toast("Could not copy previous month.");}
    }

    void backupData(){ Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.setType("application/json");i.putExtra(Intent.EXTRA_TITLE,"IR_NDA_APP_BACKUP.json");startActivityForResult(i,9001); }
    void restoreData(){ Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("application/json");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,9002); }
    @Override public void onActivityResult(int requestCode,int resultCode,Intent data){ super.onActivityResult(requestCode,resultCode,data); if(resultCode!=RESULT_OK||data==null||data.getData()==null)return; Uri u=data.getData(); try{ if(requestCode==9001){JSONObject all=new JSONObject();for(Map.Entry<String,?> e:sp.getAll().entrySet()){if(e.getKey().startsWith("license_"))continue; Object v=e.getValue();if(v instanceof Boolean)all.put(e.getKey(),(Boolean)v);else if(v instanceof Integer)all.put(e.getKey(),(Integer)v);else all.put(e.getKey(),String.valueOf(v));}try(OutputStream o=getContentResolver().openOutputStream(u)){o.write(all.toString(2).getBytes("UTF-8"));}toast("Backup saved successfully.");}else if(requestCode==9002){StringBuilder b=new StringBuilder();try(InputStream in=getContentResolver().openInputStream(u);BufferedReader r=new BufferedReader(new InputStreamReader(in,"UTF-8"))){String line;while((line=r.readLine())!=null)b.append(line);}JSONObject all=new JSONObject(b.toString());SharedPreferences.Editor ed=sp.edit();java.util.Iterator<String> it=all.keys(); while(it.hasNext()){String k=it.next(); Object v=all.get(k);if(v instanceof Boolean)ed.putBoolean(k,(Boolean)v);else if(v instanceof Integer)ed.putInt(k,(Integer)v);else ed.putString(k,String.valueOf(v));}ed.apply();darkMode=sp.getBoolean("dark_mode",false);applyThemeColors();build();toast("Backup restored successfully.");}}catch(Exception e){toast("Backup/restore error: "+e.getMessage());}}

    void showAbout(){ new AlertDialog.Builder(this).setTitle("IR NDA APP by PRATIK").setMessage("Version 1.2.8\n\nDeveloped by\nPRATIK MUKHERJEE (SIM/ASN/ER)\n\n© 2026 PRATIK MUKHERJEE\n\nOffline Railway Night Duty Allowance calculator.\nCore calculation works without login or internet.\n\nLicense: offline device-bound activation with validity options.\n\nFormula: (Basic + Basic × DA%) ÷ 200 × NDA Units.").setPositiveButton("OK",null).show(); }

    void shareLastPdf(){
        try{
            if(lastPdfUri==null && (lastPdf==null || !lastPdf.exists())){
                toast("Generate and save a PDF first.");
                return;
            }
            Intent i=new Intent(Intent.ACTION_SEND);
            i.setType("application/pdf");
            Uri uri=lastPdfUri!=null ? lastPdfUri : FileProvider.getUriForFile(this,"com.pratik.irndaapp.fileprovider",lastPdf);
            i.putExtra(Intent.EXTRA_STREAM,uri);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(i,"Share NDA PDF"));
        }catch(Exception e){toast("Share error: "+e.getMessage());}
    }

    void saveHistory(ArrayList<DutyDate>a,double u,double amt){String key=monthKey();sp.edit().putString("history_units_"+key,fmtUnits(u)).putString("history_night_"+key,fmtHours(totalNightMinutes(a)/60.0)).putString("history_amount_"+key,Double.isNaN(amt)?"Not calculated":money(amt)).apply();}
    void showHistory(){
        ArrayList<String> keys=new ArrayList<>();for(Map.Entry<String,?>e:sp.getAll().entrySet())if(e.getKey().startsWith("history_units_"))keys.add(e.getKey().substring(14));Collections.sort(keys);Collections.reverse(keys);
        if(keys.isEmpty()){toast("No monthly history saved yet.");return;}
        String[] items=new String[keys.size()];for(int i=0;i<keys.size();i++){String k=keys.get(i);items[i]=k+"\nNight duty: "+sp.getString("history_night_"+k,"-")+"   Units: "+sp.getString("history_units_"+k,"-")+"   Amount: "+sp.getString("history_amount_"+k,"Not calculated");}
        new AlertDialog.Builder(this).setTitle("MONTHLY HISTORY").setItems(items,(d,w)->showHistoryDetail(keys.get(w))).setNegativeButton("CLOSE",null).show();
    }
    void showHistoryDetail(String key){new AlertDialog.Builder(this).setTitle(key).setMessage("Night duty: "+sp.getString("history_night_"+key,"-")+"\nNDA units: "+sp.getString("history_units_"+key,"-")+"\nCalculated amount: "+sp.getString("history_amount_"+key,"Not calculated")+"\n\nThe amount is kept for reference; the PDF contains units only.").setPositiveButton("OK",null).show();}

    void drawPage(Canvas c,ArrayList<DutyDate>a,int start,int end,double totalUnits){
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);p.setColor(Color.BLACK);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextAlign(Paint.Align.CENTER);p.setTextSize(15);c.drawText("NIGHT DUTY ALLOWANCE",421,34,p);p.setTextSize(12);c.drawText("FOR THE MONTH OF "+month.getText().toString().toUpperCase(Locale.ENGLISH),421,53,p);
        final float L=34,R=808,TOP=72,INFO=110,HEAD=138,BOT=530,C1=155,C2=315,C3=410,C4=470,C5=535,C6=625,C7=705;
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1);c.drawRect(L,TOP,R,BOT,p);c.drawLine(L,INFO,R,INFO,p);c.drawLine(C1,INFO,C1,BOT,p);c.drawLine(C2,INFO,C2,BOT,p);c.drawLine(C3,INFO,C3,BOT,p);c.drawLine(C4,INFO,C4,BOT,p);c.drawLine(C5,INFO,C5,BOT,p);c.drawLine(C6,INFO,C6,BOT,p);c.drawLine(C7,INFO,C7,BOT,p);
        p.setStyle(Paint.Style.FILL);p.setTextAlign(Paint.Align.LEFT);p.setTextSize(8.5f);c.drawText("PC 7 LEVEL: "+level.getText().toString(),43,96,p);p.setTextAlign(Paint.Align.RIGHT);c.drawText("RATE OF PAY: "+basic.getText().toString(),799,96,p);
        p.setTextAlign(Paint.Align.CENTER);p.setTextSize(8);c.drawText("NAME",95,128,p);c.drawText("DESIG. & P.F. A/C NO.",235,128,p);c.drawText("DATE",362,128,p);c.drawText("FROM",440,128,p);c.drawText("TO",503,128,p);c.drawText("ND",580,128,p);c.drawText("NDA",670,128,p);c.drawText("REMARKS",756,128,p);
        p.setTextSize(8);p.setTypeface(Typeface.DEFAULT);
        // Keep NAME vertical, but place DESIGNATION and P.F. one above the other
        // in the vertical center of their merged box for a cleaner form layout.
        c.save();c.rotate(-90,95,330);c.drawText(name.getText().toString(),95,330,p);c.restore();
        p.setTextAlign(Paint.Align.CENTER);
        float infoMidY=(INFO+BOT)/2f;
        c.drawText(designation.getText().toString(),235,infoMidY-6,p);
        c.drawText("P.F. A/C NO. "+pf.getText().toString(),235,infoMidY+8,p);
        float gridTop=138,rowH=15.8f; p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(.6f);for(int i=0;i<=24;i++)c.drawLine(C2,gridTop+i*rowH,C7,gridTop+i*rowH,p);p.setStyle(Paint.Style.FILL);p.setTextSize(7.2f);
        for(int i=start;i<end;i++){DutyDate d=a.get(i);float y=gridTop+(i-start)*rowH+10.5f;StringBuilder fs=new StringBuilder(),ts=new StringBuilder();for(int j=0;j<d.spells.size();j++){if(j>0){fs.append(" / ");ts.append(" / ");}fs.append(d.spells.get(j).from);ts.append(d.spells.get(j).to);}double h=dateNightMinutes(d)/60.0;c.drawText(d.date,362,y,p);c.drawText(fs.toString(),440,y,p);c.drawText(ts.toString(),503,y,p);c.drawText(fmtHours(h),580,y,p);c.drawText(fmtWeight(h),670,y,p);String rem=d.remarks==null?"":d.remarks;if(rem.length()>18)rem=rem.substring(0,18);c.drawText(rem,756,y,p);}
        if(end==a.size()){float totalTop=gridTop+24*rowH;p.setStyle(Paint.Style.FILL);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(8);c.drawText("TOTAL",503,totalTop+11,p);c.drawText(fmtHours(totalNightMinutes(a)/60.0),580,totalTop+11,p);c.drawText(fmtUnits(totalUnits),670,totalTop+11,p);p.setTypeface(Typeface.DEFAULT);p.setTextAlign(Paint.Align.LEFT);c.drawText("TOTAL NDA UNITS = "+fmtUnits(totalUnits),43,552,p);p.setTextAlign(Paint.Align.RIGHT);p.setTypeface(Typeface.DEFAULT_BOLD);c.drawText("SIGNATURE",799,552,p);}else{p.setTextAlign(Paint.Align.RIGHT);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(8);c.drawText("Continued on next page",799,552,p);}
    }

    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}

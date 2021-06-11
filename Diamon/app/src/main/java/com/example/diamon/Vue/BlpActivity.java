package com.example.diamon.Vue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.diamon.Modele.HistoryAdapter;
import com.example.diamon.Modele.ProviderAdapter;
import com.example.diamon.Modele.SelectProvider;
import com.example.diamon.Modele.Select_In_Db;
import com.example.diamon.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BlpActivity extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String URL_MAIN ="http://192.168.1.120/dmd/dmd_work.php";
    public static final String URL_DMD ="http://192.168.1.120/dmd/function_valeur_dmd.php";
    //public static final String URL_MAIN ="http://dmd.moulenetadi.com/dmd_work.php";
    //public static final String URL_DMD ="http://dmd.moulenetadi.com/function_valeur_dmd.php";
    public static  String USER_NAME="",USER_PSEUDO="",USER_ACCOUNT_ID="";

    public AlertDialog.Builder dialogBuilder;
    public AlertDialog dialog;
    private Button pop_cancel, pop_save;
    FloatingActionButton add_btn;

    EditText id_town, id_lcl_money,id_phone,txt_search;
    Button btn_search;
    RecyclerView recyclerView;

    public String s1[][] ;
    boolean result = false;
    int OUT =0;

    Spinner pays, type_provider,code_pays;
    ShimmerFrameLayout shime;


    SelectProvider selectProvider = new SelectProvider();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_blp);




        if(isConnectingToInternet(BlpActivity.this)) {
            Toast.makeText(getApplicationContext(), "internet is available", Toast.LENGTH_LONG).show();

            selectProvider.select_lp("");
            check_provider_result();

        }
        else {
            Toast.makeText(getApplicationContext(),"Your are not connected ",Toast.LENGTH_LONG).show();
        }

        add_btn = findViewById(R.id.id_add_blp);
        txt_search= findViewById(R.id.txt_search);
        btn_search = findViewById(R.id.btn_search);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_search.getText().toString()!=""){
                    if(isConnectingToInternet(BlpActivity.this)) {
                        //Toast.makeText(getApplicationContext(), "internet is available", Toast.LENGTH_LONG).show();

                        selectProvider.select_lp(txt_search.getText().toString());
                        ProgressDialog providerLoding1 = new ProgressDialog(BlpActivity.this);
                        providerLoding1.setMessage("\tSearching...");
                        providerLoding1.setCancelable(false);
                        providerLoding1.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                recyclerView = findViewById(R.id.id_provider_recyclerview);

                                ProviderAdapter providerAdapter = new ProviderAdapter(BlpActivity.this, selectProvider.getS1());
                                recyclerView.setAdapter(providerAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(BlpActivity.this));
                                providerLoding1.dismiss();
                            }
                        }, 10000);


                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Your are not connected ",Toast.LENGTH_LONG).show();
                    }

                }
            }
        });


        Intent blp_intent= getIntent();
        Bundle bundle = blp_intent.getExtras();
        if(bundle!=null)
        {
            USER_ACCOUNT_ID =(String) bundle.get("user_account_id");
            USER_NAME =(String) bundle.get("user_name");

            if(isConnectingToInternet(BlpActivity.this)) {
                Toast.makeText(getApplicationContext(),"internet is available", Toast.LENGTH_LONG).show();
               // select_provider();
            }
            else {
                Toast.makeText(getApplicationContext(),"Your are not connected ",Toast.LENGTH_LONG).show();
            }

        }


       add_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               pop_add();
           }
       });
    }


    public static boolean isConnectingToInternet(BlpActivity context)
    {
        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;

    }


    public void pop_add(){

        dialogBuilder = new AlertDialog.Builder(this);
        final View pop_add_view= getLayoutInflater().inflate(R.layout.add_popup,null);
        id_town = pop_add_view.findViewById(R.id.id_ville);
        id_lcl_money = pop_add_view.findViewById(R.id.id_pay_agent);
        id_phone = pop_add_view.findViewById(R.id.id_phone);
        pays = pop_add_view.findViewById(R.id.id_pays);
        type_provider = pop_add_view.findViewById(R.id.id_type_provider);
        code_pays = pop_add_view.findViewById(R.id.id_code_pays);

        ArrayList<String> pays_liste = new ArrayList<>();

        pays_liste.add("Your Country");
        pays_liste.add("Allemagne");
        pays_liste.add("Autriche");
        pays_liste.add("Belgique");
        pays_liste.add("Bulgarie");
        pays_liste.add("Croatie");
        pays_liste.add("Danemark");
        pays_liste.add("Espagne");
        pays_liste.add("Estonie");
        pays_liste.add("Finlande");
        pays_liste.add("France");
        pays_liste.add("Grèce");
        pays_liste.add("Hongrie");
        pays_liste.add("Irlande");
        pays_liste.add("Italie");
        pays_liste.add("Lettonie");
        pays_liste.add("Lituanie");
        pays_liste.add("Luxembourg");
        pays_liste.add("Angola");
        pays_liste.add("Bénin");
        pays_liste.add("Botswana");
        pays_liste.add("Faso Burkina Faso");
        pays_liste.add("Burundi");
        pays_liste.add("Cameroun");
        pays_liste.add("Cap-Vert");
        pays_liste.add("Congo");
        pays_liste.add("Ghana");
        pays_liste.add("Togo");
        pays_liste.add("Nigeria");
        pays_liste.add("Zambie");

        pays.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,pays_liste));


        ArrayList<String> provider_liste = new ArrayList<>();

        provider_liste.add("Type of Provider");
        provider_liste.add("Seller");
        provider_liste.add("Buyer");
        provider_liste.add("Seller & Buyer");
        type_provider.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,provider_liste));

        ArrayList<String> code_pays_liste = new ArrayList<>();

        code_pays_liste.add("+1");code_pays_liste.add("+2");code_pays_liste.add("+3");code_pays_liste.add("+4");code_pays_liste.add("+5");code_pays_liste.add("+6");code_pays_liste.add("+7");code_pays_liste.add("+8");code_pays_liste.add("+9");code_pays_liste.add("+10");code_pays_liste.add("+11");code_pays_liste.add("+12");code_pays_liste.add("+13");code_pays_liste.add("+14");code_pays_liste.add("+15");code_pays_liste.add("+16");code_pays_liste.add("+17");code_pays_liste.add("+18");code_pays_liste.add("+19");code_pays_liste.add("+20");code_pays_liste.add("+21");code_pays_liste.add("+22");code_pays_liste.add("+23");code_pays_liste.add("+24");code_pays_liste.add("+25");code_pays_liste.add("+26");code_pays_liste.add("+27");code_pays_liste.add("+28");code_pays_liste.add("+29");code_pays_liste.add("+30");code_pays_liste.add("+31");code_pays_liste.add("+32");code_pays_liste.add("+33");code_pays_liste.add("+34");code_pays_liste.add("+35");code_pays_liste.add("+36");code_pays_liste.add("+37");code_pays_liste.add("+38");code_pays_liste.add("+39");code_pays_liste.add("+40");code_pays_liste.add("+41");code_pays_liste.add("+42");code_pays_liste.add("+43");code_pays_liste.add("+44");code_pays_liste.add("+45");code_pays_liste.add("+46");code_pays_liste.add("+47");code_pays_liste.add("+48");code_pays_liste.add("+49");code_pays_liste.add("+50");code_pays_liste.add("+51");code_pays_liste.add("+52");code_pays_liste.add("+53");code_pays_liste.add("+54");code_pays_liste.add("+55");code_pays_liste.add("+56");code_pays_liste.add("+57");code_pays_liste.add("+58");code_pays_liste.add("+59");code_pays_liste.add("+60");code_pays_liste.add("+61");code_pays_liste.add("+62");code_pays_liste.add("+63");code_pays_liste.add("+64");code_pays_liste.add("+65");code_pays_liste.add("+66");code_pays_liste.add("+67");code_pays_liste.add("+68");code_pays_liste.add("+69");code_pays_liste.add("+70");code_pays_liste.add("+71");code_pays_liste.add("+72");code_pays_liste.add("+73");code_pays_liste.add("+74");code_pays_liste.add("+75");code_pays_liste.add("+76");code_pays_liste.add("+77");code_pays_liste.add("+78");code_pays_liste.add("+79");code_pays_liste.add("+80");code_pays_liste.add("+81");code_pays_liste.add("+82");code_pays_liste.add("+83");code_pays_liste.add("+84");code_pays_liste.add("+85");code_pays_liste.add("+86");code_pays_liste.add("+87");code_pays_liste.add("+88");code_pays_liste.add("+89");code_pays_liste.add("+90");code_pays_liste.add("+91");code_pays_liste.add("+92");code_pays_liste.add("+93");code_pays_liste.add("+94");code_pays_liste.add("+95");code_pays_liste.add("+96");code_pays_liste.add("+97");code_pays_liste.add("+98");code_pays_liste.add("+99");code_pays_liste.add("+100");code_pays_liste.add("+101");code_pays_liste.add("+102");code_pays_liste.add("+103");code_pays_liste.add("+104");code_pays_liste.add("+105");code_pays_liste.add("+106");code_pays_liste.add("+107");code_pays_liste.add("+108");code_pays_liste.add("+109");code_pays_liste.add("+110");code_pays_liste.add("+111");code_pays_liste.add("+112");code_pays_liste.add("+113");code_pays_liste.add("+114");code_pays_liste.add("+115");code_pays_liste.add("+116");code_pays_liste.add("+117");code_pays_liste.add("+118");code_pays_liste.add("+119");code_pays_liste.add("+120");code_pays_liste.add("+121");code_pays_liste.add("+122");code_pays_liste.add("+123");code_pays_liste.add("+124");code_pays_liste.add("+125");code_pays_liste.add("+126");code_pays_liste.add("+127");code_pays_liste.add("+128");code_pays_liste.add("+129");code_pays_liste.add("+130");code_pays_liste.add("+131");code_pays_liste.add("+132");code_pays_liste.add("+133");code_pays_liste.add("+134");code_pays_liste.add("+135");code_pays_liste.add("+136");code_pays_liste.add("+137");code_pays_liste.add("+138");code_pays_liste.add("+139");code_pays_liste.add("+140");code_pays_liste.add("+141");code_pays_liste.add("+142");code_pays_liste.add("+143");code_pays_liste.add("+144");code_pays_liste.add("+145");code_pays_liste.add("+146");code_pays_liste.add("+147");code_pays_liste.add("+148");code_pays_liste.add("+149");code_pays_liste.add("+150");code_pays_liste.add("+151");code_pays_liste.add("+152");code_pays_liste.add("+153");code_pays_liste.add("+154");code_pays_liste.add("+155");code_pays_liste.add("+156");code_pays_liste.add("+157");code_pays_liste.add("+158");code_pays_liste.add("+159");code_pays_liste.add("+160");code_pays_liste.add("+161");code_pays_liste.add("+162");code_pays_liste.add("+163");code_pays_liste.add("+164");code_pays_liste.add("+165");code_pays_liste.add("+166");code_pays_liste.add("+167");code_pays_liste.add("+168");code_pays_liste.add("+169");code_pays_liste.add("+170");code_pays_liste.add("+171");code_pays_liste.add("+172");code_pays_liste.add("+173");code_pays_liste.add("+174");code_pays_liste.add("+175");code_pays_liste.add("+176");code_pays_liste.add("+177");code_pays_liste.add("+178");code_pays_liste.add("+179");code_pays_liste.add("+180");code_pays_liste.add("+181");code_pays_liste.add("+182");code_pays_liste.add("+183");code_pays_liste.add("+184");code_pays_liste.add("+185");code_pays_liste.add("+186");code_pays_liste.add("+187");code_pays_liste.add("+188");code_pays_liste.add("+189");code_pays_liste.add("+190");code_pays_liste.add("+191");code_pays_liste.add("+192");code_pays_liste.add("+193");code_pays_liste.add("+194");code_pays_liste.add("+195");code_pays_liste.add("+196");code_pays_liste.add("+197");code_pays_liste.add("+198");code_pays_liste.add("+199");code_pays_liste.add("+200");code_pays_liste.add("+201");code_pays_liste.add("+202");code_pays_liste.add("+203");code_pays_liste.add("+204");code_pays_liste.add("+205");code_pays_liste.add("+206");code_pays_liste.add("+207");code_pays_liste.add("+208");code_pays_liste.add("+209");code_pays_liste.add("+210");code_pays_liste.add("+211");code_pays_liste.add("+212");code_pays_liste.add("+213");code_pays_liste.add("+214");code_pays_liste.add("+215");code_pays_liste.add("+216");code_pays_liste.add("+217");code_pays_liste.add("+218");code_pays_liste.add("+219");code_pays_liste.add("+220");code_pays_liste.add("+221");code_pays_liste.add("+222");code_pays_liste.add("+223");code_pays_liste.add("+224");code_pays_liste.add("+225");code_pays_liste.add("+226");code_pays_liste.add("+227");code_pays_liste.add("+228");code_pays_liste.add("+229");code_pays_liste.add("+230");code_pays_liste.add("+231");code_pays_liste.add("+232");code_pays_liste.add("+233");code_pays_liste.add("+234");code_pays_liste.add("+235");code_pays_liste.add("+236");code_pays_liste.add("+237");code_pays_liste.add("+238");code_pays_liste.add("+239");code_pays_liste.add("+240");code_pays_liste.add("+241");code_pays_liste.add("+242");code_pays_liste.add("+243");code_pays_liste.add("+244");code_pays_liste.add("+245");code_pays_liste.add("+246");code_pays_liste.add("+247");code_pays_liste.add("+248");code_pays_liste.add("+249");code_pays_liste.add("+250");code_pays_liste.add("+251");code_pays_liste.add("+252");code_pays_liste.add("+253");code_pays_liste.add("+254");code_pays_liste.add("+255");code_pays_liste.add("+256");code_pays_liste.add("+257");code_pays_liste.add("+258");code_pays_liste.add("+259");code_pays_liste.add("+260");code_pays_liste.add("+261");code_pays_liste.add("+262");code_pays_liste.add("+263");code_pays_liste.add("+264");code_pays_liste.add("+265");code_pays_liste.add("+266");code_pays_liste.add("+267");code_pays_liste.add("+268");code_pays_liste.add("+269");code_pays_liste.add("+270");code_pays_liste.add("+271");code_pays_liste.add("+272");code_pays_liste.add("+273");code_pays_liste.add("+274");code_pays_liste.add("+275");code_pays_liste.add("+276");code_pays_liste.add("+277");code_pays_liste.add("+278");code_pays_liste.add("+279");code_pays_liste.add("+280");code_pays_liste.add("+281");code_pays_liste.add("+282");code_pays_liste.add("+283");code_pays_liste.add("+284");code_pays_liste.add("+285");code_pays_liste.add("+286");code_pays_liste.add("+287");code_pays_liste.add("+288");code_pays_liste.add("+289");code_pays_liste.add("+290");code_pays_liste.add("+291");code_pays_liste.add("+292");code_pays_liste.add("+293");code_pays_liste.add("+294");code_pays_liste.add("+295");code_pays_liste.add("+296");code_pays_liste.add("+297");code_pays_liste.add("+298");code_pays_liste.add("+299");code_pays_liste.add("+300");code_pays_liste.add("+301");code_pays_liste.add("+302");code_pays_liste.add("+303");code_pays_liste.add("+304");code_pays_liste.add("+305");code_pays_liste.add("+306");code_pays_liste.add("+307");code_pays_liste.add("+308");code_pays_liste.add("+309");code_pays_liste.add("+310");code_pays_liste.add("+311");code_pays_liste.add("+312");code_pays_liste.add("+313");code_pays_liste.add("+314");code_pays_liste.add("+315");code_pays_liste.add("+316");code_pays_liste.add("+317");code_pays_liste.add("+318");code_pays_liste.add("+319");code_pays_liste.add("+320");code_pays_liste.add("+321");code_pays_liste.add("+322");code_pays_liste.add("+323");code_pays_liste.add("+324");code_pays_liste.add("+325");code_pays_liste.add("+326");code_pays_liste.add("+327");code_pays_liste.add("+328");code_pays_liste.add("+329");code_pays_liste.add("+330");code_pays_liste.add("+331");code_pays_liste.add("+332");code_pays_liste.add("+333");code_pays_liste.add("+334");code_pays_liste.add("+335");code_pays_liste.add("+336");code_pays_liste.add("+337");code_pays_liste.add("+338");code_pays_liste.add("+339");code_pays_liste.add("+340");code_pays_liste.add("+341");code_pays_liste.add("+342");code_pays_liste.add("+343");code_pays_liste.add("+344");code_pays_liste.add("+345");code_pays_liste.add("+346");code_pays_liste.add("+347");code_pays_liste.add("+348");code_pays_liste.add("+349");code_pays_liste.add("+350");code_pays_liste.add("+351");code_pays_liste.add("+352");code_pays_liste.add("+353");code_pays_liste.add("+354");code_pays_liste.add("+355");code_pays_liste.add("+356");code_pays_liste.add("+357");code_pays_liste.add("+358");code_pays_liste.add("+359");code_pays_liste.add("+360");code_pays_liste.add("+361");code_pays_liste.add("+362");code_pays_liste.add("+363");code_pays_liste.add("+364");code_pays_liste.add("+365");code_pays_liste.add("+366");code_pays_liste.add("+367");code_pays_liste.add("+368");code_pays_liste.add("+369");code_pays_liste.add("+370");code_pays_liste.add("+371");code_pays_liste.add("+372");code_pays_liste.add("+373");code_pays_liste.add("+374");code_pays_liste.add("+375");code_pays_liste.add("+376");code_pays_liste.add("+377");code_pays_liste.add("+378");code_pays_liste.add("+379");code_pays_liste.add("+380");code_pays_liste.add("+381");code_pays_liste.add("+382");code_pays_liste.add("+383");code_pays_liste.add("+384");code_pays_liste.add("+385");code_pays_liste.add("+386");code_pays_liste.add("+387");code_pays_liste.add("+388");code_pays_liste.add("+389");code_pays_liste.add("+390");code_pays_liste.add("+391");code_pays_liste.add("+392");code_pays_liste.add("+393");code_pays_liste.add("+394");code_pays_liste.add("+395");code_pays_liste.add("+396");code_pays_liste.add("+397");code_pays_liste.add("+398");code_pays_liste.add("+399");code_pays_liste.add("+400");code_pays_liste.add("+401");code_pays_liste.add("+402");code_pays_liste.add("+403");code_pays_liste.add("+404");code_pays_liste.add("+405");code_pays_liste.add("+406");code_pays_liste.add("+407");code_pays_liste.add("+408");code_pays_liste.add("+409");code_pays_liste.add("+410");code_pays_liste.add("+411");code_pays_liste.add("+412");code_pays_liste.add("+413");code_pays_liste.add("+414");code_pays_liste.add("+415");code_pays_liste.add("+416");code_pays_liste.add("+417");code_pays_liste.add("+418");code_pays_liste.add("+419");code_pays_liste.add("+420");code_pays_liste.add("+421");code_pays_liste.add("+422");code_pays_liste.add("+423");code_pays_liste.add("+424");code_pays_liste.add("+425");code_pays_liste.add("+426");code_pays_liste.add("+427");code_pays_liste.add("+428");code_pays_liste.add("+429");code_pays_liste.add("+430");code_pays_liste.add("+431");code_pays_liste.add("+432");code_pays_liste.add("+433");code_pays_liste.add("+434");code_pays_liste.add("+435");code_pays_liste.add("+436");code_pays_liste.add("+437");code_pays_liste.add("+438");code_pays_liste.add("+439");code_pays_liste.add("+440");code_pays_liste.add("+441");code_pays_liste.add("+442");code_pays_liste.add("+443");code_pays_liste.add("+444");code_pays_liste.add("+445");code_pays_liste.add("+446");code_pays_liste.add("+447");code_pays_liste.add("+448");code_pays_liste.add("+449");code_pays_liste.add("+450");code_pays_liste.add("+451");code_pays_liste.add("+452");code_pays_liste.add("+453");code_pays_liste.add("+454");code_pays_liste.add("+455");code_pays_liste.add("+456");code_pays_liste.add("+457");code_pays_liste.add("+458");code_pays_liste.add("+459");code_pays_liste.add("+460");code_pays_liste.add("+461");code_pays_liste.add("+462");code_pays_liste.add("+463");code_pays_liste.add("+464");code_pays_liste.add("+465");code_pays_liste.add("+466");code_pays_liste.add("+467");code_pays_liste.add("+468");code_pays_liste.add("+469");code_pays_liste.add("+470");code_pays_liste.add("+471");code_pays_liste.add("+472");code_pays_liste.add("+473");code_pays_liste.add("+474");code_pays_liste.add("+475");code_pays_liste.add("+476");code_pays_liste.add("+477");code_pays_liste.add("+478");code_pays_liste.add("+479");code_pays_liste.add("+480");code_pays_liste.add("+481");code_pays_liste.add("+482");code_pays_liste.add("+483");code_pays_liste.add("+484");code_pays_liste.add("+485");code_pays_liste.add("+486");code_pays_liste.add("+487");code_pays_liste.add("+488");code_pays_liste.add("+489");code_pays_liste.add("+490");code_pays_liste.add("+491");code_pays_liste.add("+492");code_pays_liste.add("+493");code_pays_liste.add("+494");code_pays_liste.add("+495");code_pays_liste.add("+496");code_pays_liste.add("+497");code_pays_liste.add("+498");code_pays_liste.add("+499");code_pays_liste.add("+500");code_pays_liste.add("+501");code_pays_liste.add("+502");code_pays_liste.add("+503");code_pays_liste.add("+504");code_pays_liste.add("+505");code_pays_liste.add("+506");code_pays_liste.add("+507");code_pays_liste.add("+508");code_pays_liste.add("+509");code_pays_liste.add("+510");code_pays_liste.add("+511");code_pays_liste.add("+512");code_pays_liste.add("+513");code_pays_liste.add("+514");code_pays_liste.add("+515");code_pays_liste.add("+516");code_pays_liste.add("+517");code_pays_liste.add("+518");code_pays_liste.add("+519");code_pays_liste.add("+520");code_pays_liste.add("+521");code_pays_liste.add("+522");code_pays_liste.add("+523");code_pays_liste.add("+524");code_pays_liste.add("+525");code_pays_liste.add("+526");code_pays_liste.add("+527");code_pays_liste.add("+528");code_pays_liste.add("+529");code_pays_liste.add("+530");code_pays_liste.add("+531");code_pays_liste.add("+532");code_pays_liste.add("+533");code_pays_liste.add("+534");code_pays_liste.add("+535");code_pays_liste.add("+536");code_pays_liste.add("+537");code_pays_liste.add("+538");code_pays_liste.add("+539");code_pays_liste.add("+540");code_pays_liste.add("+541");code_pays_liste.add("+542");code_pays_liste.add("+543");code_pays_liste.add("+544");code_pays_liste.add("+545");code_pays_liste.add("+546");code_pays_liste.add("+547");code_pays_liste.add("+548");code_pays_liste.add("+549");code_pays_liste.add("+550");code_pays_liste.add("+551");code_pays_liste.add("+552");code_pays_liste.add("+553");code_pays_liste.add("+554");code_pays_liste.add("+555");code_pays_liste.add("+556");code_pays_liste.add("+557");code_pays_liste.add("+558");code_pays_liste.add("+559");code_pays_liste.add("+560");code_pays_liste.add("+561");code_pays_liste.add("+562");code_pays_liste.add("+563");code_pays_liste.add("+564");code_pays_liste.add("+565");code_pays_liste.add("+566");code_pays_liste.add("+567");code_pays_liste.add("+568");code_pays_liste.add("+569");code_pays_liste.add("+570");code_pays_liste.add("+571");code_pays_liste.add("+572");code_pays_liste.add("+573");code_pays_liste.add("+574");code_pays_liste.add("+575");code_pays_liste.add("+576");code_pays_liste.add("+577");code_pays_liste.add("+578");code_pays_liste.add("+579");code_pays_liste.add("+580");code_pays_liste.add("+581");code_pays_liste.add("+582");code_pays_liste.add("+583");code_pays_liste.add("+584");code_pays_liste.add("+585");code_pays_liste.add("+586");code_pays_liste.add("+587");code_pays_liste.add("+588");code_pays_liste.add("+589");code_pays_liste.add("+590");code_pays_liste.add("+591");code_pays_liste.add("+592");code_pays_liste.add("+593");code_pays_liste.add("+594");code_pays_liste.add("+595");code_pays_liste.add("+596");code_pays_liste.add("+597");code_pays_liste.add("+598");code_pays_liste.add("+599");code_pays_liste.add("+600");code_pays_liste.add("+601");code_pays_liste.add("+602");code_pays_liste.add("+603");code_pays_liste.add("+604");code_pays_liste.add("+605");code_pays_liste.add("+606");code_pays_liste.add("+607");code_pays_liste.add("+608");code_pays_liste.add("+609");code_pays_liste.add("+610");code_pays_liste.add("+611");code_pays_liste.add("+612");code_pays_liste.add("+613");code_pays_liste.add("+614");code_pays_liste.add("+615");code_pays_liste.add("+616");code_pays_liste.add("+617");code_pays_liste.add("+618");code_pays_liste.add("+619");code_pays_liste.add("+620");code_pays_liste.add("+621");code_pays_liste.add("+622");code_pays_liste.add("+623");code_pays_liste.add("+624");code_pays_liste.add("+625");code_pays_liste.add("+626");code_pays_liste.add("+627");code_pays_liste.add("+628");code_pays_liste.add("+629");code_pays_liste.add("+630");code_pays_liste.add("+631");code_pays_liste.add("+632");code_pays_liste.add("+633");code_pays_liste.add("+634");code_pays_liste.add("+635");code_pays_liste.add("+636");code_pays_liste.add("+637");code_pays_liste.add("+638");code_pays_liste.add("+639");code_pays_liste.add("+640");code_pays_liste.add("+641");code_pays_liste.add("+642");code_pays_liste.add("+643");code_pays_liste.add("+644");code_pays_liste.add("+645");code_pays_liste.add("+646");code_pays_liste.add("+647");code_pays_liste.add("+648");code_pays_liste.add("+649");code_pays_liste.add("+650");code_pays_liste.add("+651");code_pays_liste.add("+652");code_pays_liste.add("+653");code_pays_liste.add("+654");code_pays_liste.add("+655");code_pays_liste.add("+656");code_pays_liste.add("+657");code_pays_liste.add("+658");code_pays_liste.add("+659");code_pays_liste.add("+660");code_pays_liste.add("+661");code_pays_liste.add("+662");code_pays_liste.add("+663");code_pays_liste.add("+664");code_pays_liste.add("+665");code_pays_liste.add("+666");code_pays_liste.add("+667");code_pays_liste.add("+668");code_pays_liste.add("+669");code_pays_liste.add("+670");code_pays_liste.add("+671");code_pays_liste.add("+672");code_pays_liste.add("+673");code_pays_liste.add("+674");code_pays_liste.add("+675");code_pays_liste.add("+676");code_pays_liste.add("+677");code_pays_liste.add("+678");code_pays_liste.add("+679");code_pays_liste.add("+680");code_pays_liste.add("+681");code_pays_liste.add("+682");code_pays_liste.add("+683");code_pays_liste.add("+684");code_pays_liste.add("+685");code_pays_liste.add("+686");code_pays_liste.add("+687");code_pays_liste.add("+688");code_pays_liste.add("+689");code_pays_liste.add("+690");code_pays_liste.add("+691");code_pays_liste.add("+692");code_pays_liste.add("+693");code_pays_liste.add("+694");code_pays_liste.add("+695");code_pays_liste.add("+696");code_pays_liste.add("+697");code_pays_liste.add("+698");code_pays_liste.add("+699");code_pays_liste.add("+700");code_pays_liste.add("+701");code_pays_liste.add("+702");code_pays_liste.add("+703");code_pays_liste.add("+704");code_pays_liste.add("+705");code_pays_liste.add("+706");code_pays_liste.add("+707");code_pays_liste.add("+708");code_pays_liste.add("+709");code_pays_liste.add("+710");code_pays_liste.add("+711");code_pays_liste.add("+712");code_pays_liste.add("+713");code_pays_liste.add("+714");code_pays_liste.add("+715");code_pays_liste.add("+716");code_pays_liste.add("+717");code_pays_liste.add("+718");code_pays_liste.add("+719");code_pays_liste.add("+720");code_pays_liste.add("+721");code_pays_liste.add("+722");code_pays_liste.add("+723");code_pays_liste.add("+724");code_pays_liste.add("+725");code_pays_liste.add("+726");code_pays_liste.add("+727");code_pays_liste.add("+728");code_pays_liste.add("+729");code_pays_liste.add("+730");code_pays_liste.add("+731");code_pays_liste.add("+732");code_pays_liste.add("+733");code_pays_liste.add("+734");code_pays_liste.add("+735");code_pays_liste.add("+736");code_pays_liste.add("+737");code_pays_liste.add("+738");code_pays_liste.add("+739");code_pays_liste.add("+740");code_pays_liste.add("+741");code_pays_liste.add("+742");code_pays_liste.add("+743");code_pays_liste.add("+744");code_pays_liste.add("+745");code_pays_liste.add("+746");code_pays_liste.add("+747");code_pays_liste.add("+748");code_pays_liste.add("+749");code_pays_liste.add("+750");code_pays_liste.add("+751");code_pays_liste.add("+752");code_pays_liste.add("+753");code_pays_liste.add("+754");code_pays_liste.add("+755");code_pays_liste.add("+756");code_pays_liste.add("+757");code_pays_liste.add("+758");code_pays_liste.add("+759");code_pays_liste.add("+760");code_pays_liste.add("+761");code_pays_liste.add("+762");code_pays_liste.add("+763");code_pays_liste.add("+764");code_pays_liste.add("+765");code_pays_liste.add("+766");code_pays_liste.add("+767");code_pays_liste.add("+768");code_pays_liste.add("+769");code_pays_liste.add("+770");code_pays_liste.add("+771");code_pays_liste.add("+772");code_pays_liste.add("+773");code_pays_liste.add("+774");code_pays_liste.add("+775");code_pays_liste.add("+776");code_pays_liste.add("+777");code_pays_liste.add("+778");code_pays_liste.add("+779");code_pays_liste.add("+780");code_pays_liste.add("+781");code_pays_liste.add("+782");code_pays_liste.add("+783");code_pays_liste.add("+784");code_pays_liste.add("+785");code_pays_liste.add("+786");code_pays_liste.add("+787");code_pays_liste.add("+788");code_pays_liste.add("+789");code_pays_liste.add("+790");code_pays_liste.add("+791");code_pays_liste.add("+792");code_pays_liste.add("+793");code_pays_liste.add("+794");code_pays_liste.add("+795");code_pays_liste.add("+796");code_pays_liste.add("+797");code_pays_liste.add("+798");code_pays_liste.add("+799");code_pays_liste.add("+800");code_pays_liste.add("+801");code_pays_liste.add("+802");code_pays_liste.add("+803");code_pays_liste.add("+804");code_pays_liste.add("+805");code_pays_liste.add("+806");code_pays_liste.add("+807");code_pays_liste.add("+808");code_pays_liste.add("+809");code_pays_liste.add("+810");code_pays_liste.add("+811");code_pays_liste.add("+812");code_pays_liste.add("+813");code_pays_liste.add("+814");code_pays_liste.add("+815");code_pays_liste.add("+816");code_pays_liste.add("+817");code_pays_liste.add("+818");code_pays_liste.add("+819");code_pays_liste.add("+820");code_pays_liste.add("+821");code_pays_liste.add("+822");code_pays_liste.add("+823");code_pays_liste.add("+824");code_pays_liste.add("+825");code_pays_liste.add("+826");code_pays_liste.add("+827");code_pays_liste.add("+828");code_pays_liste.add("+829");code_pays_liste.add("+830");code_pays_liste.add("+831");code_pays_liste.add("+832");code_pays_liste.add("+833");code_pays_liste.add("+834");code_pays_liste.add("+835");code_pays_liste.add("+836");code_pays_liste.add("+837");code_pays_liste.add("+838");code_pays_liste.add("+839");code_pays_liste.add("+840");code_pays_liste.add("+841");code_pays_liste.add("+842");code_pays_liste.add("+843");code_pays_liste.add("+844");code_pays_liste.add("+845");code_pays_liste.add("+846");code_pays_liste.add("+847");code_pays_liste.add("+848");code_pays_liste.add("+849");code_pays_liste.add("+850");code_pays_liste.add("+851");code_pays_liste.add("+852");code_pays_liste.add("+853");code_pays_liste.add("+854");code_pays_liste.add("+855");code_pays_liste.add("+856");code_pays_liste.add("+857");code_pays_liste.add("+858");code_pays_liste.add("+859");code_pays_liste.add("+860");code_pays_liste.add("+861");code_pays_liste.add("+862");code_pays_liste.add("+863");code_pays_liste.add("+864");code_pays_liste.add("+865");code_pays_liste.add("+866");code_pays_liste.add("+867");code_pays_liste.add("+868");code_pays_liste.add("+869");code_pays_liste.add("+870");code_pays_liste.add("+871");code_pays_liste.add("+872");code_pays_liste.add("+873");code_pays_liste.add("+874");code_pays_liste.add("+875");code_pays_liste.add("+876");code_pays_liste.add("+877");code_pays_liste.add("+878");code_pays_liste.add("+879");code_pays_liste.add("+880");code_pays_liste.add("+881");code_pays_liste.add("+882");code_pays_liste.add("+883");code_pays_liste.add("+884");code_pays_liste.add("+885");code_pays_liste.add("+886");code_pays_liste.add("+887");code_pays_liste.add("+888");code_pays_liste.add("+889");code_pays_liste.add("+890");code_pays_liste.add("+891");code_pays_liste.add("+892");code_pays_liste.add("+893");code_pays_liste.add("+894");code_pays_liste.add("+895");code_pays_liste.add("+896");code_pays_liste.add("+897");code_pays_liste.add("+898");code_pays_liste.add("+899");code_pays_liste.add("+900");code_pays.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,code_pays_liste));

        pop_cancel= pop_add_view.findViewById(R.id.save2);
        pop_save= pop_add_view.findViewById(R.id.save);

        dialogBuilder.setView(pop_add_view);
        dialog= dialogBuilder.create();
        dialog.show();

        pop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        pop_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //Toast.makeText(BlpActivity.this,USER_ACCOUNT_ID,Toast.LENGTH_LONG).show();
                blp();
            }
        });
    }

    public void blp() {


        String txt_pays = pays.getSelectedItem().toString();
        String txt_code_pays =code_pays.getSelectedItem().toString();
        String txt_ville = id_town.getText().toString();
        String txt_lcl_money = id_lcl_money.getText().toString();
        String txt_type_provider= type_provider.getSelectedItem().toString();
        String txt_phone = id_phone.getText().toString();

        if(txt_pays!="Your Country" && txt_ville!=""  && txt_lcl_money!=""
                && txt_type_provider!= "Type of Provider" && txt_phone!="" && txt_code_pays!="" ){

            new BlpActivity.AsyncBLP().execute(txt_pays,txt_ville,txt_lcl_money,txt_type_provider,txt_code_pays+""+txt_phone,USER_ACCOUNT_ID,USER_NAME,"blp");


        }
       // Toast.makeText(BlpActivity.this,txt_pays+" "+txt_type_provider+" "+txt_phone,Toast.LENGTH_LONG).show();

    }


    public void pop_success(){

        dialogBuilder = new AlertDialog.Builder(this);
        final View pop_success_view= getLayoutInflater().inflate(R.layout.success_pop,null);


        pop_cancel= pop_success_view.findViewById(R.id.canc);
        pop_save= pop_success_view.findViewById(R.id.hom);

        dialogBuilder.setView(pop_success_view);
        dialog= dialogBuilder.create();
        dialog.show();

        pop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        pop_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent_home = new Intent(BlpActivity.this,PageMainActivity.class);
               startActivity(intent_home);
                BlpActivity.this.finish();
            }
        });
    }

    private class AsyncBLP extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(BlpActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(URL_DMD);
                Log.d("///////////////////////////////////////////////////////////////////////",  "*********************************************************************************************************************" );
                //Log.d("connection" ,"doInBackground:*************************************************** ");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("txt_pays", params[0])
                        .appendQueryParameter("txt_ville", params[1])
                        .appendQueryParameter("txt_lcl_money", params[2])
                        .appendQueryParameter("txt_type_provider", params[3])
                        .appendQueryParameter("txt_phone", params[4])
                        .appendQueryParameter("user_account_id", params[5])
                        .appendQueryParameter("user_name", params[6])
                        .appendQueryParameter("blp", params[6]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }
            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();
            Log.d("message du serveur", result);
            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                */
                dialog.dismiss();
                pop_success();


                // Intent intent = new Intent(BlpActivity.this,PageMainActivity.class);
                //startActivity(intent);
                //

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(BlpActivity.this, "Try again please", Toast.LENGTH_LONG).show();

            }else if (result.equalsIgnoreCase("vide") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(BlpActivity.this, "VIDE Problem.", Toast.LENGTH_LONG).show();

            }else{
                Log.d("///////////////////////////////////////////////////////////////////////", " "+result);
            }

        }

    }


    public boolean check_provider_result() {

        ProgressDialog providerLoding = new ProgressDialog(BlpActivity.this);
        providerLoding.setMessage("\tSearching...");
        providerLoding.setCancelable(false);
        providerLoding.show();




        if (result == false && OUT == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (selectProvider.getS1() == null) {
                        result = false;
                        check_provider_result();
                    } else {
                        recyclerView = findViewById(R.id.id_provider_recyclerview);

                        ProviderAdapter providerAdapter = new ProviderAdapter(BlpActivity.this, selectProvider.getS1());
                        recyclerView.setAdapter(providerAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(BlpActivity.this));
                        providerLoding.dismiss();
                        result = true;
                    }
                }
            }, 1000);
        }else{if(OUT==1){
            providerLoding.dismiss();
            Toast.makeText(BlpActivity.this, "No result Time out",Toast.LENGTH_LONG).show();
            result=true;
            OUT=0;
        } }
        return result;

    }


}


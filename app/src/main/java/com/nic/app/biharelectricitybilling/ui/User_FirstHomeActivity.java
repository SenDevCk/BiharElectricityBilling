package com.nic.app.biharelectricitybilling.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.Block;
import com.nic.app.biharelectricitybilling.entity.Dist;
import com.nic.app.biharelectricitybilling.entity.PanchayatData;
import com.nic.app.biharelectricitybilling.entity.TOLLA;
import com.nic.app.biharelectricitybilling.entity.VILLAGE;
import com.nic.app.biharelectricitybilling.entity.consumer_address;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.GlobalVariables;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.util.ArrayList;


public class User_FirstHomeActivity extends Activity {
    ArrayList<Block> BlockList = new ArrayList<Block>();
    ArrayList<Dist> DistList = new ArrayList<Dist>();
    ArrayList<PanchayatData> PanchayatList = new ArrayList<PanchayatData>();
    ArrayList<VILLAGE> VillageList = new ArrayList<VILLAGE>();
    ArrayList<TOLLA> TollaList = new ArrayList<TOLLA>();
    ArrayAdapter<String> Blockadapter;
    ArrayAdapter<String> Distadapter;
    ArrayAdapter<String> Panchayatadapter;
    ArrayAdapter<String> Villageadapter;
    ArrayAdapter<String> Tollaadapter;
    String Distname = "na", Block_Name = "na", Panchayat_Name = "na", Village_Name = "na", Tolla_Name = "na";
    int _Dist_ID, _Block_ID = 0, _Panchayat_ID = 0, _Village_ID = 0, _Tolla_ID = 0;
    String _Dist_ID1 = "", _Block_ID1 = "", _Panchayat_ID1 = "", _Village_ID1 = "", _Tolla_ID1 = "";
    Spinner spdist, spBlock, sppanchayat, spvillage, sptolla;
    DataBaseHelper dbHelper;
    ProgressDialog progressDialog;
    private ActionBar actionBar;
    consumer_address address;
    String stringAcNo = "";
    String stringconid = "";
    String stringname = "";
    LinearLayout villlay;
    Bundle bundle = null;
    TextView tolltext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_firsthome);
        TextView userLastDate = (TextView) findViewById(R.id.textLastVisit);
        dbHelper = new DataBaseHelper(this);
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        // For displaying title and subtitle and change text color
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Address Update</font>"));
        userLastDate.setText("Last Visited : " + GlobalVariables.Last_Visited);
        final TextView userName = (TextView) findViewById(R.id.textName);
        final TextView userDist = (TextView) findViewById(R.id.textDist);
        tolltext=(TextView)findViewById(R.id.tola) ;
        //   userDist.setText("SUBDIV NAME: " + CommonPref.getUserDetails(getApplicationContext()).get_SubdivName());
        spBlock = (Spinner) findViewById(R.id.spinnerblock);
        sppanchayat = (Spinner) findViewById(R.id.spinnerpanchayatnew);
        spvillage = (Spinner) findViewById(R.id.spinnervillagenew);
        sptolla = (Spinner) findViewById(R.id.spinnertolanew);
        spdist = (Spinner) findViewById(R.id.spinnerdistrict);
        villlay=(LinearLayout)findViewById(R.id.lin_village) ;
        stringAcNo = getIntent().getStringExtra("ACCOUNT_NO");
        stringconid = getIntent().getStringExtra("CON_ID");
        stringname = getIntent().getStringExtra("CON_NAME");
        bundle=getIntent().getExtras();
        userName.setText("NAME:" + stringname + ".");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        DistList = dbHelper.getallDist(CommonPref.getUserDetails(User_FirstHomeActivity.this).get_distcode());
        _Dist_ID = Integer.parseInt(CommonPref.getUserDetails(User_FirstHomeActivity.this).get_distcode());
        if (DistList.size() >= 1) {
            loadDistSpinnerData();
        } else {
            if (!Utiilties.isOnline(User_FirstHomeActivity.this)) {
                AlertDialog.Builder ab = new AlertDialog.Builder(User_FirstHomeActivity.this);
                ab.setMessage("Internet Connection is not avaliable.Please Turn ON Network Connection to download data.");
                ab.setPositiveButton("Turn On Network Connection", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(I);
                    }
                });
                ab.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                });
                ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
                ab.show();
            } else {
                progressDialog.show();
                new GetDistList(CommonPref.getUserDetails(User_FirstHomeActivity.this).get_distcode()).execute();
            }

        }
        //  distname.setText(CommonPref.getUserDetails(User_FirstHomeActivity.this).get_distname());
       /* DistList = dbHelper.getallBlock(CommonPref.getUserDetails(User_FirstHomeActivity.this).get_distcode());
        if (BlockList.size() >= 1) {
            loadBlockSpinnerData();
        } else {
            if (!Utiilties.isOnline(User_FirstHomeActivity.this)) {
                AlertDialog.Builder ab = new AlertDialog.Builder(User_FirstHomeActivity.this);
                ab.setMessage("Internet Connection is not avaliable.Please Turn ON Network Connection to download data.");
                ab.setPositiveButton("Turn On Network Connection", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(I);
                    }
                });
                ab.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                });
                ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
                ab.show();
            } else {
                progressDialog.show();
                new GetBlockList(CommonPref.getUserDetails(User_FirstHomeActivity.this).get_distcode()).execute();
            }

        }*/
        spdist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 > 0) {
                    Dist blk = DistList.get(arg2 - 1);
                    _Dist_ID1 = blk.getDistCode();
                    _Dist_ID = Integer.parseInt(_Dist_ID1);
                    Distname = blk.getDistName();
                    BlockList = dbHelper.getallBlock(_Dist_ID1);
                    if (BlockList.size() >= 1) {
                        loadBlockSpinnerData();
                    } else {
                        if (!Utiilties.isOnline(User_FirstHomeActivity.this)) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(User_FirstHomeActivity.this);
                            ab.setMessage("Internet Connection is not avaliable.Please Turn ON Network Connection to download data.");
                            ab.setPositiveButton("Turn On Network Connection", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(I);
                                }
                            });
                            ab.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }

                            });
                            ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
                            ab.show();
                        } else {
                            progressDialog.show();
                            new GetBlockList(_Dist_ID1).execute();
                        }
                    }
                } else if (arg2 == 0) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spBlock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 > 0) {
                    Block blk = BlockList.get(arg2 - 1);
                    _Block_ID1 = blk.getBlockCode();
                    _Block_ID = Integer.parseInt(_Block_ID1);
                    Block_Name = blk.getBlockName();
                    PanchayatList = dbHelper.getPanchayatLocal(_Block_ID1);
                    if (PanchayatList.size() >0) {
                        loadpanchayatSpinnerData();
                    } else {
                        if (!Utiilties.isOnline(User_FirstHomeActivity.this)) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(User_FirstHomeActivity.this);
                            ab.setMessage("Internet Connection is not avaliable.Please Turn ON Network Connection to download data.");
                            ab.setPositiveButton("Turn On Network Connection", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(I);
                                }
                            });
                            ab.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }

                            });
                            ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
                            ab.show();
                        } else {
                            progressDialog.show();
                            new GetPAnchayatList(_Block_ID1).execute();
                        }
                    }
                } else if (arg2 == 0) {
                    //
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        sppanchayat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 > 0) {
                    PanchayatData panchayat = PanchayatList.get(arg2 - 1);
                    _Panchayat_ID1 = panchayat.getPcode();
                    _Panchayat_ID = Integer.parseInt(_Panchayat_ID1);
                    Panchayat_Name = panchayat.getPname();

                 /*   if(Panchayat_Name.contains("(NP)")){
                        villlay.setVisibility(View.GONE);
                        tolltext.setText(" वार्ड ");
                    }else{*/
                        VillageList = dbHelper.getVILLAGE(_Panchayat_ID1);
                 //   }
                    if (VillageList.size() > 0) {
                        loadvillageSpinnerData();
                    } else {
                        if (!Utiilties.isOnline(User_FirstHomeActivity.this)) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(User_FirstHomeActivity.this);
                            ab.setMessage("Internet Connection is not avaliable.Please Turn ON Network Connection to download data.");
                            ab.setPositiveButton("Turn On Network Connection", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(I);
                                }
                            });
                            ab.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }

                            });
                            ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
                            ab.show();
                        } else {
                            progressDialog.show();
                            new GetVILLAGEList(_Panchayat_ID1).execute();
                        }
                    }

                } else if (arg2 == 0) {
                    //
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spvillage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 > 0) {
                    VILLAGE village = VillageList.get(arg2 - 1);
                    _Village_ID1 = village.getVILLAGEID();
                    _Village_ID = Integer.parseInt(_Village_ID1);
                    Village_Name = village.getVILLAGEName();

                    TollaList = dbHelper.getTOLLA(_Village_ID1);
                    if (TollaList.size() > 0) {
                        loadtollaSpinnerData();
                    } else {
                        if (!Utiilties.isOnline(User_FirstHomeActivity.this)) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(User_FirstHomeActivity.this);
                            ab.setMessage("Internet Connection is not avaliable.Please Turn ON Network Connection to download data.");
                            ab.setPositiveButton("Turn On Network Connection", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent I = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(I);
                                }
                            });
                            ab.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }

                            });
                            ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
                            ab.show();
                        } else {
                            progressDialog.show();
                            new GetTOLLAList(_Village_ID1).execute();
                        }
                    }
                    loadtollaSpinnerData();
                } else if (arg2 == 0) {
                    //
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        sptolla.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 > 0) {
                    TOLLA tolla = TollaList.get(arg2 - 1);
                    _Tolla_ID1 = tolla.getTOLLAID();
                    _Tolla_ID = Integer.parseInt(_Tolla_ID1);
                    Tolla_Name = tolla.getTOLLAName();
                    //TollaList = dbHelper.getTOLLA(_Village_ID1);
                    //loadtollaSpinnerData();
                } else if (arg2 == 0) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


    }

    private void loadDistSpinnerData() {
        String[] DistNameArray = new String[DistList.size() + 1];
        DistNameArray[0] = "-Please Select-";
        int i = 1;
        int setID = 0;
        for (Dist cert : DistList) {
            DistNameArray[i] = cert.getDistName().trim();
            int cid = Integer.parseInt(cert.getDistCode());
            if (cid == _Dist_ID) {
                setID = i;
            }
            i++;
        }

        Distadapter = new ArrayAdapter<String>(this, R.layout.dropdownlist, DistNameArray);
        Distadapter.setDropDownViewResource(R.layout.dropdownlist);
        if (spdist != null)
            spdist.setAdapter(Distadapter);
        spdist.setSelection(setID);
    }

    private void loadBlockSpinnerData() {
        String[] BLOCKNameArray = new String[BlockList.size() + 1];
        BLOCKNameArray[0] = "-Please Select-";
        int i = 1;

        int setID = 0;
        for (Block cert : BlockList) {
            BLOCKNameArray[i] = cert.getBlockName();
            Log.e(BLOCKNameArray[i], "i=" + i + " Code=" + cert.getBlockCode());
            int cid = Integer.parseInt(cert.getBlockCode());
            if (cid == _Block_ID) {
                setID = i;
            }
            i++;
        }

        Blockadapter = new ArrayAdapter<String>(this, R.layout.dropdownlist, BLOCKNameArray);
        Blockadapter.setDropDownViewResource(R.layout.dropdownlist);
        if (spBlock != null)
            spBlock.setAdapter(Blockadapter);
        spBlock.setSelection(setID);
    }

    private void loadpanchayatSpinnerData() {
        String[] PanNameArray = new String[PanchayatList.size() + 1];
        PanNameArray[0] = "-Please Select-";
        int i = 1;
        int setID = 0;
        for (PanchayatData cert : PanchayatList) {
            PanNameArray[i] = cert.getPname();
            Log.e(PanNameArray[i], "i=" + i + " Code=" + cert.getPcode());
            int cid = Integer.parseInt(cert.getPcode());
            if (cid == _Panchayat_ID) {
                setID = i;
            }
            i++;
        }
        Panchayatadapter = new ArrayAdapter<String>(this, R.layout.dropdownlist, PanNameArray);
        Panchayatadapter.setDropDownViewResource(R.layout.dropdownlist);
        if (sppanchayat != null)
            sppanchayat.setAdapter(Panchayatadapter);
        sppanchayat.setSelection(setID);
    }

    private void loadvillageSpinnerData() {
        String[] VillageNameArray = new String[VillageList.size() + 1];
        VillageNameArray[0] = "-Please Select-";
        int i = 1;

        int setID = 0;
        for (VILLAGE cert : VillageList) {
            VillageNameArray[i] = cert.getVILLAGEName();
            Log.e(VillageNameArray[i], "i=" + i + " Code=" + cert.getVILLAGEID());
            int cid = Integer.parseInt(cert.getVILLAGEID());
            if (cid == _Village_ID) {
                setID = i;
            }
            i++;
        }

        Villageadapter = new ArrayAdapter<String>(this, R.layout.dropdownlist, VillageNameArray);
        Villageadapter.setDropDownViewResource(R.layout.dropdownlist);
        if (spvillage != null)
            spvillage.setAdapter(Villageadapter);
        spvillage.setSelection(setID);
    }

    private void loadtollaSpinnerData() {
        String[] TollaNameArray = new String[TollaList.size() + 1];
        TollaNameArray[0] = "-Please Select-";
        int i = 1;

        int setID = 0;
        for (TOLLA cert : TollaList) {
            TollaNameArray[i] = cert.getTOLLAName();
            Log.e(TollaNameArray[i], "i=" + i + " Code=" + cert.getTOLLAID());
            int cid = Integer.parseInt(cert.getTOLLAID());
            if (cid == _Block_ID) {
                setID = i;
            }
            i++;
        }
        Tollaadapter = new ArrayAdapter<String>(this, R.layout.dropdownlist, TollaNameArray);
        Tollaadapter.setDropDownViewResource(R.layout.dropdownlist);
        if (sptolla != null)
            sptolla.setAdapter(Tollaadapter);
        sptolla.setSelection(setID);
    }

    public void onClickrefresh(View view) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(User_FirstHomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(User_FirstHomeActivity.this);
        }
        builder.setTitle("Delete Camp Data")
                .setMessage("Are you sure you want to delete this Camp Data?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        //    dbHelper.deleteblock();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }


    public void onClickNewMeterConnection(View view) {
        if (Block_Name.equalsIgnoreCase("na")) {
            Toast.makeText(User_FirstHomeActivity.this, "Please Select Block", Toast.LENGTH_SHORT).show();
        } else if (Panchayat_Name.equalsIgnoreCase("na")) {
            Toast.makeText(User_FirstHomeActivity.this, "Please Select Panchayat", Toast.LENGTH_SHORT).show();
        } else if (Village_Name.equalsIgnoreCase("na")) {
            Toast.makeText(User_FirstHomeActivity.this, "Please Select Village", Toast.LENGTH_SHORT).show();
        } else if (Tolla_Name.equalsIgnoreCase("na")) {
            Toast.makeText(User_FirstHomeActivity.this, "Please Select tola", Toast.LENGTH_SHORT).show();
        } else {
            address = new consumer_address();
            address.setConid(stringconid);
            address.setDistCode(CommonPref.getUserDetails(User_FirstHomeActivity.this).get_distcode());
            address.setBlockCode(_Block_ID1);
            address.setPanchayatcode(_Panchayat_ID1);
            address.setVillcode(_Village_ID1);
            address.setTollcode(_Tolla_ID1);
            if (Utiilties.isOnline(User_FirstHomeActivity.this)) {
                new updateaddress().execute();
            } else {
                long c = dbHelper.insertaddress(address);
                if (c >= 0) {
                    Intent cPannel = null;
                    cPannel = new Intent(getApplicationContext(), MeterReadingStatusActivity.class);
                    cPannel.putExtra("ACCOUNT_NO", stringAcNo);
                    cPannel.putExtra("FLAG", "0");
                    startActivity(cPannel);
                    finish();
                }
            }

        }

    }

    public class GetVILLAGEList extends AsyncTask<Void, Void, ArrayList<VILLAGE>> {
        String _panid;

        public GetVILLAGEList(String panid) {
            this._panid = panid;
        }

        private final ProgressDialog dialog = new ProgressDialog(User_FirstHomeActivity.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(User_FirstHomeActivity.this).create();

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Downloading Village data.\nPlease wait...");

        }

        @Override
        protected ArrayList<VILLAGE> doInBackground(Void... params) {

            ArrayList<VILLAGE> res1 = WebServiceHelper.loadVILLAGEList(_panid);
            return res1;
        }

        @Override
        protected void onPostExecute(ArrayList<VILLAGE> result) {

            if (result != null) {
                DataBaseHelper placeData = new DataBaseHelper(User_FirstHomeActivity.this);
                placeData.insertVILLAGE(result, _panid);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    VillageList = dbHelper.getVILLAGE(_panid);
                    loadvillageSpinnerData();
                    //Toast.makeText(User_FirstHomeActivity.this,"Village list downloaded.\r\nDownloading Tola list...",Toast.LENGTH_SHORT).show();
                    //progressDialog.show();
                    //new UserHomeActivity.GetTOLLAList(_panid).execute();
                }
            } else {
                //	Toast.makeText(UserHomeActivity.this,"Data already exist in mobile for panchayat"+PANCHAYAT_Name,Toast.LENGTH_SHORT).show();
                /*if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
					progressDialog.show();
				}
				new UserHomeActivity.GetTOLLAList(_panid).execute();*/

            }
        }
    }

    public class GetPAnchayatList extends AsyncTask<Void, Void, ArrayList<PanchayatData>> {
        String _panid;
        int id = 0;

        public GetPAnchayatList
                (String panid) {
            this._panid = panid;
        }

        private final ProgressDialog dialog = new ProgressDialog(User_FirstHomeActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(User_FirstHomeActivity.this).create();

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Downloading Panchayat data.\nPlease wait...");
        }

        @Override
        protected ArrayList<PanchayatData> doInBackground(Void... params) {

            ArrayList<PanchayatData> res1 = WebServiceHelper.loadPanchayatList(_panid, id);
            return res1;
        }

        @Override
        protected void onPostExecute(ArrayList<PanchayatData> result) {
            if (result != null) {
                DataBaseHelper placeData = new DataBaseHelper(User_FirstHomeActivity.this);
                placeData.insertPanchayat(result, _panid);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    PanchayatList = dbHelper.getPanchayatLocal(_panid);
                    loadpanchayatSpinnerData();

                }
            } else {

            }
        }
    }

    public class GetBlockList extends AsyncTask<Void, Void, ArrayList<Block>> {
        String _panid;
        int id = 0;

        public GetBlockList
                (String panid) {
            this._panid = panid;
        }

        private final ProgressDialog dialog = new ProgressDialog(User_FirstHomeActivity.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(User_FirstHomeActivity.this).create();

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Downloading Block data.\nPlease wait...");

        }

        @Override
        protected ArrayList<Block> doInBackground(Void... params) {


            ArrayList<Block> res1 = WebServiceHelper.loadBlockList(_panid, id);
            return res1;
        }

        @Override
        protected void onPostExecute(ArrayList<Block> result) {
            if (result != null) {
                DataBaseHelper placeData = new DataBaseHelper(User_FirstHomeActivity.this);
                placeData.insertBlock(result, _panid, id);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    BlockList = dbHelper.getallBlock(_panid);
                    loadBlockSpinnerData();
                }
            } else {
            }
        }
    }

    public class GetDistList extends AsyncTask<Void, Void, ArrayList<Dist>> {
        String _panid;
        int id = 0;

        public GetDistList
                (String panid) {
            this._panid = panid;
        }

        private final ProgressDialog dialog = new ProgressDialog(User_FirstHomeActivity.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(User_FirstHomeActivity.this).create();

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Downloading District data.\nPlease wait...");

        }

        @Override
        protected ArrayList<Dist> doInBackground(Void... params) {


            ArrayList<Dist> res1 = WebServiceHelper.loadDistList(_panid, id);
            return res1;
        }

        @Override
        protected void onPostExecute(ArrayList<Dist> result) {
            if (result != null) {
                DataBaseHelper placeData = new DataBaseHelper(User_FirstHomeActivity.this);
                placeData.insertDist(result, _panid, id);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    DistList = dbHelper.getallDist(_panid);
                    loadDistSpinnerData();
                }
            } else {

            }
        }
    }

    public class GetTOLLAList extends AsyncTask<Void, Void, ArrayList<TOLLA>> {
        String _panid;

        public GetTOLLAList(String panid) {
            this._panid = panid;
        }

        private final ProgressDialog dialog = new ProgressDialog(User_FirstHomeActivity.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(User_FirstHomeActivity.this).create();

        @Override
        protected void onPreExecute() {
            // this.dialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Downloading Tolla data.\nPlease wait...");
            // this.dialog.show();
        }

        @Override
        protected ArrayList<TOLLA> doInBackground(Void... params) {

            //check if database is empty for that panchayat id
            //if not then download from remote server
            DataBaseHelper placeData = new DataBaseHelper(User_FirstHomeActivity.this);
            SQLiteDatabase db = placeData.getReadableDatabase();
            ArrayList<TOLLA> res1 = WebServiceHelper.loadTOLLAList(_panid);
            return res1;
        }

        @Override
        protected void onPostExecute(ArrayList<TOLLA> result) {

            if (result != null) {
                DataBaseHelper placeData = new DataBaseHelper(User_FirstHomeActivity.this);
                placeData.insertTOLLA(result, _panid);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Download Completed", Toast.LENGTH_SHORT).show();
                    TollaList = dbHelper.getTOLLA(_panid);
                    loadtollaSpinnerData();
                }
            } else {
                //Toast.makeText(UserHomeActivity.this, "Data already exist in mobile for panchayat" + CommonPref.getUserDetails(UserHomeActivity.this).get_SubdivName(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    private class updateaddress extends AsyncTask<consumer_address, Void, String> {

        private final ProgressDialog dialog = new ProgressDialog(
                User_FirstHomeActivity.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(
                User_FirstHomeActivity.this).create();

        @Override
        protected void onPreExecute() {

            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Address Updating...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(consumer_address... address1) {
            return WebServiceHelper.updateaddress(address, CommonPref.getUserDetails(User_FirstHomeActivity.this));
        }

        @Override
        protected void onPostExecute(String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (result.contains("SUCCESS")) {
                    Intent cPannel = null;
                    cPannel = new Intent(getApplicationContext(), MeterReadingStatusActivity.class);
                    cPannel.putExtra("ACCOUNT_NO", stringAcNo);
                    cPannel.putExtra("FLAG", "0");
                    startActivity(cPannel);
                    finish();
                } else if (result.contains("UPDATED")) {
                    Toast.makeText(getApplicationContext(), "" + result, Toast.LENGTH_SHORT).show();
                    Intent cPannel = null;
                    cPannel = new Intent(getApplicationContext(), MeterReadingStatusActivity.class);
                    cPannel.putExtra("ACCOUNT_NO", stringAcNo);
                    cPannel.putExtra("FLAG", "0");
                    startActivity(cPannel);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "" + result, Toast.LENGTH_SHORT).show();
                    long c = dbHelper.insertaddress(address);
                    if (c >= 0) {
                        Intent cPannel = null;
                        cPannel = new Intent(getApplicationContext(), MeterReadingStatusActivity.class);
                        cPannel.putExtra("ACCOUNT_NO", stringAcNo);
                        cPannel.putExtra("FLAG", "0");
                        startActivity(cPannel);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Some error in database" + result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
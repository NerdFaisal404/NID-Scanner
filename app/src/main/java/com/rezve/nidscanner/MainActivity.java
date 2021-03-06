package com.rezve.nidscanner;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.rezve.nidscanner.history.HistoryActivity;
import com.rezve.nidscanner.history.HistoryViewModel;
import com.rezve.nidscanner.models.History;
import com.rezve.nidscanner.models.Nid;
import com.rezve.nidscanner.parser.DataParser;
import com.rezve.nidscanner.parser.NewNidDataParser;
import com.rezve.nidscanner.parser.OldNidDataParser;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private HistoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);

        Button historyBtn = findViewById(R.id.historyBtn);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
    }

    private void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PDF_417);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                String rawData = result.getContents();
                Utils.CARD_TYPE cardType = Utils.getCardType(rawData);

                if ( cardType == Utils.CARD_TYPE.SMART_NID_CARD ) {
                    NewNidDataParser parser = new NewNidDataParser(this, rawData);
                    saveCardData(parser);
                    showListActivity();
                } else if (cardType == Utils.CARD_TYPE.OLD_NID_CARD) {
                    OldNidDataParser parser = new OldNidDataParser(this, rawData);
                    saveCardData(parser);
                    showListActivity();
                } else {
                    Toast.makeText(this, "Invalid NID Card", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void saveCardData(DataParser parser) {
        String name = parser.getName();
        String nidNo = parser.getNidNo();
        String dateOfBirth = parser.getDateOfBirth();
        String issueDate = parser.getIssueDate();
        String rawData = parser.getRawData();

        Nid nid = new Nid(name, nidNo, dateOfBirth, issueDate, rawData, new Date());
        viewModel.insert(nid);
    }

    private void showListActivity() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Scanning successful", Toast.LENGTH_LONG).show();
    }

    public void viewHistory(View view) {
        Intent intent = new Intent(getBaseContext(), HistoryActivity.class);
        startActivity(intent);
    }
}

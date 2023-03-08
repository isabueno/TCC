package com.example.myapptcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextRecognition extends AppCompatActivity {

    EditText edittext1;
    EditText edittext2;
    String allText;
    TextView textVCalorias;
    TextView textValorCarboidrato;
    TextView textValorProteinas;
    TextView textValorGTotais;
    TextView textValorGSaturadas;
    TextView textValorGTrans;
    TextView textValorFibra;
    TextView textValorSodio;
    TextView textValorAcucares;
    TextView textVPorcao;
    List<String> processText = new ArrayList<>();
    Button buttonCalculate;
    String porcao;
    String calorias;
    String carboidrato;
    String proteinas;
    String totais;
    String saturadas;
    String trans;
    String fibra;
    String sodio;
    String acucares;
    String por;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        edittext1 = findViewById(R.id.edittext1);
        edittext2 = findViewById(R.id.edittext2);
        textVCalorias = findViewById(R.id.textVCalorias);
        textValorCarboidrato = findViewById(R.id.textValorCarboidrato);
        textValorProteinas = findViewById(R.id.textValorProteinas);
        textValorGTotais = findViewById(R.id.textValorGTotais);
        textValorGSaturadas = findViewById(R.id.textValorGSaturadas);
        textValorGTrans = findViewById(R.id.textValorGTrans);
        textValorFibra = findViewById(R.id.textValorFibra);
        textValorSodio = findViewById(R.id.textValorSodio);
        textValorAcucares = findViewById(R.id.textValorAcucares);
        textVPorcao = findViewById(R.id.textVPorcao);
        buttonCalculate = findViewById(R.id.buttonCalculate);

        String uriString = getIntent().getStringExtra("uri");
        Uri uri = Uri.parse(uriString);
        _extractTextFromUri(getApplicationContext(), uri);

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculate(view);
            }
        });
    }

    public void  _extractTextFromUri(Context context, Uri _uri) {
        TextRecognizer recognizer = com.google.mlkit.vision.text.TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        try {
            InputImage image = InputImage.fromFilePath(context, _uri);
            Task<Text> result =
                    recognizer.process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text visionText) {
                                    // Task completed successfully
                                    edittext1.setText(visionText.getText());
                                    allText = visionText.getText();

                                    for (Text.TextBlock block : visionText.getTextBlocks()) {
                                        String blockText = block.getText();
                                        Point[] blockCornerPoints = block.getCornerPoints();
                                        Rect blockFrame = block.getBoundingBox();
                                        Log.d("blocktext", blockText);
                                        for (Text.Line line : block.getLines()) {
                                            String lineText = line.getText();
                                            Point[] lineCornerPoints = line.getCornerPoints();
                                            Rect lineFrame = line.getBoundingBox();
                                            Log.d("lineText",lineText);
                                            for (Text.Element element : line.getElements()) {
                                                String elementText = element.getText();
                                                Point[] elementCornerPoints = element.getCornerPoints();
                                                Rect elementFrame = element.getBoundingBox();
                                                Log.d("elementText",elementText);
                                                processText.add(elementText);
                                            }
                                        }
                                    }
                                    processText(processText);

                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                        }
                                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processText(List<String> processText) {
        por = processText.get(4);
        porcao = por.replace("g(1", "");
        textVPorcao.setText(porcao);
        calorias = processText.get(23);
        textVCalorias.setText(calorias);
        carboidrato = processText.get(27);
        textValorCarboidrato.setText(carboidrato);
        String prot = processText.get(29);
        proteinas = prot.replace(",", ".");
        textValorProteinas.setText(proteinas);
        totais = processText.get(31);
        textValorGTotais.setText(totais);
        saturadas = processText.get(33);
        textValorGSaturadas.setText(saturadas);
        String gtrans = processText.get(35);
        trans = gtrans.replace(",", ".");
        textValorGTrans.setText(trans);
        String fali = processText.get(37);
        fibra = fali.replace("g", "");
        textValorFibra.setText(fibra);
        sodio = processText.get(38);
        textValorSodio.setText(sodio);
        String acuc = processText.get(46);
        acucares = acuc.replace(",", ".");
        textValorAcucares.setText(acucares);
    }

    public void calculate(View view){
        //parsing to int
        int nPorcao = Integer.parseInt(edittext2.getText().toString());
        int cPorcao = Integer.parseInt(porcao);
        int cCal = Integer.parseInt(calorias);
        int cCarbo = Integer.parseInt(carboidrato);
        double cProt = Double.parseDouble(proteinas);
        int cTotais = Integer.parseInt(totais);
        int cSaturadas = Integer.parseInt(saturadas);
        double cTrans = Double.parseDouble(trans);
        int cFibra = Integer.parseInt(fibra);
        int cSodio = Integer.parseInt(sodio);
        double cAcucares = Double.parseDouble(acucares);

        //calling methods to calculate
        int rCal = calcInt(cPorcao, cCal, nPorcao);
        int rCarbo = calcInt(cPorcao, cCarbo, nPorcao);
        int rTotais = calcInt(cPorcao, cTotais, nPorcao);
        int rSaturadas = calcInt(cPorcao, cSaturadas, nPorcao);
        int rFibra = calcInt(cPorcao, cFibra, nPorcao);
        int rSodio = calcInt(cPorcao, cSodio, nPorcao);
        double rProt = calc(cPorcao, cProt, nPorcao);
        double rTrans = calc(cPorcao, cTrans, nPorcao);
        double rAcucares = calc(cPorcao, cAcucares, nPorcao);

        //parsing to string
        String textPorcao = Integer.toString(nPorcao);
        String textCal = Integer.toString(rCal);
        String textCarbo = Integer.toString(rCarbo);
        String textTotais = Integer.toString(rTotais);
        String textSaturadas = Integer.toString(rSaturadas);
        String textFibra = Integer.toString(rFibra);
        String textSodio = Integer.toString(rSodio);
        String textProt = Double.toString(rProt);
        String textTrans = Double.toString(rTrans);
        String textAcucares = Double.toString(rAcucares);

        //setting the texts
        textVPorcao.setText(textPorcao);
        textVCalorias.setText(textCal);
        textValorCarboidrato.setText(textCarbo);
        textValorGTotais.setText(textTotais);
        textValorGSaturadas.setText(textSaturadas);
        textValorFibra.setText(textFibra);
        textValorSodio.setText(textSodio);
        textValorProteinas.setText(textProt);
        textValorGTrans.setText(textTrans);
        textValorAcucares.setText(textAcucares);
    }

    public double calc(int porcao, double valor, int novaPorcao){
        double result = ((valor*novaPorcao) / porcao);
        return result;
    }

    public int calcInt(int porcao, int valor, int novaPorcao){
        int result = ((valor*novaPorcao) / porcao);
        return result;
    }

    //String textres = Double.toString(result);
   //Log.d("textlog res", textres);
    //String textres = Integer.toString(result);
      //Log.d("textlog res", textres);
}
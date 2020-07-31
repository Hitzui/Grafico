package com.dysconcsa.app.grafico;

import com.dysconcsa.app.grafico.util.Variables;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@SpringBootApplication
public class GraficoApplication {

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                File f = new File(args[0]);
                if (f.isFile()) {
                    Variables.getInstance().file = f;
                    writeParameters(f.getPath());
                }
            }
        } catch (Exception ex) {
            writeParameters(ex.getMessage());
        }
        Application.launch(StockApplication.class, args);
    }

    public static void writeParameters(String value) {
        PrintWriter fw;
        try {
            fw = new PrintWriter("messages.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("--Start line--");
            bw.newLine();
            bw.write(value);
            bw.newLine();
            bw.write("--End line--");
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

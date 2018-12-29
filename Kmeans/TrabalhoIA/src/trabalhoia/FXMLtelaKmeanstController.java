package trabalhoia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import trabalhoia.classes.Base;

/**
 * FXML Controller class
 *
 * @author Luan Esteves
 */
public class FXMLtelaKmeanstController implements Initializable {

    @FXML
    private TextField txCentroides;
    @FXML
    private ComboBox<String> cbX;
    @FXML
    private ComboBox<String> cbY;
    @FXML
    private Pane pnGrafico;
    @FXML
    private TextField txArquivo;
    @FXML
    private Pane pnAcertosErros;

    String c[];
    ArrayList<Base> base;
    Base centros[];
    ArrayList<String> resultado;
    int ind_resultado[], qntCentroides;
    float g_minX;
    float g_MaxX;
    float g_minY;
    float g_MaxY;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txArquivo.setText("Iris.csv");
    }

    public void carregarComboX() {
        ArrayList<String> cb = new ArrayList();
        for (int i = 0; i < c.length; i++) {
            cb.add(c[i]);
        }
        ObservableList<String> modelo;
        modelo = FXCollections.observableArrayList(cb);
        cbX.setItems(modelo);
    }

    public void carregarComboY() {
        ArrayList<String> cb = new ArrayList();
        for (int i = 0; i < c.length; i++) {
            cb.add(c[i]);
        }
        ObservableList<String> modelo;
        modelo = FXCollections.observableArrayList(cb);
        cbY.setItems(modelo);
    }

    void valorescentroides() {
        centros = new Base[qntCentroides];

        for (int i = 0; i < qntCentroides; i++) {
            Base b = base.get(i);
            float v[] = new float[c.length];
            ////adicionar valores ao vetor vindo da Base
            for (int j = 0; j < v.length; j++) {
                v[j] = b.getV()[j];
            }

            centros[i] = new Base(v, i, 0, "");
        }
        ///iniciar a classe
        for (Base b : base) {
            b.setClasse(0);
        }
    }

    public void executar() {
        valorescentroides();
        float dist, min = Float.MAX_VALUE, mat_centros[][], soma;
        int posmin = 0, contador_centros[];
        boolean executar = true;
        while (executar) {
            executar = false;
            ///cada ponto
            for (Base b : base) {
                min = Float.MAX_VALUE;
                ///cada centroide
                for (int i = 0; i < qntCentroides; i++) {
                    soma = 0;
                    ///caracteristicas
                    for (int j = 0; j < c.length; j++) {
                        soma += Math.pow(b.getV()[j] - centros[i].getV()[j], 2);
                    }
                    dist = (float) Math.sqrt(soma);

                    if (dist < min) {
                        min = dist;
                        posmin = i;
                    }
                }
                if (b.getClasse() != posmin) {
                    executar = true;
                    ///nova classe
                    b.setClasse(posmin);
                }
            }

            ///redefinir centros
            if (executar) {
                ///matriz centros X caracteristicas
                mat_centros = new float[qntCentroides][c.length];
                contador_centros = new int[qntCentroides];
                //inicia matriz
                for (int i = 0; i < qntCentroides; i++) {
                    for (int j = 0; j < c.length; j++) {
                        mat_centros[i][j] = 0;
                    }
                    contador_centros[i] = 0;
                }

                ///somador de centros
                for (Base b : base) {
                    for (int i = 0; i < c.length; i++) {
                        mat_centros[b.getClasse()][i] += b.getV()[i];
                    }
                    contador_centros[b.getClasse()]++;
                }
                //novos centros
                for (int i = 0; i < qntCentroides; i++) {
                    float vetor[] = new float[c.length];
                    for (int j = 0; j < c.length; j++) {
                        vetor[j] = mat_centros[i][j] / contador_centros[i];
                    }
                    centros[i].setV(vetor);
                }
            }
        }
    }

    public void definirClasses() {

        resultado = new ArrayList();
        boolean existe;
        int maior, pos_maior = 0;

        //define quais os resultador finais existentes
        for (Base b : base) {
            existe = false;
            for (String s : resultado) {
                if (s.equals(b.getOriginal())) {
                    existe = true;
                }
            }
            if (!existe) {
                resultado.add(b.getOriginal());
            }
        }

        ///iniciar matriz para contabilizar cada item
        int totalizador[][] = new int[qntCentroides][resultado.size()];
        for (int i = 0; i < qntCentroides; i++) {
            for (int j = 0; j < resultado.size(); j++) {
                totalizador[i][j] = 0;
            }
        }

        ///quantidade de cada item
        for (Base b : base) {
            for (int i = 0; i < resultado.size(); i++) {
                if (b.getOriginal().equals(resultado.get(i))) {
                    totalizador[b.getClasse()][i]++;
                }
            }
        }

        //define maior
        ind_resultado = new int[qntCentroides];
        for (int i = 0; i < qntCentroides; i++) {
            ind_resultado[i] = -1;
        }

        for (int i = 0; i < qntCentroides; i++) {
            maior = Integer.MIN_VALUE;
            for (int j = 0; j < resultado.size(); j++) {
                if (totalizador[i][j] > maior) {
                    maior = totalizador[i][j];
                    pos_maior = j;
                }
            }
            ind_resultado[i] = pos_maior;
        }
    }

    public void exibir_individual(int classe) {
        int x = -1;
        int y = -1;

        for (int i = 0; i < c.length; i++) {
            if (c[i].equals(cbX.getSelectionModel().getSelectedItem())) {
                x = i;
            }
            if (c[i].equals(cbY.getSelectionModel().getSelectedItem())) {
                y = i;
            }
        }

        //tamGrafico(x, y);
        final NumberAxis xAxis = new NumberAxis(g_minX, g_MaxX, 0.5);
        final NumberAxis yAxis = new NumberAxis(g_minY, g_MaxY, 0.5);
        ScatterChart<Number, Number> sc = new ScatterChart<Number, Number>(xAxis, yAxis);
        sc = new ScatterChart<Number, Number>(xAxis, yAxis);
        xAxis.setLabel(c[x]);
        yAxis.setLabel(c[y]);
        sc.setTitle(resultado.get(ind_resultado[classe]));
        sc.setPrefSize(600, 390);

        XYChart.Series series[] = new XYChart.Series[resultado.size()];
        for (int i = 0; i < resultado.size(); i++) {
            XYChart.Series s = new XYChart.Series();
            s.setName("" + resultado.get(i));
            series[i] = s;
        }

        for (Base b : base) {
            if (b.getClasse() == classe) {
                for (int i = 0; i < resultado.size(); i++) {
                    if (resultado.get(i).equals(b.getOriginal())) {
                        series[i].getData().add(new XYChart.Data(b.getV()[x], b.getV()[y]));
                    }
                }
            }
        }

        for (int i = 0; i < resultado.size(); i++) {
            sc.getData().addAll(series[i]);
        }

        Pane p = new Pane();
        p.setMaxSize(600, 390);
        p.setMinSize(600, 390);
        p.setStyle("-fx-border-style: solid");
        p.getChildren().add(sc);
        pnAcertosErros.getChildren().add(p);
    }

    public void tamGrafico(int x, int y) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;
        float bx, by;
        for (Base b : base) {
            bx = b.getV()[x];
            by = b.getV()[y];

            if (bx > maxX) {
                maxX = bx;
            }

            if (by > maxY) {
                maxY = bx;
            }

            if (bx < minX) {
                minX = bx;
            }
            if (by < minY) {
                minY = by;
            }
        }

        g_minX = minX;
        g_MaxX = maxX;
        g_minY = minY;
        g_MaxY = minX;
        
       g_minX = 0;
        g_MaxX = 9;
        g_minY = 0;
        g_MaxY = 9;
    }

    public void grafico() {
        int x = -1;
        int y = -1;

        for (int i = 0; i < c.length; i++) {
            if (c[i].equals(cbX.getSelectionModel().getSelectedItem())) {
                x = i;
            }
            if (c[i].equals(cbY.getSelectionModel().getSelectedItem())) {
                y = i;
            }
        }

        tamGrafico(x, y);
        final NumberAxis xAxis = new NumberAxis(g_minX, g_MaxX, 0.5);
        final NumberAxis yAxis = new NumberAxis(g_minY, g_MaxY, 0.5);
        ScatterChart<Number, Number> sc = new ScatterChart<Number, Number>(xAxis, yAxis);
        sc = new ScatterChart<Number, Number>(xAxis, yAxis);
        xAxis.setLabel(c[x]);
        yAxis.setLabel(c[y]);
        sc.setTitle("K-Means");
        //sc.setLayoutX(500);
        //sc.setLayoutY(300);
        sc.setPrefSize(700, 500);
        XYChart.Series series[] = new XYChart.Series[centros.length + 1];

        for (int i = 0; i < centros.length; i++) {
            XYChart.Series s = new XYChart.Series();
            s.setName("" + i);
            series[i] = s;
        }
        XYChart.Series s = new XYChart.Series();
        s.setName("Centro");
        series[centros.length] = s;

        for (Base b : centros) {
            series[centros.length].getData().add(new XYChart.Data(b.getV()[x], b.getV()[y]));
        }

        for (Base b : base) {
            series[b.getClasse()].getData().add(new XYChart.Data(b.getV()[x], b.getV()[y]));
        }
        for (int i = 0; i < centros.length + 1; i++) {
            sc.getData().addAll(series[i]);
        }
        pnGrafico.getChildren().add(sc);
    }

    public void lerCSV(String arquivo) {
        BufferedReader br = null;
        String linha;
        base = new ArrayList();
        try {
            br = new BufferedReader(new FileReader(arquivo));
            linha = br.readLine();
            ///ler nome das classes
            String[] l = linha.split(",");
            c = new String[l.length - 2];
            for (int i = 1, j = 0; i < l.length - 1; i++, j++) {
                c[j] = l[i];
            }

            while ((linha = br.readLine()) != null) {
                l = linha.split(",");
                float v[] = new float[l.length - 2];
                int id = Integer.parseInt(l[0]);
                for (int i = 1, j = 0; i < l.length - 1; i++, j++) {
                    v[j] = Float.parseFloat(l[i]);
                }
                String species = l[l.length - 1];

                base.add(new Base(v, 0, id, species));
            }

            carregarComboX();
            carregarComboY();
        } catch (FileNotFoundException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Arquivo não existe!");
            a.showAndWait();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void clkCarregarArquivo(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        // Seta o título do diálogo.
        fileChooser.setDialogTitle("Selecione o arquivo fonte");
        // Define o filtro de seleção.
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
                "Arquivo CSV (*.csv)", "csv"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        // Impede seleções múltiplas.
        fileChooser.setMultiSelectionEnabled(false);
        // Exibe o diálogo.
        fileChooser.showOpenDialog(null);
        File arqsel = fileChooser.getSelectedFile();
        if (arqsel != null) {
            txArquivo.setText(arqsel.getAbsolutePath());
        }
    }

    public void exibir_graficos() {
        pnAcertosErros.getChildren().clear();
        pnGrafico.getChildren().clear();
        grafico();
        for (int i = 0; i < qntCentroides; i++) {
            exibir_individual(i);
        }
    }

    @FXML
    private void clkExecutar(ActionEvent event) {
        lerCSV(txArquivo.getText());
        qntCentroides = Integer.parseInt(txCentroides.getText());
        executar();
        cbX.getSelectionModel().selectFirst();
        cbY.getSelectionModel().selectLast();
        definirClasses();
        exibir_graficos();
    }

    @FXML
    private void clkVisualizar(ActionEvent event) {
        exibir_graficos();
    }
}

package com.dysconcsa.app.grafico.util;

import com.dysconcsa.app.grafico.controller.AdemeController;
import com.dysconcsa.app.grafico.dao.DaoValores;
import com.dysconcsa.app.grafico.model.AdemeProperty;
import com.dysconcsa.app.grafico.model.ValoresProperty;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CrearXmlValores {
    private ValoresProperty valoresProperty;
    private Integer linea;
    private Document document;

    public CrearXmlValores() {
    }

    public void setDocument() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            AlertError.showAlert(e);
        }
        if (dBuilder != null) {
            this.document = dBuilder.newDocument();
        }
    }

    @SuppressWarnings("static-access")
	public void prepararElementos(ObservableList<ValoresProperty> rows_x, AnchorPane anchorPane) {
        Utility utility = new Utility();
        JFXButton buttonAceptar = new JFXButton("Aceptar");
        JFXDialog dialog = utility.showDialog(anchorPane, "Ademe", "Debe especificar los ademes para continuar. Intente de nuevo", buttonAceptar);
        buttonAceptar.setOnAction(e -> dialog.close());
        try {
            ObservableList<AdemeProperty> ademeProperties = AdemeController.ademeProperties;
            if (ademeProperties.size() <= 0) {
                dialog.show();
                return;
            }
            DaoValores daoValores = new DaoValores();
            linea = 1;
            Element rootElement = this.document.createElement("elementos");
            Element ademeElement = this.document.createElement("ademes");
            document.appendChild(rootElement);
            rootElement.appendChild(ademeElement);
            for (AdemeProperty property : ademeProperties) {
                Element valores = document.createElement("valores");
                ademeElement.appendChild(valores);
                valores.setAttribute("linea", String.valueOf(linea));
                Element profundidad = document.createElement("profundidad");
                profundidad.appendChild(document.createTextNode(Double.toString(property.getProfundidad())));
                valores.appendChild(profundidad);
                Element descripcion = document.createElement("descripcion");
                descripcion.appendChild(document.createTextNode(property.getDescripcion()));
                valores.appendChild(descripcion);
                linea += 1;
            }
            linea = 1;
            rows_x.stream().peek((node) -> {
                //elemento raiz
                this.valoresProperty = node;
            }).map((_item) -> document.createElement("valores")).peek(rootElement::appendChild).peek((valores) -> {
                Attr atrr = document.createAttribute("linea");
                atrr.setValue("" + linea);
                valores.setAttributeNode(atrr);
            }).peek((valores) -> {
                //elemento espesor
                Element espesor = document.createElement("espesor");
                espesor.appendChild(document.createTextNode(Double.toString(valoresProperty.getEspesor())));
                valores.appendChild(espesor);
            }).peek((valores) -> {
                //elemento trepano
                Element trepano = document.createElement("trepano");
                trepano.appendChild(document.createTextNode(valoresProperty.getTrepano()));
                valores.appendChild(trepano);
            }).peek((valores) -> {
                //elemento tipo suelo
                Element tipoSuelo = document.createElement("tiposuelo");
                tipoSuelo.appendChild(document.createTextNode(Integer.toString(valoresProperty.getTipoSuelo())));
                valores.appendChild(tipoSuelo);
            }).peek((valores) -> {
                //elemento limite liquido
                Element limiteLiquido = document.createElement("limiteliquido");
                limiteLiquido.appendChild(document.createTextNode(valoresProperty.getLimiteLiquido()));
                valores.appendChild(limiteLiquido);
            }).peek((valores) -> {
                //elemento indice de plasticidad
                Element indicePlasticidad = document.createElement("indiceplasticidad");
                indicePlasticidad.appendChild(document.createTextNode(valoresProperty.getIndicePlasticidad()));
                valores.appendChild(indicePlasticidad);
            }).peek((valores) -> {
                //elemento indice de humedad
                Element indiceHumedad = document.createElement("indicehumedad");
                indiceHumedad.appendChild(document.createTextNode(Double.toString((valoresProperty.getIndiceHumedad()))));
                valores.appendChild(indiceHumedad);
            }).peek((valores) -> {
                //elemento recobro
                Element recobro = document.createElement("recobro");
                recobro.appendChild(document.createTextNode(Integer.toString(valoresProperty.getRecobro())));
                valores.appendChild(recobro);
            }).peek((valores) -> {
                //elemento golpes
                Element golpes = document.createElement("golpes");
                golpes.appendChild(document.createTextNode(Integer.toString((valoresProperty.getGolpes()))));
                valores.appendChild(golpes);
            }).peek((valores) -> {
                Element idempresa = document.createElement("idempresa");
                idempresa.appendChild(document.createTextNode(Integer.toString((valoresProperty.getIdEmpresa()))));
                valores.appendChild(idempresa);
            }).peek((valores)->{
                Element elevacion = document.createElement("elevacion");
                elevacion.appendChild(document.createTextNode(Double.toString(valoresProperty.getElevacion())));
                valores.appendChild(elevacion);
            }).peek((_item) -> linea += 1).forEachOrdered((_item) -> daoValores.save(valoresProperty));

        } catch (DOMException ex) {
            AlertError.showAlert(ex);
        } catch (NullPointerException nullPointer) {
            dialog.show();
        }
    }

    /**
     * <p>Guardamos el archivo xml despues de haber generado los datos en el TableView</p>
     *
     * @param file Lugar donde se guardar el archivo XML
     */
    public void guardarArchivoXml(File file) {
        try {
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            // Output to console for testing
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (TransformerException ex) {
            AlertError.showAlert(ex);
        }
    }

    public List<AdemeProperty> ademeProperties = new ArrayList<>();

    /**
     * <p>Cargar archivo xml desde la ubicación especificada</p>
     *
     * @return
     */
    public ObservableList<ValoresProperty> cargarArchivoXml(File file) {
        try {
            ademeProperties = FXCollections.observableArrayList();
            if (file == null) {
                return FXCollections.observableArrayList();
            }
            ObservableList<ValoresProperty> datos = FXCollections.observableArrayList();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("elementos");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    NodeList espesorList = eElement.getElementsByTagName("espesor");
                    NodeList ademeList = eElement.getElementsByTagName("profundidad");
                    for (int i = 0; i < ademeList.getLength(); i++) {
                        Double profundidad = Double.parseDouble(eElement.getElementsByTagName("profundidad").item(i).getTextContent());
                        String descripcion = eElement.getElementsByTagName("descripcion").item(i).getTextContent();
                        AdemeProperty ademeProperty = new AdemeProperty(profundidad, descripcion);
                        ademeProperties.add(ademeProperty);
                    }
                    for (int k = 0; k < espesorList.getLength(); k++) {
                        double espesor = Double.parseDouble(espesorList.item(k).getTextContent());
                        String trepano = eElement.getElementsByTagName("trepano").item(k).getTextContent();
                        if (trepano == null || trepano.length() <= 0) trepano = "";
                        int tipoSuelo = Integer.parseInt(eElement.getElementsByTagName("tiposuelo").item(k).getTextContent());
                        String limiteLiquido = eElement.getElementsByTagName("limiteliquido").item(k).getTextContent();
                        if (limiteLiquido == null || limiteLiquido.length() <= 0) limiteLiquido = "";
                        String indicePlasticidad = eElement.getElementsByTagName("indiceplasticidad").item(k).getTextContent();
                        if (indicePlasticidad == null || indicePlasticidad.length() <= 0) indicePlasticidad = "";
                        double indiceHumedad = Double.parseDouble(eElement.getElementsByTagName("indicehumedad").item(k).getTextContent());
                        int recobro = Integer.parseInt(eElement.getElementsByTagName("recobro").item(k).getTextContent());
                        int golpes = Integer.parseInt(eElement.getElementsByTagName("golpes").item(k).getTextContent());
                        int empresa = Integer.parseInt(eElement.getElementsByTagName("idempresa").item(k).getTextContent());
                        double elevacion = Double.parseDouble(eElement.getElementsByTagName("elevacion").item(k).getTextContent());
                        datos.add(new ValoresProperty(espesor, trepano, tipoSuelo, limiteLiquido,
                                indicePlasticidad, indiceHumedad, recobro, golpes, empresa,elevacion));
                    }
                }
            }
            return datos;
        } catch (IOException | NumberFormatException | ParserConfigurationException | DOMException | SAXException ex) {
            AlertError.showAlert(ex);
            return FXCollections.observableArrayList();
        }
    }
}
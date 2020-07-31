package com.dysconcsa.app.grafico.util;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dysconcsa.app.grafico.model.AdemeProperty;
import com.dysconcsa.app.grafico.model.ClasificacionSucsProperty;
import com.dysconcsa.app.grafico.model.DatosCampoProperty;
import com.dysconcsa.app.grafico.model.DatosSondeo;
import com.dysconcsa.app.grafico.model.HumedadProperty;
import com.dysconcsa.app.grafico.model.TrepanoProperty;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;

public class ArchivoXml {

	private Integer linea;
	private Document document;
	private Element rootElement;

	public ArchivoXml() {
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
			this.rootElement = this.document.createElement("elementos");
			document.appendChild(rootElement);
		}
	}

	public void prepararElementosDatos(ObservableList<DatosCampoProperty> datosCampoProperties, AnchorPane anchorPane) {
		try {
			JFXButton buttonAceptar = new JFXButton("Aceptar");
			JFXDialog dialog = Utility.showDialog(anchorPane, "Datos",
					"Debe especificar los datos de campo para poder continuar", buttonAceptar);
			buttonAceptar.setOnAction(e -> dialog.close());
			if (datosCampoProperties.size() <= 0) {
				dialog.show();
				return;
			}
			linea = 1;
			Element datosElement = document.createElement("datos");
			rootElement.appendChild(datosElement);
			for (DatosCampoProperty datos : datosCampoProperties) {
				Element valores = document.createElement("valores");
				datosElement.appendChild(valores);
				valores.setAttribute("linea", String.valueOf(linea));
				Element profundidadInicial = document.createElement("profundidadInicial");
				profundidadInicial.appendChild(document.createTextNode(Double.toString(datos.getProfundidadInicial())));
				valores.appendChild(profundidadInicial);
				Element profundidadFinal = document.createElement("profundidadFinal");
				profundidadFinal.appendChild(document.createTextNode(Double.toString(datos.getProfundidadFinal())));
				valores.appendChild(profundidadFinal);
				Element recobro = document.createElement("recobro");
				recobro.appendChild(document.createTextNode(Integer.toString(datos.getRecobro())));
				valores.appendChild(recobro);
				Element golpe1 = document.createElement("golpe1");
				golpe1.appendChild(document.createTextNode(Integer.toString(datos.getGolpe1())));
				valores.appendChild(golpe1);
				Element golpe2 = document.createElement("golpe2");
				golpe2.appendChild(document.createTextNode(Integer.toString(datos.getGolpe2())));
				valores.appendChild(golpe2);
				Element golpe3 = document.createElement("golpe3");
				golpe3.appendChild(document.createTextNode(Integer.toString(datos.getGolpe3())));
				valores.appendChild(golpe3);
				linea += 1;
			}
		} catch (Exception ex) {
			AlertError.showAlert(ex);
		}
	}

	public void prepararElementosProfundidad(DatosSondeo datosSondeo) {
		try {
			System.out.println(datosSondeo);
			Element valores = document.createElement("valores");
			Element rootProfundidad = document.createElement("estrato");
			rootElement.appendChild(rootProfundidad);
			rootProfundidad.appendChild(valores);
			Element elemntSondeoNumero = document.createElement("sondeoNumero");
			elemntSondeoNumero.appendChild(document.createTextNode(datosSondeo.getSondeoNumero()));
			valores.appendChild(elemntSondeoNumero);
			Element elementProfundidadMinima = document.createElement("profundidadMinima");
			elementProfundidadMinima
					.appendChild(document.createTextNode(Double.toString(datosSondeo.getProfundidadMinima())));
			valores.appendChild(elementProfundidadMinima);
			Element elementProfundidadMaxima = document.createElement("profundidadMaxima");
			elementProfundidadMaxima
					.appendChild(document.createTextNode(Double.toString(datosSondeo.getProfundidadMaxima())));
			valores.appendChild(elementProfundidadMaxima);
			Element elementElevacion = document.createElement("elevacion");
			elementElevacion.appendChild(document.createTextNode(Double.toString(datosSondeo.getElevacion())));
			valores.appendChild(elementElevacion);
			Element elementLugar = document.createElement("lugar");
			elementLugar.appendChild(document.createTextNode(datosSondeo.getLugar()));
			valores.appendChild(elementLugar);
			Element elementObservaciones = document.createElement("observaciones");
			elementObservaciones.appendChild(document.createTextNode(datosSondeo.getObservaciones()));
			valores.appendChild(elementObservaciones);
			Element elementOperador = document.createElement("operador");
			elementOperador.appendChild(document.createTextNode(datosSondeo.getOperador()));
			valores.appendChild(elementOperador);
			Element elementArchivo = document.createElement("archivo");
			elementArchivo.appendChild(document.createTextNode(datosSondeo.getArchivo()));
			valores.appendChild(elementArchivo);
			Element elementNivelFreatico = document.createElement("nivelFreatico");
			elementNivelFreatico.appendChild(document.createTextNode(datosSondeo.getNivelFreatico()));
			valores.appendChild(elementNivelFreatico);
			Element elementFecha = document.createElement("fecha");
			elementFecha.appendChild(document.createTextNode(datosSondeo.getFecha()));
			valores.appendChild(elementFecha);
		} catch (Exception ex) {
			AlertError.showAlert(ex);
		}
	}

	public void prepararElementosClasificacion(ObservableList<ClasificacionSucsProperty> clasificacionSucsProperties,
			AnchorPane anchorPane) {
		try {
			JFXButton buttonAceptar = new JFXButton("Aceptar");
			JFXDialog dialog = Utility.showDialog(anchorPane, "Datos",
					"Debe especificar los datos de campo para poder continuar", buttonAceptar);
			buttonAceptar.setOnAction(e -> dialog.close());
			if (clasificacionSucsProperties.size() <= 0) {
				dialog.show();
				return;
			}
			linea = 1;
			Element elementClasificacion = document.createElement("clasificacion");
			rootElement.appendChild(elementClasificacion);
			for (ClasificacionSucsProperty datos : clasificacionSucsProperties) {
				Element valores = document.createElement("valores");
				elementClasificacion.appendChild(valores);
				valores.setAttribute("linea", String.valueOf(linea));
				Element profundidad = document.createElement("profundidad");
				profundidad.appendChild(document.createTextNode(Double.toString(datos.getProfundidad())));
				valores.appendChild(profundidad);
				Element limiteLiquido = document.createElement("limiteLiquido");
				limiteLiquido.appendChild(document.createTextNode(Integer.toString(datos.getLimiteLiquido())));
				valores.appendChild(limiteLiquido);
				Element indicePlasticidad = document.createElement("indicePlasticidad");
				indicePlasticidad.appendChild(document.createTextNode(Integer.toString(datos.getIndicePlasticidad())));
				valores.appendChild(indicePlasticidad);
				Element tipoSuelo = document.createElement("tipoSuelo");
				tipoSuelo.appendChild(document.createTextNode(Integer.toString(datos.getTipoSuelo())));
				valores.appendChild(tipoSuelo);
				Element descripcion = document.createElement("descripcion");
				descripcion.appendChild(document.createTextNode(datos.getDescripcion().toUpperCase()));
				valores.appendChild(descripcion);
				Element color = document.createElement("colorSucs");
				color.appendChild(document.createTextNode(datos.getColor().name()));
				valores.appendChild(color);
				Element pattern = document.createElement("pattern");
				pattern.appendChild(document.createTextNode(datos.getPattern().name()));
				valores.appendChild(pattern);
				linea += 1;
			}
		} catch (Exception ex) {
			AlertError.showAlert(ex);
		}
	}

	@SuppressWarnings("static-access")
	public void prepararElementHumedad(ObservableList<HumedadProperty> humedadProperties, AnchorPane anchorPane) {
		Utility utility = new Utility();
		JFXButton buttonAceptar = new JFXButton("Aceptar");
		JFXDialog dialog = utility.showDialog(anchorPane, "Datos",
				"Debe especificar los datos de campo para poder continuar", buttonAceptar);
		buttonAceptar.setOnAction(e -> dialog.close());
		if (humedadProperties.size() <= 0) {
			dialog.show();
			return;
		}
		linea = 1;
		Element elementHumedad = document.createElement("humedad");
		rootElement.appendChild(elementHumedad);
		for (HumedadProperty datos : humedadProperties) {
			Element valores = document.createElement("valores");
			elementHumedad.appendChild(valores);
			valores.setAttribute("linea", String.valueOf(linea));
			Element profundidadInicial = document.createElement("profundidadInicial");
			profundidadInicial.appendChild(document.createTextNode(Double.toString(datos.getProfundidadInicial())));
			valores.appendChild(profundidadInicial);
			Element profundidadFinal = document.createElement("profundidadFinal");
			profundidadFinal.appendChild(document.createTextNode(Double.toString(datos.getProfundidadFinal())));
			valores.appendChild(profundidadFinal);
			Element humedad = document.createElement("humedad");
			humedad.appendChild(document.createTextNode(Double.toString(datos.getHumedad())));
			valores.appendChild(humedad);
			linea += 1;
		}
	}

	@SuppressWarnings("static-access")
	public void prepararElementosAdeme(ObservableList<AdemeProperty> ademeProperties, AnchorPane anchorPane) {
		Utility utility = new Utility();
		JFXButton buttonAceptar = new JFXButton("Aceptar");
		JFXDialog dialog = utility.showDialog(anchorPane, "Datos",
				"Debe especificar los datos de campo para poder continuar", buttonAceptar);
		buttonAceptar.setOnAction(e -> dialog.close());
		if (ademeProperties.size() <= 0) {
			dialog.show();
			return;
		}
		linea = 1;
		Element elementAdeme = document.createElement("ademe");
		rootElement.appendChild(elementAdeme);
		for (AdemeProperty datos : ademeProperties) {
			Element valores = document.createElement("valores");
			elementAdeme.appendChild(valores);
			valores.setAttribute("linea", String.valueOf(linea));
			Element profundidad = document.createElement("profundidad");
			profundidad.appendChild(document.createTextNode(String.valueOf(datos.getProfundidad())));
			valores.appendChild(profundidad);
			Element descripcion = document.createElement("descripcion");
			descripcion.appendChild(document.createTextNode(datos.getDescripcion()));
			valores.appendChild(descripcion);
			linea += 1;
		}
	}

	@SuppressWarnings("static-access")
	public void prepararElementosTrepano(ObservableList<TrepanoProperty> trepanoProperties, AnchorPane anchorPane) {
		Utility utility = new Utility();
		JFXButton buttonAceptar = new JFXButton("Aceptar");
		JFXDialog dialog = utility.showDialog(anchorPane, "Datos",
				"Debe especificar los datos de campo para poder continuar", buttonAceptar);
		buttonAceptar.setOnAction(e -> dialog.close());
		if (trepanoProperties.size() <= 0) {
			dialog.show();
			return;
		}
		linea = 1;
		Element elementTrepano = document.createElement("trepano");
		rootElement.appendChild(elementTrepano);
		for (TrepanoProperty dato : trepanoProperties) {
			Element valores = document.createElement("valores");
			elementTrepano.appendChild(valores);
			valores.setAttribute("linea", String.valueOf(linea));
			Element profundidad = document.createElement("profundidad");
			profundidad.appendChild(document.createTextNode(String.valueOf(dato.getProfundidad())));
			valores.appendChild(profundidad);
			Element trepano = document.createElement("trepano");
			trepano.appendChild(document.createTextNode(dato.getTrepano().toUpperCase()));
			valores.appendChild(trepano);
			linea += 1;
		}
	}

	/**
	 * <p>
	 * Guardamos el archivo xml despues de haber generado los datos en el TableView
	 * </p>
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
			/*
			 * StreamResult consoleResult = new StreamResult(System.out);
			 * transformer.transform(source, consoleResult);
			 */
		} catch (TransformerException ex) {
			AlertError.showAlert(ex);
		}
	}

	public ObservableList<DatosCampoProperty> cargarDatosCampo(File file) {
		try {
			if (file == null) {
				return FXCollections.observableArrayList();
			}
			ObservableList<DatosCampoProperty> datos = FXCollections.observableArrayList();
			Element element = this.getNodeList(file, "datos");
			NodeList nodeList = element.getElementsByTagName("valores");
			for (int j = 0; j < nodeList.getLength(); j++) {
				Double profundidadInicial = Double
						.parseDouble(element.getElementsByTagName("profundidadInicial").item(j).getTextContent());
				Double profundidadFinal = Double
						.parseDouble(element.getElementsByTagName("profundidadFinal").item(j).getTextContent());
				Integer recobro = Integer.parseInt(element.getElementsByTagName("recobro").item(j).getTextContent());
				Integer golpe1 = Integer.parseInt(element.getElementsByTagName("golpe1").item(j).getTextContent());
				Integer golpe2 = Integer.parseInt(element.getElementsByTagName("golpe2").item(j).getTextContent());
				Integer golpe3 = Integer.parseInt(element.getElementsByTagName("golpe3").item(j).getTextContent());
				datos.add(
						new DatosCampoProperty(profundidadInicial, profundidadFinal, recobro, golpe1, golpe2, golpe3));
			}
			return datos;
		} catch (Exception ex) {
			ex.printStackTrace();
			return FXCollections.observableArrayList();
		}
	}

	public ObservableList<ClasificacionSucsProperty> cargarDatosClasificacion(File file) {
		try {
			ObservableList<ClasificacionSucsProperty> datos = FXCollections.observableArrayList();
			Element element = this.getNodeList(file, "clasificacion");
			NodeList nodeList = element.getElementsByTagName("valores");
			for (int j = 0; j < nodeList.getLength(); j++) {
				Double profundidad = Double
						.parseDouble(element.getElementsByTagName("profundidad").item(j).getTextContent());
				Integer limiteLiquido = Integer
						.parseInt(element.getElementsByTagName("limiteLiquido").item(j).getTextContent());
				Integer indicePlasticidad = Integer
						.parseInt(element.getElementsByTagName("indicePlasticidad").item(j).getTextContent());
				Integer tipoSuelo = Integer
						.parseInt(element.getElementsByTagName("tipoSuelo").item(j).getTextContent());
				String descripcion = element.getElementsByTagName("descripcion").item(j).getTextContent();
				String color;
				String pattern;
				try {
					color = element.getElementsByTagName("colorSucs").item(j).getTextContent();
					pattern = element.getElementsByTagName("pattern").item(j).getTextContent();
				} catch (Exception ex) {
					color = IndexedColors.WHITE.name();
					pattern = FillPatternType.NO_FILL.name();
				}
				datos.add(new ClasificacionSucsProperty(profundidad, limiteLiquido, indicePlasticidad, tipoSuelo,
						descripcion, IndexedColors.valueOf(color), FillPatternType.valueOf(pattern)));
			}
			return datos;
		} catch (Exception ex) {
			ex.printStackTrace();
			return FXCollections.observableArrayList();
		}
	}

	public ObservableList<HumedadProperty> cargarDatosHumedad(File file) {
		try {
			ObservableList<HumedadProperty> datos = FXCollections.observableArrayList();
			Element element = this.getNodeList(file, "humedad");
			NodeList nodeList = element.getElementsByTagName("valores");
			for (int j = 0; j < nodeList.getLength(); j++) {
				Double profundidadInicial = Double
						.parseDouble(element.getElementsByTagName("profundidadInicial").item(j).getTextContent());
				Double profundidadFinal = Double
						.parseDouble(element.getElementsByTagName("profundidadFinal").item(j).getTextContent());
				Double humedad = Double.parseDouble(element.getElementsByTagName("humedad").item(j).getTextContent());
				datos.add(new HumedadProperty(profundidadInicial, profundidadFinal, humedad));
			}
			return datos;
		} catch (Exception ex) {
			AlertError.showAlert(ex);
			return FXCollections.observableArrayList();
		}
	}

	public List<DatosSondeo> cargarDatosIniciales(File file) {
		List<DatosSondeo> datos = new ArrayList<>();
		try {
			Element element = this.getNodeList(file, "estrato");
			NodeList nodeList = element.getElementsByTagName("valores");
			for (int j = 0; j < nodeList.getLength(); j++) {
				String sondeoNumero;
				String lugar;
				String observaciones;
				String operador;
				String nivelFreatico;
				String archivo;
				double profundidadMinima;
				double profundidadMaxima;
				double elevacion;
				String fecha;
				try {
					sondeoNumero = element.getElementsByTagName("sondeoNumero").item(j).getTextContent();
					lugar = element.getElementsByTagName("lugar").item(j).getTextContent();
					observaciones = element.getElementsByTagName("observaciones").item(j).getTextContent();
					operador = element.getElementsByTagName("operador").item(j).getTextContent();
					nivelFreatico = element.getElementsByTagName("nivelFreatico").item(j).getTextContent();
					archivo = element.getElementsByTagName("archivo").item(j).getTextContent();
					profundidadMinima = Double
							.parseDouble(element.getElementsByTagName("profundidadMinima").item(j).getTextContent());
					profundidadMaxima = Double
							.parseDouble(element.getElementsByTagName("profundidadMaxima").item(j).getTextContent());
					elevacion = Double.parseDouble(element.getElementsByTagName("elevacion").item(j).getTextContent());
					fecha = element.getElementsByTagName("fecha").item(j).getTextContent();
				} catch (Exception ex) {
					sondeoNumero = "1";
					lugar = "";
					observaciones = "";
					operador = "";
					nivelFreatico = "";
					archivo = "";
					profundidadMinima = 0d;
					profundidadMaxima = 0d;
					elevacion = 0d;
					fecha = LocalDate.now().toString();
				}
				datos.add(new DatosSondeo(sondeoNumero, profundidadMinima, profundidadMaxima, lugar, observaciones,
						operador, archivo, nivelFreatico, elevacion, fecha));
			}
			return datos;
		} catch (Exception ex) {
			AlertError.showAlert(ex);
			return null;
		}
	}

	public ObservableList<AdemeProperty> cargarDatosAdeme(File file) {
		try {
			ObservableList<AdemeProperty> datos = FXCollections.observableArrayList();
			Element element = this.getNodeList(file, "ademe");
			NodeList nodeList = element.getElementsByTagName("valores");
			for (int j = 0; j < nodeList.getLength(); j++) {
				Double profundidad = Double
						.parseDouble(element.getElementsByTagName("profundidad").item(j).getTextContent());
				String descripcion = element.getElementsByTagName("descripcion").item(j).getTextContent();
				datos.add(new AdemeProperty(profundidad, descripcion));
			}
			return datos;
		} catch (Exception ex) {
			return FXCollections.observableArrayList();
		}
	}

	public ObservableList<TrepanoProperty> cargarDatosTrepano(File file) {
		try {
			ObservableList<TrepanoProperty> datos = FXCollections.observableArrayList();
			Element element = this.getNodeList(file, "trepano");
			NodeList nodeList = element.getElementsByTagName("valores");
			for (int j = 0; j < nodeList.getLength(); j++) {
				Double profundidad = Double
						.parseDouble(element.getElementsByTagName("profundidad").item(j).getTextContent());
				String descripcion = element.getElementsByTagName("trepano").item(j).getTextContent();
				datos.add(new TrepanoProperty(profundidad, descripcion));
			}
			return datos;
		} catch (Exception ex) {
			return FXCollections.observableArrayList();
		}
	}

	private Element getNodeList(File file, String node) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Element _ele = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("elementos");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					NodeList _datos = eElement.getElementsByTagName(node);
					Node _node = _datos.item(0);
					if (_node.getNodeType() == Node.ELEMENT_NODE) {
						_ele = (Element) _node;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _ele;
	}

}

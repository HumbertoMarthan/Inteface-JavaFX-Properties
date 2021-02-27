package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Principal extends Application {

	private Text actionStatus;
	private Stage savedStage;
	private static final String titleTxt = "Gerenciador de Config";
	private static Properties cfg = new Properties();

	private TableView<Tabela> table = new TableView<Tabela>();
	private List<Tabela> data = new ArrayList<>();

	public static void main(String[] args) {
		Application.launch(args);
	}
	
	
	// inicio da aplicação FX
	@Override
	public void start(Stage primaryStage) {
		telaInicial(primaryStage);
		savedStage = primaryStage;
	}

	private Stage telaInicial(Stage primaryStage) {

		primaryStage.setTitle(titleTxt);

		// Window label
		Label label = new Label("Anexe um arquivo");
		label.setTextFill(Color.DARKBLUE);
		label.setFont(Font.font("Calibri", FontWeight.BOLD, 36));
		HBox labelHb = new HBox();
		labelHb.setAlignment(Pos.CENTER);
		labelHb.getChildren().add(label);

		// Buttons
		Button btn1 = new Button("Anexar");
		btn1.setOnAction(new SingleFcButtonListener());
		HBox buttonHb1 = new HBox(10);
		buttonHb1.setAlignment(Pos.CENTER);
		buttonHb1.getChildren().addAll(btn1);

		// Status message text
		actionStatus = new Text();
		actionStatus.setFont(Font.font("Calibri", FontWeight.NORMAL, 20));
		actionStatus.setFill(Color.FIREBRICK);

		// Vbox
		VBox vbox = new VBox(30);
		vbox.setPadding(new Insets(25, 25, 25, 25));
		;
		vbox.getChildren().addAll(labelHb, buttonHb1, actionStatus);

		// Scene
		Scene scene = new Scene(vbox, 500, 500); // w x h
		primaryStage.setScene(scene);
		primaryStage.show();

		return primaryStage;
	}

	// evento anexar
	private class SingleFcButtonListener implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			showSingleFileChooser();
		}
	}

	// anexa arquivo
	private void showSingleFileChooser() {

		FileChooser fileChooser = new FileChooser();
		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {

			actionStatus.setText("Arquivo Selecionado: " + selectedFile.getName());

			InputStream targetStream = null;
			try {
				targetStream = new FileInputStream(selectedFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			Properties cfg = carregaProperties(targetStream);

			arquivoConfiguracao();

			Set<String> keys = cfg.stringPropertyNames();

			List<String> lst = new ArrayList<>();
			lst.addAll(keys);

			// orderna a lista
			Collections.sort(lst, Collator.getInstance(new Locale("pt")));
			
			//Filtro de categoria
			Properties keyvalue = arquivoConfiguracao();

			//Properties keyDescricao = arquivoDescricao();
			// Categoria
			for (String key : lst) {

				String descricao = "";
				String desc1 = "";

				for (String string : arquivoConfiguracao().stringPropertyNames()) {
					if(keyvalue.get(string).equals("S")) {
						if (key.contains(string)) {
							descricao = string;
						}
					}
				}
				
//				for (String v : arquivoDescricao().stringPropertyNames()) {
//					if(keyDescricao.getProperty(key) != null) {
//						System.out.println("v:" + key);
//						System.out.println("descrica: " + keyDescricao.getProperty(key));
//						if(keyDescricao.getProperty(key).equals(key)) {
//							desc1 = keyDescricao.getProperty(v);
//						}
//					}
//					
//				}

				data.add(new Tabela(key, cfg.getProperty(key), cfg.getProperty(key), descricao));
			}

			Scene scene = new Scene(new Group(), 1400, 900);
			savedStage.setTitle("Tabela de Configurações");
			savedStage.setWidth(1400);
			savedStage.setHeight(900);
			savedStage.setResizable(false);
			savedStage.centerOnScreen();
			criaTabela(scene, savedStage, cfg);

		} else {
			actionStatus.setText("Seleção cancelada");
		}
	}

	private Properties arquivoConfiguracao() {
		try {
			Properties prop = new Properties();
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream("categoria.properties");
			prop.load(is);
			return prop;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
//	private Properties arquivoDescricao() {
//		try {
//			Properties prop = new Properties();
//			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//			InputStream is = classloader.getResourceAsStream("descricao_propriedade.properties");
//			prop.load(is);
//			return prop;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void criaTabela(Scene scene, Stage stage, Properties cfg) {

		stage.setTitle("Aprendendo JavaFX"); // 9

		final Label label = new Label("Configurações");
		label.setFont(new Font("Arial", 20));

		TableColumn propriedadeCol = new TableColumn("Propriedade");
		propriedadeCol.setCellValueFactory(new PropertyValueFactory<Tabela, Object>("propriedade"));
		propriedadeCol.setMinWidth(400.00);
		// propriedadeCol.setStyle(" -fx-background-color: linear-gradient(to bottom,
		// #1dbbdd44, #93f9b944)");

		TableColumn descricaoCol = new TableColumn("Categoria");
		descricaoCol.setCellValueFactory(new PropertyValueFactory<Tabela, Object>("descricao"));
		descricaoCol.setMinWidth(200.00);

		TableColumn valorColAnterior = new TableColumn("Valor Anterior");
		valorColAnterior.setCellValueFactory(new PropertyValueFactory<>("valorAnterior"));
		valorColAnterior.setMinWidth(400.00);

		TableColumn valorCol = new TableColumn("Valor Atual");

		ObservableList<Tabela> list = FXCollections.observableArrayList(data);
		table.setFixedCellSize(35.0);
		table.setEditable(true);
		table.prefWidthProperty().bind(stage.widthProperty().subtract(20));
		table.prefHeightProperty().bind(stage.heightProperty().subtract(100));
		table.setItems(list);

		BackgroundFill background_fill = new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY);
		Background background = new Background(background_fill);
		//table.getColumns().addAll(descricaoCol, propriedadeCol, valorCol);
		table.getColumns().addAll(descricaoCol, propriedadeCol, valorColAnterior, valorCol);
		table.setBackground(background);
		VBox vbox = new VBox();
		vbox.setSpacing(10);
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.getChildren().addAll(label, table);

		((Group) scene.getRoot()).getChildren().addAll(vbox);
		valorCol.setMinWidth(400.00);

		// valor inicial do campo
		valorCol.setCellValueFactory(new PropertyValueFactory<>("valor"));

		// edicao
		valorCol.setCellFactory((TextFieldTableCell.<Tabela>forTableColumn()));

		valorCol.setOnEditCommit(new EventHandler<CellEditEvent<Tabela, String>>() {
			@Override
			public void handle(CellEditEvent<Tabela, String> t) {
				((Tabela) t.getTableView().getItems().get(t.getTablePosition().getRow())).setValor(t.getNewValue());

				Tabela tab = ((Tabela) t.getTableView().getItems().get(t.getTablePosition().getRow()));
				// setaValor(cfg, tab.getPropriedade(), tab.getValor());
			}
		});

		// chama seletor de local para download

		Button btn = new Button("Download");
		btn.setLayoutX(250);
		btn.setLayoutY(10);

		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				System.out.println("Download clicked");

				FileChooser fileChooser = new FileChooser();

				// Set extension filter
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Properties", "*.properties");
				fileChooser.getExtensionFilters().add(extFilter);

				// Show save file dialog
				File file = fileChooser.showSaveDialog(stage);

				if (file != null) {
					List<Map<String, String>> mapa = new ArrayList<>();

					Map<String, String> map;
					for (Tabela tabela : data) {
						map = new HashMap<>();
						map.put("valor", tabela.getValor());
						map.put("key", tabela.getPropriedade());

						mapa.add(map);
					}
					SaveFile(file, mapa);
				}

			}
		});

		Button btnSair = new Button("Sair");
		btnSair.setLayoutX(550);
		btnSair.setLayoutY(10);

		btnSair.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				stage.close();
			}
		});

		Button btnNovo = new Button("Carregar Outro");
		btnNovo.setLayoutX(400);
		btnNovo.setLayoutY(10);

		btnNovo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				data.clear();
				telaInicial(stage);
			}
		});

		table.getItems();

		((Group) scene.getRoot()).getChildren().addAll(btn);
		((Group) scene.getRoot()).getChildren().addAll(btnSair);
		((Group) scene.getRoot()).getChildren().addAll(btnNovo);

		stage.setTitle("Listagem Config");
		stage.setScene(scene);
		stage.show();

	}

	public static Properties carregaProperties(InputStream i) {

		try {
			cfg.load(i);
			System.out.println(cfg);

			return cfg;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Properties setaValor(Properties cfg, String propriedade, String valor) {
		if (cfg != null) {
			cfg.setProperty(propriedade, valor);
		} else {
			System.out.println("Erro - cfg null");
		}

		return cfg;
	}

	private void SaveFile(File file, List<Map<String, String>> list) {
		try {
			FileOutputStream fos = new FileOutputStream(file);

			SortedProperties sp = new SortedProperties();
			Properties props = asProperties(list, sp);
			props.store(fos, "Config Gerado com Sucesso");
			fos.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public static Properties asProperties(List<Map<String, String>> list, Properties props) {
		for (Map<String, ?> entry : list) {
			props.put(entry.get("key"), entry.get("valor"));
		}
		return props;
	}

	private void criarCombo(Scene scene, Stage savedStage, Properties cfg) {
		ComboBox comboBox;

		FlowPane flowPane = new FlowPane();
		flowPane.setAlignment(Pos.TOP_CENTER);
		flowPane.setMinHeight(200);
		flowPane.setMinWidth(200);
		flowPane.setHgap(25);
		flowPane.setVgap(25);
		flowPane.setMaxHeight(200);
		flowPane.setMaxWidth(200);

		for (Tabela tabela : data) {
			comboBox = new ComboBox();
			Label label = new Label(tabela.getPropriedade());
			comboBox.getItems().addAll(tabela.getValor());
			VBox vbox = new VBox(label, comboBox);
			flowPane.getChildren().add(vbox);

		}
		// ((Group) scene.getRoot()).getChildren().addAll(flowPane);
		scene = new Scene(flowPane, 800, 500);

		savedStage.setScene(scene);
		savedStage.show();

	}

}
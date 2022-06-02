package com.dysconcsa.app.grafico;

import com.dysconcsa.app.grafico.StockApplication.StageReadyEvent;
import com.dysconcsa.app.grafico.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    @Value("classpath:css/table.css")
    private Resource cssTable;

    @Value("classpath:/main.fxml")
    private Resource mainResource;

    @Value("classpath:image/cliente.png")
    private Resource icoResource;

    private String applicationTitle;
    private ApplicationContext applicationContext;

    public StageInitializer(@Value("${application.title}") String applicationTitle, ApplicationContext applicationContext) {
        this.applicationTitle = applicationTitle;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(@NotNull StageReadyEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(mainResource.getURL());
            fxmlLoader.setControllerFactory(aClass -> applicationContext.getBean(aClass));
            MainController mainController = applicationContext.getBean(MainController.class);
            mainController.setGetHostController(event.hostServices);
            Parent parent = fxmlLoader.load();
            parent.getStylesheets().add(String.valueOf(cssTable.getURL()));
            Stage stage = event.getStage();
            Scene scene = new Scene(parent, 800, 600);
            stage.setScene(scene);
            stage.setTitle(applicationTitle);
            stage.setMaximized(true);
            stage.getIcons().add(new Image(String.valueOf(icoResource.getURL())));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

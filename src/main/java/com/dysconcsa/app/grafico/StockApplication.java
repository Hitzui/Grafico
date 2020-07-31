package com.dysconcsa.app.grafico;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class StockApplication extends Application {
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(GraficoApplication.class).run();
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }

    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    class StageReadyEvent extends ApplicationEvent {

		private static final long serialVersionUID = 1L;
		StageReadyEvent(Stage stage) {
            super(stage);
        }
        HostServices hostServices = getHostServices();
        Stage getStage() {
            return ((Stage) getSource());
        }
    }
}

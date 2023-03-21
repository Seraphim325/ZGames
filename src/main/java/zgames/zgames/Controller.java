package zgames.zgames;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Controller {
    @FXML
    private Button btnBackward;
    @FXML
    private Button btnForward;
    @FXML
    private static Scene scene;
    @FXML
    private static ArrayList<HBox> videoList;
    @FXML
    private static HBox innerVideoList;
    private static Map<Object, File> links;
    private static int currentVideoListPage = 0;

    @FXML
    public void startPage(ActionEvent event) throws IOException {
        initStageAndScene(event, "fxml/start.fxml");
    }

    @FXML
    public void listPage(ActionEvent event) throws IOException,
            InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        initStageAndScene(event, "fxml/list.fxml");
        init(new File("src/main/resources/zgames/zgames/video"));
        setVideoList(videoList.get(currentVideoListPage), "#videoList");
    }

    public void innerListPage(ActionEvent event, File file) throws IOException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        initStageAndScene(event, "fxml/innerList.fxml");
        innerVideoList = createVideoListHBox(List.of(Objects.requireNonNull(file.listFiles())),
                Controller.class
                        .getDeclaredMethod("getVideosPageAction", File.class));
        setVideoList(innerVideoList, "#parts");
    }

    @FXML
    public void innerListPage(ActionEvent event) throws IOException {
        initStageAndScene(event, "fxml/innerList.fxml");
        setVideoList(innerVideoList, "#parts");
    }

    @FXML
    public void videosPage(ActionEvent event, File file) throws IOException {
        initStageAndScene(event, "fxml/videos.fxml");
    }

    public void btnBackward(ActionEvent event) throws IOException {
        currentVideoListPage--;
        changeVideoList();
    }

    public void btnForward(ActionEvent event) throws IOException {
        currentVideoListPage++;
        changeVideoList();
    }

    private void initStageAndScene(ActionEvent event, String file) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(file));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass()
                .getResource("css/style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }


    private void init(File file) throws IOException,
            InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (videoList == null) {
            initVideoList(file);
        }
        initButtons();
    }

    private void initVideoList(File file) throws IOException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        videoList = new ArrayList<>();
        links = new HashMap<>();
        List<File> files;

        int len = Objects.requireNonNull(file.listFiles()).length;
        int hboxNum = len % 3 == 0 ? len / 3 : len / 3 + 1;
        for (int i = 1; i <= hboxNum; i++) {
            if (i == hboxNum) {
                files = Arrays.stream(Objects.requireNonNull(file.listFiles())).toList().subList(i * 3 - 3, len);
            } else {
                files = Arrays.stream(Objects.requireNonNull(file.listFiles())).toList().subList(i * 3 - 3, i * 3);
            }
            HBox currentPage = createVideoListHBox(files, Controller.class
                    .getDeclaredMethod("getInnerListPageAction", File.class));
            videoList.add(currentPage);
        }
    }

    private HBox createVideoListHBox(List<File> files, Method method) throws IOException,
            InvocationTargetException, IllegalAccessException {
        HBox list = new HBox();
        for (int i = 0; i < files.size(); i++) {
            Button button = createButton(files.get(i), method);
            switch (i) {
                case 0 -> HBox.setMargin(button, new Insets(0, 0, 0, 10));
                case 1 -> HBox.setMargin(button, new Insets(0, 0, 0, 165));
                case 2 -> HBox.setMargin(button, new Insets(0, 10, 0, 165));
            }
            links.put(button, files.get(i));
            list.getChildren().add(button);
        }
        return list;
    }

    private void setVideoList(HBox page, String id) {
        HBox fxmlId = (HBox) scene.lookup(id);
        fxmlId.getChildren().clear();
        fxmlId.getChildren().add(page);
    }

    private void changeVideoList() {
        disableButtons(currentVideoListPage == 0,
                currentVideoListPage == videoList.size() - 1);
        setVideoList(videoList.get(currentVideoListPage), "#videoList");
    }

    private Button createButton(File file, Method method) throws
            InvocationTargetException, IllegalAccessException {
        Button button = new Button();
        button.setPrefWidth(150);
        button.setPrefHeight(150);
        button.setText(file.getName());
        EventHandler<ActionEvent> action = (EventHandler<ActionEvent>) method.invoke(this, file);
        button.setOnAction(action);
        return button;
    }

    private void initButtons() {
        btnBackward = (Button) scene.lookup("#btnBackward");
        btnForward = (Button) scene.lookup("#btnForward");
        disableButtons(currentVideoListPage == 0, currentVideoListPage == videoList.size() - 1);
    }

    private void disableButtons(boolean btnBackward, boolean btnForward) {
        this.btnBackward.setDisable(btnBackward);
        this.btnForward.setDisable(btnForward);

    }
    private EventHandler<ActionEvent> getInnerListPageAction(File file) {
        return event -> {
            try {
                innerListPage(event, file);
            } catch (IOException | InvocationTargetException
                     | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private EventHandler<ActionEvent> getVideosPageAction(File file) {
        return event -> {
            try {
                videosPage(event, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}

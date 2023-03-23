package zgames.zgames;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
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
    private Button btnBackwardVideoPage;
    @FXML
    private Button btnForward;
    @FXML
    private Button btnForwardVideoPage;
    @FXML
    private static ArrayList<HBox> list;
    @FXML
    private static HBox innerList;
    @FXML
    private static ArrayList<VBox> videoList;
    @FXML
    private static MediaView mediaView;
    private static MediaPlayer mediaPlayer;
    private static Scene scene;
    private static int currentListPage = 0;
    private static int currentVideoListPage = 0;

    @FXML
    public void startPage(ActionEvent event) throws IOException {
        currentListPage = 0;
        initStageAndScene(event, "fxml/start.fxml");
    }

    @FXML
    public void listPage(ActionEvent event) throws IOException,
            InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        initStageAndScene(event, "fxml/list.fxml");
        initListPage(new File("src/main/resources/zgames/zgames/video"));
        setList(list.get(currentListPage), "#list");
    }

    public void innerListPage(ActionEvent event, File file) throws IOException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        initInnerListPage();
        initStageAndScene(event, "fxml/innerList.fxml");
        if (innerList == null) {
            innerList = createListHBox(List.of(Objects.requireNonNull(file.listFiles())),
                    Controller.class
                            .getDeclaredMethod("getVideoListPageAction", File.class));
        }
        setList(innerList, "#parts");
    }

    @FXML
    public void innerListPage(ActionEvent event) throws IOException,
            InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        innerListPage(event, new File(""));
    }

    @FXML
    public void videoListPage(ActionEvent event, File file) throws IOException,
            InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        initStageAndScene(event, "fxml/videoList.fxml");
        initVideoListPage(file);
        setList(videoList.get(currentVideoListPage), "#videoList");
    }

    @FXML
    public void videoListPage(ActionEvent event) throws IOException,
            InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        interruptMedia();
        videoListPage(event, new File(""));
    }

    @FXML
    public void videoPage(ActionEvent event, File file) throws IOException {
        initStageAndScene(event, "fxml/video.fxml");
        initVideoPage(file);
    }

    public void btnBackward(ActionEvent event) {
        currentListPage--;
        changeList();
    }

    public void btnForward(ActionEvent event) {
        currentListPage++;
        changeList();
    }

    public void btnBackwardVideoPage(ActionEvent event) {
        currentVideoListPage--;
        changeVideoList();
    }

    public void btnForwardVideoPage(ActionEvent event) {
        currentVideoListPage++;
        changeVideoList();
    }

    public void play(ActionEvent event) {
        mediaPlayer.play();
    }

    public void pause(ActionEvent event) {
        mediaPlayer.pause();
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


    private void initListPage(File file) throws IOException,
            InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        innerList = null;
        if (list == null) {
            initList(file);
        }
        initButtons();
    }

    private void initInnerListPage() {
        currentVideoListPage = 0;
        videoList = null;
    }

    private void initVideoListPage(File file) throws IOException,
            InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (videoList == null) {
            initVideoList(file);
        }
        initButtonsVideoPage();
    }

    private void initVideoPage(File file) {
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaView = (MediaView) scene.lookup("#mediaView");
        mediaView.setMediaPlayer(mediaPlayer);
    }

    private void interruptMedia() {
        mediaPlayer.pause();
        mediaView = null;
        mediaPlayer = null;
    }

    private void initList(File file) throws IOException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        list = new ArrayList<>();
        List<File> files;

        int len = Objects.requireNonNull(file.listFiles()).length;
        int hboxNum = len % 3 == 0 ? len / 3 : len / 3 + 1;
        for (int i = 1; i <= hboxNum; i++) {
            if (i == hboxNum) {
                files = Arrays.stream(Objects.requireNonNull(file.listFiles())).toList().subList(i * 3 - 3, len);
            } else {
                files = Arrays.stream(Objects.requireNonNull(file.listFiles())).toList().subList(i * 3 - 3, i * 3);
            }
            HBox currentPage = createListHBox(files, Controller.class
                    .getDeclaredMethod("getInnerListPageAction", File.class));
            list.add(currentPage);
        }
    }

    private void initVideoList(File file) throws NoSuchMethodException,
            IOException, InvocationTargetException, IllegalAccessException {
        videoList = new ArrayList<>();
        List<File> files;

        int len = Objects.requireNonNull(file.listFiles()).length;
        int vboxNum = len % 8 == 0 ? len / 8 : len / 8 + 1 ;
        for (int i = 1; i <= vboxNum; i++) {
            if (i == vboxNum) {
                files = Arrays.stream(Objects.requireNonNull(file.listFiles())).toList().subList(i * 8 - 8, len);
            } else {
                files = Arrays.stream(Objects.requireNonNull(file.listFiles())).toList().subList(i * 8 - 8, i * 8);
            }
            VBox currentPage = createVideoListVBox(files, Controller.class
                    .getDeclaredMethod("getVideoPageAction", File.class));
            videoList.add(currentPage);
        }
    }

    private HBox createListHBox(List<File> files, Method method) throws IOException,
            InvocationTargetException, IllegalAccessException {
        HBox list = new HBox();
        for (int i = 0; i < files.size(); i++) {
            Button button = createButton(files.get(i), method, 150, 150);
            switch (i) {
                case 0 -> HBox.setMargin(button, new Insets(0, 165, 0, 10));
                case 1 -> HBox.setMargin(button, new Insets(0, 165, 0, 0));
                case 2 -> HBox.setMargin(button, new Insets(0, 10, 0, 0));
            }
            list.getChildren().add(button);
        }
        return list;
    }

    private VBox createVideoListVBox(List<File> files, Method method) throws IOException,
            InvocationTargetException, IllegalAccessException {
        HBox miniList1 = createMiniList();
        HBox miniList2 = createMiniList();


        for (int i = 0; i < files.size(); i++) {
            Button button = createButton(files.get(i), method, 100, 100);
            switch (i) {
                case 0, 4 -> HBox.setMargin(button, new Insets(0, 126.33, 0, 10));
                case 1, 2, 5, 6 -> HBox.setMargin(button, new Insets(0, 126.33, 0, 0));
                case 3, 7 -> HBox.setMargin(button, new Insets(0, 10, 0, 0));
            }
            if (i < 4) {
                miniList1.getChildren().add(button);
            } else {
                miniList2.getChildren().add(button);
            }
        }

        VBox list = new VBox();
        list.getChildren().addAll(miniList1, miniList2);

        return list;
    }

    private HBox createMiniList() {
        HBox miniList = new HBox();
        miniList.setPrefHeight(175);
        miniList.setAlignment(Pos.CENTER_LEFT);
        return miniList;
    }

    private void setList(HBox page, String id) {
        HBox fxmlId = (HBox) scene.lookup(id);
        fxmlId.getChildren().clear();
        fxmlId.getChildren().add(page);
    }

    private void setList(VBox page, String id) {
        VBox fxmlId = (VBox) scene.lookup(id);
        fxmlId.getChildren().clear();
        fxmlId.getChildren().add(page);
    }

    private void changeList() {
        disableButtons(btnBackward, currentListPage == 0,
                btnForward, currentListPage == list.size() - 1);
        setList(list.get(currentListPage), "#list");
    }

    private void changeVideoList() {
        disableButtons(btnBackwardVideoPage, currentVideoListPage == 0,
                btnForwardVideoPage, currentVideoListPage == list.size() - 1);
        setList(videoList.get(currentVideoListPage), "#videoList");
    }

    private Button createButton(File file, Method method, int width, int height) throws
            InvocationTargetException, IllegalAccessException {
        Button button = new Button();
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.setText(file.getName());
        EventHandler<ActionEvent> action = (EventHandler<ActionEvent>) method.invoke(this, file);
        button.setOnAction(action);
        return button;
    }

    private void initButtons() {
        btnBackward = (Button) scene.lookup("#btnBackward");
        btnForward = (Button) scene.lookup("#btnForward");
        disableButtons(btnBackward, currentListPage == 0,
                btnForward, currentListPage == list.size() - 1);
    }

    private void initButtonsVideoPage() {
        btnBackwardVideoPage = (Button) scene.lookup("#btnBackwardVideoPage");
        btnForwardVideoPage = (Button) scene.lookup("#btnForwardVideoPage");
        disableButtons(btnBackwardVideoPage, currentVideoListPage == 0,
                btnForwardVideoPage, currentVideoListPage == videoList.size() - 1);
    }

    private void disableButtons(Button btnBackward, boolean btnBackwardStatus,
                                Button btnForward, boolean btnForwardStatus) {
        btnBackward.setDisable(btnBackwardStatus);
        btnForward.setDisable(btnForwardStatus);

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

    private EventHandler<ActionEvent> getVideoListPageAction(File file) {
        return event -> {
            try {
                videoListPage(event, file);
            } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private EventHandler<ActionEvent> getVideoPageAction(File file) {
        return event -> {
            try {
                videoPage(event, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}

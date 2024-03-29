package main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import main.java.fxapp.Main;
import main.java.model.DatabaseRef;

import java.io.File;

/**
 * Created by Ashwin Ignatius on 4/15/2017.
 */
public class CityOfficialHomeController extends Controller{

    @FXML
    public void handlePOIReportPressed() { myApp.load(new File("../view/POIReportScreen.fxml")); }

    @FXML
    public void handleLogOutPressed() {myApp.load(new File("../view/LoginScreen.fxml")); }

    @FXML
    public void handleFilterPressed() {myApp.load(new File("../view/ViewPOIScreen.fxml")); }

}

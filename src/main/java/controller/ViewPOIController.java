package main.java.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.java.fxapp.Main;
import main.java.model.DataPoint;
import main.java.model.DatabaseRef;
import main.java.model.POI;
import main.java.model.Type;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class ViewPOIController extends Controller{

    @FXML
    private TableView<POI> table;

    @FXML
    private ComboBox<String> locName;

    @FXML
    private DatePicker dateTimeStart;

    @FXML
    private DatePicker dateTimeEnd;

    @FXML
    private CheckBox flagged;

    @FXML
    private ComboBox<String> city;

    @FXML
    private ComboBox<String> state;


    private ObservableList<String> pois = FXCollections.observableArrayList();

    private ObservableList<String> cities = FXCollections.observableArrayList();

    private ObservableList<String> states = FXCollections.observableArrayList();

    @FXML
    //Location Name, City, State, Zip Code, Flagged, Date Flagged
    //locationCol, cityCol, stateCol, zipCodeCol, checkBoxCol, dateCol

    private TableColumn locationCol;

    private TableColumn cityCol;

    private TableColumn stateCol;

    private TableColumn zipCodeCol;

    private TableColumn checkBoxCol;

    private TableColumn dateCol;

    private ObservableList<POI> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() throws Exception {
        this.db = Main.getDb();
        /*table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println(newSelection.getLocationName());
                myApp.loadPOIDetail(new File("../view/POIDetail.fxml"), newSelection.getLocationName());
            }
        });*/

        db.rs = db.stmt.executeQuery("SELECT LocationName FROM POI ORDER BY LocationName");
        db.rs.beforeFirst();
        while (db.rs.next()) {
            pois.add(db.rs.getString("LocationName"));
        }
        db.rs = db.stmt.executeQuery("SELECT DISTINCT State FROM POI ORDER BY State");
        db.rs.beforeFirst();
        while (db.rs.next()) {
            states.add(db.rs.getString("State"));
        }
        db.rs = db.stmt.executeQuery("SELECT DISTINCT City FROM POI ORDER BY City");
        db.rs.beforeFirst();
        while (db.rs.next()) {
            cities.add(db.rs.getString("City"));
        }
        locName.setItems(pois);
        city.setItems(cities);
        state.setItems(states);

        //Location Name, City, State, Zip Code, Flagged, Date Flagged
        //locationCol, cityCol, stateCol, zipCodeCol, checkBoxCol, dateCol

        locationCol = new TableColumn("Location Name");
        cityCol = new TableColumn("City");
        stateCol = new TableColumn("State");
        zipCodeCol = new TableColumn("Zip Code");
        checkBoxCol = new TableColumn("Flagged?");
        dateCol = new TableColumn("Date Flagged");


        locationCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                    @Override
                    public ObservableValue call(TableColumn.CellDataFeatures dataFeatures) {
                        POI poi = (POI) dataFeatures.getValue();
                        return new SimpleStringProperty(poi.getLocationName());
                    }
                }
        );


        cityCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                    @Override
                    public ObservableValue call(TableColumn.CellDataFeatures dataFeatures) {
                        POI poi = (POI) dataFeatures.getValue();
                        return new SimpleStringProperty(poi.getCity());
                    }
                }
        );


        stateCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                    @Override
                    public ObservableValue call(TableColumn.CellDataFeatures dataFeatures) {
                        POI poi = (POI) dataFeatures.getValue();
                        return new SimpleStringProperty(poi.getState());
                    }
                }
        );


        zipCodeCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                    @Override
                    public ObservableValue call(TableColumn.CellDataFeatures dataFeatures) {
                        POI poi = (POI) dataFeatures.getValue();
                        return new SimpleStringProperty(poi.getZip());
                    }
                }
        );

        checkBoxCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                    @Override
                    public ObservableValue call(TableColumn.CellDataFeatures dataFeatures) {
                        POI poi = (POI) dataFeatures.getValue();
                        return new SimpleStringProperty(poi.getFlaggedString());
                    }
                }
        );


        dateCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                    @Override
                    public ObservableValue call(TableColumn.CellDataFeatures dataFeatures) {
                        POI poi = (POI) dataFeatures.getValue();
                        if (poi.getDateFlagged() != null) {
                            String date = new SimpleDateFormat("yyyy/MM/dd").format(poi.getDateFlagged());
                            return new SimpleStringProperty(date);
                        }
                        return new SimpleStringProperty("~N/A~");
                    }
                }
        );

        //locationCol, cityCol, stateCol, zipCodeCol, dateCol

        db.rs = db.stmt.executeQuery("SELECT * FROM `POI` ");
        db.rs.beforeFirst();
        data = FXCollections.observableArrayList();
        while (db.rs.next()) {
            POI poi = new POI();

            poi.setLocationName(db.rs.getString("LocationName"));
            poi.setCity(db.rs.getString("City"));
            poi.setState(db.rs.getString("State"));
            poi.setZip(db.rs.getString("Zipcode"));
            poi.setFlagged(db.rs.getBoolean("Flag"));
            poi.setDateFlagged(db.rs.getTimestamp("DateFlagged"));
            data.add(poi);
        }


        table.setItems(data);
        table.getColumns().addAll(locationCol, cityCol, stateCol, zipCodeCol, checkBoxCol, dateCol);

    }

    public boolean baseChecker(String base) {
        if (base.equals("SELECT * FROM POI ")) {
            return true;
        }
        return false;
    }

    @FXML
    public void handleApplyFilterPressed() throws SQLException {
        data.clear();
        String query = "SELECT * FROM POI ";
        String locat = locName.getValue();
        String cityData = city.getValue();
        String stateData = state.getValue();
        Boolean isFlagged = flagged.isSelected();
        LocalDate start = dateTimeStart.getValue();
        LocalDate end = dateTimeEnd.getValue();
        if (locat != null) {
            query += "WHERE LocationName ='" + locat + "'";
        }
        if (cityData != null) {
            if (baseChecker(query)) {
                query += "WHERE ";
            } else {
                query += "AND ";
            }
             query += "City ='" + cityData + "'";
        }
        if (stateData != null) {
            if (baseChecker(query)) {
                query += "WHERE ";
            } else {
                query += "AND ";
            }
            query += "State ='" + stateData + "'";
        }
        if (isFlagged) {
            if (baseChecker(query)) {
                query += "WHERE ";
            } else {
                query += "AND ";
            }
            query += "Flag =" + isFlagged;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        if (start != null && end != null) {
            Timestamp startTime = new Timestamp(start.getYear() - 1900, start.getMonthValue() - 1,
                    start.getDayOfMonth(), 0, 0, 0, 0);
            Timestamp endTime = new Timestamp(end.getYear() - 1900, end.getMonthValue() - 1,
                    end.getDayOfMonth(), 0, 0, 0, 0);
            if (baseChecker(query)) {
                query += "WHERE ";
            } else {
                query += "AND ";
            }
            query += "DateFlagged BETWEEN '" + dateFormat.format(startTime) + "' AND '" + dateFormat.format(endTime) + "'";
        } else if (start != null && end == null) {
            Timestamp startTime = new Timestamp(start.getYear() - 1900, start.getMonthValue() - 1,
                    start.getDayOfMonth(), 0, 0, 0, 0);
            if (baseChecker(query)) {
                query += "WHERE ";
            } else {
                query += "AND ";
            }
            query += "DateFlagged >= '" + dateFormat.format(startTime) + "'";
        } else if (start == null && end != null) {
            Timestamp endTime = new Timestamp(end.getYear() - 1900, end.getMonthValue() - 1,
                    end.getDayOfMonth(), 0, 0, 0, 0);
            if (baseChecker(query)) {
                query += "WHERE ";
            } else {
                query += "AND ";
            }
            query += "DateFlagged <= '" + dateFormat.format(endTime) + "'";
        }

        //System.out.println(query);
        db.rs = db.stmt.executeQuery(query);
        db.rs.beforeFirst();
        data = FXCollections.observableArrayList();
        while (db.rs.next()) {
            POI poi = new POI();
            poi.setLocationName(db.rs.getString("LocationName"));
            poi.setCity(db.rs.getString("City"));
            poi.setState(db.rs.getString("State"));
            poi.setZip(db.rs.getString("Zipcode"));
            poi.setFlagged(db.rs.getBoolean("Flag"));
            poi.setDateFlagged(db.rs.getTimestamp("DateFlagged"));
            data.add(poi);
        }

        table.setItems(data);
    }


    //@FXML
    //public void onCityChosen() throws Exception {
    //    System.out.println(state.getValue());
    //    if (state.getValue() == null) {
    //        states.clear();
    //        db.preparedStatement = db.conn.prepareStatement(
    //                "SELECT DISTINCT State "
    //                        + "FROM POI "
    //                        + "WHERE City = ?"
    //                        + "ORDER BY State");
    //        db.preparedStatement.setString(1, city.getValue());
    //        System.out.println(db.preparedStatement);
    //        db.rs = db.preparedStatement.executeQuery();
    //        db.rs.beforeFirst();
    //        while (db.rs.next()) {
    //            states.add(db.rs.getString("State"));
    //        }
    //        state.setItems(states);
    //    }
    //}
    //
    //@FXML
    //public void onStateChosen() throws Exception {
    //    System.out.println(city.getValue());
    //    if (city.getValue() == null) {
    //        cities.clear();
    //        db.preparedStatement = db.conn.prepareStatement(
    //                "SELECT City "
    //                        + "FROM POI "
    //                        + "WHERE State = ?"
    //                        + "ORDER BY City");
    //        db.preparedStatement.setString(1, state.getValue());
    //        System.out.println(db.preparedStatement);
    //        db.rs = db.preparedStatement.executeQuery();
    //        db.rs.beforeFirst();
    //        while (db.rs.next()) {
    //            cities.add(db.rs.getString("City"));
    //        }
    //        city.setItems(cities);
    //    }
    //}

    @FXML
    public void handleResetFilterPressed() {
        myApp.load(new File("../view/ViewPOIScreen.fxml"));
    }

    @FXML
    public void handleBackPressed()  {
        myApp.load(new File("../view/CityOfficialHome.fxml"));
    }

    @FXML
    public void handleViewDetailPressed() throws Exception {
        if (table.getSelectionModel().getSelectedItem() != null) {
            myApp.loadPOIDetail(table.getSelectionModel().getSelectedItem().getLocationName(), table.getSelectionModel().getSelectedItem().getFlagged());
        }
    }
}

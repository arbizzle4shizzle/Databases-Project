package main.java.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import main.java.fxapp.Main;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import main.java.model.DataPoint;
import main.java.model.Type;

import javax.xml.crypto.Data;

/**
 * Created by Ashwin Ignatius on 4/21/2017.
 */
public class PendingDataPointsController extends Controller {

    @FXML
    private TableView<DataPoint> table;

    private TableColumn locationCol;

    private TableColumn typeCol;

    private TableColumn valueCol;

    private TableColumn dateCol;

    private ObservableList<DataPoint> data = FXCollections.observableArrayList();

    public void initialize() throws SQLException {
        this.db = Main.getDb();
        table.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
        locationCol = new TableColumn("POI Location");
        typeCol = new TableColumn("Data Type");
        valueCol = new TableColumn("Data Value");
        dateCol = new TableColumn("Time & Date of Data Reading");

        locationCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                    @Override
                    public ObservableValue call(TableColumn.CellDataFeatures dataFeatures) {
                        DataPoint dataPoint = (DataPoint) dataFeatures.getValue();
                        return new SimpleStringProperty(dataPoint.getLocationName());
                    }
                }
        );

        typeCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                    @Override
                    public ObservableValue call(TableColumn.CellDataFeatures dataFeatures) {
                        DataPoint dataPoint = (DataPoint) dataFeatures.getValue();
                        return new SimpleStringProperty(dataPoint.getPointTypeString());
                    }
                }
        );

        valueCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                    @Override
                    public ObservableValue call(TableColumn.CellDataFeatures dataFeatures) {
                        DataPoint dataPoint = (DataPoint) dataFeatures.getValue();
                        return new SimpleStringProperty(dataPoint.getDataValue() + "");
                    }
                }
        );

        dateCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                    @Override
                    public ObservableValue call(TableColumn.CellDataFeatures dataFeatures) {
                        DataPoint dataPoint = (DataPoint) dataFeatures.getValue();

                        String date = new SimpleDateFormat("MM/dd/yyyy " +
                                "HH:mm").format(dataPoint.getMyDate());

                        return new SimpleStringProperty(date);
                    }
                }
        );
        db.rs = db.stmt.executeQuery(
                "SELECT * FROM `Data_Point` "
                + "WHERE Accepted IS NULL ");
        db.rs.beforeFirst();
        data = FXCollections.observableArrayList();
        while (db.rs.next()) {
            DataPoint myPoint = new DataPoint();
            myPoint.setAccepted(db.rs.getBoolean("Accepted"));
            myPoint.setDataValue(db.rs.getInt("DataValue"));
            myPoint.setMyDate(db.rs.getTimestamp("DateTime"));
            myPoint.setPointType(Type.valueOf(db.rs.getString("Type")));
            myPoint.setLocationName(db.rs.getString("LocationName"));
            data.add(myPoint);
        }

        table.setItems(data);
        table.getColumns().addAll(locationCol, typeCol, valueCol, dateCol);
    }

    @FXML
    public void handleBackPressed() {
        myApp.load(new File("../view/AdminHome.fxml"));
    }

    @FXML
    public void handleRejectPressed() {

    }

    @FXML
    public void handleAcceptPressed() {
        for (DataPoint o : table.getSelectionModel().getSelectedItems()) {
            System.out.println(o);
        }
    }
}

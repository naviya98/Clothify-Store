package controller.product;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import controller.employee.EmployeeDashboardFormController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.Category;
import model.Product;
import model.Supplier;
import service.ServiceFactory;
import service.custom.CategoryService;
import service.custom.ProductService;
import service.custom.SupplierService;
import util.Type;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateProductFormController implements Initializable {
    private EmployeeDashboardFormController mainController;

    public void setMainController(EmployeeDashboardFormController mainController) {
        this.mainController = mainController;
    }

    private final SupplierService supplierService = ServiceFactory.getInstance().getServiceType(Type.SUPPLIER);
    private final ProductService productService = ServiceFactory.getInstance().getServiceType(Type.PRODUCT);
    private final CategoryService categoryService = ServiceFactory.getInstance().getServiceType(Type.CATEGORY);

    private final Product selectedProductToEdit = mainController.getSelectedProductToEdit();

    @FXML
    public JFXComboBox<String> cmbEditProductCategory;

    @FXML
    private JFXComboBox<String> cmbEditProductSupplier;

    @FXML
    private Label lblEditProductID;

    @FXML
    private Spinner<Integer> spinnerEditProductQuantity;

    @FXML
    private JFXTextField txtEditProductName;

    @FXML
    private JFXTextField txtEditProductUnitPrice;

    @FXML
    void btnRemoveSupplierOnAction(ActionEvent event) {
        cmbEditProductSupplier.setValue("");
    }

    @FXML
    void btnCancelEditProductsOnAction(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
        Scene scene = EmployeeDashboardFormController.getEmployeeDashboardStage().getScene();
        AnchorPane root = (AnchorPane) scene.getRoot();
        VBox vbox = (VBox) root.getChildren().get(7);
        vbox.setVisible(false);
        vbox.setDisable(true);
    }

    @FXML
    void btnSaveChangesOnAction(ActionEvent event) {
        try{
            selectedProductToEdit.setName(txtEditProductName.getText());
            selectedProductToEdit.setQuantity(spinnerEditProductQuantity.getValue());
            selectedProductToEdit.setUnitPrice(Double.parseDouble(txtEditProductUnitPrice.getText()));
            selectedProductToEdit.setCategory(categoryService.findCategoryByName(cmbEditProductCategory.getValue()));

            if (!cmbEditProductSupplier.getValue().equals("")){
                selectedProductToEdit.setSupplier(supplierService.findSupplierByName(cmbEditProductSupplier.getValue()));
            }else{
                selectedProductToEdit.setCategory(null);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(null);
            alert.setContentText("Please Enter Correct Data");
            alert.show();
            setInitialProductData();
            return;
        }
        productService.updateProduct(selectedProductToEdit);
        btnCancelEditProductsOnAction(event);
        mainController.loadCatalogProductsTable(productService.getAllProducts());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setInitialProductData();
    }

    private void setInitialProductData(){
        lblEditProductID.setText(selectedProductToEdit.getProductId());
        txtEditProductName.setText(selectedProductToEdit.getName());
        spinnerEditProductQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,750,selectedProductToEdit.getQuantity()));
        txtEditProductUnitPrice.setText(selectedProductToEdit.getUnitPrice().toString());

        ObservableList<Category> categoryObservableList=categoryService.getAllCategories();
        ObservableList<String> a= FXCollections.observableArrayList();
        categoryObservableList.forEach(category -> a.add(category.getName()));
        cmbEditProductCategory.setItems(a);
        if(selectedProductToEdit.getCategory()!=null) cmbEditProductCategory.setValue(selectedProductToEdit.getCategory().getName());

        ObservableList<Supplier> supplierObservableList=supplierService.getAllSuppliers();
        ObservableList<String> b= FXCollections.observableArrayList();
        supplierObservableList.forEach(supplier -> b.add(supplier.getName()));
        cmbEditProductSupplier.setItems(b);
        if(selectedProductToEdit.getSupplier()!=null) cmbEditProductSupplier.setValue(selectedProductToEdit.getSupplier().getName());
    }
}

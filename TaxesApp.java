/*************************************************************************
 * GUI application to output taxes depending on one's family size, gross *
 * income, and state of residence. This "fair" tax system does not       *
 * charge you taxes if your gross income is below the poverty line,      *
 * which depends on state of residence (poverty guidelines taken from    *
 * https://aspe.hhs.gov/poverty-guidelines). It also sets a max amount   *
 * of taxes at 50%.                                                      *
 *************************************************************************/

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;

public class TaxesApp extends Application {   
   
   /* final variables */
   protected final double taxConstant = .25;
   protected final int numStates = 50;
   
   /* dynamic variables */
   protected HashMap states;
   protected ChoiceBox stateChoiceBox;
   protected Spinner numFamilySpinner;
   protected TextField incomeTextField;
   protected Label resultLabel;
   
   /* Sets up the hashmap of states */
   protected void setStates() {
      states = new HashMap(numStates);
      states.put("AL","Alabama");
      states.put("AK","Alaska");
      states.put("AZ","Arizona");
      states.put("AR","Arkansas");
      states.put("CA","California");
      states.put("CO","Colorado");
      states.put("CT","Connecticut");
      states.put("DE","Delaware");
      states.put("FL","Flordia");
      states.put("GA","Georgia");
      states.put("HI","Hawaii");
      states.put("ID","Idaho");
      states.put("IL","Illinois");
      states.put("IN","Indiana");
      states.put("IA","Iowa");
      states.put("KS","Kansas");
      states.put("KY","Kentucky");
      states.put("LA","Lousiana");
      states.put("ME","Maine");
      states.put("MD","Maryland");
      states.put("MA","Massachusetts");
      states.put("MI","Michigan");
      states.put("MN","Minnesota");
      states.put("MS","Mississippi");
      states.put("MO","Missouri");
      states.put("MT","Montana");
      states.put("NE","Nebraska");
      states.put("NV","Nevada");
      states.put("NH","New Hampshire");
      states.put("NJ","New Jersey");
      states.put("NM","New Mexico");
      states.put("NY","New York");
      states.put("NC","North Carolina");
      states.put("ND","North Dakota");
      states.put("OH","Ohio");
      states.put("OK","Oklahoma");
      states.put("OR","Oregon");
      states.put("PA","Pennsylvania");
      states.put("RI","Rhode Island");
      states.put("SC","South Carolina");
      states.put("SD","South Dakota");
      states.put("TN","Tennessee");
      states.put("TX","Texas");
      states.put("UT","Utah");
      states.put("VT","Vermont");
      states.put("VA","Virginia");
      states.put("WA","Washington");
      states.put("WV","West Virginia");
      states.put("WI","Wisconsin");
      states.put("WY","Wyoming");
   }
   
   /* Sets up the state choice box */
   protected void setStateChoiceBox() {
      stateChoiceBox = new ChoiceBox();
      Iterator it = states.entrySet().iterator();
      String[] temp = new String[numStates];
      int i = 0;
      while (it.hasNext()) {
         Map.Entry pair = (Map.Entry) it.next();
         temp[i] = (String) pair.getKey();
         i++;
      }
      Arrays.sort(temp);
      for (i=0; i<numStates; i++)
         stateChoiceBox.getItems().addAll(temp[i]);
      stateChoiceBox.getSelectionModel().selectFirst();
   }
   
   /* calcuates and shows the taxes depending on one's
    * family size, income, and state of residence */
   protected void calculateTaxes(Stage primaryStage) {
      // set up output string
      String str = "--------------------------------------------------\n";

      // get poverty line depending on state and family number
      String state = (String) stateChoiceBox.getValue();
      System.out.println(states.get(state));
      int numFamily = (int) numFamilySpinner.getValue();
      int povertyLine = 0;
      if (state.equals("HI"))       povertyLine = 15060 + (numFamily-1)*5230;
      else if (state.equals("AK"))  povertyLine = 12060 + (numFamily-1)*4810;
      else                          povertyLine = 13860 + (numFamily-1)*4810;
            
      // compute taxes (try taxPercent = income/povertyLine-1)
      try {
         double income = Double.parseDouble(incomeTextField.getText());
         double taxPercent = (taxConstant*(income-povertyLine))/income;
         if (taxPercent < 0)        taxPercent = 0;
         else if (taxPercent > .5)  taxPercent = .5;
         double taxes = income*taxPercent;
         double leftover = income-taxes;
         double amountAbovePoverty = leftover-povertyLine;
            
         // show result
         NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
         str += "You live in " + states.get(state) + ",\n"
             +  "make " + formatter.format(income) + " a year,\n"
             +  "and have a family of " + numFamily + ".\n\n";
                
         if (amountAbovePoverty > 0) {
            str += "You will pay " + formatter.format(taxes) + " in taxes,\n"
                +  "so you still make " + formatter.format(leftover) + "\n"
                +  "which is " + formatter.format(amountAbovePoverty) + "\n"
                +  "above the poverty line for your\nstate and family size.\n";
         } else if (amountAbovePoverty == 0) {
            str += "You make exactly the poverty line,\nso you will not pay taxes.\n";
         } else {
            amountAbovePoverty = -1*amountAbovePoverty;
            str += "You are " + formatter.format(amountAbovePoverty) + " below the poverty line,\n"
                +  "so you will not pay any taxes.\n";
         }         
      } catch (Exception e) {
         str += "Please make sure income is an integer.\n";
      }
      str += "--------------------------------------------------";
      resultLabel.setText(str);
      primaryStage.sizeToScene();
   }
   
   @Override
   public void start(Stage primaryStage) throws Exception {
      // Set the hashmap of states
      setStates();

      // Header
      Label headerLabel = new Label("Fair Tax Calculator");
      HBox headerBox = new HBox(5);
      headerBox.getChildren().add(headerLabel);
      headerBox.setAlignment(Pos.CENTER);

      // State of residence
      Label stateLabel = new Label("State of residence: ");
      setStateChoiceBox();
      HBox stateBox = new HBox(5);
      stateBox.getChildren().addAll(stateLabel, stateChoiceBox);

      // Number in family - spinner
      Label numFamilyLabel = new Label("Number in family: ");
      SpinnerValueFactory svf = 
         new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99);
      numFamilySpinner = new Spinner();
      numFamilySpinner.setValueFactory(svf);
      numFamilySpinner.getStyleClass().add("spinner");
      numFamilySpinner.setPrefWidth(80);
      HBox numFamilyBox = new HBox(5);
      numFamilyBox.getChildren().addAll(numFamilyLabel, numFamilySpinner);

      // Gross income
      Label incomeLabel = new Label("Family's gross income (USD): ");
      incomeTextField = new TextField();
      HBox incomeBox = new HBox(5);
      incomeBox.getChildren().addAll(incomeLabel, incomeTextField);

      // Calculate button and show result
      resultLabel = new Label("");
      Button button = new Button("Calculate taxes");
      button.setOnAction((ActionEvent t) -> { calculateTaxes(primaryStage); } );
      resultLabel.setAlignment(Pos.CENTER);
      button.setAlignment(Pos.CENTER);
           
      // Set up scene and show 
      VBox vBox = new VBox(7);
      vBox.setPadding(new Insets(12));
      vBox.getChildren().addAll(headerBox, stateBox, numFamilyBox,
         incomeBox, button, resultLabel);
      vBox.setAlignment(Pos.CENTER);
      primaryStage.setScene(new Scene(vBox));
      primaryStage.show();
   }

   /* Main method to start application */
   public static void main(String args[]) {
      launch(args);
   }
}
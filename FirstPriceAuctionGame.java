

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;


public class FirstPriceAuctionGame extends Application {

	private DisplayComponent upperBorder;

	private Label curItemLabel = new Label();
	private Label lastRoundResult = new Label();

	private Button player1BidButton = new Button("Bid");
	private Button player1PassButton = new Button("Pass");

	private Button player2BidButton = new Button("Bid");
	private Button player2PassButton = new Button("Pass");

	private Button newGameButton = new Button("Start a new game");
	private Button repeatButton = new Button("repeat game");

	private Player player1 = new Player();
	private Player player2 = new Player();

	private TextField player1tf;
	private TextField player2tf;

	private Label player1OfferStatus = new Label();
	private Label player2OfferStatus = new Label();

	private Item items = new Item();
	private ItemInfo[] listOfItem = items.itemList;

	private int index = -1;
	private int hintPrice;

	private int player1OfferedPrice;
	private int player2OfferedPrice;

	boolean player1turn = true;
	boolean player2turn = false;


	private Random random = new Random();




	@Override
	public void start(Stage primaryStage) {
		//initial the items


		//upper border is used to display all the item information and their order
		upperBorder = new DisplayComponent(listOfItem);


		//bottom board is
		HBox playerStatus = new HBox(35);
		playerStatus.setPadding(new Insets(15, 15, 15, 15));
		playerStatus.setStyle("-fx-border-color: black");
		playerStatus.getChildren().add(player1.vbox);
		playerStatus.getChildren().add(player2.vbox);

		VBox console = buildConsole();


		BorderPane mainPane = new BorderPane();

		mainPane.setTop(upperBorder.pane);
		mainPane.setBottom(playerStatus);
		mainPane.setCenter(console);



		Scene scene = new Scene(mainPane);
		primaryStage.setTitle("Frist Price Auction Demo by Mengchen Yang");
		primaryStage.setScene(scene);
		primaryStage.show();

		nextRound();


	}

	private void nextRound() {
		player1OfferStatus.setText("                ");
		player2OfferStatus.setText("                ");
		index++;
		if (index < listOfItem.length) {
			ItemInfo cur = listOfItem[index];
			if (cur.isRent) {
				player1.moneyLeft += player1.ownedProperty * 0.1;
				player1.updateStatus();
				player2.moneyLeft += player2.ownedProperty * 0.1;
				player2.updateStatus();
				index++;
				upperBorder.deleteItemIndisplay(cur);
				//lastRoundLabel.setText("last round is collection rent");
				lastRoundResult.setText("last round is collecting rent");
				this.nextRound();
			} else {

				double coefficient;

				if (index < 10) {
					coefficient = 0.3;
				} else if (index < 14) {
					coefficient = 0.4;
				} else if (index < 18) {
					coefficient = 0.6;
				} else {
					coefficient = 0.7;
				}
				int randomNum = random.nextInt(5);
				hintPrice = (int)(cur.labeledProperty * coefficient + randomNum);

				if (hintPrice > player1.moneyLeft || hintPrice > player2.moneyLeft) {
					hintPrice = Math.min(player1.moneyLeft, player1.moneyLeft);
				}

				curItemLabel.setText("Current bid is " + cur.name + ", its value is " + cur.labeledProperty +
						", hint is " + hintPrice);


			}

		} else {
			int player1Score = player1.moneyLeft + player1.ownedProperty;
			int player2Score = player1.moneyLeft + player1.ownedProperty;
			if (player1Score > player2Score) {
				curItemLabel.setText("player 1 won!");
			} else if (player1Score == player2Score) {
				curItemLabel.setText("Tied!");
			} else {
				curItemLabel.setText("player 2 won!");
			}
		}
	}


	private VBox buildConsole() {

		player1BidButton.setOnAction(e -> player1BidCurItem());
		player1PassButton.setOnAction(e -> player1PassCurItem());

		player2BidButton.setOnAction(e -> player2BidCurItem());
		player2PassButton.setOnAction(e -> player2PassCurItem());

		newGameButton.setOnAction(e -> startNewGame());
		repeatButton.setOnAction(e -> repeatGame());

		player1tf = new TextField();
		player1tf.setFont(Font.font("Times New Roman", 20));
		player1tf.setAlignment(Pos.BASELINE_RIGHT);

		player2tf = new TextField();
		player2tf.setFont(Font.font("Times New Roman", 20));
		player2tf.setAlignment(Pos.BASELINE_RIGHT);

		VBox pane = new VBox(10);

		//organize the 4 buttons
		GridPane player1Pane = new GridPane();
		player1Pane.setVgap(5);
		player1Pane.setHgap(5);
		player1Pane.add(new Label("Player 1 offers : "), 0,0);
		player1Pane.add(player1tf, 1,0);
		player1Pane.add(player1BidButton, 2,0);
		player1Pane.add(player1PassButton, 3,0);
		player1Pane.add(player1OfferStatus, 4,0);

		GridPane player2Pane = new GridPane();
		player2Pane.setVgap(5);
		player2Pane.setHgap(5);
		player2Pane.add(new Label("Player 2 offers : "), 0,0);
		player2Pane.add(player2tf, 1,0);
		player2Pane.add(player2BidButton, 2,0);
		player2Pane.add(player2PassButton, 3,0);
		player2Pane.add(player2OfferStatus, 4,0);

		GridPane gameControl = new GridPane();
		gameControl.setVgap(5);
		gameControl.setHgap(5);
		gameControl.add(repeatButton, 0, 0);
		gameControl.add(newGameButton, 1, 0);




		pane.getChildren().add(curItemLabel);
		pane.getChildren().add(player1Pane);
		pane.getChildren().add(player2Pane);
		pane.getChildren().add(lastRoundResult);
		pane.getChildren().add(gameControl);
		//pane.getChildren().add(lastRoundLabel);

		return pane;
	}


	private void player1BidCurItem() {
		if (player1turn) {
			int num = Integer.parseInt(player1tf.getText());
			if (num > 0 && num <= player1.moneyLeft) {
				player1OfferedPrice = num;
				player1tf.clear();
				player1OfferStatus.setText("player 1 has finished bid");
				player1turn = false;
				player2turn = true;
			} else {
				player1tf.clear();
				player1OfferStatus.setText("Invalid price, please bid again");
			}
		}

	}


	private void player1PassCurItem() {
		if (player1turn) {
			player1tf.clear();
			player1OfferedPrice = -1;
			player1OfferStatus.setText("player 1 has finished bid");
			player1turn = false;
			player2turn = true;
		}
	}


	private void player2BidCurItem() {
		if (player2turn) {
			int num = Integer.parseInt(player2tf.getText());
			if (num > 0 && num <= player2.moneyLeft) {
				player2OfferedPrice = num;
				player2tf.clear();
				player2OfferStatus.setText("player 1 has finished bid");
				player2turn = false;
				player1turn = true;
				finishThisRound();
			} else {
				player1tf.clear();
				player1OfferStatus.setText("Invalid price, please bid again");
			}
		}

	}


	private void player2PassCurItem() {
		if (player2turn) {
			player2tf.clear();
			player2OfferedPrice = -1;
			player2OfferStatus.setText("player 1 has finished bid");
			player2turn = false;
			player1turn = true;
			finishThisRound();
		}

	}

	private void finishThisRound() {
		if (player1OfferedPrice == -1 && player2OfferedPrice == -1) {
			upperBorder.deleteItemIndisplay(listOfItem[index]);
			nextRound();
			lastRoundResult.setText("No one bid the last item");
		} else if (player1OfferedPrice == -1 || player2OfferedPrice > player1OfferedPrice) {
			player2.moneyLeft -= player2OfferedPrice;
			player2.ownedProperty += listOfItem[index].labeledProperty;
			player2.updateStatus();
			player2.addProperty(listOfItem[index]);
			lastRoundResult.setText("player 2 get the last item");
			nextRound();
		} else if (player2OfferedPrice == -1 || player1OfferedPrice > player2OfferedPrice) {
			player1.moneyLeft -= player1OfferedPrice;
			player1.ownedProperty += listOfItem[index].labeledProperty;
			player1.updateStatus();
			player1.addProperty(listOfItem[index]);
			lastRoundResult.setText("player 1 get the last item");
			nextRound();
		} else if (player1OfferedPrice == player2OfferedPrice) {
			int randomNum = random.nextInt(2);
			if (randomNum == 0) {
				player1.moneyLeft -= player1OfferedPrice;
				player1.ownedProperty += listOfItem[index].labeledProperty;
				player1.updateStatus();
				player1.addProperty(listOfItem[index]);
				lastRoundResult.setText("Two players offered same price last round, player 1 get the last item");
				nextRound();
			} else if (randomNum == 1) {
				player2.moneyLeft -= player2OfferedPrice;
				player2.ownedProperty += listOfItem[index].labeledProperty;
				player2.updateStatus();
				player2.addProperty(listOfItem[index]);
				lastRoundResult.setText("Two players offered same price last round, player 2 get the last item");
				nextRound();
			}
//            upperBorder.deleteItemIndisplay(listOfItem[index]);
//            nextRound();
//            lastRoundResult.setText("Two players offered same price last round");
		}
	}



	private void repeatGame() {
		index = -1;
		upperBorder.repaint(listOfItem);

		player1.propertyList.getChildren().clear();
		player1.ownedProperty = 0;
		player1.moneyLeft = 750;
		player1.curStatus.setText("Total owned property is : " + player1.ownedProperty + "  Money left : " + player1.moneyLeft);
		player1.listIndex = 0;

		player2.propertyList.getChildren().clear();
		player2.ownedProperty = 0;
		player2.moneyLeft = 750;
		player2.curStatus.setText("Total owned property is : " + player2.ownedProperty + "  Money left : " + player2.moneyLeft);
		player2.listIndex = 0;

		nextRound();
	}

	private void startNewGame() {
		Item secondItem = new Item();
		ItemInfo[] newList = secondItem.itemList;
		listOfItem = newList;
		index = -1;
		upperBorder.repaint(listOfItem);

		player1.propertyList.getChildren().clear();
		player1.ownedProperty = 0;
		player1.moneyLeft = 750;
		player1.curStatus.setText("Total owned property is : " + player1.ownedProperty + "  Money left : " + player1.moneyLeft);
		player1.listIndex = 0;

		player2.propertyList.getChildren().clear();
		player2.ownedProperty = 0;
		player2.moneyLeft = 750;
		player2.curStatus.setText("Total owned property is : " + player2.ownedProperty + "  Money left : " + player2.moneyLeft);
		player2.listIndex = 0;

		nextRound();

	}













	public static void main(String[] args) {
		launch(args);
	}




}

class Item {
	private static final String[] ITEMNAME= new String[] {"St.Louis RR", "SantaFe RR", "B&O RR", "Boston RR",
			"Arizona", "Alabama", "Connecticut", "California",
			"Illinois", "Indiana", "Maryland", "Mass.",
			"New York", "New Jersey", "Oregon", "Oklahoma",
			"Tennessee", "Texas", "Virginia", "Vermont"};

	private static final int[] COST = {100, 100, 100, 100, 100, 100, 100, 100, 200, 200, 200, 200,
			200, 200, 200, 200, 300, 300, 300, 300};

	public ItemInfo[] initialList;
	public ItemInfo[] shuffledList;
	public ItemInfo[] itemList;


	public Item() {
		this.initialList = initialItemList(ITEMNAME, COST);
		this.shuffledList = shuffle(initialList);
		this.itemList = addRent(shuffledList, new int[] {10,14,18,22});
	}

	public ItemInfo[] addRent(ItemInfo[] input, int[] rent) {
		ItemInfo[]  itemList = new ItemInfo[24];
		for (int i = 0; i < rent.length; i++) {
			ItemInfo item = new ItemInfo(0, true, "Collection rent");

			//item.setStyle("-fx-border-color: black");
			item.setStyle("-fx-background-color: yellow");
			Label label = new Label("Collection rent");
			label.setFont(Font.font("Times New Roman"));
			item.getChildren().add(label);
			itemList[rent[i] - 1] = item;
		}

		int index = 0;
		for (int i = 0; i < input.length; i++) {
			if (itemList[index] != null) {
				index++;
			}
			itemList[index++] = input[i];
		}
		return itemList;

	}

	public ItemInfo[] shuffle(ItemInfo[] input) {
		for (int i = input.length; i >=1; i--) {
			int index = (int) (Math.random() * i);
			swap(input, i - 1, index);
		}

		return input;
	}

	private void swap(ItemInfo[] input, int left, int right) {
		ItemInfo temp = input[left];
		input[left] = input[right];
		input[right] = temp;
	}

	public ItemInfo[]  initialItemList(String[] names, int[] costs) {
		ItemInfo[] list = new ItemInfo[20];
		for (int i = 0; i < names.length; i++) {
			ItemInfo item = new ItemInfo(costs[i], false, names[i]);

			if (i < 4) {
				item.setStyle("-fx-background-color: green");
			} else if (i < 8) {
				item.setStyle("-fx-background-color: lightskyblue");
			} else if (i < 12) {
				item.setStyle("-fx-background-color: orange");
			} else if (i < 16) {
				item.setStyle("-fx-background-color: hotpink");
			} else {
				item.setStyle("-fx-background-color: lightgray");
			}

			Label label = new Label(names[i] + "  "  + costs[i]);
			label.setFont(Font.font("Times New Roman"));
			item.getChildren().add(label);
			//item.setStyle("-fx-border-color: black");
			list[i] = item;
		}
		return list;
	}

}

class DisplayComponent {

	GridPane pane = new GridPane();

	public DisplayComponent(ItemInfo[] list) {
		this.pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(10,10,10,10));
		pane.setHgap(2.5);
		pane.setVgap(2.5);
		int curIndex = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 8; j++) {
				this.pane.add(list[curIndex], j, i);
				curIndex++;
			}
		}
	}

	public void repaint(ItemInfo[] list) {
		this.pane.getChildren().removeAll();
		this.pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(10,10,10,10));
		pane.setHgap(2.5);
		pane.setVgap(2.5);
		int curIndex = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 8; j++) {
				this.pane.add(list[curIndex], j, i);
				curIndex++;
			}
		}
	}

	public void deleteItemIndisplay(ItemInfo item) {
		pane.getChildren().remove(item);
	}
}


class ItemInfo extends Pane {
	int labeledProperty;
	boolean isRent;
	String name;

	public ItemInfo(int cost, boolean isRent, String name) {
		this.labeledProperty = cost;
		this.isRent = isRent;
		this.name = name;
	}
}

class Player {

	int moneyLeft;
	int ownedProperty;
	VBox vbox = new VBox(10);
	Label curStatus;
	GridPane propertyList = new GridPane();
	int listIndex;

	public Player() {
		moneyLeft = 750;
		ownedProperty = 0;
		listIndex = 0;
		curStatus = new Label("Total owned property is : " + ownedProperty + "  Money left : " + moneyLeft);
		//vbox = new VBox();

		propertyList.setHgap(2.5);
		propertyList.setVgap(2.5);

		vbox.getChildren().add(curStatus);
		vbox.getChildren().add(propertyList);
	}

	public void updateStatus() {
		curStatus.setText("Total owned property is : " + ownedProperty + "  Money left : " + moneyLeft);
	}

	public void addProperty(ItemInfo item) {
		propertyList.add(item, listIndex % 2, listIndex / 2);
		listIndex++;
	}

}



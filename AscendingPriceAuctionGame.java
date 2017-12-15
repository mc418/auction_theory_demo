

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;


public class AscendingPriceAuctionGame extends Application {

	private DisplayComponent upperBorder;

	private Label curItemLabel = new Label();
	private Label curPriceLabel = new Label();
	//Label lastRoundLabel = new Label();
	private Button addFiveButton = new Button("Add 5");
	private Button reduceFiveButton = new Button("Reduce 5");
	private Button bidButton = new Button("Bid");
	private Button passButton = new Button("Pass");
	private Button newGameButton = new Button("Start a new game");
	private Button repeatButton = new Button("repeat game");

	private Player player = new Player();
	private Player computer = new Player();

	private Item items = new Item();
	private ItemInfo[] listOfItem = items.itemList;

	private int index = 0;
	private int hintPrice;
	private int startPrice;
	private int bidPrice;
	private boolean isFirst = true;

	private Random random = new Random();




	@Override
	public void start(Stage primaryStage) {
		//initial the items


		//upper border is used to display all the item information and their order
		upperBorder = new DisplayComponent(items.itemList);


		//bottom board is
		HBox playerStatus = new HBox(35);
		playerStatus.setPadding(new Insets(15, 15, 15, 15));
		playerStatus.setStyle("-fx-border-color: black");
		playerStatus.getChildren().add(player.vbox);
		playerStatus.getChildren().add(computer.vbox);

		VBox console = buildConsole();


		BorderPane mainPane = new BorderPane();

		mainPane.setTop(upperBorder.pane);
		mainPane.setBottom(playerStatus);
		mainPane.setCenter(console);



		Scene scene = new Scene(mainPane);
		primaryStage.setTitle("Ascending Price Auction Demo by Mengchen Yang");
		primaryStage.setScene(scene);
		primaryStage.show();

		nextRound();


	}

	private void nextRound() {
		if (index < listOfItem.length) {
			ItemInfo cur = listOfItem[index];
			if (cur.isRent) {
				player.moneyLeft += player.ownedProperty * 0.1;
				player.curStatus.setText("Total owned property is : " + player.ownedProperty + "  Money left : " + player.moneyLeft);
				computer.moneyLeft += computer.ownedProperty * 0.1;
				computer.curStatus.setText("Total owned property is : " + computer.ownedProperty + "  Money left : " + computer.moneyLeft);
				index++;
				upperBorder.deleteItemIndisplay(cur);
				//lastRoundLabel.setText("last round is collection rent");
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

				if (hintPrice > player.moneyLeft || hintPrice > computer.moneyLeft) {
					upperBorder.deleteItemIndisplay(cur);
					index++;
					this.nextRound();
				}

				curItemLabel.setText("Current bid is " + cur.name + ", its value is " + cur.labeledProperty +
									", hint is " + hintPrice);

				if (isFirst) {
					startPrice = (hintPrice / 10) * 10;
					bidPrice = startPrice;

					curPriceLabel.setText("You First bid, price is " + bidPrice);

				} else {
					if (randomNum == 3 || randomNum == 4) {
						startPrice =   (hintPrice / 10) * 10 + 5;
						bidPrice = startPrice + 5;
						curPriceLabel.setText("Others bid " + startPrice + ", your bid price is " + bidPrice);
						//startPrice = bidPrice;
					} else {
						startPrice =   (hintPrice / 10) * 10;
						bidPrice = startPrice + 5;
						curPriceLabel.setText("Others bid " + startPrice + ", your bid price is " + bidPrice);
						//startPrice = bidPrice;
					}
				}


			}

		} else {
			int playerScore = player.moneyLeft + player.ownedProperty;
			int computerScore = computer.moneyLeft + computer.ownedProperty;
			if (playerScore > computerScore) {
				curItemLabel.setText("player won!");
			} else if (playerScore == computerScore) {
				curItemLabel.setText("Tied!");
			} else {
				curItemLabel.setText("computer won!");
			}
		}
	}


	private VBox buildConsole() {
		addFiveButton.setOnAction(e -> addFiveToPrice());
		reduceFiveButton.setOnAction(e -> reduceFiveOnPrice());
		bidButton.setOnAction(e -> bidCurItem());
		passButton.setOnAction(e -> passCurItem());
		newGameButton.setOnAction(e -> startNewGame());
		repeatButton.setOnAction(e -> repeatGame());

		VBox pane = new VBox(10);

		//organize the 4 buttons
		GridPane buttonPane = new GridPane();
		buttonPane.setVgap(5);
		buttonPane.setHgap(5);
		buttonPane.add(addFiveButton, 0,0);
		buttonPane.add(reduceFiveButton, 1,0);
		buttonPane.add(bidButton, 2,0);
		buttonPane.add(passButton, 3,0);
		buttonPane.add(newGameButton, 4,0);
		buttonPane.add(repeatButton, 5,0);


		pane.getChildren().add(curItemLabel);
		pane.getChildren().add(curPriceLabel);
		pane.getChildren().add(buttonPane);
		//pane.getChildren().add(lastRoundLabel);

		return pane;
	}

	private void repeatGame() {
		index = 0;
		upperBorder.repaint(listOfItem);

		player.propertyList.getChildren().clear();
		player.ownedProperty = 0;
		player.moneyLeft = 750;
		player.curStatus.setText("Total owned property is : " + player.ownedProperty + "  Money left : " + player.moneyLeft);
		player.listIndex = 0;

		computer.propertyList.getChildren().clear();
		computer.ownedProperty = 0;
		computer.moneyLeft = 750;
		computer.curStatus.setText("Total owned property is : " + computer.ownedProperty + "  Money left : " + computer.moneyLeft);
		computer.listIndex = 0;

		nextRound();
	}

	private void startNewGame() {
		Item secondItem = new Item();
		ItemInfo[] newList = secondItem.itemList;
		listOfItem = newList;
		index = 0;
		upperBorder.repaint(listOfItem);

		player.propertyList.getChildren().clear();
		player.ownedProperty = 0;
		player.moneyLeft = 750;
		player.curStatus.setText("Total owned property is : " + player.ownedProperty + "  Money left : " + player.moneyLeft);
		player.listIndex = 0;

		computer.propertyList.getChildren().clear();
		computer.ownedProperty = 0;
		computer.moneyLeft = 750;
		computer.curStatus.setText("Total owned property is : " + computer.ownedProperty + "  Money left : " + computer.moneyLeft);
		computer.listIndex = 0;

		nextRound();

	}


	private void addFiveToPrice() {
		if (index < listOfItem.length && bidPrice <= player.moneyLeft - 5) {
			bidPrice += 5;
			if (isFirst) {
				curPriceLabel.setText("You First bid, price is " + bidPrice);
			} else {
				curPriceLabel.setText("Others bid " + startPrice + ", your bid price is " + bidPrice);
			}
		}

	}


	private void reduceFiveOnPrice() {
		if (index < listOfItem.length) {
			if (bidPrice > startPrice) {
				bidPrice -= 5;
				if (isFirst) {
					curPriceLabel.setText("You First bid, price is " + bidPrice);
				} else {
					curPriceLabel.setText("Others bid " + startPrice + ", your bid price is " + bidPrice);
				}
			}
		}

	}


	private void bidCurItem() {
		if (index < listOfItem.length) {

			if (isFirst) {
				if (bidPrice > hintPrice || hintPrice % 10 == 0 || hintPrice % 10 == 1 || hintPrice % 10 == 2) {
					player.moneyLeft -= bidPrice;
					player.ownedProperty += listOfItem[index].labeledProperty;
					player.curStatus.setText("Total owned property is : " + player.ownedProperty + "  Money left : " + player.moneyLeft);
					player.addProperty(listOfItem[index]);
					isFirst = false;

				} else {
					computer.moneyLeft -= (bidPrice + 5);
					computer.ownedProperty += listOfItem[index].labeledProperty;
					computer.curStatus.setText("Total owned property is : " + computer.ownedProperty + "  Money left : " + computer.moneyLeft);
					computer.addProperty(listOfItem[index]);
					isFirst = true;

				}
			} else {
				if (bidPrice == startPrice) {
					computer.moneyLeft -= (bidPrice);
					computer.ownedProperty += listOfItem[index].labeledProperty;
					computer.curStatus.setText("Total owned property is : " + computer.ownedProperty + "  Money left : " + computer.moneyLeft);
					computer.addProperty(listOfItem[index]);
					isFirst = true;
				} else {
					player.moneyLeft -= bidPrice;
					player.ownedProperty += listOfItem[index].labeledProperty;
					player.curStatus.setText("Total owned property is : " + player.ownedProperty + "  Money left : " + player.moneyLeft);
					player.addProperty(listOfItem[index]);
					isFirst = false;
				}
			}
			index++;
			this.nextRound();
		}

	}


	private void passCurItem() {
		if (index < listOfItem.length) {
			if (isFirst) {
				computer.moneyLeft -= (startPrice);
				computer.ownedProperty += listOfItem[index].labeledProperty;
				computer.curStatus.setText("Total owned property is : " + computer.ownedProperty + "  Money left : " + computer.moneyLeft);
				computer.addProperty(listOfItem[index]);
				isFirst = true;
			} else {
				computer.moneyLeft -= (startPrice);
				computer.ownedProperty += listOfItem[index].labeledProperty;
				computer.curStatus.setText("Total owned property is : " + computer.ownedProperty + "  Money left : " + computer.moneyLeft);
				computer.addProperty(listOfItem[index]);
				isFirst = true;
			}




			index++;
			this.nextRound();
		}
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
		int index = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 8; j++) {
				this.pane.add(list[index], j, i);
				index++;
			}
		}
	}

	public void repaint(ItemInfo[] list) {
		this.pane.getChildren().removeAll();
		this.pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(10,10,10,10));
		pane.setHgap(2.5);
		pane.setVgap(2.5);
		int index = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 8; j++) {
				this.pane.add(list[index], j, i);
				index++;
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



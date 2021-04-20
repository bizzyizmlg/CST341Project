package edu.gcu.cst341.project;

import java.beans.Statement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MyStore {

	Scanner sc = new Scanner(System.in);
	private String name;
	private DBConnect con;
	private boolean cartOrder = false;
	private String customer;
	private int userId;

	MyStore(String name) {
		this.name = name;
		con = new DBConnect();
	}

	public void open() throws Exception {
		String user = null;
		boolean exit = false;
		do {
			switch (UserInterface.menuMain()) {
			case 0:
				System.out.println("...Exiting");
				exit = true;
				break;
			case 1:
				user = login();
				if (user != null) {
					System.out.println("...You are now logged in!!");
					shop();
				} else {
					System.out.println("...Sorry, wrong password.");
				}
				break;
			case 2:

				admin();

				break;
			default:
				open();
			}
		} while (!exit);
	}

	private String login() {
		String result = null;

		String[] login = UserInterface.login();

		String sql = "SELECT user_Id, user_First_Name FROM users WHERE user_Name = ? AND user_Password = ? AND user_Status = 1";

		try (PreparedStatement ps = con.getConnection().prepareStatement(sql)) {
			ps.setString(1, login[0]);
			ps.setString(2, login[1]);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				result = rs.getString("user_First_Name");
				userId = rs.getInt("user_Id");
				customer = rs.getString("user_First_Name");
				System.out.println("Thank you, " + customer + " you are now logged in...");
			} else {
				result = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private void shop() throws Exception {
		switch (UserInterface.menuShop()) {
		case 0:
			return;
		case 1:
			createCartItem();
			break;
		case 2:
			readCartItems();
			break;
		case 3:
			break;
		case 4:
			deleteCartItem();
			break;
		default:
			open();
		}
	}

	private void admin() throws Exception {
		switch (UserInterface.menuAdmin()) {
		case 0:
			return;
		case 1:
			createProduct();
			break;
		case 2:
			readProducts();
			break;
		case 3:
			updateProduct();
			break;
		case 4:
			deleteProduct();
			break;
		default:
			open();
		}
	}

	// Bruce Brown
	private void createCartItem() throws Exception {

		System.out.println("Add item to cart...");
		System.out.println("================");
		readProducts();
		System.out.println("What is your user name Id?...");

		int uId2 = sc.nextInt();
		sc.nextLine();
		System.out.println("Please Enter the Product Id: ");
		String pId2 = sc.nextLine();
		System.out.println("Please Enter the Date: ");
		String pTime = sc.nextLine();

		String sql_command = "INSERT INTO cst341nproject.shoppingcart (user_Id, product_Id, "
				+ "product_Added_Date_Time) VALUE (?, ?, ?);";

		try (PreparedStatement initiate = con.getConnection().prepareStatement(sql_command)) {

			initiate.setInt(1, uId2);
			initiate.setString(2, pId2);
			initiate.setString(3, pTime);

			initiate.executeUpdate();
			System.out.println(initiate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		readProducts();
	}

	
	
	private void readCartItems() {
		System.out.println("View (Read) cart...");
		System.out.println();
	}
	
	// Bruce Brown
	private void deleteCartItem() throws Exception {
		System.out.println("Delete from cart...");
		System.out.println("===============");
		System.out.println("Which item would you like to delete?...");
		readCartItems();

		int cart_Id = sc.nextInt();
		sc.nextLine();

		String sql4 = "DELETE FROM cst341nproject.shoppingcart WHERE user_Id = ?";

		try (PreparedStatement initiate2 = con.getConnection().prepareStatement(sql4)) {

			initiate2.setInt(1, cart_Id);
			initiate2.executeUpdate();
			System.out.println(initiate2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		admin();
	}

	private void createProduct() throws Exception {

		System.out.println("Create product...");
		System.out.println("==================");
		System.out.println("Please Enter a Unique Product Id: ");
		int pId = sc.nextInt();
		sc.nextLine();
		System.out.println("Please Enter the Product Name: ");
		String pName = sc.nextLine();
		System.out.println("Please Enter the Products Price: ");
		String pPrice = sc.nextLine();
		System.out.println("Please Enter Products Inventory Status [true] = In stock [false] = Out of Stock: ");
		String pStatus = sc.nextLine();

		String sql_command = "INSERT INTO cst341nproject.products (product_Id, product_Name, "
				+ "product_Price, product_Stock_Status) VALUE (?, ?, ?, ?);";

		try (PreparedStatement initiate = con.getConnection().prepareStatement(sql_command)) {

			initiate.setInt(1, pId);
			initiate.setString(2, pName);
			initiate.setString(3, pPrice);
			initiate.setString(4, pStatus);
			initiate.executeUpdate();
			System.out.println(initiate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		readProducts();
	}

	private void readProducts() throws Exception {
		System.out.println("View (Read) all products...");
		System.out.println();

		String sql_command = "SELECT product_Id, product_Name, product_Price, product_Stock_Status FROM cst341nproject.products;";

		try (PreparedStatement initiate = con.getConnection().prepareStatement(sql_command)) {

			ResultSet rs = initiate.executeQuery();
			while (rs.next()) {
				System.out.println("ALL Products: " + rs.getInt("product_Id") + " " + rs.getString("product_Name") + " "
						+ rs.getString("product_Price") + " " + rs.getString("product_Stock_Status"));
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateProduct() throws Exception {

		System.out.println("Update product...");
		System.out.println("===========");
		System.out.println("");

		System.out.println("Which Product Id Number would you like to update? ");
		int id = sc.nextInt();
		sc.nextLine();

		String sql = "SELECT product_Id, product_Name, product_Price, product_Stock_Status"
				+ " FROM cst341nproject.products WHERE product_Id = ?";
		try (PreparedStatement initiate = con.getConnection().prepareStatement(sql)) {

			initiate.setInt(1, id);
			ResultSet results = initiate.executeQuery();
			results.next();

			System.out.println("Update Product Id Number : [" + results.getInt("product_Id") + "] ? : ");
			int pId = sc.nextInt();
			sc.nextLine();
			System.out.println("Update Your Product Name : [" + results.getString("product_Name") + "] ? : ");
			String pName = sc.nextLine();
			System.out.println("Update Your Product Price : [" + results.getString("product_Price") + "] ? : ");
			String pPrice = sc.nextLine();
			System.out.println("Update Inventory Status [true] = In Stock [false] = Out of Stock : ["
					+ results.getString("product_Stock_Status") + "] ? : ");

			String inventory = sc.nextLine();

			String sql1 = "UPDATE cst341nproject.products SET product_Id = ?, product_Name = ?, product_Price = ?, "
					+ "product_Stock_Status = ? WHERE product_Id = 132";

			try (PreparedStatement initiate1 = con.getConnection().prepareStatement(sql1)) {

				initiate1.setInt(1, pId);
				initiate1.setString(2, pName);
				initiate1.setString(3, pPrice);
				initiate1.setString(4, inventory);

				initiate1.executeUpdate();

				String sql2 = "SELECT * FROM cst341nproject.products WHERE product_Id = ?";
				try (PreparedStatement initiate2 = con.getConnection().prepareStatement(sql2)) {
					initiate2.setInt(1, id);
					ResultSet results3 = initiate2.executeQuery();
					results3.next();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void deleteProduct() throws SQLException {

		System.out.println("Delete product...");
		System.out.println("=============");
		System.out.println("Which Product Id Would You Like To Delete? ");
		int product_Id = sc.nextInt();
		sc.nextLine();

		String sql3 = "DELETE FROM cst341nproject.products WHERE product_Id = ?";

		try (PreparedStatement initiate = con.getConnection().prepareStatement(sql3)) {

			initiate.setInt(1, product_Id);
			initiate.executeUpdate();
			System.out.println(initiate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
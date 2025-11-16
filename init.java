import java.util.*;
import java.io.ByteArrayInputStream;
import java.sql.*;
import GetImages.*;

abstract class Person {
  String name;
  String email;
  long phoneNo;
  String address;

  public Person(String n, String e, long p, String a) {
    name = n;
    email = e;
    phoneNo = p;
    address = a;
  }

  public void displayDeets() {
    System.out.println("Name:" + name);
    System.out.println("Email:" + email);
    System.out.println("Phone Number:" + phoneNo);
    System.out.println("Address:" + address);
  }
}

class Product {
  int ASIN;
  String name;
  int ratings;
  int price;
  int qty;
  String description;
  ArrayList<Review> reviews = new ArrayList<>();

  public Product(int a, String n, int r, int p, int q, String d) {
    ASIN = a;
    name = n;
    ratings = r;
    price = p;
    qty = q;
    description = d;
  }


  // Load ALL products (without reviews)
  public static List<Product> loadAll(Connection conn) throws SQLException {
    String sql = "SELECT product_id, name, rating, price, quantity, description FROM Products";
    List<Product> result = new ArrayList<>();

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        int id = rs.getInt("product_id");
        String n = rs.getString("name");

        int r = rs.getInt("rating");
        if (rs.wasNull()) r = 0;  // rating is nullable in schema

        int p = rs.getInt("price");
        int q = rs.getInt("quantity");
        String d = rs.getString("description");

        Product prod = new Product(id, n, r, p, q, d);
        result.add(prod);
      }
    }
    return result;
  }

  // Load ONE product by id (without reviews)
  public static Product loadById(Connection conn, int asin) throws SQLException {
    String sql = "SELECT product_id, name, rating, price, quantity, description " +
                 "FROM Products WHERE product_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, asin);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;

        int id = rs.getInt("product_id");
        String n = rs.getString("name");
        int r = rs.getInt("rating");
        if (rs.wasNull()) r = 0;
        int p = rs.getInt("price");
        int q = rs.getInt("quantity");
        String d = rs.getString("description");

        return new Product(id, n, r, p, q, d);
      }
    }
  }

  // Load products for a specific seller_username (inventory)
  public static List<Product> loadBySeller(Connection conn, String sellerUsername) throws SQLException {
    String sql = "SELECT product_id, name, rating, price, quantity, description " +
                 "FROM Products WHERE seller_username = ?";
    List<Product> result = new ArrayList<>();

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, sellerUsername);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          int id = rs.getInt("product_id");
          String n = rs.getString("name");
          int r = rs.getInt("rating");
          if (rs.wasNull()) r = 0;
          int p = rs.getInt("price");
          int q = rs.getInt("quantity");
          String d = rs.getString("description");
          result.add(new Product(id, n, r, p, q, d));
        }
      }
    }
    return result;
  }

  // Load products that appear in a customer's cart (by username)
  public static List<Product> loadCartProductsForCustomer(Connection conn, String customerUsername) throws SQLException {
    String sql =
        "SELECT p.product_id, p.name, p.rating, p.price, p.quantity, p.description " +
        "FROM CartItems ci " +
        "JOIN Products p ON p.product_id = ci.product_id " +
        "WHERE ci.customer_username = ?";
    List<Product> result = new ArrayList<>();

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, customerUsername);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          int id = rs.getInt("product_id");
          String n = rs.getString("name");
          int r = rs.getInt("rating");
          if (rs.wasNull()) r = 0;
          int p = rs.getInt("price");
          int q = rs.getInt("quantity");
          String d = rs.getString("description");
          result.add(new Product(id, n, r, p, q, d));
        }
      }
    }
    return result;
  }

  // Load reviews into this Product (helper instance method using Review.loadByProduct)
  public void loadReviews(Connection conn) throws SQLException {
    this.reviews.clear();
    this.reviews.addAll(Review.loadByProduct(conn, this.ASIN));
  }

  // ===============================================================

  public void addReview() {
    int r;
    String c;
    String rv;
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter Customer name:");
    c = sc.nextLine();
    System.out.println("Enter Ratings:");
    r = sc.nextInt();
    sc.nextLine();
    System.out.println("Enter Review:");
    rv = sc.nextLine();
    reviews.add(new Review(c, r, rv));
  }

  public void displayDeets() {
    System.out.println("ASIN:" + ASIN);
    System.out.println("Name:" + name);
    System.out.println("Ratings:" + ratings);
    System.out.println("Price:" + price);
    System.out.println("Quantity:" + qty);
    System.out.println("Description:" + description);
    if (!reviews.isEmpty()) {
      System.out.println("---- Reviews ----");
      for (Review r : reviews) {
        r.DisplayReview();
        System.out.println("-----------------");
      }
    }
  }

  public int asinNum() {
    return ASIN;
  }

  public void updateDeets() {
    String a;
    String n;
    String r;
    String q;
    String p;
    String d;

    Scanner sc = new Scanner(System.in);

    System.out.println("Enter ASIN Number:");
    a = sc.nextLine();
    if (!a.isEmpty()) {
      try {
        ASIN = Integer.parseInt(a);
      } catch (NumberFormatException e) {
      }
    }
    System.out.println("Enter Product Name:");
    n = sc.nextLine();
    if (!n.isEmpty()) {
      name = n;
    }
    System.out.println("Enter ratings:");
    r = sc.nextLine();
    if (!r.isEmpty()) {
      try {
        ratings = Integer.parseInt(r);
      } catch (NumberFormatException e) {
      }
    }
    System.out.println("Enter price:");
    p = sc.nextLine();
    if (!p.isEmpty()) {
      try {
        price = Integer.parseInt(p);
      } catch (NumberFormatException e) {
      }
    }
    System.out.println("Enter quantity:");
    q = sc.nextLine();
    if (!q.isEmpty()) {
      try {
        qty = Integer.parseInt(q);
      } catch (NumberFormatException e) {
      }
    }
    System.out.println("Enter description:");
    d = sc.nextLine();
    if (!d.isEmpty()) {
      description = d;
    }
  }
}

class Review {
  String cust_name;
  int ratings;
  String review;

  public Review(String c, int rt, String r) {
    cust_name = c;
    ratings = rt;
    review = r;
  }


  // All reviews for a given product_id
  public static List<Review> loadByProduct(Connection conn, int productId) throws SQLException {
    String sql = "SELECT customer_username, rating, review FROM Reviews WHERE product_id = ?";
    List<Review> result = new ArrayList<>();

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, productId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          String custUser = rs.getString("customer_username");
          int rt = rs.getInt("rating");
          String rv = rs.getString("review");
          result.add(new Review(custUser, rt, rv)); // using username as "name"
        }
      }
    }
    return result;
  }

  // Optional: load ALL reviews (rarely needed, but here for completeness)
  public static List<Review> loadAll(Connection conn) throws SQLException {
    String sql = "SELECT customer_username, rating, review FROM Reviews";
    List<Review> result = new ArrayList<>();

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        String custUser = rs.getString("customer_username");
        int rt = rs.getInt("rating");
        String rv = rs.getString("review");
        result.add(new Review(custUser, rt, rv));
      }
    }
    return result;
  }


  public void DisplayReview() {
    System.out.println("Customer name:" + cust_name);
    System.out.println("Ratings:" + ratings);
    System.out.println("Review:" + review);
  }
}

class Customer extends Person {
  String dbUsername;
  ArrayList<Product> product_cart = new ArrayList<>();

  public Customer(String n, String e, long p, String a) {
    super(n, e, p, a);
  }


  // Helper: phone TEXT -> long
  private static long parsePhone(String phone) {
    if (phone == null) return 0L;
    String digits = phone.replaceAll("\\D", "");
    if (digits.isEmpty()) return 0L;
    try {
      return Long.parseLong(digits);
    } catch (NumberFormatException e) {
      return 0L;
    }
  }

  // Load ALL customers (without carts)
  public static List<Customer> loadAll(Connection conn) throws SQLException {
    String sql = "SELECT username, full_name, email, phone, address FROM Customers";
    List<Customer> result = new ArrayList<>();

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        String username = rs.getString("username");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String address = rs.getString("address");
        long phoneNo = parsePhone(phone);

        Customer c = new Customer(fullName, email, phoneNo, address);
        c.dbUsername = username;
        result.add(c);
      }
    }
    return result;
  }

  // Load ONE customer by username (without cart)
  public static Customer loadByUsername(Connection conn, String username) throws SQLException {
    String sql = "SELECT username, full_name, email, phone, address FROM Customers WHERE username = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;

        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String address = rs.getString("address");
        long phoneNo = parsePhone(phone);

        Customer c = new Customer(fullName, email, phoneNo, address);
        c.dbUsername = username;
        return c;
      }
    }
  }

  // Load ONE customer by username, including products in their cart
  public static Customer loadByUsernameWithCart(Connection conn, String username) throws SQLException {
    Customer c = loadByUsername(conn, username);
    if (c == null) return null;

    List<Product> cartProducts = Product.loadCartProductsForCustomer(conn, username);
    c.product_cart.addAll(cartProducts);
    return c;
  }


  public void addToCart(Product p) {
    product_cart.add(p);
  }

  public void showCart() {
    if (product_cart == null || product_cart.isEmpty()) {
      System.out.println("Cart is empty.");
      return;
    }
    for (Product p : product_cart)
      p.displayDeets();
  }

  public void deleteFromCart(int a) {
    try {
      for (int i = 0; i < product_cart.size(); i++) {
        if (a == product_cart.get(i).asinNum()) {
          product_cart.remove(i);
          break;
        }
      }
    } catch (NullPointerException e) {
    }
  }

}

class Seller extends Person {
  String dbUsername;
  ArrayList<Product> product_inventory = new ArrayList<>();

  public Seller(String n, String e, long p, String a) {
    super(n, e, p, a);
  }


  private static long parsePhone(String phone) {
    if (phone == null) return 0L;
    String digits = phone.replaceAll("\\D", "");
    if (digits.isEmpty()) return 0L;
    try {
      return Long.parseLong(digits);
    } catch (NumberFormatException e) {
      return 0L;
    }
  }

  // Load ALL sellers (without inventory)
  public static List<Seller> loadAll(Connection conn) throws SQLException {
    String sql = "SELECT username, display_name, email, phone, address FROM Sellers";
    List<Seller> result = new ArrayList<>();

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        String username = rs.getString("username");
        String displayName = rs.getString("display_name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String address = rs.getString("address");
        long phoneNo = parsePhone(phone);

        Seller s = new Seller(displayName, email, phoneNo, address);
        s.dbUsername = username;
        result.add(s);
      }
    }
    return result;
  }

  // Load ONE seller by username (without inventory)
  public static Seller loadByUsername(Connection conn, String username) throws SQLException {
    String sql = "SELECT username, display_name, email, phone, address FROM Sellers WHERE username = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;

        String displayName = rs.getString("display_name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String address = rs.getString("address");
        long phoneNo = parsePhone(phone);

        Seller s = new Seller(displayName, email, phoneNo, address);
        s.dbUsername = username;
        return s;
      }
    }
  }

  // Load seller + their inventory in one go
  public static Seller loadByUsernameWithInventory(Connection conn, String username) throws SQLException {
    Seller s = loadByUsername(conn, username);
    if (s == null) return null;
    List<Product> inventory = Product.loadBySeller(conn, username);
    s.product_inventory.addAll(inventory);
    return s;
  }


  public void addProduct() {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter ASIN Number:");
    int a = sc.nextInt();
    sc.nextLine();

    System.out.println("Enter Product Name:");
    String n = sc.nextLine();

    System.out.println("Enter ratings:");
    int r = sc.nextInt();
    sc.nextLine();

    System.out.println("Enter price:");
    int p = sc.nextInt();
    sc.nextLine();

    System.out.println("Enter quantity:");
    int q = sc.nextInt();
    sc.nextLine();

    System.out.println("Enter description:");
    String d = sc.nextLine();

    product_inventory.add(new Product(a, n, r, p, q, d));
  }

  public void showInventory() {
    if (product_inventory == null || product_inventory.isEmpty()) {
      System.out.println("Cart is empty.");
      return;
    }
    for (Product p : product_inventory)
      p.displayDeets();
  }

  public void deleteFromInventory(int a) {
    try {
      for (int i = 0; i < product_inventory.size(); i++) {
        if (a == product_inventory.get(i).asinNum()) {
          product_inventory.remove(i);
          break;
        }
      }
    } catch (NullPointerException e) {
    }
  }

  public void showSingleProduct(int a) {
    try {
      for (int i = 0; i < product_inventory.size(); i++) {
        if (a == product_inventory.get(i).asinNum()) {
          product_inventory.get(i).displayDeets();
          break;
        }
      }
    } catch (NullPointerException e) {
    }
  }

  public void updateProduct(int a) {
    try {
      for (int i = 0; i < product_inventory.size(); i++) {
        if (a == product_inventory.get(i).asinNum()) {
          product_inventory.get(i).updateDeets();
          break;
        }
      }
    } catch (NullPointerException e) {
    }
  }

}

public class init {
  public static void main(String[] args) {
    getimg imgGetter = new getimg();
    try {
      imgGetter.getImages("cats");
    } catch (Exception e) {
      System.out.println(e);
    }

    // ===== DEMO: using the static load methods with SQLite =====
    String dbPath = "shop.db"; // adjust to your actual DB file
    String url = "jdbc:sqlite:" + dbPath;

    try (Connection conn = DriverManager.getConnection(url)) {

      // Example 1: Load one product by id and its reviews
      Product p = Product.loadById(conn, 1);
      if (p != null) {
        p.loadReviews(conn);
        System.out.println("=== Product 1 (with reviews) ===");
        p.displayDeets();
      }

      // Example 2: Load a seller with their inventory
      Seller s = Seller.loadByUsernameWithInventory(conn, "seller_novatech");
      if (s != null) {
        System.out.println("\n=== Seller 'seller_novatech' (with inventory) ===");
        s.displayDeets();
        s.showInventory();
      }

      // Example 3: Load a customer with their cart
      Customer c = Customer.loadByUsernameWithCart(conn, "cust_arya");
      if (c != null) {
        System.out.println("\n=== Customer 'cust_arya' (with cart) ===");
        c.displayDeets();
        c.showCart();
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }


    // // --- People (also exercises Person.displayDeets) ---
    // Seller s = new Seller("Alice Seller", "alice@shop.com", 9876543210L, "123 Market St");
    // Customer c = new Customer("Carl Customer", "carl@mail.com", 9123456780L, "45 River Rd");
    // s.displayDeets();
    // c.displayDeets();
    //
    // // --- Products + Reviews (displayDeets, asinNum, Review.DisplayReview) ---
    // Product p1 = new Product(111, "Keyboard", 4, 1999, 10, "Compact mechanical keyboard");
    // Product p2 = new Product(222, "Mouse", 5, 999, 25, "Wireless mouse");
    // Review r1 = new Review("Bob", 5, "Great build quality!");
    // p1.reviews.add(r1);
    // p1.displayDeets();
    // p2.displayDeets();
    //
    // // --- Seller inventory (showInventory, showSingleProduct) ---
    // s.product_inventory.add(p1);
    // s.product_inventory.add(p2);
    // s.showInventory();
    // s.showSingleProduct(222);
    //
    // // --- Customer cart (addToCart, showCart, deleteFromCart) ---
    // c.addToCart(p1);
    // c.addToCart(p2);
    // c.showCart();
    // c.deleteFromCart(111);
    // c.showCart();
    //
    // // --- Product.updateDeets via Seller.updateProduct (hardcoded System.in) ---
    // String updateInputs = String.join("\n",
    //   "333",         // new ASIN
    //   "Mouse Pro",   // new name
    //   "4",           // ratings
    //   "1299",        // price
    //   "30",          // qty
    //   "Upgraded sensor"
    // );
    // System.setIn(new ByteArrayInputStream(updateInputs.getBytes()));
    // s.updateProduct(222);          // calls Product.updateDeets() internally
    // s.showSingleProduct(333);      // ASIN changed from 222 -> 333
    //
    // // --- Seller.addProduct (hardcoded System.in) ---
    // String addInputs = String.join("\n",
    //   "444",   // ASIN (int)
    //   "Mouse X",
    //   "5",     // ratings (int)
    //   "1499",  // price (int)
    //   "12",    // qty (int)
    //   ""       // description (empty)
    // ) + "\n";
    //
    // System.setIn(new ByteArrayInputStream(addInputs.getBytes()));
    // s.addProduct();
    //
    // // --- Seller.deleteFromInventory ---
    // s.deleteFromInventory(111); // remove p1 by ASIN
    // s.showInventory();
  }
}

CREATE DATABASE ShopInventory;
USE ShopInventory;

CREATE TABLE Categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

-- Create Products table
CREATE TABLE Products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    category_id INT,
    name VARCHAR(200) NOT NULL,
    barcode VARCHAR(50) UNIQUE,
    selling_price DECIMAL(10, 2) NOT NULL,
    cost_price DECIMAL(10, 2) NOT NULL,
    unit_type VARCHAR(50),
    min_stock_level INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id)
);

-- Create Inventory table
CREATE TABLE Inventory (
    inventory_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT UNIQUE,
    current_quantity INT DEFAULT 0,
    reserved_quantity INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

-- Create Suppliers table
CREATE TABLE Suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(50),
    email VARCHAR(100),
    address TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    balance DECIMAL(12, 2) DEFAULT 0.00
);

-- Create Staff table
CREATE TABLE Staff (
    staff_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    contact VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    last_login DATETIME
);

-- Create Customers table
CREATE TABLE Customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(100),
    address TEXT,
    credit_limit DECIMAL(12, 2) DEFAULT 0.00,
    current_balance DECIMAL(12, 2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    registered_date DATE DEFAULT (CURRENT_DATE)
);

-- Create Purchase Orders table
CREATE TABLE PurchaseOrders (
    po_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_id INT,
    staff_id INT,
    order_date DATE DEFAULT (CURRENT_DATE),
    status VARCHAR(50) DEFAULT 'PENDING',
    total_amount DECIMAL(12, 2) DEFAULT 0.00,
    paid_amount DECIMAL(12, 2) DEFAULT 0.00,
    payment_status VARCHAR(50) DEFAULT 'UNPAID',
    notes TEXT,
    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id),
    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id)
);

-- Create Purchase Order Items table
CREATE TABLE PurchaseOrderItems (
    po_item_id INT PRIMARY KEY AUTO_INCREMENT,
    po_id INT,
    product_id INT,
    quantity INT NOT NULL,
    unit_cost DECIMAL(10, 2) NOT NULL,
    total_cost DECIMAL(12, 2) GENERATED ALWAYS AS (quantity * unit_cost) STORED,
    FOREIGN KEY (po_id) REFERENCES PurchaseOrders(po_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

-- Create Sales table
CREATE TABLE Sales (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    staff_id INT,
    sale_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(12, 2) DEFAULT 0.00,
    tax_amount DECIMAL(12, 2) DEFAULT 0.00,
    discount_amount DECIMAL(12, 2) DEFAULT 0.00,
    total_amount DECIMAL(12, 2) DEFAULT 0.00,
    payment_method VARCHAR(50),
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    notes TEXT,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id)
);


-- Create Sale Items table
CREATE TABLE SaleItems (
    sale_item_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_id INT,
    product_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(12, 2) GENERATED ALWAYS AS (quantity * unit_price) STORED,
    discount DECIMAL(12, 2) DEFAULT 0.00,
    FOREIGN KEY (sale_id) REFERENCES Sales(sale_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

-- Create Stock Movements table
CREATE TABLE StockMovements (
    movement_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    movement_type VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    reference_type VARCHAR(50),
    reference_id INT,
    staff_id INT,
    movement_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (product_id) REFERENCES Products(product_id),
    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id)
);

-- Create Payment Transactions table
CREATE TABLE PaymentTransactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    transaction_type VARCHAR(50) NOT NULL,
    reference_id INT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    staff_id INT,
    status VARCHAR(50) DEFAULT 'COMPLETED',
    notes TEXT,
    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id)
);


-- Create indexes for better query performance
CREATE INDEX idx_products_category ON Products(category_id);
CREATE INDEX idx_inventory_product ON Inventory(product_id);
CREATE INDEX idx_po_supplier ON PurchaseOrders(supplier_id);
CREATE INDEX idx_po_staff ON PurchaseOrders(staff_id);
CREATE INDEX idx_poitems_po ON PurchaseOrderItems(po_id);
CREATE INDEX idx_poitems_product ON PurchaseOrderItems(product_id);
CREATE INDEX idx_sales_customer ON Sales(customer_id);
CREATE INDEX idx_sales_staff ON Sales(staff_id);
CREATE INDEX idx_saleitems_sale ON SaleItems(sale_id);
CREATE INDEX idx_saleitems_product ON SaleItems(product_id);
CREATE INDEX idx_stockmov_product ON StockMovements(product_id);
CREATE INDEX idx_stockmov_staff ON StockMovements(staff_id);
CREATE INDEX idx_paytrans_staff ON PaymentTransactions(staff_id);



-- 1. Opening Shop Procedures
DELIMITER //

-- Staff Login Procedure
CREATE PROCEDURE StaffLogin(
    IN p_username VARCHAR(50),
    IN p_password_hash VARCHAR(255),
    OUT p_login_success BOOLEAN,
    OUT p_staff_id INT
)
BEGIN
    DECLARE staff_exists INT;

    SELECT staff_id, COUNT(*) INTO p_staff_id, staff_exists
    FROM Staff 
    WHERE username = p_username 
    AND password_hash = p_password_hash 
    AND is_active = TRUE;

    IF staff_exists > 0 THEN
        UPDATE Staff 
        SET last_login = CURRENT_TIMESTAMP 
        WHERE staff_id = p_staff_id;

        SET p_login_success = TRUE;
    ELSE
        SET p_login_success = FALSE;
        SET p_staff_id = NULL;
    END IF;
END //

-- Check Low Stock Items
CREATE PROCEDURE CheckLowStockItems()
BEGIN
    SELECT 
        p.product_id,
        p.name,
        p.min_stock_level,
        i.current_quantity,
        s.name AS supplier_name,
        s.contact_person,
        s.phone
    FROM Products p
    JOIN Inventory i ON p.product_id = i.product_id
    LEFT JOIN PurchaseOrders po ON po.product_id = p.product_id  -- FIXED JOIN CONDITION
    LEFT JOIN Suppliers s ON po.supplier_id = s.supplier_id
    WHERE i.current_quantity <= p.min_stock_level
    AND p.is_active = TRUE;
END //

-- Initialize New Sale
CREATE PROCEDURE InitializeSale(
    IN p_staff_id INT,
    IN p_customer_id INT,
    OUT p_sale_id INT
)
BEGIN
    INSERT INTO Sales (staff_id, customer_id, sale_date, payment_status)
    VALUES (p_staff_id, p_customer_id, CURRENT_TIMESTAMP, 'PENDING');

    SET p_sale_id = LAST_INSERT_ID();
END //

-- Add Item to Sale
CREATE PROCEDURE AddSaleItem(
    IN p_sale_id INT,
    IN p_product_id INT,
    IN p_quantity INT,
    OUT p_success BOOLEAN
)
BEGIN
    DECLARE v_available_qty INT;
    DECLARE v_unit_price DECIMAL(10, 2);

    -- Check available quantity
    SELECT current_quantity - reserved_quantity 
    INTO v_available_qty
    FROM Inventory 
    WHERE product_id = p_product_id;

    SELECT selling_price 
    INTO v_unit_price
    FROM Products 
    WHERE product_id = p_product_id;

    IF v_available_qty >= p_quantity THEN
        INSERT INTO SaleItems (sale_id, product_id, quantity, unit_price)
        VALUES (p_sale_id, p_product_id, p_quantity, v_unit_price);

        UPDATE Inventory 
        SET reserved_quantity = reserved_quantity + p_quantity
        WHERE product_id = p_product_id;

        SET p_success = TRUE;
    ELSE
        SET p_success = FALSE;
    END IF;
END //

-- Complete Sale
CREATE PROCEDURE CompleteSale(
    IN p_sale_id INT,
    IN p_payment_method VARCHAR(50),
    OUT p_success BOOLEAN
)
BEGIN
    DECLARE v_total DECIMAL(12, 2);

    START TRANSACTION;

    SELECT SUM(total_price) INTO v_total 
    FROM SaleItems 
    WHERE sale_id = p_sale_id;

    UPDATE Sales 
    SET 
        total_amount = v_total,
        payment_method = p_payment_method,
        payment_status = 'COMPLETED'
    WHERE sale_id = p_sale_id;

    INSERT INTO PaymentTransactions (
        transaction_type,
        reference_id,
        amount,
        payment_method,
        status
    )
    VALUES (
        'SALE',
        p_sale_id,
        v_total,
        p_payment_method,
        'COMPLETED'
    );

    COMMIT;

    SET p_success = TRUE;
END //

-- Trigger for updating stock after sale completion
CREATE TRIGGER after_sale_complete
AFTER UPDATE ON Sales
FOR EACH ROW
BEGIN
    IF NEW.payment_status = 'COMPLETED' AND OLD.payment_status = 'PENDING' THEN
        UPDATE Inventory i
        JOIN SaleItems si ON i.product_id = si.product_id
        SET 
            i.current_quantity = i.current_quantity - si.quantity,
            i.reserved_quantity = i.reserved_quantity - si.quantity
        WHERE si.sale_id = NEW.sale_id;

        INSERT INTO StockMovements (
            product_id,
            movement_type,
            quantity,
            reference_type,
            reference_id,
            staff_id
        )
        SELECT 
            si.product_id,
            'SALE',
            si.quantity,
            'SALE',
            NEW.sale_id,
            NEW.staff_id
        FROM SaleItems si
        WHERE si.sale_id = NEW.sale_id;
    END IF;
END //

-- Daily Sales Report
CREATE PROCEDURE GetDailySalesReport(IN p_date DATE)
BEGIN
    SELECT 
        DATE(sale_date) AS sale_date,
        COUNT(DISTINCT sale_id) AS total_sales,
        SUM(total_amount) AS total_revenue,
        SUM(tax_amount) AS total_tax,
        SUM(discount_amount) AS total_discounts,
        payment_method,
        payment_status,
        COUNT(DISTINCT customer_id) AS unique_customers
    FROM Sales
    WHERE DATE(sale_date) = p_date
    GROUP BY DATE(sale_date), payment_method, payment_status;
END //

-- Daily Profit Report
CREATE PROCEDURE GetDailyProfitReport(IN p_date DATE)
BEGIN
    SELECT 
        DATE(s.sale_date) AS sale_date,
        SUM(si.quantity * (si.unit_price - p.cost_price)) AS gross_profit,
        SUM(si.quantity * p.cost_price) AS total_cost,
        SUM(si.total_price) AS total_revenue,
        COUNT(DISTINCT s.sale_id) AS number_of_sales
    FROM Sales s
    JOIN SaleItems si ON s.sale_id = si.sale_id
    JOIN Products p ON si.product_id = p.product_id
    WHERE DATE(s.sale_date) = p_date
    GROUP BY DATE(s.sale_date);
END //

-- Stock Level Report
CREATE PROCEDURE GetStockLevelReport()
BEGIN
    SELECT 
        p.category_id,
        c.name AS category_name,
        p.product_id,
        p.name AS product_name,
        i.current_quantity,
        i.reserved_quantity,
        p.min_stock_level,
        CASE 
            WHEN i.current_quantity <= p.min_stock_level THEN 'LOW'
            WHEN i.current_quantity <= (p.min_stock_level * 1.5) THEN 'MEDIUM'
            ELSE 'GOOD'
        END AS stock_status
    FROM Products p
    JOIN Categories c ON p.category_id = c.category_id
    JOIN Inventory i ON p.product_id = i.product_id
    WHERE p.is_active = TRUE
    ORDER BY stock_status, c.name, p.name;
END //

DELIMITER ;

INSERT INTO Staff (name, username, password_hash, role, contact, is_active, last_login) VALUES ('John Doe', 'johndoe', 'password', 'Manager', '9876543210', TRUE, '2024-01-30 10:15:00');



select * from Products;
INSERT INTO Suppliers (supplier_id, name, contact, email) VALUES
(1, 'Tech Distributors', '9876543210', 'techsupplies@example.com'),
(2, 'Gadgets Wholesale', '9123456789', 'gadgetswholesale@example.com'),
(3, 'Digital Solutions', '9001112222', 'digitalsolutions@example.com');

INSERT INTO Suppliers (supplier_id, name, contact_person, phone, email, address, is_active, balance) 
VALUES 
(1, 'Tech Supplies Inc.', 'John Doe', '123-456-7890', 'johndoe@techsupplies.com', '123 Tech St, Silicon Valley, CA', TRUE, 5000.00),
(2, 'Alpha Distributors', 'Jane Smith', '987-654-3210', 'janesmith@alphadistributors.com', '456 Alpha Rd, New York, NY', TRUE, 3500.50),
(3, 'Greenfield Enterprises', 'Michael Johnson', '555-123-4567', 'michael.johnson@greenfield.com', '789 Greenfield Ave, Los Angeles, CA', TRUE, 1200.75),
(4, 'Beta Global', 'Emily Davis', '444-987-6543', 'emilydavis@betaglobal.com', '101 Beta Blvd, Miami, FL', FALSE, 0.00),
(5, 'Omega Logistics', 'David Brown', '333-555-7777', 'davidb@omegalogistics.com', '202 Omega Ln, Chicago, IL', TRUE, 2150.00);

INSERT INTO Categories (category_id, name, description, is_active)
VALUES
(1, 'Electronics', 'Devices like computers, phones, and gadgets', TRUE),
(2, 'Furniture', 'Office and home furniture', TRUE),
(3, 'Accessories', 'Items that complement main products, such as chargers and cables', TRUE),
(4, 'Peripherals', 'Peripheral devices like keyboards, mice, and monitors', TRUE);


INSERT INTO Products (category_id, name, barcode, selling_price, cost_price, unit_type, min_stock_level, is_active)
VALUES
(1, 'Laptop', '123456789012', 1500.00, 1200.00, 'Piece', 5, TRUE),
(2, 'Office Chair', '987654321098', 250.00, 180.00, 'Piece', 10, TRUE),
(3, 'Wireless Mouse', '192837465011', 20.00, 12.00, 'Piece', 50, TRUE),
(1, 'Smartphone', '564738291001', 800.00, 600.00, 'Piece', 10, TRUE),
(4, 'Keyboard', '102938475600', 45.00, 30.00, 'Piece', 30, TRUE);


select * from PurchaseOrders;



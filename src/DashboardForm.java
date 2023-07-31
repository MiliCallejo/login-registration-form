import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardForm extends JFrame {
    private JLabel lbAdmin;
    private JButton btnRegister;
    private JPanel dashboardPanel;

    public DashboardForm(){
        setTitle("Dashboard Panel");
        setContentPane(dashboardPanel);
        setMinimumSize(new Dimension(450,475));
        //setSize(1200,760);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        boolean hasRegistredUsers = connectDatabase();

        if (hasRegistredUsers){
            //Formulario de Acceso
            LoginForm loginForm = new LoginForm(this);
            User user = loginForm.user;

            if (user != null){
                lbAdmin.setText("User " + user.name);
                setLocationRelativeTo(null);
                setVisible(true);
            } else {
                dispose();
            }
        } else {
            //Formulario de Registro
            RegistrationForm registrationForm = new RegistrationForm(this);
            User user = registrationForm.user;

            if (user != null){
                lbAdmin.setText("User " + user.name);
                setLocationRelativeTo(null);
                setVisible(true);
            } else {
                dispose();
            }
        }
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegistrationForm registrationForm = new RegistrationForm(DashboardForm.this);
                User user = registrationForm.user;

                if (user != null){
                    JOptionPane.showMessageDialog(DashboardForm.this, "Nuevo Usuario: " + user.name,
                            "Registración Exitosa",JOptionPane.ERROR_MESSAGE );
                }
            }
        });
    }

    private boolean connectDatabase() {
        boolean hasRegistredUsers = false;

        final String MYSQL_SERVER_URL = "jdbc:mysql://localhost/";
        final String DB_URL = "jdbc:mysql://localhost/login-registration?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            //Conectar a la base de datos, si no existe crearla
            Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, USERNAME, PASSWORD);
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS users");
            statement.close();
            conn.close();

            //Conectar a la base de datos, y crear la tabla "users" si no existe
            conn = DriverManager.getConnection(MYSQL_SERVER_URL, USERNAME, PASSWORD);
            statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXIST users ("
                    +"id INT( 10 ) NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    +"name VARCHAR( 200 ) NOT NULL,"
                    +"email VARCHAR(200) NOT NULL UNIQUE,"
                    +"phone VARCHAR(200),"
                    +"address VARCHAR(200),"
                    +"password VARCHAR(200) NOT NULL"
                    +")";
            statement.executeUpdate(sql);

            //Verificar si hay users en la tabla Users
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");

            if (resultSet.next()){
                int numUsers = resultSet.getInt(1);
                if (numUsers > 0) {
                    hasRegistredUsers = true;
                }
            }
            statement.close();
            conn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return hasRegistredUsers;
    }

    public static void main(String[] args) {
        DashboardForm myForm = new DashboardForm();
    }
}

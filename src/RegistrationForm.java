import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class RegistrationForm extends JDialog{

    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JTextField tfName;
    private JPasswordField pfPassword;
    private JPasswordField pfRepeatPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel registerPanel;

    public RegistrationForm(JFrame parent){
        super(parent);
        setTitle("Crear nueva cuenta: ");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setVisible(true);
    }

    private void registerUser() {
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String address = tfAddress.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPasswrod = String.valueOf(pfRepeatPassword.getPassword());

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos",
                    "Intenta Nuevamente",JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPasswrod)){
            JOptionPane.showMessageDialog(this, "Las contrasenias no coinciden",
                    "Intenta Nuevamente",JOptionPane.ERROR_MESSAGE );
            return;
        }

        user = addUserToDatabase(name, email, password, confirmPasswrod, address, phone);

        if (user != null){
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear nuevo usuario",
                    "Intenta Nuevamente",JOptionPane.ERROR_MESSAGE );
        }

    }

    public User user;
    private User addUserToDatabase(String name, String email, String password, String confirmPasswrod, String address, String phone) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/login-registration?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            //Conexion exitosa

            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO users (name, email, phone, address, password)" + "VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, password);

            //Insertar filas en las tablas
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0){
                user = new User();
                user.name = name;
                user.email = email;
                user.phone = phone;
                user.address = address;
                user.password = password;
            }

            stmt.close();
            conn.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        return user;
    }

    public static void main(String[] args) {
        RegistrationForm myForm = new RegistrationForm(null);
        User user = myForm.user;

        if (user != null){
            System.out.println("Registro exitoso de: " + user.name);
        } else {
            System.out.println("Registro cancelado");
        }
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog {
    private JButton btnIngresar;
    private JButton btnCancelar;
    private JPanel loginPanel;
    private JTextField miTextField;
    private JLabel tfEmail;
    private JLabel tfPassword;
    private JPasswordField pfPassword;

    public LoginForm(JFrame parent){
        super(parent);
        setTitle("Acceso: ");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());

                user = getAuthenticatedUser(email, password);

                if (user != null){
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, "Mail o Contraseña incorrectos",
                            "Intenta Nuevamente",JOptionPane.ERROR_MESSAGE );
                }
            }
        });
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setVisible(true);
    }

    public User user;
    private User getAuthenticatedUser(String email, String password) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/login-registration?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            //Conexion exitosa

            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                user = new User();
                user.name = resultSet.getString("name");
                user.email = resultSet.getString("email");
                user.phone = resultSet.getString("phone");
                user.address = resultSet.getString("address");
                user.password = resultSet.getString("password");
            }

            stmt.close();
            conn.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        return user;

    }

    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm(null);
        User user = loginForm.user;

        if (user != null){
            System.out.println("Autenticación exitosa de: " + user.name);
            System.out.println("Email: " + user.email);
            System.out.println("Teléfono: " + user.phone);
            System.out.println("Dirección: " + user.address);
        }
        else {
            System.out.println("Autenticación cancelada");
        }
    }


}

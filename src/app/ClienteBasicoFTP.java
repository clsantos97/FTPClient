package app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.DefaultListModel;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;


public class ClienteBasicoFTP extends JFrame implements ActionListener, ListSelectionListener{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JButton btnEliminar;
    private JButton btnSalir;
    private JButton btnDescargar;
    private JButton btnSubir;
    private JButton btnConnect;
    private JLabel lblSelectedFile;
    private final DefaultListModel<String> model = new DefaultListModel<String>();
    private JList<String> list = new JList<String>(model);
    private JLabel lblError;
    private FTPClient client;
    
    // WINDOWS
    private final String directorioInicial=System.getProperty("user.home")+"/Documents/";
    private final String carpetaDestino=System.getProperty("user.home")+"/Downloads/"; 
    
    // UBUNTU
//    private final String directorioInicial="/home/ubuntu/ftpClientFolder/";
//    private final String carpetaDestino="/home/ubuntu/ftpClienteDownloads"; 
    
    private String directorioSeleccionado=directorioInicial;
    private String directorioDescarga=carpetaDestino;
    private String ficheroSeleccionado="";
    

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ClienteBasicoFTP frame = new ClienteBasicoFTP();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ClienteBasicoFTP() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 470, 630);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.setBackground(new java.awt.Color(110, 146, 161));

        JLabel lblUserName = new JLabel("Usuario: ");
        lblUserName.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblUserName.setBounds(10, 11, 135, 24);
        contentPane.add(lblUserName);

        JTextField txtUserName = new JTextField();
        txtUserName.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txtUserName.setBounds(90, 15, 100, 20);
        contentPane.add(txtUserName);
        txtUserName.setColumns(10);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txtPass.setBounds(90, 46, 100, 20);
        contentPane.add(txtPass);

        JLabel lblPassword = new JLabel("Password :");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblPassword.setBounds(10, 44, 90, 24);
        contentPane.add(lblPassword);

        JLabel lblServidorFtp = new JLabel("Servidor FTP: ");
        lblServidorFtp.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblServidorFtp.setBounds(200, 11, 93, 20);
        contentPane.add(lblServidorFtp);

        JTextField textField = new JTextField();
        textField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        textField.setBounds(310, 15, 135, 20);
        contentPane.add(textField);
        textField.setColumns(10);

        btnConnect = new JButton("Conectar");
        btnConnect.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnConnect.setBounds(309, 50, 135, 23);
        contentPane.add(btnConnect);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 100, 280, 435);
        contentPane.add(scrollPane);

        list = new JList<String>();
        scrollPane.setViewportView(list);
        list.setFont(new Font("Tahoma", Font.PLAIN, 15));

        btnSubir = new JButton("Subir");
        btnSubir.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnSubir.setBounds(309, 100, 135, 23);
        contentPane.add(btnSubir);

        btnDescargar = new JButton("Descargar");
        btnDescargar.setEnabled(false);
        btnDescargar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnDescargar.setBounds(309, 130, 135, 23);
        contentPane.add(btnDescargar);

        btnSalir = new JButton("Salir");
        btnSalir.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnSalir.setBounds(309, 190, 135, 23);
        contentPane.add(btnSalir);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnEliminar.setBounds(309, 160, 135, 23);
        contentPane.add(btnEliminar);

        lblSelectedFile = new JLabel("-");
        lblSelectedFile.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblSelectedFile.setBounds(10, 534, 280, 14);
        contentPane.add(lblSelectedFile);

        lblError = new JLabel("-");
        lblError.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblError.setForeground(Color.RED);
        lblError.setBounds(10, 557, 280, 14);
        contentPane.add(lblError);

        //Add ActionListener
        btnConnect.addActionListener(this);
        btnDescargar.addActionListener(this);
        btnSubir.addActionListener(this);
        btnSalir.addActionListener(this);
        btnEliminar.addActionListener(this);
        list.addListSelectionListener(this);
        
        client= new FTPClient();
        

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnSubir)) {
            JFileChooser c= new JFileChooser();
            c.setDialogTitle("Seleccione el fichero a subir");
            int retorno=c.showDialog(this,"Subir");
            
            if(retorno==JFileChooser.APPROVE_OPTION){
                File fichero= c.getSelectedFile();
                String path= fichero.getAbsolutePath();
                String name= fichero.getName();
                //System.out.println("path= "+path );
                //System.out.println("nombre= "+name );
                subirFichero(path,name);
            }
            
            

        } else if (e.getSource().equals(btnConnect)) {
            try {
                client.connect(InetAddress.getByName("127.0.0.1"),21);
                boolean login=client.login("ubuntu", "ubuntu");
                //FTPFile [] files= client.listFiles();
                //System.out.println("Conection maked");
                printAllFiles(directorioSeleccionado);
                btnConnect.setEnabled(false);
                client.changeWorkingDirectory(directorioSeleccionado);
            } catch (IOException ex) {
                Logger.getLogger(ClienteBasicoFTP.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("gui.ClienteBasicoFTP.actionPerformed()");
            }
            
        } else if (e.getSource().equals(btnDescargar)) {
            //ficheroSeleccionado=list.getName();
            descargarFichero(ficheroSeleccionado);
        } else if (e.getSource().equals(btnEliminar)) {
            //ficheroSeleccionado=list.getName();
            eliminarFichero(ficheroSeleccionado);
        }else if(e.getSource().equals(btnSalir)){
        
        }

    }

    private void printAllFiles(String directorioSeleccinado) {
        try {
            model.clear();
            FTPFile [] files= client.listFiles(directorioSeleccinado);
            model.addElement(directorioSeleccinado);
            System.out.println(files.length);
            for(int i=0; i<files.length;i++){
                if (!(files[i].getName()).equals(".") && !(files[i].getName()).equals("..")){
                    String f= files[i].getName();
                    //si es directorio se le añade (DIR)
                    if(files[i].isDirectory()){
                        f="(DIR) "+f;
                    }
                    model.addElement(f);
                }
            }
            list.setModel(model);
            
        } catch (IOException ex) {
            Logger.getLogger(ClienteBasicoFTP.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }

    private void subirFichero(String path, String name) {
        try {
            //Asignamos el tipo de fichero a tipo binario
            client.setFileType(FTP.BINARY_FILE_TYPE);
            BufferedInputStream in= new BufferedInputStream(new FileInputStream(path));
            boolean ok=client.storeFile(name, in);
            if(ok){
                lblSelectedFile.setText("-"+ path);
                printAllFiles(directorioSeleccionado);
            }else{
                lblError.setText("-Error, al cargar el archivo");
            }
            
        } catch (IOException ex) {
            Logger.getLogger(ClienteBasicoFTP.class.getName()).log(Level.SEVERE, null, ex);
            lblError.setText("-Error, al cargar el archivo");
        }
        
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(list.getSelectedIndex()!=-1&&list.getSelectedValue().contains("(DIR)")){
            btnDescargar.setEnabled(false);
            lblSelectedFile.setForeground(Color.red);
        }else if(list.getSelectedIndex()!=-1&&list.getSelectedValue().equals(directorioInicial)){
            btnDescargar.setEnabled(false);
            lblSelectedFile.setForeground(Color.red);
        }else{
            btnDescargar.setEnabled(true);
            lblSelectedFile.setForeground(Color.black);
        }
        lblSelectedFile.setText("- "+list.getSelectedValue());
    }

    private void descargarFichero(String selectedFile) {
        try {
            client.changeWorkingDirectory(directorioDescarga);
            selectedFile=list.getSelectedValue();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(carpetaDestino+"/"+selectedFile));
            if(client.retrieveFile(selectedFile, out)){
                lblSelectedFile.setText("-descargado correctamente");
            }else{
                lblError.setText("-Error al descargar el archivo");
            }
        } catch (IOException ex) {
                Logger.getLogger(ClienteBasicoFTP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void eliminarFichero(String selectedFile) {
        try{
            directorioSeleccionado=directorioInicial;
            client.changeWorkingDirectory(directorioInicial);
            selectedFile=directorioInicial+"/"+list.getSelectedValue();
            //client.deleteFile(selectedFile);
            if(client.deleteFile(selectedFile)){
                lblSelectedFile.setText("-Eliminado correctamente");
                printAllFiles(directorioInicial);
            }else{
                lblError.setText("-Error al eliminar");
            }
            
        }catch(IOException ex){
            Logger.getLogger(ClienteBasicoFTP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
        
